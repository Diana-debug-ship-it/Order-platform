package diana.dev.order_service.domain;

import diana.dev.order_service.api.OrderDto;
import diana.dev.order_service.api.OrderItemDto;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderEntityMapper {

    public static OrderEntity toEntity(OrderDto dto) {

        if (dto == null) return null;

        OrderEntity entity = new OrderEntity(
                dto.id(),
                dto.customerId(),
                dto.address(),
                dto.totalAmount(),
                dto.courierName(),
                dto.etaMinutes(),
                dto.status(),
                new LinkedHashSet<>()
        );

        if (dto.orderItemEntities() != null) {
            Set<OrderItemEntity> itemEntities = dto.orderItemEntities().stream()
                    .map(itemDto -> {
                        OrderItemEntity itemEntity = new OrderItemEntity();
                        itemEntity.setId(itemDto.id());
                        itemEntity.setQuantity(itemDto.quantity());
                        itemEntity.setPriceAtPurchase(itemDto.priceAtPurchase());
                        itemEntity.setItemId(itemDto.itemId());
                        itemEntity.setOrder(entity);
                        return itemEntity;
                    }).collect(Collectors.toCollection(LinkedHashSet::new));

            entity.setOrderItemEntities(itemEntities);
        }

        return entity;
    }

    public static OrderDto toDto(OrderEntity entity) {

        if (entity == null) return null;

        Set<OrderItemDto> itemDtos = new LinkedHashSet<>();

        if (entity.getOrderItemEntities() != null) {
            entity.getOrderItemEntities().stream()
                    .map(itemEntity -> new OrderItemDto(
                            itemEntity.getId(),
                            itemEntity.getQuantity(),
                            itemEntity.getPriceAtPurchase(),
                            itemEntity.getItemId()
                    ))
                    .forEach(itemDtos::add);
        }

        return new OrderDto(
                entity.getId(),
                entity.getCustomerId(),
                entity.getAddress(),
                entity.getTotalAmount(),
                entity.getCourierName(),
                entity.getEtaMinutes(),
                entity.getStatus(),
                itemDtos
        );
    }
}
