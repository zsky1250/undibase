package com.udf.common.orm;

import java.io.Serializable;

/**
 * Created by zwr on 2015/2/15.
 */
public interface NestedTree<T,ID extends Serializable> {

    public T getParent();

    public ID getLFT();

    public ID getRGT();


}
