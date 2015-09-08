package com.udf.core.web.menu.service;

import org.springframework.core.io.Resource;

/**
 * Created by zwr on 2014/12/16.
 * 配置menu菜单
 */
public class MenuConfigurer {

    private Resource configLocation;

    public MenuConfigurer(Resource resource) {
        this.configLocation = resource;
    }

    public Resource getConfigLocation() {
        return configLocation;
    }

    public void setConfigLocation(Resource configLocation) {
        this.configLocation = configLocation;
    }
}
