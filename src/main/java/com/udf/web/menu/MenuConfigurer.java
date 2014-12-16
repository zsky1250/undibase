package com.udf.web.menu;

import org.springframework.core.io.Resource;

/**
 * Created by zwr on 2014/12/16.
 * 配置menu菜单
 */
public class MenuConfigurer {
    private Resource resource;

    public MenuConfigurer(Resource resource) {
        this.resource = resource;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
