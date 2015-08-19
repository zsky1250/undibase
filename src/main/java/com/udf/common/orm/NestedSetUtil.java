package com.udf.common.orm;

import com.udf.common.orm.bean.NestedSetBean;
import com.udf.core.entity.Catalog;
import com.udf.core.entity.NestedSetEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by 张未然 on 2015/8/19.
 */
public class NestedSetUtil {

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
            resultList.add(buildTree(iterator));
        }
        return resultList;
    }

    /**
     * 后续遍历 递归攒树结构
     * @param iter
     * @param <T>
     * @return
     */
    private static <T extends NestedSetEntity>T buildTree(ListIterator<T> iter){
        T node = iter.next();
        if(isLeaf(node)){
            node.setChildren(null);
            return node;
        }
        ArrayList<T> children = new ArrayList<>();
        node.setChildren(children);
        while(iter.hasNext()){
            T nextNode = buildTree(iter);
            if(isInByNestedSet(node,nextNode)) {
                children.add(nextNode);
            }else {
                iter.previous();
                break;
            }
        }
        return node;
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

}
