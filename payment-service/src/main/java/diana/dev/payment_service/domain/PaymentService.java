package diana.dev.payment_service.domain;


import diana.dev.api.http.payment.CreatePaymentRequestDto;
import diana.dev.api.http.payment.CreatePaymentResponseDto;
import diana.dev.api.http.payment.PaymentMethod;
import diana.dev.payment_service.domain.db.PaymentEntityMapper;
import diana.dev.payment_service.domain.db.PaymentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@AllArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository repository;
    private final PaymentEntityMapper mapper;


    public CreatePaymentResponseDto processPayment(CreatePaymentRequestDto dto) {

        var found = repository.findByOrderId(dto.orderId());
        if (found.isPresent()) {
            log.info("Payment already exists for orderId={}", dto.orderId());
            return mapper.toResponseDto(found.get());
        }

        var entity = mapper.toEntity(dto);

        var status = dto.paymentMethod().equals(PaymentMethod.QR)
                ? diana.dev.api.http.payment.PaymentStatus.PAYMENT_FAILED
                : diana.dev.api.http.payment.PaymentStatus.PAYMENT_SUCCEEDED;

        entity.setPaymentStatus(status);


        var savedEntity = repository.save(entity);
        return mapper.toResponseDto(savedEntity);
    }

}
