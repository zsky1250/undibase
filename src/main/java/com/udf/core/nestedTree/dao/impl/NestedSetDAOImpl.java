package com.udf.core.nestedTree.dao.impl;

import com.sun.xml.internal.bind.v2.model.core.ID;
import com.udf.core.entity.Catalog;
import com.udf.core.nestedTree.dao.INestedSetDAO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 张未然 on 2015/8/19.
 */
@Service("iCatalogDaoImpl")
public class NestedSetDAOImpl implements INestedSetDAO<Catalog,ID> {

    @PersistenceContext
    EntityManager em;

    @Override
    public List<Catalog> getTreeByRootID(Long ID) {
       return em.createQuery("SELECT child from Catalog child,Catalog parent where child.lft between parent.lft and parent.rgt and parent.id=?1")
               .setParameter(1,ID)
               .getResultList();
    }

    @Override
    public List<Catalog> getTreeByRoot(Catalog node) {
        return em.createQuery("from Catalog child where child.lft between parent.lft and parent.rgt and parent=?1")
                .setParameter(1,node)
                .getResultList();
    }

    @Override
    public List<Catalog> findPath(Long ID) {
        return null;
    }

    @Override
    public List<Catalog> findPath(Catalog node) {
        return null;
    }

    @Override
    public ArrayList<Catalog> buildTree(List<Catalog> flatTreeList) {
        return null;
    }

    @PostConstruct
    public void init(){
        System.out.println("Hello!");
    }
}
