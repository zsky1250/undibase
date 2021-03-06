package com.udf.showcase.entity;

import com.udf.core.orm.nestedSet.entity.NestedSetEntity;
import com.udf.core.orm.nestedSet.support.JPATreeListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;

/**
 * Created by zwr on 2015/2/15.
 */
@Entity
@EntityListeners({JPATreeListener.class})
public class Catalog extends NestedSetEntity<Catalog,Long> {

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
