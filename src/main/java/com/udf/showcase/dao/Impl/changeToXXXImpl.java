package com.udf.showcase.dao.Impl;

import com.udf.core.orm.nestedSet.dao.AbstractNestedSetRepository;
import com.udf.core.orm.nestedSet.dao.ISpringDataJpaNestedTreeDao;
import com.udf.showcase.entity.Catalog;

/**
 * Created by zwr on 2015/9/9.
 */
public class changeToXXXImpl extends AbstractNestedSetRepository<Catalog,Long> implements ISpringDataJpaNestedTreeDao<Catalog,Long> {

    @Override
    public Class<Catalog> getDomainClass(){
        return Catalog.class;
    }

}
