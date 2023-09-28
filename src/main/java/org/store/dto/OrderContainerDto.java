package org.store.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderContainerDto {
    OrderDto orderDto;
    GoodsDto goodsDto;
    OnlineStoreDto onlineStoreDto;

    public OrderContainerDto() {
        orderDto = new OrderDto();
        goodsDto = new GoodsDto();
        onlineStoreDto = new OnlineStoreDto();
    }
}
