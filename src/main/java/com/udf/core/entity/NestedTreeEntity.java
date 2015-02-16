package com.udf.core.entity;

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
    private int id;

    private int lft;

    private int rgt;

    @ManyToOne
    @JoinColumn(name="parent")
    private T parent;

    @OneToMany(mappedBy = "parent",cascade = CascadeType.REMOVE)
    private List<T> children;

    @Transient
    private T oriParent;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
        this.oriParent = this.parent;
        this.parent = parent;
    }

    public T getOriParent() {
        return oriParent;
    }

    public void emptyOriParent(){
        this.oriParent = null;
    }

    public List<T> getChildren() {
        return children;
    }

    public void setChildren(List<T> children) {
        this.children = children;
    }
}
