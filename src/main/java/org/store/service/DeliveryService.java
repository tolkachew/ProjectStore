package org.store.service;

import org.store.dto.DeliveryDto;
import org.store.dto.DeliveryFilterDto;
import org.store.dto.OrderDto;
import org.springframework.data.util.Pair;

import java.util.List;

public interface DeliveryService {
    List<DeliveryDto> getAll(DeliveryFilterDto filter);

    Pair<DeliveryDto, OrderDto> getDeliveryAndOrder(long deliveryId);

    DeliveryDto getById(long id);

    DeliveryDto getByOrderId(long orderId);

    void update(DeliveryDto deliveryDto);

    void create(DeliveryDto deliveryDto);

    void replaceOrder(long deliveryId, long oldOrderId, long newOrderId);

    void delete(long id);

    void completeDelivery(long id);
}
