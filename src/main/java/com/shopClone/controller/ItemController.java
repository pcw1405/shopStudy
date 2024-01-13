package com.shopClone.controller;

import com.shopClone.dto.ItemFormDto;
import com.shopClone.service.ItemService;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping(value = "/admin/item/new")
    public String ItemForm(Model model){
        model.addAttribute("itemFormDto",new ItemFormDto());
        return "/item/itemForm";
    }
}
