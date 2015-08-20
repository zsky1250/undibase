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
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by zwr on 2015/2/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:config/applicationContext.xml")
@TransactionConfiguration(transactionManager = "transactionManager",defaultRollback = false)
public class NestedTreeCRUDTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    ICatalogDao catDao;

    private static Logger logger = LoggerFactory.getLogger(NestedTreeCRUDTest.class);

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
        insertCATIntoParent(3, "HH");
        insertCATIntoParent(3, "QQ");
    }

    @Test
    @Transactional
    public void deleteCATNode(){
        Catalog node = em.find(Catalog.class,3);
        em.remove(node);
    }


    @Test
    public void updateBySDJSave(){
        Catalog node = catDao.findOne(12L);
        System.out.println(node.getParentIDBeforeUpdate());
        Catalog newParent = catDao.findOne(2l);
        node.setParent(newParent);
        catDao.save(node);

    }

    @Test
    @Transactional
    public void updateByJPAUpdate(){
        Catalog node = em.find(Catalog.class, 11);
        Catalog newParent = em.find(Catalog.class, 2);
        node.setParent(newParent);
    }

    @Test
    @Transactional
    public void updateByJPAMerge(){
        Catalog node = em.find(Catalog.class, 11);
        Catalog newParent = em.find(Catalog.class, 3);
        em.detach(node);
        node.setParent(newParent);
        em.merge(node);
    }

    @Test
    @Transactional
    public void insertBySDJ(){
        Catalog cat = new Catalog();
        cat.setName("abc");
        catDao.save(cat);
    }



    @Test
    public void testLazyLoad(){
        Catalog node = catDao.findOne(10l);
//        System.out.println(node.getParent());
        Catalog newParent = catDao.findOne(1L);
        node.setParent(newParent);
        catDao.save(node);

    }

    @Test
    public void testQueryandMerge(){
        Catalog node = catDao.findOne(2l);
//        System.out.println(node.getParent()+"---"+node.getParentIDBeforeUpdate());
        Catalog newNode = new Catalog();
        newNode.setUrl("sdf");
        newNode.setId(3l);
        newNode.setParent(node);
        catDao.save(newNode);
    }

}
