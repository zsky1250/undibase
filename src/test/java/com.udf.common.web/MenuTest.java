package com.udf.common.web;

import com.udf.web.menu.bean.Menu;
import com.udf.web.menu.bean.MenuItem;
import com.udf.web.menu.service.MenuService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by zwr on 2014/12/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-test.xml")
public class MenuTest {

    @Autowired
    MenuService menuService;

    @Test
    public void testLoadMenu(){
        Menu menu = menuService.getMenu();
        System.out.println(menu);
        for (MenuItem menuItem : menu.getItems()) {
            System.out.println(menuItem);
        }
    }

}
