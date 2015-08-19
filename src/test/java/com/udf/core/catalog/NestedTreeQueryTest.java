package com.udf.core.catalog;

import com.udf.core.nestedTree.dao.ICatalogDao;
import com.udf.core.entity.Catalog;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by zwr on 2015/2/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:config/applicationContext.xml")
@TransactionConfiguration(transactionManager = "transactionManager",defaultRollback = false)
public class NestedTreeQueryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    ICatalogDao catDao;

    private static Logger logger = LoggerFactory.getLogger(NestedTreeQueryTest.class);

    @Test
    public void testQueryTree(){
        List<Catalog> tree = catDao.getTreeByRootID(3L);
        for (Catalog catalog : tree) {
            System.out.println(catalog.getName());
        }

    }

}
