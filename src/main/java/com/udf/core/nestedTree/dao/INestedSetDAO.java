package com.udf.core.nestedTree.dao;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 标记结构，树操作的根接口.方便以后针对不同数据库扩展
 * Created by 张未然 on 2015/8/18.
 */
@Repository
public interface INestedSetDAO<T,ID extends Serializable> {

    /**
     * 根据节点ID返回树
     * @param ID--父节点ID
     * @return List--扁平树集合
     */
    public List<T> getTreeByRootID(Long ID);

    /**
     * 根据根节点返回树
     * @param node--父节点Entity
     * @return List--扁平树集合
     */
    public List<T> getTreeByRoot(T node);

    /**
     * 查找父路径
     * @param ID--子节点ID
     * @return List--扁平树集合
     */
    public List<T> findPath(Long ID);

    /**
     * 查找父路径
     * @param node--子节点Entity
     * @return List--扁平树集合
     */
    public List<T> findPath(T node);

    /**
     * 将一个扁平的树集合转化为嵌套结构
     * Example:[a,b,c]-->[a,[b,[c]]]
     * @Param 扁平化树节点集合
     * @return  嵌套的树结构集合
     */
    public ArrayList<T> buildTree(List<T> flatTreeList);
}
