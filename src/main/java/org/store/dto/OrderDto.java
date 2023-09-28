package org.store.dto;

import org.store.domain.Order;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDto {
    long id;

    long storeId;

    long goodsId;

    String orderDate;

    String orderTime;

    String goodsName;

    int amount;

    String clientFullName;

    String telephone;

    String orderConfirmation;

    String onlineStore;

    public OrderDto(Order o) {
        id = o.getId();
        storeId = o.getOnlineStore().getId();
        goodsId = o.getGoods().getId();
        orderDate = o.getOrderDateTime().toLocalDate().toString();
        orderTime = o.getOrderDateTime().toLocalTime().toString().substring(0, 5);
        goodsName = o.getGoods().getName();
        amount = o.getAmount();
        clientFullName = o.getClientFullName();
        telephone = o.getTelephone();
        orderConfirmation = o.getOrderConfirmation() ? "Yes" : "No";
        onlineStore = o.getOnlineStore().getInternetAddress();
    }
}
