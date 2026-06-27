package diana.dev.order_service.domain;


import diana.dev.order_service.api.OrderDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderProcessor {

    private final OrderJpaRepository repository;

    public OrderDto createOrder(OrderDto dto) {

        if (dto.id()!=null) {
            throw new IllegalArgumentException("Id must be empty!");
        }

        if (dto.customerId()==null) {
            throw new IllegalArgumentException("Customer id can't be null");
        }

        if (dto.address()==null) {
            throw new IllegalArgumentException("Address can't be null");
        }

        if (dto.status()!=null) {
            throw new IllegalArgumentException("Status must be empty!");
        }

        OrderEntity entityToSave = OrderEntityMapper.toEntity(dto);
        OrderEntity savedEntity = repository.save(entityToSave);

        log.info("Created order with id={}", savedEntity.getId());

        return OrderEntityMapper.toDto(savedEntity);
    }

    public OrderDto getOrderById(Long id) {

        OrderEntity entity = repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Not found order by id " + id)
        );

        log.info("Retrieved order by id = {}", id);
        return OrderEntityMapper.toDto(entity);
    }

    public List<OrderDto> getAllOrders() {

        List<OrderEntity> entities = repository.findAll();
        log.info("Retrieved all orders");
        return entities.stream().map(OrderEntityMapper::toDto).toList();
    }

}
