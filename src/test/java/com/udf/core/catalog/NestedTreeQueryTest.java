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
import java.util.ArrayList;
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
        List<Catalog> tree = queryByRootID(3);
        for (Catalog catalog : tree) {
            System.out.println(catalog.getName());
        }

    }


    private List queryByRootID(Integer rootID){
        Catalog root = catDao.findOne(rootID);
        List tree = queryByRootNode(root);
        return tree;
    }

    private List<Catalog> queryByRootNode(Catalog root){
        List<Catalog> resultList = em.createQuery("from Catalog node where node.lft >=?1 and node.rgt<=?2")
                .setParameter(1, root.getLft())
                .setParameter(2,root.getRgt())
                .getResultList();
        return resultList;
    }
}
