package com.udf.core.nestedTree.service;

import com.udf.core.entity.Catalog;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * Created by 张未然 on 2015/8/20.
 */
public interface ICatalogService {

    List<Catalog> getTreeByRootID(Long rootID);

    List<Catalog> getTreeByRootNode(Catalog root);

    public boolean deleteLeafNode(Long id);

    public boolean deleteSubTree(Long id);

    public Catalog findNodeByID(Long id);

    public boolean saveNode(Catalog node);
}