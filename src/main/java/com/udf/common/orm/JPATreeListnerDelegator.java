package com.udf.common.orm;

import com.udf.core.entity.NestedTreeEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;


/**
 * Created by zwr on 2015/2/16.
 */
@Service
@Lazy(false)
public class JPATreeListnerDelegator {

    @PersistenceContext
    EntityManager em;

    private CriteriaBuilder builder;

    private static boolean inProcess=false;

    private static Logger logger = LoggerFactory.getLogger(JPATreeListnerDelegator.class);

    /**
     * 插入新节点，调整树结构(lft & rgt)
     * 找到当前合适的位置（如果需要，调整其他节点给当前节点腾出位置）
     * 设置当前节点的lft rgt
     * @param node
     */
    public void preSave(NestedTreeEntity node){
        logger.debug("-->Start Listner for insert node:");
        NestedTreeEntity parent = node.getParent();
        int span = 2-1+1;
        int position;
        if(parent==null){
            /**
             * 如果当前节点是根节点:
             * 1.找到可用的lft和rgt值。（lft=表中现有节点中最大的rgt+1，rgt=lft+1）
             * 2.修改bean的lft,rgt
             * 3.bean会执行插入操作（由于这是lisnter，bean以执行插入，这里不要重复执行操作，设置好值即可）
            */
            position = queryForRootNodePosition(node.getClass());
            position++;
        }else{
            /**
             * 如果当前节点不是根节点:
             * 1.查询父节点的最大右值，作为新节点的插入位置。
             * 2.批量修改现存受影响的节点（受到）
             * 2.修改新节点的左右值
             */
            position = queryForNonRootNodePosition(node.getClass(),parent);
            makeSpaceForNode(node.getClass(), span, position);
        }
        node.setLft(position);
        node.setRgt(position+1);
        logger.debug("-->finish insert node:");
    }

    /**
     * 调整树的位置
     * 1.计算当前节点的宽度 span
     * 2.为当前节点的新位置腾出空间（在targetParent下，扩展span个空间）
     * 3.调整当前节点的位置 （先计算偏移量，targetParent原来的rgt-node.lft）
     * 4.调整当前节点之前的位置（在oriParent下，缩减span个空间）
     * **************
     * 话虽如此，考虑到move操作都是从 原状态->中间态->(中间态2)->最终态，而JPA只能通过listener动态修改位置
     * 所以最后还要执行保存现有节点的操作
     * 我们只能模拟出现在节点的变化
     * 用simulateXXX函数代表现有节点的变化
     *
     * @param node
     */

    public void preUpdate(NestedTreeEntity node){
        if(node.getOriParent()!=null&&node.getOriParent()!=node.getParent()){

            logger.debug("-->Start Listner for Move node={} from parentID={} to parentID={}",node.getId(),node.getOriParent().getId(),node.getParent().getId());
            Class beanClass = node.getClass();
            //step1 计算span
            int span = node.getRgt()-node.getLft()+1;

            //step2 扩展空间
            logger.debug("-->1. query for parent RGT,and make space for new position");
            NestedTreeEntity curParent = node.getParent();
            int curParentRgt;
            if(curParent==null){
                curParentRgt = queryForRootNodePosition(beanClass);
            }else{
                curParentRgt = queryForNonRootNodePosition(beanClass, curParent);
                makeSpaceForNode(beanClass, span, curParentRgt);
                simulateCurNodeChange(node, span, curParentRgt);
            }

            //step 3.
            logger.debug("-->2. adjust self including subtree");
            int offset = curParentRgt-node.getLft();
            int MiddleStatusOfRgt = node.getRgt();
            moveSubTree(node, node.getLft() + 1, node.getRgt() - 1, offset);
            simulateCurNodeChange(node,offset,-1);

            //step 4.缩减原先位置
            logger.debug("-->3. remove the space of original position");
            makeSpaceForNode(beanClass, -span, MiddleStatusOfRgt);
            simulateCurNodeChange(node,-span,MiddleStatusOfRgt);

            //清空node的oriParent，以免给对象再次被持久化的时候。误认为是更改了parent
            logger.debug("-->finish Move node={} from parentID={} to parentID={}",node.getId(),node.getOriParent().getId(),node.getParent().getId());
            node.emptyOriParent();
        }

    }

