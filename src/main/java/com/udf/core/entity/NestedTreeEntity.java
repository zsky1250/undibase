package com.udf.core.entity;

import org.springframework.context.annotation.Lazy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zwr on 2015/2/15.
 */
@MappedSuperclass
public abstract class NestedTreeEntity<T extends NestedTreeEntity> {

    @Id
    @GeneratedValue
    private long id;

    private int lft;

    private int rgt;

    @Column(name="parent",updatable = false,insertable = false,nullable = true)
    private Integer parentID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent")
    private T parent;

    @OneToMany(mappedBy = "parent",fetch = FetchType.LAZY)
    private List<T> children;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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


    public Integer getParentIDBeforeUpdate() {
        return parentID;
    }


    @Override
    public String toString(){
        return this.getClass().toString()+"_"+id;
    }

    public String getParentString(){
        return this.getClass().toString()+"_"+parentID;
    }
}
