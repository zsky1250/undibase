package com.udf.web.menu.service;

import com.udf.web.menu.MenuConfigurer;
import com.udf.web.menu.bean.Menu;
import com.udf.web.menu.bean.MenuItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.xml.transform.stream.StreamSource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by zwr on 2014/12/16.
 */
@Service
public class MenuService {
    private Menu menu;
    @Resource
    private MenuConfigurer configurer;
    @Resource
    private Unmarshaller unmarshaller;
    private static Logger log = LoggerFactory.getLogger(MenuService.class);


    @PostConstruct
    public void init(){
        loadMenu();
    }

    private Menu loadMenu(){
        try {
            File f = configurer.getResource().getFile();
            menu = (Menu) unmarshaller.unmarshal(new StreamSource(f));
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.debug("载入Menu配置文件[name={}]",menu.getName());
        return menu;
    }

    public Menu getMenu(){
        return menu;
    }

}
