package org.store.controller;

import org.store.service.FirmService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/firms")
@RequiredArgsConstructor
public class FirmController {

    private final FirmService service;

    @GetMapping
    public String getAllFirms(Model model){
        model.addAttribute("firms", service.getAll());
        return "firm/list";
    }

    @GetMapping("/{id}")
    public String getFirm(@PathVariable("id") long id, Model model){
        model.addAttribute("id", id);
        model.addAttribute("name", service.getFirmById(id));
        return "firm/edit";
    }

    @GetMapping("/creation")
    public String getFirmCreationPage(){
        return "firm/creation";
    }

    @PostMapping
    public String createFirm(@RequestParam("name") String name, Model model){
        if(service.create(name)){
            model.addAttribute("firms", service.getAll());
            return "firm/list";
        }
        model.addAttribute("errorMessage", "Firm with name "+name+" already exists");
        model.addAttribute("name", name);

        return "firm/creation";
    }

    @PutMapping
    public String updateFirm(@RequestParam("id") long id, @RequestParam("name") String name, Model model){
        if(service.updateFirm(id, name)){
            model.addAttribute("firms", service.getAll());
            return "firm/list";
        }
        model.addAttribute("errorMessage", "Firm with name "+name+" already exists");
        model.addAttribute("id", id);

        return "firm/edit";
    }

    @DeleteMapping("/{id}")
    public String deleteFirm(@PathVariable("id") long id, @RequestParam("name") String name, Model model){
        if(!service.deleteFirm(id, name)){
            model.addAttribute("errorMessage", "Could not delete firm "+name+
                    " because there are goods of this firm");
            model.addAttribute("name", name);
            return "firm/edit";
        }

        model.addAttribute("firms", service.getAll());
        return "firm/list";
    }
}
