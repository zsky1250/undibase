package com.udf.core.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by zwr on 2015/2/15.
 */
@MappedSuperclass
public abstract class NestedSetEntity<T extends NestedSetEntity,ID extends Serializable>{

    @Id
    @GeneratedValue
    private ID id;

    private int lft;

    private int rgt;

    @Column(name="parent",updatable = false,insertable = false,nullable = true)
    private Long parentID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent")
    private T parent;

    @OneToMany(mappedBy = "parent",fetch = FetchType.LAZY)
    private List<T> children;

    private int order=1;

    @Transient
    private boolean batchInsert=false;

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    public int getLft() {
        return lft;
    }

    public void setLft(int lft) {
        this.lft = lft;
    }

    public int getRgt() {
        return rgt;
    }

    public void setRgt(int rgt) {
        this.rgt = rgt;
    }

    public T getParent() {
        return parent;
    }

    public void setParent(T parent) {
        this.parent = parent;
    }

    public List<T> getChildren() {
        return children;
    }

    public void setChildren(List<T> children) {
        this.children = children;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Long getParentIDBeforeUpdate() {
        return parentID;
    }

    public boolean isBatchInsert() {
        return batchInsert;
    }

    public void setBatchInsert(boolean batchInsert) {
        this.batchInsert = batchInsert;
    }

    @Override
    public String toString(){
        return this.getClass().toString()+"_"+id;
    }

}
