package com.udf.common.orm;

import com.udf.core.entity.NestedTreeEntity;

import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

/**
 * Created by zwr on 2015/2/15.
 */

public class JPATreeListner {



    private static JPATreeListnerDelegator delegator;


    public static JPATreeListnerDelegator getDelegator() {
        return delegator;
    }

    public static void setDelegator(JPATreeListnerDelegator delegator) {
        JPATreeListner.delegator = delegator;
    }

    @PrePersist
    public void preSave(NestedTreeEntity node){
        delegator.preSave(node);
    }

    @PreUpdate
    public void preUpdate(NestedTreeEntity node){
        delegator.preUpdate(node);
    }

    @PreRemove
    public void preRemove(NestedTreeEntity node){
        delegator.preRemove(node);
    }


}
