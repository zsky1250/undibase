package com.udf.core.nestedTree.dao;

import com.udf.core.entity.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by zwr on 2015/2/15.
 */
public interface ICatalogDao extends INestedSetDAO<Catalog,Long>,CrudRepository<Catalog,Long> {


}
