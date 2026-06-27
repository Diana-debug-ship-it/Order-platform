package diana.dev.order_service.api;

import diana.dev.order_service.domain.OrderEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;

public record OrderItemDto(

        Long id,
        Integer quantity,
        BigDecimal priceAtPurchase,
        Long itemId
) {
}
