package com.udf.core.nestedTree.dao;

import com.udf.core.entity.NestedSetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 张未然 on 2015/8/21.
 */
@NoRepositoryBean
public interface NestedSetRepository<T extends NestedSetEntity,ID extends Serializable> extends JpaRepository<T,ID> {
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
