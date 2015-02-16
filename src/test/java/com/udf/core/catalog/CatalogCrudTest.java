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

    private void insertCATIntoParent(int parentID, String name){
        Catalog parent = em.find(Catalog.class,parentID);
        Catalog cat = new Catalog();
        cat.setName(name);
        cat.setParent(parent);
        em.persist(cat);
    }

    @Transactional
    @Test
    public void batchInsert(){
        char name = 'A';
        for(int i = 0;i<10;i++){
            insertCATIntoParent(i, String.valueOf(name++));
        }
        insertCATIntoParent(3,"HH");
        insertCATIntoParent(3,"QQ");
    }

    @Test
    @Transactional
    public void deleteCATNode(){
        Catalog node = em.find(Catalog.class,4);
        em.remove(node);
    }

    @Test
    @Transactional
    public void updateCATNode(){
        Catalog node = em.find(Catalog.class,3);
        Catalog newParent = em.find(Catalog.class,1);
        node.setParent(newParent);
    }

}
