package com.udf.core.nestedTree.dao;

import com.udf.core.entity.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

/**
 * Created by zwr on 2015/2/15.
 */
public interface ICatalogDao extends JpaRepository<Catalog,Integer> {

//    public ArrayList<Catalog> queryBy


}
