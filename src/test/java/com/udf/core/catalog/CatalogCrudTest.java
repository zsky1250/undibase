package com.udf.core.catalog;

import com.udf.core.catalog.dao.ICatalogDao;
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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Created by zwr on 2015/2/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:config/applicationContext.xml")
@TransactionConfiguration(transactionManager = "transactionManager",defaultRollback = false)
public class CatalogCrudTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    ICatalogDao catDao;

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
    @Transactional
    public void updateCATNode(){
        Catalog node = em.find(Catalog.class,11);
        Catalog newParent = em.find(Catalog.class,2);
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
    public void updateBySDJ(){
        Catalog node = catDao.findOne(22);
        System.out.println(node.getParentID());
//        Catalog newParent = catDao.findOne(2);
//        node.setParent(newParent);
//        catDao.save(node);

    }

    @Test
    @Transactional
    public void testMerge(){

        Catalog node = catDao.findOne(9);
        Catalog medianode = catDao.findOne(8);
        Catalog medianode2 = catDao.findOne(6);
        Catalog newparent = catDao.findOne(1);
        System.out.println("before merge:"+node+" hash code:"+node.hashCode());
//        node.setUrl("abccc");


        node.setParent(medianode);
        node.setParent(medianode2);
        node.setParent(newparent);
//        System.out.println(node.getParentID());
//        System.out.println(node.getParent().toString());
        catDao.save(node);
        System.out.println("after merge:"+node+" hash code:"+node.hashCode());


//        CriteriaBuilder builder = em.getCriteriaBuilder();
//        CriteriaQuery<Catalog> cq = builder.createQuery(Catalog.class);
//        Root<Catalog> root = cq.from(Catalog.class);
//        cq.where(builder.equal(root, builder.parameter(Catalog.class, "parent")));
//        Catalog parent = em.createQuery(cq).setParameter("parent",node.getParent()).getSingleResult();
//        System.out.println("parent:"+

//        int parentid = em.createQuery("select cat.id from Catalog cat where cat = :parent",Integer.class).setParameter("parent",node.getParent()).getSingleResult();
//        System.out.println("parent:"+parentid);
    }

}
