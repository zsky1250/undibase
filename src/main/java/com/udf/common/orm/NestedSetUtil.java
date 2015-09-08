package com.udf.common.orm;

import com.udf.core.entity.NestedSetEntity;
import org.springframework.orm.jpa.SharedEntityManagerCreator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;

/**
 * Created by 张未然 on 2015/8/19.
 */
public class NestedSetUtil {

    private static EntityManager em;

    /**
     * 将一个扁平的树集合转化为嵌套结构
     * Example:[a,b,c]-->[a,[b,[c]]]
     * @Param 扁平化树节点集合
     * @return  嵌套的树结构集合
     */
    public static <T extends NestedSetEntity>List<T> toHierachyTree(List<T> flatTreeList) {
        if(flatTreeList==null) return null;
        ArrayList<T> resultList = new ArrayList<>();
        ListIterator<T> iterator = flatTreeList.listIterator();
        while(iterator.hasNext()){
            resultList.add(buildTree(iterator,false));
        }
        return resultList;
    }

    public static <T extends NestedSetEntity>List<T> toHierachyTree(List<T> flatTreeList,boolean needOrderd) {
        if(flatTreeList==null) return null;
        ArrayList<T> resultList = new ArrayList<>();
        ListIterator<T> iterator = flatTreeList.listIterator();
        while(iterator.hasNext()){
            resultList.add(buildTree(iterator,needOrderd));
        }
        //递归算法会忽略最后一棵树的根节点排序。手工加上
        if(needOrderd&&!resultList.isEmpty()){
            T lastRoot = resultList.get(resultList.size() - 1);
            if(lastRoot.getChildren().size()>1){
                sort(lastRoot.getChildren());
            }
        }

        return resultList;
    }

    /**
     * 后续遍历 递归攒树结构
     * @param iter
     * @param <T>
     * @return
     */
    private static <T extends NestedSetEntity>T buildTree(ListIterator<T> iter,boolean needOrdered){
        T node = iter.next();
        if(isLeaf(node)){
            node.setChildren(null);
            return node;
        }
        ArrayList<T> children = new ArrayList<>();
        node.setChildren(children);
        while(iter.hasNext()){
            T nextNode = buildTree(iter,needOrdered);
            if(isInByNestedSet(node,nextNode)) {
                children.add(nextNode);
            }else {
                iter.previous();
                //本级节点调整完毕
                if(needOrdered&&children.size()>1) sort(children);
                break;
            }
        }
        return node;
    }

    private static <T extends NestedSetEntity>void sort(List<T> children){
        Collections.sort(children, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o2.getSeq()-o1.getSeq();
            }
        });
    }

    /**
     * 通过NestedSet方式，检查B是不是A的子孙节点
     * @return
     */
    public static <T extends NestedSetEntity> boolean isInByNestedSet(T a,T b){
        return a.getLft()<b.getLft()&&a.getRgt()>b.getRgt();
    }

    /**
     * 通过NestedSet方式，检查节点是否是叶节点
     * @param node
     * @return
     */

    public static <T extends NestedSetEntity> boolean isLeaf(T node){
        return node.getLft()+1==node.getRgt();
    }

    public static <T extends NestedSetEntity> boolean isNewNode(T node){
        return node.getId()==null;
    }


    /**
     * 检查node的parent是不是改变了
     * step 1
     * oriParentID和现有parentID作比较 如果不一样
     * 进入step 2：
     **  有一种可能new一个node 然后把它的parent设置为原来的parent（谁这么无聊...）
     **  对于新增的node:oriParentID肯定为空，他满足step1的条件，然后事实上parent并没有改变
     **  需要读一下数据，获得oriParentID的正确值。
     * @param node
     * @param <T>
     * @return
     */
    public static <T extends NestedSetEntity> boolean isParentChanged(T node){
        Long oriParentID = node.getParentIDBeforeUpdate();
        NestedSetEntity newParent = node.getParent();
        if((oriParentID!=null&&oriParentID!=newParent.getId())||(oriParentID==null&&newParent!=null)){
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery cq = builder.createQuery(NestedSetEntity.class);
            Root<? extends NestedSetEntity> root = cq.from(node.getClass());
            cq.select(root);
            cq.where(builder.equal(root, builder.parameter(NestedSetEntity.class, "node")));
            NestedSetEntity nodeInDB = (NestedSetEntity) em.createQuery(cq).setFlushMode(FlushModeType.COMMIT)
                    .setParameter("node", node).getSingleResult();
            oriParentID = (Long) nodeInDB.getParentIDBeforeUpdate();
            if(oriParentID!=newParent.getId()){
                return true;
            }
        }
        return false;
    }

    public static void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        em = SharedEntityManagerCreator.createSharedEntityManager(entityManagerFactory);
    }

}
