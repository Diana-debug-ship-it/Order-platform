package diana.dev.api.http.order;

import java.util.Set;

public record CreateOrderRequestDto(
        Long customerId,
        String address,
        Set<diana.dev.api.http.order.OrderItemRequestDto> items
) {
}
