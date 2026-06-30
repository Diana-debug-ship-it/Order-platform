package diana.dev.payment_service.api;

import diana.dev.payment_service.domain.PaymentMethod;
import diana.dev.payment_service.domain.PaymentStatus;

import java.math.BigDecimal;

public record CreatePaymentResponseDto(
        Long paymentId,
        PaymentStatus paymentStatus,
        Long orderId,
        PaymentMethod paymentMethod,
        BigDecimal amount
) {
}
