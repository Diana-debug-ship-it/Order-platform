package diana.dev.order_service.domain;


import diana.dev.api.http.order.CreateOrderRequestDto;
import diana.dev.api.http.order.OrderDto;
import diana.dev.api.http.order.OrderStatus;
import diana.dev.api.http.payment.CreatePaymentRequestDto;
import diana.dev.api.http.payment.CreatePaymentResponseDto;
import diana.dev.api.http.payment.PaymentStatus;
import diana.dev.api.kafka.OrderPaidEvent;
import diana.dev.order_service.api.OrderPaymentRequest;
import diana.dev.order_service.domain.db.OrderEntity;
import diana.dev.order_service.domain.db.OrderEntityMapper;
import diana.dev.order_service.domain.db.OrderItemEntity;
import diana.dev.order_service.domain.db.OrderJpaRepository;
import diana.dev.order_service.external.PaymentHttpClient;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@AllArgsConstructor
@Service
public class OrderProcessor {

    private final OrderJpaRepository repository;
    private final OrderEntityMapper mapper;
    private final PaymentHttpClient httpClient;
    private final KafkaTemplate<Long, OrderPaidEvent> kafkaTemplate;

    @Value("${order-paid-topic}")
    private String orderPaidTopic;

    public OrderDto createOrder(CreateOrderRequestDto request) {

        if (request.customerId()==null) {
            throw new IllegalArgumentException("Customer id can't be null");
        }

        if (request.address()==null) {
            throw new IllegalArgumentException("Address can't be null");
        }

        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item!");
        }

        var entityToSave = mapper.toEntity(request);
        calculatePriceForOrder(entityToSave);
        entityToSave.setStatus(diana.dev.api.http.order.OrderStatus.PENDING_PAYMENT);
        entityToSave.setCourierName("courier");
        entityToSave.setEtaMinutes(10);
        OrderEntity savedEntity = repository.save(entityToSave);

        log.info("Created order with id={}", savedEntity.getId());

        return mapper.toOrderDto(savedEntity);
    }

    public OrderDto getOrderById(Long id) {

        OrderEntity entity = repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Not found order by id " + id)
        );

        log.info("Retrieved order by id = {}", id);
        return mapper.toOrderDto(entity);
    }

    public List<OrderDto> getAllOrders() {

        List<OrderEntity> entities = repository.findAll();
        log.info("Retrieved all orders");
        return entities.stream().map(mapper::toOrderDto).toList();
    }

//    private boolean validateItem(OrderItemDto dto) {
//        return dto.id() == null && dto.quantity() > 0 && dto.itemId() != null && dto.priceAtPurchase().compareTo(BigDecimal.ZERO) > 0;
//    }

    private void calculatePriceForOrder(OrderEntity entity) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItemEntity item: entity.getItems()) {
            var randomPrice = ThreadLocalRandom.current().nextDouble(100, 5000);
            item.setPriceAtPurchase(BigDecimal.valueOf(randomPrice));

            totalPrice = item.getPriceAtPurchase()
                    .multiply(BigDecimal.valueOf(item.getQuantity()))
                    .add(totalPrice);
        }

        entity.setTotalAmount(totalPrice);
    }

    public OrderDto processPayment(
            Long id, OrderPaymentRequest request
    ) {

        var entity = repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Not found order by id " + id)
        );

        if (!entity.getStatus().equals(OrderStatus.PENDING_PAYMENT)) {
            throw new IllegalStateException("Order must be in status PENDING_PAYMENT");
        }

        var response = httpClient.createPayment(CreatePaymentRequestDto.builder()
                        .orderId(id)
                        .paymentMethod(request.paymentMethod())
                        .amount(entity.getTotalAmount())
                .build());

        var status = response.paymentStatus().equals(PaymentStatus.PAYMENT_SUCCEEDED)
                ? OrderStatus.PAID
                : OrderStatus.PAYMENT_FAILED;

        entity.setStatus(status);

        sendOrderPaidEvent(entity, response);

        return mapper.toOrderDto(repository.save(entity));
    }

    private void sendOrderPaidEvent(OrderEntity entity, CreatePaymentResponseDto responseDto) {
        kafkaTemplate.send(
                orderPaidTopic,
                entity.getId(),
                OrderPaidEvent.builder()
                        .orderId(entity.getId())
                        .amount(entity.getTotalAmount())
                        .paymentMethod(responseDto.paymentMethod())
                        .paymentId(responseDto.paymentId())
                        .build()
        ).thenAccept(result -> {
            log.info("Order paid event sent with id={}", entity.getId());
        })
        ;
    }

}
