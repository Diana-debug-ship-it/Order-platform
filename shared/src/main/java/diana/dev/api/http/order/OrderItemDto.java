package diana.dev.api.http.order;

import java.math.BigDecimal;

public record OrderItemDto(

        Long id,
        Integer quantity,
        BigDecimal priceAtPurchase,
        Long itemId
) {
}
