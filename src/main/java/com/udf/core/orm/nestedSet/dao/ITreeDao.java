package com.udf.core.orm.nestedSet.dao;

import com.udf.core.orm.nestedSet.entity.NestedSetEntity;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 标记接口：
 * 树操作的根接口.方便以后针对不同数据库扩展
 * Created by 张未然 on 2015/8/18.
 */
public interface ITreeDao<T,ID extends Serializable> {

    /**
     * 根据节点ID返回树
     * @param id--父节点ID
     * @return List--扁平树集合
     */
    List<T> getTreeByRootID(ID id);

    /**
     * 根据根节点返回树,如果能获取到node节点,建议使用这个方法,效率更高
     * @param node--父节点Entity
     * @return List--扁平树集合
     */
    List<T> getTreeByRootNode(T node);

    /**
     * 查找父路径
     * @param ID--子节点ID
     * @return List--扁平树集合
     */
    List<T> getParentPath(ID ID);

    /**
     * 查找父路径
     * @param node--子节点Entity,如果能获取到node节点,建议使用这个方法,效率更高
     * @return List--扁平树集合
     */
    List<T> getParentPath(T node);

    void addChildrenInBatch(T parentNode, List<T> children);

}
