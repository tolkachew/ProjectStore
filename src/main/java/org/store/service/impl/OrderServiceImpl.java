package org.store.service.impl;

import org.store.dao.GoodsRepository;
import org.store.dao.OnlineStoresRepository;
import org.store.dao.OrderRepository;
import org.store.domain.Goods;
import org.store.domain.OnlineStore;
import org.store.domain.Order;
import org.store.dto.GoodsDto;
import org.store.dto.OnlineStoreDto;
import org.store.dto.OrderContainerDto;
import org.store.dto.OrderDto;
import org.store.dto.OrderFilterDto;
import org.store.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {

    OrderRepository orderRepository;
    OnlineStoresRepository onlineStoreRepository;

    private static final int DEFAULT_PAGE_SIZE = 50;

    private static final int DEFAULT_PAGE_NUM = 0;

    private static final String DEFAULT_SORT_FIELD = "Order date";

    private static final Map<String, String> map;
    private final GoodsRepository goodsRepository;

    static{
        map = new HashMap<>();
        map.put("Order date", "orderDateTime");
        map.put("Goods' name", "goods_name");
        map.put("Online store", "onlineStore_internetAddress");
        map.put("Amount", "amount");
        map.put("Client", "clientFullName");
        map.put("Order confirmation", "");

    }

    @Override
    public OrderDto getById(long id) {
        Optional<Order> optional = orderRepository.findById(id);
        return optional.map(OrderDto::new).orElseGet(OrderDto::new);
    }

    @Override
    public List<OrderDto> getOrdersWithoutDelivery() {
        List<Order> orders = orderRepository.findWithoutDelivery();
        return convertToDtoList(orders.stream());
    }

    @Override
    public List<OrderDto> getAll(OrderFilterDto filterDto) {
        correctFilter(filterDto);
        Pageable pageable = PageRequest.of(filterDto.getPageNum(),
                filterDto.getPageSize(), Sort.by(map.get(filterDto.getSortBy())));
        Example<Order> example = Example.of(new Order(null, null, null, null, null, null, null, null, false));
        Page<Order> page = orderRepository.findAll(example, pageable);
        return convertToDtoList(page.stream());
    }

    @Override
    public OrderContainerDto getOrderContainerById(long id) {
        Example<Order> example = Example.of(new Order(id, null, null, null, null, null, null, null, false));
        Optional<Order> orderOptional = orderRepository.findOne(example);
        if(orderOptional.isPresent()){
            OrderDto orderDto = new OrderDto(orderOptional.get());
            GoodsDto goodsDto = new GoodsDto(orderOptional.get().getGoods());
            goodsDto.setId(null);
            OnlineStoreDto onlineStoreDto = new OnlineStoreDto(orderOptional.get().getOnlineStore());
            onlineStoreDto.setId(null);
            return new OrderContainerDto(orderDto, goodsDto, onlineStoreDto);
        }
        return new OrderContainerDto();
    }

    @Override
    public void save(OrderDto orderDto) {
        orderRepository.save(toEntity(orderDto));
    }

    @Override
    public void delete(long id) {
        orderRepository.setDeletedById(true, id);
    }

    private void correctFilter(OrderFilterDto filterDto){
        if(filterDto.getPageSize() <= 0 || filterDto.getPageNum() < 0){
            filterDto.setPageSize(DEFAULT_PAGE_SIZE);
            filterDto.setPageNum(DEFAULT_PAGE_NUM);
        }
        if(!map.containsKey(filterDto.getSortBy())){
            filterDto.setSortBy(DEFAULT_SORT_FIELD);
        }
    }

    private Order toEntity(OrderDto dto){
        Long id = dto.getId() == 0 ? null : dto.getId();
        OnlineStore onlineStore;
        Goods goods;
        try {
            onlineStore = onlineStoreRepository.findById(dto.getStoreId()).get();
            goods = goodsRepository.findById(dto.getGoodsId()).get();
        }catch (NoSuchElementException e){
            throw new IllegalStateException("Online store and goods must be selected");
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dateTime;
        try {
            dateTime = dto.getOrderDate() == null || dto.getOrderTime() == null
                    ? parseDateTime(now.toLocalDate().toString(), now.toLocalTime().format(DateTimeFormatter.ISO_TIME))
                    : parseDateTime(dto.getOrderDate(), dto.getOrderTime());
        }catch (DateTimeParseException e){
            throw new IllegalStateException("Incorrect date or time");
        }
        boolean confirmation = "Yes".equals(dto.getOrderConfirmation());
        return new Order(id, onlineStore, goods, dateTime, dto.getAmount(),
                dto.getClientFullName(), dto.getTelephone(), confirmation, false);
    }

    private List<OrderDto> convertToDtoList(Stream<Order> stream){
        return stream.map(OrderDto::new).toList();
    }

    private LocalDateTime parseDateTime(String dateStr, String timeStr){
        LocalTime time = LocalTime.parse(timeStr);
        LocalDate date = LocalDate.parse(dateStr);
        return LocalDateTime.of(date, time);
    }
}
