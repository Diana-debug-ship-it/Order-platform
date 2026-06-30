package diana.dev.payment_service.api;

import diana.dev.payment_service.domain.PaymentMethod;

import java.math.BigDecimal;

public record CreatePaymentRequestDto(
        Long orderId,
        PaymentMethod paymentMethod,
        BigDecimal amount
) {
}
