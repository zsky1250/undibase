package com.udf.core.entity;

import com.udf.common.orm.JPATreeListner;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;

/**
 * Created by zwr on 2015/2/15.
 */
@Entity
@EntityListeners({JPATreeListner.class})
public class Catalog extends NestedTreeEntity<Catalog> {

    private String name;
    private String url;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
