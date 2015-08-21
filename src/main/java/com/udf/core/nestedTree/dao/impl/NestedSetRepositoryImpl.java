package com.udf.core.nestedTree.dao.impl;

import com.udf.core.entity.NestedSetEntity;
import com.udf.core.nestedTree.dao.NestedSetRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.FlushModeType;
import javax.persistence.criteria.*;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

/**
 * Created by 张未然 on 2015/8/21.
 */

public class NestedSetRepositoryImpl<T extends NestedSetEntity,ID extends Serializable>
    extends SimpleJpaRepository<T,ID> implements NestedSetRepository<T,ID>{


    private final EntityManager em;
    private CriteriaBuilder builder;


    public NestedSetRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        // Keep the EntityManager around to used from the newly introduced methods.
        this.em = entityManager;
        builder = em.getCriteriaBuilder();
    }

    /**
     * 根据节点ID返回树
     * @param id--父节点ID
     * 等效SQL：SELECT child from Catalog child,Catalog parent where child.lft between parent.lft and parent.rgt and parent.id=?1
     * @return List--扁平树集合
     */
    public List<T> getTreeByRootID(ID id){
        Class<T> domainType = getDomainClass();
        System.out.println("class type:+++"+domainType);
        CriteriaQuery query =  builder.createQuery(domainType);
        Root<T> child = query.from(domainType);
        Root<T> parent = query.from(domainType);
/*        Predicate where = builder.conjunction();
        where = builder.and(WHERE表达式);
        where = builder.and(WHERE表达式);
        query.where(where);*/
        query.select(child)
             .where(builder.between(child.<Integer>get("lft"), parent.<Integer>get("lft"), parent.<Integer>get("rgt")),
                     builder.equal(parent.<ID>get("id"),builder.parameter(id.getClass(), "parentID")));
        return em.createQuery(query)
                 .setParameter("parentID", id)
                 .getResultList();

    }

    /**
     * 根据根节点返回树,如果能获取到node节点,建议使用这个方法,效率更高
     * JQPL--FROM Catalog node where node.lft between ?1 and ?2
     * @param node--父节点Entity
     * @return List--扁平树集合
     */
    public List<T> getTreeByRootNode(T node){
        Class<T> domainType = getDomainClass();
        CriteriaQuery query =  builder.createQuery(domainType);
        Root<T> root = query.from(domainType);
        query.where(builder.between(root.<Integer>get("lft"),builder.parameter(Integer.class,"lft"),builder.parameter(Integer.class,"rgt")));
        return em.createQuery(query)
                 .setParameter("lft",node.getLft()).setParameter("rgt",node.getRgt())
                 .getResultList();
    }

    /**
     * 查找父路径
     * JPQL--SELECT parent FROM Catalog child,Catalog parent WHERE child.lft between parent.lft and parent.rgt and child.id=?1
     * @param id--子节点ID
     * @return List--扁平树集合
     */
    public List<T> getParentPath(ID id){
        Class<T> domainType = getDomainClass();
        CriteriaQuery query =  builder.createQuery(domainType);
        Root<T> child = query.from(domainType);
        Root<T> parent = query.from(domainType);
        query.select(parent)
             .where(builder.between(child.<Integer>get("lft"), parent.<Integer>get("lft"), parent.<Integer>get("rgt")),
                     builder.equal(child.<ID>get("id"),builder.parameter(id.getClass(), "childID")));
        return em.createQuery(query).setParameter("childID", id).getResultList();

    }

    /**
     * 查找父路径
     * JPQL--SELECT node FROM Catalog node where ?1 between node.lft and node.rgt
     * @param node--子节点Entity,如果能获取到node节点,建议使用这个方法,效率更高
     * @return List--扁平树集合
     */
    public List<T> getParentPath(T node){
        Class<T> domainType = getDomainClass();
        CriteriaQuery query =  builder.createQuery(domainType);
        Root<T> root = query.from(domainType);
        query.where(builder.between(builder.parameter(Integer.class, "lft"),root.<Integer>get("lft"),root.<Integer>get("rgt")));
        return em.createQuery(query)
                .setParameter("lft", node.getLft())
                .getResultList();
    }

    @Transactional
    public void addChildrenInBatch(T parentNode, List<T> children){
        int parentRgt = parentNode.getRgt();
        //step1 根据span批量调整受影响的树节点
        int span = children.size()*2;
        Class<T> domainType = getDomainClass();
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
        for (T child : children) {
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