    /**
     * 删除节点，调整树结构
     * 1.计算当前节宽度span
     * 2.删除子树（如果有的话 span>1）
     * 3.调整受影响的其他节点。
     * @param node
     */

    public void preRemove(NestedTreeEntity node){
        logger.debug("-->Start Listner for delete node={} from ID={}",node.getId());
        int span = node.getRgt()-node.getLft()+1;

        if(span>1){
            //有子树，删除子树
            logger.debug("--> cascade delete subtree:");
            deleteSubTreeCascade(node);
        }
        logger.debug("--> remove the space of original position:");
        makeSpaceForNode(node.getClass(), -span, node.getRgt());
        logger.debug("-->finish Listner for delete node={} from ID={}",node.getId());
    }

    /**
     * 插入的是一个根节点，位置为当前节点中的最大RGT值
     * JPQL:SELECT MAX(node.rgt) FROM xxx node
     * @param nodeclass
     * @return
     */
    @Transactional
    private int queryForRootNodePosition(Class nodeclass){
        CriteriaQuery<Integer> query = builder.createQuery(Integer.class);
        Root<? extends NestedTreeEntity> root = query.from(nodeclass);
        query.select(builder.max(root.<Integer>get("rgt")));
        Integer position = em.createQuery(query).setFlushMode(FlushModeType.COMMIT).getSingleResult();
        if(position==null) position = 0;
        return position;
    }

    /**
     * 插入的是一个非根节点，位置为目标父节点的RGT
     * JPQL:SELECT MAX(node.rgt) FROM xxx node
     * @param nodeclass
     * @return
     */
    @Transactional
    private int queryForNonRootNodePosition(Class nodeclass,NestedTreeEntity parent){
        CriteriaQuery<Integer> cq = builder.createQuery(Integer.class);
        Root<? extends NestedTreeEntity> root = cq.from(nodeclass);
        cq.select(root.<Integer>get("rgt"));
        cq.where(builder.equal(root, builder.parameter(nodeclass, "parent")));
        Integer position = em.createQuery(cq).setFlushMode(FlushModeType.COMMIT)
                        .setParameter("parent", parent).getSingleResult();
        return position.intValue();
    }

    /**
     * 批量更新节点的rgt,lft值，给当前节点腾出合适的地方
     * @param nodeclass
     * @param from
     */
    private void makeSpaceForNode(Class nodeclass, int nodespan, int from){
        logger.debug("make space for node change:from {},span={}", from, nodespan);
        batchUpdateLFT(nodeclass,nodespan,from);
        batchUpdateRGT(nodeclass,nodespan,from);
    }

    /**
     * JPQL:UPDATE xx node SET node.rgt=node.rgt + :nodespan WHERE node.rgt >= :from
     * @param nodecalss
     * @param nodespan
     * @param from
     */
    @Transactional
    private void batchUpdateRGT(Class nodecalss,int nodespan, int from) {
        CriteriaUpdate update = builder.createCriteriaUpdate(nodecalss);
        Root<? extends NestedTreeEntity> updateRoot = update.from(nodecalss);
        update.set(updateRoot.get("rgt"), builder.sum(updateRoot.<Integer>get("rgt"), builder.parameter(Integer.class,"span")))
              .where(builder.greaterThanOrEqualTo(updateRoot.<Integer>get("rgt"), builder.parameter(Integer.class, "position")));
        em.createQuery(update)
          .setParameter("span", nodespan)
          .setParameter("position",from)
                .setFlushMode(FlushModeType.COMMIT)
                .executeUpdate();
    }


    /**
     * 批量更新节点的rgt，给当前节点腾出合适的地方
     * JPQL:UPDATE xx node SET node.lft=node.lft+:nodespan where node.lft > :from
     * @param nodeclass
     * @Param nodespan
     * @param from
     */
    @Transactional
    private void batchUpdateLFT(Class nodeclass,int nodespan, int from) {
        CriteriaUpdate update = builder.createCriteriaUpdate(nodeclass);
        Root<? extends NestedTreeEntity> updateRoot = update.from(nodeclass);
        update.set(updateRoot.get("lft"), builder.sum(updateRoot.<Integer>get("lft"), builder.parameter(Integer.class,"span")))
              .where(builder.greaterThanOrEqualTo(updateRoot.<Integer>get("lft"), builder.parameter(Integer.class, "position")));
        em.createQuery(update)
          .setParameter("span", nodespan)
          .setParameter("position", from)
          .setFlushMode(FlushModeType.COMMIT)
          .executeUpdate();
    }

