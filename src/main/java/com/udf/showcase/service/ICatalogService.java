package com.udf.showcase.service;

import com.udf.showcase.entity.Catalog;

import java.util.List;

/**
 * Created by 张未然 on 2015/8/20.
 */
public interface ICatalogService {

    List<Catalog> getTreeByRootID(Long rootID);

    List<Catalog> getTreeByRootNode(Catalog root);

    List<Catalog> getTreeByRootID(Long rootID,boolean needSort);

    List<Catalog> getTreeByRootNode(Catalog root,boolean needSort);

    public boolean deleteNode(Catalog node);

    public Catalog findNodeByID(Long id);

    public boolean saveNode(Catalog node);

    public boolean addChildren(Catalog parent,List<Catalog> children);
}
