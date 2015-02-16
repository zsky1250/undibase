package com.udf.common.orm;

import com.udf.core.entity.Catalog;
import com.udf.core.entity.NestedTreeEntity;
import org.hibernate.cfg.ExtendsQueueEntry;
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
import javax.persistence.metamodel.EntityType;


/**
 * Created by zwr on 2015/2/16.
 */
@Service
@Lazy(false)
public class JPATreeListnerDelegator {

    @PersistenceContext
    EntityManager em;

    private static Logger logger = LoggerFactory.getLogger(JPATreeListnerDelegator.class);

    public void preSave(NestedTreeEntity node){
        NestedTreeEntity parent = node.getParent();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        //构造查询 相当于jpql：select max(xx) from xx;
        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
        Root<? extends NestedTreeEntity> root = cq.from(node.getClass());

        if(parent==null){
            /**
             * 如果当前节点是根节点:
             * 1.找到可用的lft和rgt值。（lft=表中现有节点中最大的rgt+1，rgt=lft+1）
             * 2.修改bean的lft,rgt
             * 3.bean会执行插入操作（由于这是lisnter，bean以执行插入，这里不要重复执行操作，设置好值即可）
            */
            cq.select(cb.max(root.<Integer>get("rgt")));
            Integer max = em.createQuery(cq).getSingleResult();
            if(max==null){
                max=0;
            }
            node.setLft(max+1);
            node.setRgt(max+2);
        }else{
            /**
             * 如果当前节点不是根节点:
             * 1.找到可用的lft和rgt值。（lft=表中现有节点中最大的rgt+1，rgt=lft+1）
             * 2.修改bean的lft,rgt
             * 3.bean会执行插入操作（由于这是lisnter，bean以执行插入，这里不要重复执行操作，设置好值即可）
             */
            cq.select(root.<Integer>get("rgt"));
            cq.where(cb.equal(root,cb.parameter(node.getClass(),"parent")));
            Integer parentMaxRightPosition = em.createQuery(cq).setParameter("parent",node.getParent()).getSingleResult();
            CriteriaUpdate update = cb.createCriteriaUpdate(node.getClass());
            Root<? extends NestedTreeEntity> updateRoot = update.from(node.getClass());
            //更改rht
            update.set(updateRoot.get("rgt"), cb.sum(updateRoot.<Integer>get("rgt"), 2));
            update.where(cb.greaterThanOrEqualTo(updateRoot.<Integer>get("rgt"), cb.parameter(Integer.class, "position")));
            em.createQuery(update).setParameter("position",parentMaxRightPosition).executeUpdate();
            update.set(updateRoot.get("lft"),cb.sum(updateRoot.<Integer>get("lft"),2));
            update.where(cb.greaterThanOrEqualTo(updateRoot.<Integer>get("lft"),cb.parameter(Integer.class,"position")));
            em.createQuery(update).setParameter("position",parentMaxRightPosition).executeUpdate();
            node.setLft(parentMaxRightPosition);
            node.setRgt(parentMaxRightPosition+1);
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
