package com.udf.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by zwr on 2015/2/4.
 *
 * 处理未捕获的异常
 * 如没有找到渲染页面，报404.
 * 具体情况根据web.xml <error-page> 的配置决定
 */
@Controller
public class ErrorContoller {

    @RequestMapping("/error/PageNotFound")
    public String PageNotFound(HttpServletRequest request,Model model){
        model.addAttribute("code", request.getAttribute("javax.servlet.error.status_code"));
        model.addAttribute("msg", request.getAttribute("javax.servlet.error.message"));
        return "404";
    }
}
