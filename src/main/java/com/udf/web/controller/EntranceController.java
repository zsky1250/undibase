package com.udf.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by zwr on 2014/12/18.
 */
@Controller
public class EntranceController {

    @RequestMapping("/index")
    public String index(){
        return "index";
    }


}
