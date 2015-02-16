package com.udf.core.catalog;

import com.udf.core.entity.Catalog;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;

/**
 * Created by zwr on 2015/2/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:config/applicationContext.xml")
@TransactionConfiguration(transactionManager = "transactionManager",defaultRollback = false)
public class CatalogCrudTest {

    @PersistenceContext
    EntityManager em;

    private static Logger logger = LoggerFactory.getLogger(CatalogCrudTest.class);

    @Test
    @Transactional
    public void testInsertCatNode(){
        Catalog cat = new Catalog();
        cat.setName("ABC");
//        cat.setChildren();
        logger.debug("==>in listner:instance-{}",cat);
        em.persist(cat);
    }

}
