package com.udf.showcase.dao;

import com.udf.showcase.entity.Catalog;
import com.udf.core.orm.nestedSet.dao.ITreeDao;
import com.udf.core.orm.nestedSet.dao.NestedSetRepository;

/**
 * Created by zwr on 2015/2/15.
 */
public interface ICatalogDao extends NestedSetRepository<Catalog,Long>{

}
