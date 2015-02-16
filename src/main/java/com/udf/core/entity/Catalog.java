package com.udf.core.entity;

import com.udf.core.entity.AbstracNestedTree;

import javax.persistence.Entity;

/**
 * Created by zwr on 2015/2/15.
 */
@Entity
public class Catalog extends AbstracNestedTree {

    private String name;

    private String url;

    public Catalog(String name, String url) {
        this.name = name;
        this.url = url;
    }
}
