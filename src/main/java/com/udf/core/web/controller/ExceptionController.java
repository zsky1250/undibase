package com.udf.core.web.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Created by zwr on 2015/2/4.
 * Handle global exception by using ControllerAdvice
 * @see org.springframework.web.bind.annotation.ControllerAdvice
 */
@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(Exception.class)
    public void PageNotFoundHandler(Exception ex){
        ex.printStackTrace();
    }
}
