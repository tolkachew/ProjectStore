package org.store.dto;

import org.store.domain.Delivery;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryDto {
    long id;

    String deliveryDate;

    String deliveryTime;

    String address;

    String courierFullName;

    String deliveryCost;

    long orderId;

    public DeliveryDto(Delivery delivery) {
        id = delivery.getId();
        deliveryDate = delivery.getDeliveryDateTime().toLocalDate().toString();
        deliveryTime = delivery.getDeliveryDateTime().toLocalTime().toString();
        address = delivery.getAddress();
        courierFullName = delivery.getCourierFullName();
        deliveryCost = Integer.toString(delivery.getDeliveryCost());
        deliveryCost = deliveryCost.substring(0, deliveryCost.length()-2)+'.'
                +deliveryCost.substring(deliveryCost.length()-2);
        orderId = delivery.getOrder().getId();
    }
}
