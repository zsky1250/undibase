package com.udf.core.web.controller;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.udf.core.orm.nestedSet.support.NestedSetUtil;
import com.udf.showcase.dao.ICatalogDao;
import com.udf.showcase.entity.Catalog;
import com.udf.showcase.service.ICatalogService;
import com.udf.showcase.service.Impl.ManualCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Created by 张未然 on 2015/8/20.
 */

@Controller
public class TreeController {

    @Autowired
    ICatalogDao catalogDao;

    @Autowired
    ManualCache manualCache;

    @Autowired
    ICatalogService catalogService;

    @RequestMapping("/tree")
    public void getTreeStr(Model model){
        List<Catalog> resultList  = catalogService.getTreeByRootID(1l,true);
        model.addAttribute("tree",resultList);
        manualCache.getTreeCache();

    }

    @RequestMapping("/treeNode")
    public void getTreeNode(Model model){
        Catalog catalog = catalogDao.findOne(1l);
        model.addAttribute("node", catalog);
    }

    @ModelAttribute("com.fasterxml.jackson.databind.ser.FilterProvider")
    private FilterProvider addJsonFilterToModel(){
        return new SimpleFilterProvider().addFilter("treeView",
                SimpleBeanPropertyFilter.serializeAllExcept("lft", "parent"));
    }
}
