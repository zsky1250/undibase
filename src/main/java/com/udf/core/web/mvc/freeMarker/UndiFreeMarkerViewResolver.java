package com.udf.core.web.mvc.freeMarker;

import org.springframework.web.servlet.view.AbstractTemplateViewResolver;

/**
 * Created by zwr on 2014/12/18.
 * 提供一个基础的 只暴露变量的viewResolver
 */
public class UndiFreeMarkerViewResolver extends AbstractTemplateViewResolver {
    private Class<?> viewClass;

    public Class<?> getViewClass() {
        return viewClass;
    }

    public void setViewClass(Class<?> viewClass) {
        this.viewClass = viewClass;
    }

    public UndiFreeMarkerViewResolver() {
        this.viewClass = UndiFreeMarkerView.class;
    }
}
