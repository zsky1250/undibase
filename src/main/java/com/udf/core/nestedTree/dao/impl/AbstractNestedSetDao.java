package com.udf.core.nestedTree.dao.impl;

import com.udf.core.entity.NestedSetEntity;
import com.udf.core.nestedTree.dao.ITreeDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;

/**
 * Created by 张未然 on 2015/8/21.
 */
public abstract class AbstractNestedSetDao<T,ID extends Serializable> implements ITreeDao<T,ID> {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void batchAddChildren(T parentNode, List<T> children) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery cq = builder.createQuery(NestedSetEntity.class);
        T.class
        Root<? extends NestedSetEntity> root = cq.from(node.getClass());
    }

}
