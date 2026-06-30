package diana.dev.api.http.order;

public record OrderItemRequestDto(
        Long itemId,
        String name,
        Integer quantity
) {}