    /**
     * 级联删除子树
     * step1：把关系设置为空
     * step2：删除
     * @param node
     */

    private void deleteSubTreeCascade(NestedTreeEntity node){
        int from = node.getLft()+1;
        int to = node.getRgt()-1;
        logger.debug("delete sub-tree cascade from {} to {}",from,to);
        emptyTreeRelation(node.getClass(),from,to);
        deleteSubTree(node.getClass(),from,to);
    }

    /**
     * 清空关系
     * JPQL:UPDATE XXX node SET node.parent = NULL WHERE node.lft BETWEEN :from AND :to
     * @param nodeclass
     * @param from
     * @param to
     */
    @Transactional
    private void emptyTreeRelation(Class nodeclass,int from,int to){
        CriteriaUpdate update = builder.createCriteriaUpdate(nodeclass);
        Root<? extends  NestedTreeEntity> root = update.from(nodeclass);
        update.set(root.get("parent"),builder.nullLiteral(nodeclass))
                .where(builder.between(root.<Integer>get("lft"), builder.parameter(Integer.class, "lft"), builder.parameter(Integer.class,"rgt")));
        em.createQuery(update)
          .setParameter("lft", from)
          .setParameter("rgt", to)
                .setFlushMode(FlushModeType.COMMIT)
                .executeUpdate();

    }

    /**
     * 删除子树
     * JPQL:DELETE FROM xx node where node.lft BETWEEN :from AND :to
     * @param nodeclass
     * @param from
     * @param to
     */

    @Transactional
    private void deleteSubTree(Class nodeclass,int from,int to){
        CriteriaDelete delete = builder.createCriteriaDelete(nodeclass);
        Root<? extends NestedTreeEntity> root = delete.from(nodeclass);
        delete.where(builder.between(root.<Integer>get("lft"), builder.parameter(Integer.class, "lft"), builder.parameter(Integer.class,"rgt")));
        em.createQuery(delete)
                .setParameter("lft", from)
          .setParameter("rgt", to).setFlushMode(FlushModeType.COMMIT).executeUpdate();

    }

    /**
     * 移动子树
     * JPQL:UPDATE xxx node SET node.lft=node.lft+:offset,node.rgt = node.rgt+:offset WHERE node.lft BETWEEN :from AND :to
     * @param rootnode
     * @param from
     * @param to
     * @param offset
     */
    @Transactional
    public void moveSubTree(NestedTreeEntity rootnode, int from, int to, int offset){
        logger.debug("move subTree:from={},to={},offset={}",from,to,offset);
        CriteriaUpdate update = builder.createCriteriaUpdate(rootnode.getClass());
        Root<? extends  NestedTreeEntity> root = update.from(rootnode.getClass());
        update.set(root.get("lft"), builder.sum(root.<Integer>get("lft"), builder.parameter(Integer.class,"offset")))
                .set(root.get("rgt"), builder.sum(root.<Integer>get("rgt"), builder.parameter(Integer.class,"offset")))
                .where(builder.between(root.<Integer>get("lft"), builder.parameter(Integer.class, "lft"), builder.parameter(Integer.class, "rgt")));
        em.createQuery(update)
                .setParameter("offset", offset)
                .setParameter("lft", from)
                .setParameter("rgt", to)
                .setFlushMode(FlushModeType.COMMIT)
                .executeUpdate();
    }

    /**
     * 因为使用JPA Listner，最终还要执行update node的操作。
     * 所以这里只模拟出node合适的左右值，以便最后执行。
     * @param node
     * @param offset
     * @param from
     */
    private void simulateCurNodeChange(NestedTreeEntity node,int offset,int from){
        if(from<0){
            //没有范围代表目前节点肯定受影响，直接应用影响
            node.setLft(node.getLft() + offset);
            node.setRgt(node.getRgt()+offset);
        }else{
            if(node.getRgt()>=from) node.setRgt(node.getRgt()+offset);
            if(node.getLft()>=from) node.setLft(node.getLft()+offset);
        }
    }



    @PostConstruct
    public void delegateTo(){
        JPATreeListner.setDelegator(this);
        builder = em.getCriteriaBuilder();
    }

}
