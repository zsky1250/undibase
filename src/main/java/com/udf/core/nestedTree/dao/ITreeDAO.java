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
public interface ITreeDAO<T,ID extends Serializable> {

    /**
     * 根据节点ID返回树
     * @param ID--父节点ID
     * @return List--扁平树集合
     */
    public List<T> getTreeByRootID(Long ID);

    /**
     * 根据根节点返回树,如果能获取到node节点,建议使用这个方法,效率更高
     * @param node--父节点Entity
     * @return List--扁平树集合
     */
    public List<T> getTreeByRootNode(T node);

    /**
     * 查找父路径
     * @param ID--子节点ID
     * @return List--扁平树集合
     */
    public List<T> getParentPath(Long ID);

    /**
     * 查找父路径
     * @param node--子节点Entity,如果能获取到node节点,建议使用这个方法,效率更高
     * @return List--扁平树集合
     */
    public List<T> getParentPath(T node);

}
