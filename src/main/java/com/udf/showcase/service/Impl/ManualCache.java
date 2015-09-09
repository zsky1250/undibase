package com.udf.showcase.service.Impl;

import com.udf.showcase.entity.Catalog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.List;

/*
 * Created by zwr on 2015/9/9.
 */
@Service
public class ManualCache {

    @Autowired
    CacheManager cacheManager;

   public void getTreeCache(){
       Cache treeCache = cacheManager.getCache("tree");
       List<Catalog> catalogList = (List<Catalog>) treeCache.get("1-ordered").get();
       for (Catalog catalog : catalogList) {
           System.out.println(catalog.getName()+"--"+catalog.getUrl());
       }
   }


}
