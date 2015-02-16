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
     * 插入新节点，调整树结构
     * @param node
     */
    public void preSave(NestedTreeEntity node){
        NestedTreeEntity parent = node.getParent();
        if(parent==null){
            /**
             * 如果当前节点是根节点:
             * 1.找到可用的lft和rgt值。（lft=表中现有节点中最大的rgt+1，rgt=lft+1）
             * 2.修改bean的lft,rgt
             * 3.bean会执行插入操作（由于这是lisnter，bean以执行插入，这里不要重复执行操作，设置好值即可）
            */
            int position = queryForPosition(node,true);
            node.setLft(position+1);
            node.setRgt(position+2);
        }else{
            /**
             * 如果当前节点不是根节点:
             * 1.查询父节点的最大右值，作为新节点的插入位置。
             * 2.批量修改现存受影响的节点（受到）
             * 2.修改新节点的左右值
             */
            int position = queryForPosition(node,false);
            makeSpaceForNewNode(node,2-1+1,position);
            node.setLft(position);
            node.setRgt(position+1);
        }
    }

    /**
     * 调整树的位置
     * 1.计算当前节点的宽度 span
     * 2.为当前节点的新位置腾出空间（在targetParent下，扩展span个空间）
     * 3.调整当前节点的位置 （先计算偏移量，targetParent原来的rgt-node.lft）
     * 4.调整当前节点之前的位置（在oriParent下，缩减span个空间）
     * @param node
     */

    public void preUpdate(NestedTreeEntity node){
        if(node.getOriParent()!=null&&node.getOriParent()!=node.getParent()){
            logger.debug("Move node from parentID={} to parentID={}",node.getOriParent().getId(),node.getParent().getId());
            //step1 计算span
            int oriRgt = node.getRgt();
            int oriLft = node.getLft();
            int span = oriRgt-oriLft+1;
            //step2 扩展空间
            NestedTreeEntity curParent = node.getParent();
            int curPosition = 0;
            if(curParent==null){
                /**
                 * 如果当前节点是根节点:
                 * 1.找到可用的lft和rgt值。（lft=表中现有节点中最大的rgt+1，rgt=lft+1）
                 * 2.修改bean的lft,rgt
                 * 3.bean会执行插入操作（由于这是lisnter，bean以执行插入，这里不要重复执行操作，设置好值即可）
                 */
                curPosition = queryForPosition(node,true);
            }else{
                /**
                 * 如果当前节点不是根节点:
                 * 1.查询父节点的最大右值，作为新节点的插入位置。
                 * 2.批量修改现存受影响的节点（受到）
                 * 2.修改新节点的左右值
                 */
                curPosition = queryForPosition(node,false);
                makeSpaceForNewNode(node,span,curPosition);
            }
            //step 3.
            // 3.1 调整自己
            node.setLft(curPosition);
            node.setRgt(curPosition+span);
            int offset = curPosition-node.getLft();
            //3.2 调整子树
            updateSubTree(node,node.getLft()+1,node.getRgt()-1,offset);
            //step 4.缩减原先位置
            makeSpaceForNewNode(node,-span,oriRgt);

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
        int span = node.getRgt()-node.getLft()+1;
        if(span>1){
            //有子树，删除子树
            deleteSubTreeCascade(node);
        }
        makeSpaceForNewNode(node,-span,node.getRgt());
    }

    /**
     * 查询获取节点应插入位置
     * 构造查询 相当于jpql：select max(xx) from xx;
     * @param node 当前节点
     * @param isRoot 是否为根节点
     * @return
     */

    @Transactional
    private int queryForPosition(NestedTreeEntity node,boolean isRoot){
        CriteriaQuery<Integer> cq = builder.createQuery(Integer.class);
        Root<? extends NestedTreeEntity> root = cq.from(node.getClass());
        Integer max = null;
        if(isRoot){
            //JPQL:select max(node.rgt) from XXX node
            cq.select(builder.max(root.<Integer>get("rgt")));
            max = em.createQuery(cq).setFlushMode(FlushModeType.COMMIT).getSingleResult();
        }else{
            //JPQL:SELECT node.rgt FROM xxx node WHERE node = node.parent
            cq.select(root.<Integer>get("rgt"));
            cq.where(builder.equal(root, builder.parameter(node.getClass(), "parent")));
            max = em.createQuery(cq).setFlushMode(FlushModeType.COMMIT)
                    .setParameter("parent", node.getParent()).getSingleResult();
        }
        if(max==null) return 0;
        return max;
    }

    /**
     * 批量更新节点的rgt,lft值，给当前节点腾出合适的地方
     * @param node
     * @param from
     */
    private void makeSpaceForNewNode(NestedTreeEntity node,int nodespan,int from){
        logger.debug("make space for node change:from {},span={}", from, nodespan);
        batchUpdateLFT(node,nodespan,from);
        batchUpdateRGT(node,nodespan,from);
    }

    /**
     * JPQL:UPDATE xx node set node.rgt=node.rgt+node where node.rgt>from
     * @param node
     * @param nodespan
     * @param from = ? in jpql
     */
    @Transactional
    private void batchUpdateRGT(NestedTreeEntity node,int nodespan, int from) {
        CriteriaUpdate update = builder.createCriteriaUpdate(node.getClass());
        Root<? extends NestedTreeEntity> updateRoot = update.from(node.getClass());
        update.set(updateRoot.get("rgt"), builder.sum(updateRoot.<Integer>get("rgt"), builder.parameter(Integer.class,"span")))
              .where(builder.greaterThanOrEqualTo(updateRoot.<Integer>get("rgt"), builder.parameter(Integer.class, "position")));
        em.createQuery(update)
          .setParameter("span", nodespan)
          .setParameter("position",from)
          .executeUpdate();
    }


    /**
     * 批量更新节点的rgt，给当前节点腾出合适的地方
     * JPQL:UPDATE xx node set node.lft=node.lft+:nodespan where node.lft>from
     * @param node
     * @Param nodespan
     * @param from
     */
    @Transactional
    private void batchUpdateLFT(NestedTreeEntity node,int nodespan, int from) {
        CriteriaUpdate update = builder.createCriteriaUpdate(node.getClass());
        Root<? extends NestedTreeEntity> updateRoot = update.from(node.getClass());
        update.set(updateRoot.get("lft"), builder.sum(updateRoot.<Integer>get("lft"), builder.parameter(Integer.class,"span")))
              .where(builder.greaterThanOrEqualTo(updateRoot.<Integer>get("lft"), builder.parameter(Integer.class, "position")));
        em.createQuery(update)
          .setParameter("span", nodespan)
          .setParameter("position", from)
          .executeUpdate();
    }

    private void deleteSubTreeCascade(NestedTreeEntity node){
        int from = node.getLft()+1;
        int to = node.getRgt()-1;
        logger.debug("delete sub-tree cascade from {} to {}",from,to);
        emptyTreeRelation(node,from,to);
        deleteSubTree(node,from,to);
    }

    @Transactional
    private void emptyTreeRelation(NestedTreeEntity rootnode,int from,int to){
        CriteriaUpdate update = builder.createCriteriaUpdate(rootnode.getClass());
        Root<? extends  NestedTreeEntity> root = update.from(rootnode.getClass());
        update.set(root.get("parent"),builder.nullLiteral(rootnode.getClass()))
                .where(builder.between(root.<Integer>get("lft"), builder.parameter(Integer.class, "lft"), builder.parameter(Integer.class,"rgt")));
        em.createQuery(update)
          .setParameter("lft",from)
          .setParameter("rgt", to)
          .executeUpdate();

    }

    @Transactional
    private void deleteSubTree(NestedTreeEntity node,int from,int to){
        CriteriaDelete delete = builder.createCriteriaDelete(node.getClass());
        Root<? extends NestedTreeEntity> root = delete.from(node.getClass());
        delete.where(builder.between(root.<Integer>get("lft"),builder.parameter(Integer.class, "lft"), builder.parameter(Integer.class,"rgt")));
        em.createQuery(delete)
          .setParameter("lft",from)
          .setParameter("rgt", to)
          .executeUpdate();

    }

    @Transactional
    public void updateSubTree(NestedTreeEntity rootnode,int from,int to,int offset){
        CriteriaUpdate update = builder.createCriteriaUpdate(rootnode.getClass());
        Root<? extends  NestedTreeEntity> root = update.from(rootnode.getClass());
        update.set(root.get("lft"),builder.sum(root.<Integer>get("lft"), builder.parameter(Integer.class,"offset")))
                .set(root.get("rgt"),builder.sum(root.<Integer>get("rgt"), builder.parameter(Integer.class,"offset")))
                .where(builder.between(root.<Integer>get("lft"), builder.parameter(Integer.class, "lft"), builder.parameter(Integer.class, "rgt")));
        em.createQuery(update)
                .setParameter("offset",offset)
                .setParameter("lft", from)
                .setParameter("rgt", to)
                .executeUpdate();
    }



    @PostConstruct
    public void delegateTo(){
        JPATreeListner.setDelegator(this);
        builder = em.getCriteriaBuilder();
    }

}
