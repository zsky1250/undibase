package com.udf.common.orm;

import com.udf.core.entity.NestedTreeEntity;

import java.io.Serializable;

/**
 * Created by zwr on 2015/2/15.
 */
public interface NestedTree<T extends NestedTreeEntity,I extends Serializable> {

    public T getParent();

    public I getLFT();

    public I getRGT();

    public void setParent(T node);

    public void setLFT(I lft);

    public void setRGT(I rgt);


}
