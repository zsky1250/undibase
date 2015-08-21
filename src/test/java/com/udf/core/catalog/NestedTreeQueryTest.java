package com.udf.core.catalog;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.udf.common.orm.NestedSetUtil;
import com.udf.core.entity.Catalog;
import com.udf.core.nestedTree.dao.ICatalogDao;
import com.udf.core.nestedTree.service.ICatalogService;
import com.udf.core.web.mvc.json.CatalogJsonView;
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

    @Autowired
    ICatalogService catalogService;

    private static Logger logger = LoggerFactory.getLogger(NestedTreeQueryTest.class);

    @Test
    public void testQueryTree(){
        queryTree(null);
    }

    public List<Catalog> queryTree(Long nodeID){
        nodeID=nodeID==null?1L:nodeID;
        logger.info("Query For node's subTree {} ------->",nodeID);
        List<Catalog> tree = catDao.getTreeByRootID(nodeID);
        for (Catalog catalog : tree) {
            System.out.println(catalog.getName()+"---"+catalog.getId());
        }
//        logger.info("Query For node's parentPath {} ------->",nodeID);
//        tree.clear();
//        tree = catDao.getParentPath(nodeID);
//        for (Catalog catalog : tree) {
//            System.out.println(catalog.getName());
//        }
        return tree;
    }

    @Test
    @Transactional
    public void testTreeConversion() throws JsonProcessingException {
        List<Catalog> resultList = NestedSetUtil.toHierachyTree(queryTree(1L));
        ObjectMapper jackson2Mapper = new ObjectMapper();
        jackson2Mapper.addMixInAnnotations(Catalog.class, CatalogJsonView.class);
        System.out.println(jackson2Mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultList));
    }

    @Test
    public void testTreeCache(){
        Catalog node = catDao.findOne(1L);
        System.out.println("===============");
        catalogService.getTreeByRootID(1L);
        catalogService.getTreeByRootNode(node);
    }



}
