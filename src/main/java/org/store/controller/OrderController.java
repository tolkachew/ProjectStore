package org.store.controller;

import org.store.dto.DeliveryDto;
import org.store.dto.GoodsDto;
import org.store.dto.OnlineStoreDto;
import org.store.dto.OrderContainerDto;
import org.store.dto.OrderDto;
import org.store.dto.OrderFilterDto;
import org.store.service.DeliveryService;
import org.store.service.GoodsService;
import org.store.service.OnlineStoresService;
import org.store.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {
    OrderService orderService;
    OnlineStoresService storesService;
    GoodsService goodsService;
    DeliveryService deliveryService;

    @GetMapping
    public ModelAndView list(){
        ModelAndView modelAndView = new ModelAndView();
        List<OrderDto> list = orderService.getAll(new OrderFilterDto());
        modelAndView.addObject("list", list);
        modelAndView.setViewName("orders/list");

        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView getEditPage(@PathVariable("id") long id){
        ModelAndView modelAndView = new ModelAndView();
        OrderContainerDto container = orderService.getOrderContainerById(id);
        modelAndView.addObject("orderDto", container.getOrderDto());
        modelAndView.addObject("goodsDto", container.getGoodsDto());
        modelAndView.addObject("onlineStoreDto", container.getOnlineStoreDto());
        modelAndView.setViewName("orders/edit");
        return modelAndView;
    }

    @GetMapping("/creation")
    public ModelAndView getCreationPage(OrderDto orderDto){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("orderDto", new OrderDto());
        modelAndView.addObject("goodsDto", new GoodsDto());
        modelAndView.addObject("onlineStoreDto", new OnlineStoreDto());
        modelAndView.setViewName("orders/edit");
        return modelAndView;
    }

    @GetMapping("/stores_goods")
    public ModelAndView getStoresAndGoodsSelectionPage(OrderDto orderDto){
        Map<OnlineStoreDto, List<GoodsDto>> map = storesService.getStoreToGoodsMapping();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("orderDto", orderDto);
        modelAndView.addObject("map", map);
        modelAndView.setViewName("orders/stores-goods-selection");
        return modelAndView;
    }

    @GetMapping("/stores/{storeId}/goods/{goodsId}")
    public ModelAndView selectStoreWithGoods(@PathVariable("storeId") long storeId,
                                             @PathVariable("goodsId") long goodsId,
                                             OrderDto orderDto){
        orderDto.setStoreId(storeId);
        orderDto.setGoodsId(goodsId);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("orderDto", orderDto);
        modelAndView.addObject("goodsDto", goodsService.getById(goodsId));
        modelAndView.addObject("onlineStoreDto", storesService.getById(storeId));
        modelAndView.setViewName("orders/edit");
        return modelAndView;
    }

    @PatchMapping("/{id}")
    public ModelAndView update(@PathVariable("id") long id, OrderDto orderDto){
        orderService.save(orderDto);
        return list();
    }

    @PostMapping("/creation")
    public ModelAndView create(OrderDto orderDto){
        try {
            orderService.save(orderDto);
            return list();
        }catch (IllegalStateException e){
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("errorMessage", e.getMessage());
            modelAndView.addObject("orderDto", orderDto);
            modelAndView.addObject("goodsDto", goodsService.getById(orderDto.getGoodsId()));
            modelAndView.addObject("onlineStoreDto", storesService.getById(orderDto.getStoreId()));
            modelAndView.setViewName("orders/edit");

            return modelAndView;
        }
    }

    @GetMapping("/{id}/delivery")
    public ModelAndView getDeliveryPage(@PathVariable("id") long orderId, OrderDto orderDto){
        DeliveryDto deliveryDto = deliveryService.getByOrderId(orderId);
        ModelAndView mav = new ModelAndView();
        mav.addObject("deliveryDto", deliveryDto);
        mav.addObject("orderDto", orderDto);
        mav.addObject("action", "/deliveries");
        mav.addObject("method", deliveryDto.getId() == 0 ? "post" : "patch");
        mav.setViewName("deliveries/edit");

        return mav;
    }

    @GetMapping("/stores")
    public ModelAndView getStoresSelectionPage(OrderDto orderDto){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("orderDto", orderDto);
        modelAndView.addObject("list", storesService.getAll());
        modelAndView.setViewName("orders/stores-selection");
        return modelAndView;
    }

    @GetMapping("/goods")
    public ModelAndView getGoodsSelectionPage(OrderDto orderDto){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("orderDto", orderDto);
        modelAndView.addObject("list", goodsService.getAll().getGoodsDtos());
        modelAndView.setViewName("orders/goods-selection");
        return modelAndView;
    }

    @GetMapping("/stores/{id}")
    public ModelAndView selectStore(@PathVariable("id") long id, OrderDto orderDto){
        orderDto.setStoreId(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("orderDto", orderDto);
        OnlineStoreDto onlineStoreDto = storesService.getById(id);
        onlineStoreDto.setId(null);
        GoodsDto goodsDto = goodsService.getById(orderDto.getGoodsId());
        goodsDto.setId(null);
        modelAndView.addObject("goodsDto", goodsDto);
        modelAndView.addObject("onlineStoreDto", onlineStoreDto);

        modelAndView.setViewName("orders/edit");
        return modelAndView;
    }

    @GetMapping("/goods/{id}")
    public ModelAndView selectGoods(@PathVariable("id") long id, OrderDto orderDto){
        orderDto.setGoodsId(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("orderDto", orderDto);
        OnlineStoreDto onlineStoreDto = storesService.getById(orderDto.getStoreId());
        onlineStoreDto.setId(null);
        GoodsDto goodsDto = goodsService.getById(id);
        goodsDto.setId(null);
        modelAndView.addObject("goodsDto", goodsDto);
        modelAndView.addObject("onlineStoreDto", onlineStoreDto);

        modelAndView.setViewName("orders/edit");
        return modelAndView;
    }



    @DeleteMapping("/{id}")
    public ModelAndView delete(@PathVariable("id") long id){
        orderService.delete(id);
        return list();
    }
}
