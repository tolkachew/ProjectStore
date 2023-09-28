package org.store.controller;

import org.store.dto.OnlineStoreDto;
import org.store.dto.OnlineStoreFilterDto;
import org.store.service.GoodsService;
import org.store.service.OnlineStoresService;
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

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/stores")
@RequiredArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE, makeFinal = true)
public class OnlineStoresController {

    OnlineStoresService storesService;
    GoodsService goodsService;

    @GetMapping
    public ModelAndView getAllStores(){
        return getAllStores(new OnlineStoreFilterDto());
    }

    @GetMapping("/filter")
    public ModelAndView getAllStoresFiltered(OnlineStoreFilterDto filterDto){
        return getAllStores(filterDto);
    }

    @GetMapping("/creation")
    public ModelAndView getCreationPage(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("storeDto", new OnlineStoreDto());
        //modelAndView.addObject("method", "post");
        //modelAndView.addObject("action", "/stores/creation");
        modelAndView.addObject("list", new ArrayList<OnlineStoreDto>());

        modelAndView.setViewName("stores/edit");
        return modelAndView;
    }

    @PostMapping("/creation")
    public ModelAndView create(OnlineStoreDto storeDto){
        storesService.save(storeDto);
        return getAllStores(new OnlineStoreFilterDto());
    }

    @PatchMapping("/{id}")
    public ModelAndView update(@PathVariable("id") long id, OnlineStoreDto storeDto){
        storesService.save(storeDto);
        return getAllStores(new OnlineStoreFilterDto());
    }

    @DeleteMapping("/{id}")
    public ModelAndView delete(@PathVariable("id") long id){
        storesService.delete(id);
        return getAllStores(new OnlineStoreFilterDto());
    }

    @DeleteMapping("/{storeId}/goods/{goodsId}")
    public ModelAndView removeGoodsFromStore(@PathVariable("storeId") long storeId,
                                             @PathVariable("goodsId") long goodsId,
                                             OnlineStoreDto storeDto){
        storesService.removeGoodsFromStore(storeId, goodsId);
        ModelAndView mav = new ModelAndView();
        mav.addObject("storeDto", storeDto);
        mav.addObject("list", goodsService.getGoodsFromStore(storeId));
        mav.setViewName("stores/edit");
        return mav;
    }

    @GetMapping("/{id}")
    public ModelAndView getEditPage(@PathVariable("id") long id){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("storeDto", storesService.getById(id));
        //modelAndView.addObject("method", "patch");
        //modelAndView.addObject("action", "/stores/creation");
        modelAndView.addObject("list", goodsService.getGoodsFromStore(id));

        modelAndView.setViewName("stores/edit");
        return modelAndView;
    }

    @GetMapping("/{id}/goods")
    public ModelAndView getGoodsPage(@PathVariable("id") long id, OnlineStoreDto storeDto){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("storeDto", storeDto);
        modelAndView.addObject("goodsList", goodsService.getGoodsNotFromStore(id));
        modelAndView.setViewName("stores/goods-selection");

        return modelAndView;
    }

    @GetMapping("/selecting/goods")
    public ModelAndView getGoodsPage(OnlineStoreDto storeDto){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("storeDto", storeDto);
        modelAndView.addObject("goodsList", goodsService.getGoodsNotFromStore(0));
        modelAndView.setViewName("stores/goods-selection");

        return modelAndView;
    }

    @PostMapping("/{storeId}/goods/{goodsId}")
    public ModelAndView addGoodsToStore(@PathVariable("storeId") long storeId,
                                        @PathVariable("goodsId") long goodsId,
                                        OnlineStoreDto storeDto

                                        //@ModelAttribute("gIds") Long[] ids){
                                        /*@ModelAttribute("gIds") ArrayList<Long> ids*/){
        //storesService.addGoodsToStore(id, ids.toArray(arr));
        ModelAndView mav = new ModelAndView();
        if(storeId == 0){
            storesService.save(storeDto);
        }
        storesService.addGoodsToStore(storeDto.getId(), goodsId);
        mav.addObject("storeDto", storeDto);
        mav.addObject("list", goodsService.getGoodsFromStore(storeDto.getId()));
        mav.setViewName("stores/edit");
        return mav;
    }

    private ModelAndView getAllStores(OnlineStoreFilterDto filterDto){
        ModelAndView modelAndView = new ModelAndView();
        List<String> sortingList = storesService.getSortingList(filterDto.getSortBy());
        List<OnlineStoreDto> list = storesService.getAll(filterDto);
        modelAndView.addObject("storesFilterDto", filterDto);
        modelAndView.addObject("sortingList", sortingList);
        modelAndView.addObject("list", list);
        modelAndView.setViewName("stores/list");

        return modelAndView;
    }
}
