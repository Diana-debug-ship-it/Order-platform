package diana.dev.order_service.api;

import diana.dev.order_service.domain.OrderItemEntity;
import diana.dev.order_service.domain.OrderStatus;

import java.math.BigDecimal;
import java.util.Set;

public record OrderDto(

        Long id,
        Long customerId,
        String address,
        BigDecimal totalAmount,
        String courierName,
        Integer etaMinutes,
        OrderStatus status,
        Set<OrderItemDto> orderItemEntities
) {
}
