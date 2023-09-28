package org.store.controller;

import org.store.dto.GoodsDto;
import org.store.dto.GoodsDtoContainer;
import org.store.dto.GoodsFilterDto;
import org.store.service.GoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/goods")
@RequiredArgsConstructor
public class GoodsController {

    private final GoodsService goodsService;

    @GetMapping
    public ModelAndView list(Model model){
        ModelAndView modelAndView = new ModelAndView();
        setAllEntitiesToModel(modelAndView);

        return modelAndView;
    }

    @GetMapping("/filter")
    public ModelAndView filter(@ModelAttribute GoodsFilterDto goodsFilterDto, Model model){
        ModelAndView modelAndView = new ModelAndView();
        GoodsDtoContainer container = goodsService.getAllByFilter(goodsFilterDto);
        modelAndView.addObject("list", container.getGoodsDtos());
        modelAndView.addObject("sortingList", goodsService.getSortingNames(
                goodsFilterDto.getSortBy()));
        modelAndView.addObject("goodsFilterDto", container.getFilterDto());

        modelAndView.setViewName("goods/list");

        return modelAndView;
    }

    @GetMapping("/creation")
    public ModelAndView getCreationPage(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("goodsDto", new GoodsDto());
        modelAndView.addObject("method", "post");
        modelAndView.addObject("action", "/goods/creation");

        modelAndView.setViewName("goods/edit");
        return modelAndView;
    }

    @PostMapping("/creation")
    public ModelAndView create(@ModelAttribute("goodsDto") GoodsDto goodsDto){
        ModelAndView modelAndView = new ModelAndView();
        try{
            goodsService.save(goodsDto);
        }catch (IllegalStateException e){
            modelAndView.addObject("errorMessage", e.getMessage());
            modelAndView.addObject("goodsDto", goodsDto);
            modelAndView.addObject("method", "post");
            modelAndView.addObject("action", "/goods/creation");
            modelAndView.setViewName("goods/edit");
            return modelAndView;
        }
        setAllEntitiesToModel(modelAndView);

        return modelAndView;
    }

    @DeleteMapping("/{id}")
    public ModelAndView delete(@PathVariable("id") long id){
        goodsService.delete(id);
        ModelAndView modelAndView = new ModelAndView();
        setAllEntitiesToModel(modelAndView);

        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView getEditPage(@PathVariable("id") long id){
        ModelAndView modelAndView = new ModelAndView();
        GoodsDto dto = goodsService.getById(id);
        modelAndView.addObject("goodsDto", dto);
        modelAndView.addObject("method", "patch");
        modelAndView.addObject("action", "/goods/creation");

        modelAndView.setViewName("goods/edit");
        return modelAndView;

    }

    @PatchMapping("/creation")
    public ModelAndView update(GoodsDto goodsDto) {
        ModelAndView modelAndView = new ModelAndView();
        try{
            goodsService.save(goodsDto);
        }catch (IllegalStateException e){
            modelAndView.addObject("errorMessage", e.getMessage());
            modelAndView.addObject("goodsDto", goodsDto);
            modelAndView.addObject("method", "patch");
            modelAndView.addObject("action", "/goods/creation");
            modelAndView.setViewName("goods/edit");
            return modelAndView;
        }
        setAllEntitiesToModel(modelAndView);

        return modelAndView;
    }

    private void setAllEntitiesToModel(ModelAndView modelAndView){
        GoodsDtoContainer container = goodsService.getAll();
        modelAndView.addObject("list", container.getGoodsDtos());
        modelAndView.addObject("sortingList", goodsService.getSortingNames(
                container.getFilterDto().getSortBy()));
        modelAndView.addObject("goodsFilterDto", container.getFilterDto());
        modelAndView.setViewName("goods/list");
    }
}
