package com.udf.showcase.dao;

import com.udf.core.orm.nestedSet.dao.ISpringDataJpaNestedTreeDao;
import com.udf.core.orm.nestedSet.dao.NestedSetRepository;
import com.udf.showcase.entity.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by zwr on 2015/2/15.
 */
public interface ICatalogDao extends NestedSetRepository<Catalog,Long> {

}
