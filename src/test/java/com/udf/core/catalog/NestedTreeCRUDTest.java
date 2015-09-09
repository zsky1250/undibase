package com.udf.core.catalog;

import com.udf.showcase.dao.ICatalogDao;
import com.udf.showcase.entity.Catalog;
import com.udf.core.orm.nestedSet.entity.NestedSetEntity;
import com.udf.showcase.service.ICatalogService;
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
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    ICatalogService catalogService;

    private static Logger logger = LoggerFactory.getLogger(NestedTreeCRUDTest.class);

    @Test
    @Transactional
    public void testInsertCatNode(){
        Catalog cat = new Catalog();
        cat.setName("ABC");
//        cat.setChildren();
        logger.debug("==>in listner:instance-{}", cat);
        em.persist(cat);
    }


    @Transactional
    private void insertCATIntoParent(Long parentID, String name){
        Catalog parent = em.find(Catalog.class,parentID);
        Catalog cat = new Catalog();
        cat.setName(name);
        cat.setParent(parent);
        em.persist(cat);
    }

    @Test
    @Transactional
    public void batchInsert(){
        char name = 'A';
        for(int i = 0;i<10;i++){
            insertCATIntoParent(Long.valueOf(i), String.valueOf(name++));
        }
        insertCATIntoParent(3l, "HH");
        insertCATIntoParent(3l, "QQ");
    }

    @Test
    @Transactional
    public void deleteCATNode(){
        Catalog node = em.find(Catalog.class, 3);
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

    @Test
    public void testUpdate4Insert(){
        Catalog x = new Catalog();
        x.setName("Xxxx");
        catDao.save(x);
    }

    //************service********************



    //screpit:DB/catalog-example1.SQL
    @Test
    public void testDeleteSubTree(){
        System.out.println("查B");
        Catalog B = catalogService.findNodeByID(2L);
        System.out.println("查C");
        Catalog C = catalogService.findNodeByID(3L);
        System.out.println("缓存，所以不会重复查C，下方没有查询语句");
        C = catalogService.findNodeByID(3L);
        System.out.println("删除B 非叶子节点------------清空缓存");
        catalogService.deleteNode(B);
        System.out.println("查C 由于整树删除清空缓存，下方又出现查询语句");
        C = catalogService.findNodeByID(2L);
    }

    //screpit:DB?catalog-example1.SQL
    @Test
    public void testDeleteLeaf(){
        System.out.println("查F");
        Catalog F = catalogService.findNodeByID(6L);
        System.out.println("查C");
        Catalog C = catalogService.findNodeByID(3L);
        System.out.println("缓存，所以不会重复查C，下方没有查询语句");
        C = catalogService.findNodeByID(3L);
        System.out.println("删除F 叶子节点------------清空缓存中的F");
        catalogService.deleteNode(F);
        System.out.println("查C 由于只清F缓存，下方无查询语句");
        C = catalogService.findNodeByID(3L);
        System.out.println("再查F 由于清除F缓存，下方又出现查询语句");
        F = catalogService.findNodeByID(6L);
    }

    @Test
    public void addChild(){
        Catalog xxx = catalogService.findNodeByID(19L);
        xxx.setParent(catalogService.findNodeByID(6L));
        catalogService.saveNode(xxx);
    }

    @Test
    public void BatchAddChildren(){
        List<Catalog> children = new ArrayList<>();

        Catalog x = new Catalog();
        x.setName("X");
        Catalog y = new Catalog();
        y.setName("Y");
        Catalog z = new Catalog();
        z.setName("Z");
        children.add(x);
        children.add(y);
        children.add(z);
        Catalog B = catalogService.findNodeByID(2L);
        catalogService.addChildren(B,children);
//        addChildrenInBatch(B,children);
    }

    @Transactional
    private void batchAddChildren(Catalog parentNode,List<Catalog> children){
        int parentRgt = parentNode.getRgt();
        //step1 根据span批量调整受影响的树节点
        int span = children.size()*2;
        Class<Catalog> domainType = Catalog.class;
        CriteriaBuilder builder = em.getCriteriaBuilder();
        //JPQL:UPDATE xx node SET node.lft=node.lft+:nodespan where node.lft > :from
        CriteriaUpdate update4Lft = builder.createCriteriaUpdate(domainType);
        Root<? extends NestedSetEntity> updateRoot4Lft = update4Lft.from(domainType);
        update4Lft.set(updateRoot4Lft.get("lft"), builder.sum(updateRoot4Lft.<Integer>get("lft"), builder.parameter(Integer.class, "span")))
                .where(builder.greaterThanOrEqualTo(updateRoot4Lft.<Integer>get("lft"), builder.parameter(Integer.class, "position")));
        em.createQuery(update4Lft)
                .setParameter("span", span)
                .setParameter("position", parentRgt)
                .setFlushMode(FlushModeType.COMMIT)
                .executeUpdate();
        //JPQL:UPDATE xx node SET node.rgt=node.rgt + :nodespan WHERE node.rgt >= :from
        CriteriaUpdate update4Rgt = builder.createCriteriaUpdate(domainType);
        Root<? extends NestedSetEntity> updateRoot4Rgt = update4Rgt.from(domainType);
        update4Rgt.set(updateRoot4Rgt.get("rgt"), builder.sum(updateRoot4Rgt.<Integer>get("rgt"), builder.parameter(Integer.class, "span")))
                .where(builder.greaterThanOrEqualTo(updateRoot4Rgt.<Integer>get("rgt"), builder.parameter(Integer.class, "position")));
        em.createQuery(update4Rgt)
                .setParameter("span", span)
                .setParameter("position",parentRgt)
                .setFlushMode(FlushModeType.COMMIT)
                .executeUpdate();
        //step2 给children标志位赋值执行插入
        for (Catalog child : children) {
            child.setLft(parentRgt++);
            child.setRgt(parentRgt++);
            child.setParent(parentNode);
            child.setBatchInsert(true);
            FlushModeType oriFlushMode = em.getFlushMode();
            em.setFlushMode(FlushModeType.COMMIT);
            em.persist(child);
            em.setFlushMode(oriFlushMode);
        }


    }

}
