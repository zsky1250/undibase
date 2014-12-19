package com.udf.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by zwr on 2014/12/18.
 */
@Controller
public class EntranceController {

    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String showLoginPage(){
        return "login";
    }

    @RequestMapping(value = "/login" ,method = RequestMethod.POST)
    public String login(){
        return "forward:index";
    }

    @RequestMapping("/index")
    public String index(){
        return "index";
    }


}
