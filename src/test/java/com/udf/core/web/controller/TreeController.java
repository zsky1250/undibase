package com.udf.core.web.controller;

import com.udf.common.orm.NestedSetUtil;
import com.udf.core.entity.Catalog;
import com.udf.core.nestedTree.dao.ICatalogDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Created by 张未然 on 2015/8/20.
 */

@Controller
public class TreeController {

    @Autowired
    ICatalogDao catalogDao;

    @RequestMapping("/tree")
    public void getTreeStr(Model model){
        List<Catalog> resultList = NestedSetUtil.toHierachyTree(catalogDao.getTreeByRootID(1L));
        model.addAttribute("tree",resultList);
    }
}
