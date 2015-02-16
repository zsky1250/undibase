package com.udf.common.orm;

import com.udf.core.entity.Catalog;
import com.udf.core.entity.NestedTreeEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;


/**
 * Created by zwr on 2015/2/16.
 */
@Service
@Lazy(false)
public class JPATreeListnerDelegator {

    @PersistenceContext
    EntityManager em;

    private static Logger logger = LoggerFactory.getLogger(JPATreeListnerDelegator.class);

    public void preSave(NestedTreeEntity<? extends NestedTreeEntity> node){
       NestedTreeEntity parent = node.getParent();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        //构造查询 相当于jpql：select max(xx) from xx;
        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
        Root<? extends NestedTreeEntity> root = cq.from(node.getClass());
        cq.select(cb.max(root.<Integer>get("rgt")));
        if(parent==null){
            /**
             * 如果当前节点是根节点:
             * 1.找到可用的lft和rgt值。（lft=表中现有节点中最大的rgt+1，rgt=lft+1）
             * 2.修改bean的lft,rgt
             * 3.bean会执行插入操作（由于这是lisnter，bean以执行插入，这里不要重复执行操作，设置好值即可）
            */
            int max = em.createQuery(cq).getSingleResult();
            node.setLft(max+1);
            node.setRgt(max+2);
        }else{
            /**
             * 如果当前节点不是根节点:
             * 1.找到可用的lft和rgt值。（lft=表中现有节点中最大的rgt+1，rgt=lft+1）
             * 2.修改bean的lft,rgt
             * 3.bean会执行插入操作（由于这是lisnter，bean以执行插入，这里不要重复执行操作，设置好值即可）
             */
            cq.where(cb.equal(root.<Integer>get("rgt"),cb.parameter(Integer.class,"rgt")));
            int parentMaxRightPosition = em.createQuery(cq).setParameter("rgt",node.getParent().getId()).getSingleResult();
            CriteriaUpdate<? extends NestedTreeEntity> update = cb.createCriteriaUpdate(node.getClass());
            Root<? extends NestedTreeEntity> update_root;
            node.getClass
            update_root = update.from(node.getClass());

        }
    }

    public void preUpdate(NestedTreeEntity node){

    }

    public void preRemove(NestedTreeEntity node){

    }

    @PostConstruct
    public void delegateTo(){
        JPATreeListner.setDelegator(this);
    }

}
