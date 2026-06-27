package diana.dev.order_service.domain;

public enum OrderStatus {

    PENDING_PAYMENT,
    PAID,
    PAYMENT_FAILED,
    PENDING_DELIVERY,
    DELIVERED

}
