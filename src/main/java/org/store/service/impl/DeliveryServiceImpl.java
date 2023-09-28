package org.store.service.impl;

import org.store.dao.DeliveryRepository;
import org.store.dao.OrderRepository;
import org.store.domain.Delivery;
import org.store.domain.Order;
import org.store.dto.DeliveryDto;
import org.store.dto.DeliveryFilterDto;
import org.store.dto.OrderDto;
import org.store.service.DeliveryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeliveryServiceImpl implements DeliveryService {

    DeliveryRepository deliveryRepository;
    OrderRepository orderRepository;

    private static final int DEFAULT_PAGE_SIZE = 50;

    private static final int DEFAULT_PAGE_NUM = 0;

    private static final String DEFAULT_SORT_FIELD = "Delivery date";

    private static final Map<String, String> map;

    static {
        map = new HashMap<>();
        map.put("Delivery date", "deliveryDateTime");
        map.put("Address", "address");
        map.put("Courier", "courierFullName");
        map.put("Delivery cost", "deliveryCost");
    }

    @Override
    public List<DeliveryDto> getAll(DeliveryFilterDto filter) {
        correctFilter(filter);
        Pageable pageable = PageRequest.of(filter.getPageNum(),
                filter.getPageSize(), Sort.by(map.get(filter.getSortBy())));
        List<Delivery> deliveries = deliveryRepository.findAllByDeleted(false, pageable);
        return convertToDtoList(deliveries);
    }

    @Override
    public DeliveryDto getById(long id) {
        Optional<Delivery> optional = deliveryRepository.findById(id);
        return optional.map(DeliveryDto::new).orElseGet(DeliveryDto::new);
    }

    @Override
    public DeliveryDto getByOrderId(long orderId) {
        Optional<Delivery> optional = deliveryRepository.findByOrderId(orderId);
        return optional.map(DeliveryDto::new).orElseGet(DeliveryDto::new);
    }

    @Override
    public Pair<DeliveryDto, OrderDto> getDeliveryAndOrder(long deliveryId) {
        Optional<Delivery> delivery = deliveryRepository.findById(deliveryId);
        if(delivery.isPresent()) {
            DeliveryDto deliveryDto = new DeliveryDto(delivery.get());
            OrderDto orderDto = new OrderDto(delivery.get().getOrder());

            return Pair.of(deliveryDto, orderDto);
        }
        return Pair.of(new DeliveryDto(), new OrderDto());
    }

    @Override
    public void update(DeliveryDto deliveryDto) {
        Delivery delivery = toEntity(deliveryDto);
        deliveryRepository.save(delivery);
    }

    @Override
    public void create(DeliveryDto deliveryDto) {
        if(deliveryDto.getOrderId() == 0){
            throw new IllegalStateException("Order must be selected");
        }
        Optional<Order> order = orderRepository.findById(deliveryDto.getOrderId());
        if(order.isPresent()) {
            Delivery delivery = new Delivery(null, order.get(),
                    parseDateTime(deliveryDto.getDeliveryDate(),
                            deliveryDto.getDeliveryTime()),
                    deliveryDto.getAddress(), deliveryDto.getCourierFullName(),
                    parseCost(deliveryDto.getDeliveryCost()), false);
            deliveryRepository.save(delivery);
        }else {
            throw new IllegalStateException("Order does not exist");
        }
    }

    @Override
    public void replaceOrder(long deliveryId, long oldOrderId, long newOrderId) {
        if(oldOrderId != newOrderId) {
            if (deliveryRepository.existsByOrderId(newOrderId)) {
                throw new IllegalStateException("this order already has been bound with delivery");
            }
            Optional<Order> optional = orderRepository.findById(newOrderId);
            if (optional.isPresent()) {
                Order order = optional.get();
                if (!order.getOrderConfirmation()) {
                    order.setOrderConfirmation(true);
                    order = orderRepository.save(order);
                }
                Optional<Delivery> deliveryOptional = deliveryRepository.findById(deliveryId);
                if (deliveryOptional.isPresent()) {
                    Delivery delivery = deliveryOptional.get();
                    delivery.setOrder(order);
                    deliveryRepository.save(delivery);
                }else {
                    throw new IllegalStateException("delivery does not exist");
                }
            }else {
                throw new IllegalStateException("order does not exist");
            }
        }
    }

    @Override
    public void delete(long id) {
        deliveryRepository.deleteById(id);
    }

    @Override
    public void completeDelivery(long id) {
        orderRepository.setDeletedByDeliveryId(true, id);
        deliveryRepository.setDeleted(true, id);

    }

    private void correctFilter(DeliveryFilterDto filterDto){
        if(filterDto.getPageSize() <= 0 || filterDto.getPageNum() < 0){
            filterDto.setPageSize(DEFAULT_PAGE_SIZE);
            filterDto.setPageNum(DEFAULT_PAGE_NUM);
        }
        if(!map.containsKey(filterDto.getSortBy())){
            filterDto.setSortBy(DEFAULT_SORT_FIELD);
        }
    }

    private Delivery toEntity(DeliveryDto dto){
        Optional<Order> optional = orderRepository.findById(dto.getOrderId());
        if(optional.isPresent()){
            try {
                LocalDateTime dateTime = parseDateTime(dto.getDeliveryDate(), dto.getDeliveryTime());
                return new Delivery(dto.getId() == 0 ? null : dto.getId(),
                        optional.get(), dateTime, dto.getAddress(),
                        dto.getCourierFullName(), parseCost(dto.getDeliveryCost()),
                        false);
            }catch (DateTimeParseException e) {
                throw new IllegalStateException("date or/and time are in incorrect format (use dd.MM.yyyy and hh:mm formats)");
            }catch (NumberFormatException e){
                throw new IllegalStateException("cost field is incorrect");
            }
        }

        throw new IllegalStateException("Delivery does not exist");
    }

    private LocalDateTime parseDateTime(String dateStr, String timeStr){
        LocalTime time = LocalTime.parse(timeStr);
        LocalDate date = LocalDate.parse(dateStr);
        return LocalDateTime.of(date, time);
    }

    private List<DeliveryDto> convertToDtoList(List<Delivery> page){
        return page.stream().map(DeliveryDto::new).toList();
    }

    private int parseCost(String cost){
        if(cost != null && cost.matches("\\d+([.,]\\d{1,2})?")){
            String[] costParts = cost.split("[.,]");
            int result = Integer.parseInt(costParts[0])*100;
            return costParts.length == 1 ? result : result + Integer.parseInt(costParts[1]);
        }

        return 0;
    }
}
