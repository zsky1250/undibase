package com.udf.core.nestedTree.dao.impl;

import com.sun.xml.internal.bind.v2.model.core.ID;
import com.udf.core.entity.Catalog;
import com.udf.core.nestedTree.dao.ITreeDAO;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.management.Query;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by 张未然 on 2015/8/19.
 */
@Service("iCatalogDaoImpl")
public class NestedSetTreeDAOImpl implements ITreeDAO<Catalog,ID> {

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
        return em.createQuery("FROM Catalog node where node.lft between ?1 and ?2")
                .setParameter(1,node.getLft())
                .setParameter(2,node.getRgt())
                .getResultList();
    }

    @Override
    public List<Catalog> getParentPath(Long ID) {
        return em.createQuery("SELECT parent FROM Catalog child,Catalog parent WHERE child.lft between parent.lft and parent.rgt and child.id=?1")
                .setParameter(1,ID)
                .getResultList();

    }

    @Override
    public List<Catalog> getParentPath(Catalog node) {
        return em.createQuery("SELECT node FROM Catalog node where ?1 between node.lft and node.rgt")
                .setParameter(1,node.getLft())
                .getResultList();
    }


}
