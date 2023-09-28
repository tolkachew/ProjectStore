package org.store.controller;

import org.store.dto.DeliveryDto;
import org.store.dto.DeliveryFilterDto;
import org.store.dto.OrderDto;
import org.store.service.DeliveryService;
import org.store.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/deliveries")
@RequiredArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE, makeFinal = true)
public class DeliveryController {

    DeliveryService deliveryService;
    OrderService orderService;

    @GetMapping
    public ModelAndView list(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("list", deliveryService.getAll(new DeliveryFilterDto()));
        modelAndView.setViewName("deliveries/list");

        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView getEditPage(@PathVariable("id") long id){
        ModelAndView modelAndView = new ModelAndView();
        Pair<DeliveryDto, OrderDto> pair = deliveryService.getDeliveryAndOrder(id);
        modelAndView.addObject("deliveryDto", pair.getFirst());
        modelAndView.addObject("orderDto", pair.getSecond());
        modelAndView.addObject("action", "/deliveries");
        modelAndView.addObject("method", "patch");
        modelAndView.setViewName("deliveries/edit");

        return modelAndView;
    }

    @PatchMapping
    public ModelAndView update(DeliveryDto deliveryDto){
        ModelAndView modelAndView = new ModelAndView();
        try {
            deliveryService.update(deliveryDto);
            modelAndView.addObject("list", deliveryService.getAll(new DeliveryFilterDto()));
            modelAndView.setViewName("deliveries/list");
        }catch (IllegalStateException e){
            modelAndView.addObject("errorMessage", e.getMessage());
            modelAndView.addObject("deliveryDto", deliveryDto);
            modelAndView.addObject("orderDto", orderService.getById(deliveryDto.getOrderId()));
            modelAndView.addObject("action", "/deliveries");
            modelAndView.addObject("method", "patch");
            modelAndView.setViewName("deliveries/edit");
        }

        return modelAndView;
    }

    @PostMapping
    public ModelAndView create(DeliveryDto deliveryDto){
        ModelAndView modelAndView = new ModelAndView();
        try{
            deliveryService.create(deliveryDto);
            modelAndView.addObject("list", deliveryService.getAll(new DeliveryFilterDto()));
            modelAndView.setViewName("deliveries/list");
        }catch (IllegalStateException e){
            modelAndView.addObject("errorMessage", e.getMessage());
            modelAndView.addObject("deliveryDto", deliveryDto);
            modelAndView.addObject("orderDto", orderService.getById(deliveryDto.getOrderId()));
            modelAndView.addObject("action", "/deliveries");

            modelAndView.setViewName("deliveries/edit");
        }
        modelAndView.addObject("method", "post");
        return modelAndView;
    }

    @GetMapping("/orders")
    public ModelAndView getOrderSelectionPage(DeliveryDto deliveryDto,  @RequestParam("method") String method){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("deliveryDto", deliveryDto);
        modelAndView.addObject("list", orderService.getOrdersWithoutDelivery());
        modelAndView.addObject("deliveryId", deliveryDto.getId());
        modelAndView.addObject("oldOrderId", deliveryDto.getOrderId());
        modelAndView.addObject("method", method);
        modelAndView.setViewName("deliveries/order-selection");

        return modelAndView;
    }

    @PostMapping("/{id}/order")
    public ModelAndView replaceOrder(@PathVariable("id") long id,
                                     @RequestParam("selectedOrderId") long selectedOrderId,
                                     @RequestParam("oldOrderId") long oldOrderId,
                                     DeliveryDto dto,
                                     @RequestParam("method") String method){
        ModelAndView modelAndView = new ModelAndView();
        if(id != 0) {
            try {
                deliveryService.replaceOrder(id, oldOrderId, selectedOrderId);
            } catch (IllegalStateException e) {
                modelAndView.addObject("errorMessage", e.getMessage());
            }
            modelAndView.addObject("deliveryDto", deliveryService.getById(id));
        }else {
            modelAndView.addObject("deliveryDto", dto);
        }

        modelAndView.addObject("orderDto", orderService.getById(selectedOrderId));
        modelAndView.addObject("action", "/deliveries");
        modelAndView.addObject("method", method);
        modelAndView.setViewName("deliveries/edit");

        return modelAndView;
    }

    @GetMapping("/creation")
    public ModelAndView getCreationPage(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("deliveryDto", new DeliveryDto());
        modelAndView.addObject("orderDto", new OrderDto());
        modelAndView.addObject("action", "/deliveries");
        modelAndView.addObject("method", "post");
        modelAndView.setViewName("deliveries/edit");

        return modelAndView;
    }

    @DeleteMapping("/{id}")
    public ModelAndView delete(@PathVariable("id") long id){
        deliveryService.delete(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("list", deliveryService.getAll(new DeliveryFilterDto()));
        modelAndView.setViewName("deliveries/list");

        return modelAndView;
    }

    @PostMapping("/{id}/completion")
    public ModelAndView completeDelivery(@PathVariable("id") long id){
        deliveryService.completeDelivery(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("list", deliveryService.getAll(new DeliveryFilterDto()));
        modelAndView.setViewName("deliveries/list");

        return modelAndView;
    }
}
