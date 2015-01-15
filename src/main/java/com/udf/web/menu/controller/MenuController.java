package com.udf.web.menu.controller;

import com.udf.web.menu.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by zwr on 2014/12/19.
 */

@Controller
public class MenuController {

    @Autowired
    private MenuService menuService;

    @RequestMapping("/menu")
    public String getMenu(Model model) {
        model.addAttribute("menu",menuService.getMenu());
        return "menu";
    }


}