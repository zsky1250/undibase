package com.udf.core.catalog.dao.impl;

import com.udf.core.catalog.dao.ICatalogDao;
import com.udf.core.entity.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by zwr on 2015/2/15.
 */
public interface CatalogDaoImpl extends JpaRepository<Catalog,Integer> {
}
