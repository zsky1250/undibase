package com.udf.core.menu.service;

import com.udf.core.menu.bean.Menu;
import com.udf.core.menu.bean.MenuItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Unmarshaller;

import javax.annotation.PostConstruct;

import javax.xml.transform.stream.StreamSource;
import java.util.List;


/**
 * Created by zwr on 2014/12/16.
 */
public class MenuService{
    private Menu menu;
    private Resource location;

    @Autowired
    private Unmarshaller unmarshaller;
    private static Logger log = LoggerFactory.getLogger(MenuService.class);


    @PostConstruct
    private Menu loadMenu(){
        try {
            menu = (Menu) unmarshaller.unmarshal(new StreamSource(location.getInputStream()));
        } catch (Exception e) {
            log.debug("读取menu.xml文件出错:{}",e.getMessage());
            e.printStackTrace();
        }
        log.debug("载入Menu配置文件[name={}]",menu.getName());
        return menu;
    }

    public List<MenuItem> getMenu(){
        if(menu!=null&&menu.getItems()!=null&&!menu.getItems().isEmpty()){
            return menu.getItems();
        }
        return null;
    }

    public Resource getLocation() {
        return location;
    }

    public void setLocation(Resource location) {
        this.location = location;
    }
}
