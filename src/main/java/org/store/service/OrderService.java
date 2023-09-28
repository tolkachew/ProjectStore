package org.store.service;

import org.store.dto.OrderContainerDto;
import org.store.dto.OrderDto;
import org.store.dto.OrderFilterDto;

import java.util.List;

public interface OrderService {

    List<OrderDto> getAll(OrderFilterDto filterDto);

    OrderDto getById(long id);

    List<OrderDto> getOrdersWithoutDelivery();

    OrderContainerDto getOrderContainerById(long id);

    void save(OrderDto orderDto);

    void delete(long id);
}
