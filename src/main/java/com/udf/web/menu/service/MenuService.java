package com.udf.web.menu.service;

import com.udf.web.menu.MenuConfigurer;
import com.udf.web.menu.bean.Menu;
import com.udf.web.menu.bean.MenuItem;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.xml.transform.stream.StreamSource;

import java.io.File;
import java.io.IOException;


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


    private Menu loadMenu(){
        try {
            File f = configurer.getResource().getFile();
            menu = (Menu) unmarshaller.unmarshal(new StreamSource(f));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return menu;
    }

    public Menu getMenu(){
        return loadMenu();
    }

}
