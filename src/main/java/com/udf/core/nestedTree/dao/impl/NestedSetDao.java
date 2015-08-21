package com.udf.core.nestedTree.dao.impl;

import com.udf.core.entity.Catalog;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by 张未然 on 2015/8/19.
 */
@Service("iCatalogDaoImpl")
@Deprecated
public class NestedSetDao {

    @PersistenceContext
    EntityManager em;


    public List<Catalog> getTreeByRootID(Long ID) {
       return em.createQuery("SELECT child from Catalog child,Catalog parent where child.lft between parent.lft and parent.rgt and parent.id=?1")
               .setParameter(1,ID)
               .getResultList();
    }

    public List<Catalog> getTreeByRootNode(Catalog node) {
        return em.createQuery("FROM Catalog node where node.lft between ?1 and ?2")
                .setParameter(1,node.getLft())
                .setParameter(2,node.getRgt())
                .getResultList();
    }

    public List<Catalog> getParentPath(Long ID) {
        return em.createQuery("SELECT parent FROM Catalog child,Catalog parent WHERE child.lft between parent.lft and parent.rgt and child.id=?1")
                .setParameter(1,ID)
                .getResultList();

    }

    public List<Catalog> getParentPath(Catalog node) {
        return em.createQuery("SELECT node FROM Catalog node where ?1 between node.lft and node.rgt")
                .setParameter(1,node.getLft())
                .getResultList();
    }

    public void batchAddChildren(Catalog parentNode, List<Catalog> children) {

    }


}
