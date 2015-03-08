package com.udf.common.orm;

import com.udf.core.entity.Catalog;
import com.udf.core.entity.NestedTreeEntity;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * Created by zwr on 2015/3/6.
 * JPA Merged 不会保存对象原来的瞬时属性，所以merge的时候无法判断tree node有没有修改parent，从而无法顺利执行JPATreeListner
 * 这个Aspect的作用就是在任何地方，node调用setParent的时候，把原来的parent保存到Listner中的hashmap里
 * 从而能够判断出父节点是否改变了。
 */
@Aspect
public class JPATreeAspect {

    private static Logger logger = LoggerFactory.getLogger(JPATreeAspect.class);

    @Resource
    private static HashMap<String,NestedTreeEntity> listnerMap;

    @Pointcut(value = "execution(public * com.udf.core.entity.NestedTreeEntity+.setParent(com.udf.core.entity.NestedTreeEntity+))&&args(newParent)")
    public void treeParentChange(NestedTreeEntity newParent){}

    @Before(value = "treeParentChange(newParent)")
    public void recordParentChange(JoinPoint pjp,NestedTreeEntity newParent){
        NestedTreeEntity curnode = (NestedTreeEntity)pjp.getThis();
        logger.debug("new parent:{},ori parent:{}",newParent.toString(),curnode.getParentString());
        if(curnode.getParentID()!=newParent.getId()){
            listnerMap.put(curnode.toString(), curnode.getParent());
            logger.debug("cache ori parent:{} into hashmap.",curnode.getParentString());
        }else{
            logger.debug("No noting for caching original parent");
        }
    }

    public static void setListenerHashMap(HashMap<String, NestedTreeEntity> listnerMap){
        JPATreeAspect.listnerMap = listnerMap;
    }
}
