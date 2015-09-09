package com.udf.showcase.service.Impl;

import com.udf.core.orm.nestedSet.support.NestedSetUtil;
import com.udf.showcase.dao.ICatalogDao;
import com.udf.showcase.entity.Catalog;
import com.udf.showcase.service.ICatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 张未然 on 2015/8/20.
 */
@Service
public class CatalogService implements ICatalogService {

    @Autowired
    ICatalogDao catalogDao;

    private static Logger logger = LoggerFactory.getLogger(CatalogService.class);


    @Override
    @Cacheable(value = "tree",key = "#rootID")
    public List<Catalog> getTreeByRootID(Long rootID){
        return getTreeByRootID(rootID,false);
    }

    @Override
    @Cacheable(value = "tree",key = "#rootNode?.getId()")
    public List<Catalog> getTreeByRootNode(Catalog rootNode){
        return getTreeByRootNode(rootNode,false);
    }

    @Caching(
            cacheable = {
                    @Cacheable(value="tree",key = "#rootID+'-ordered'",condition = "#needSort"),
                    @Cacheable(value="tree",key = "#rootID",condition = "!#needSort")
            }
    )
    @Override
    public List<Catalog> getTreeByRootID(Long rootID, boolean needSort) {
        List<Catalog> flatTree = catalogDao.getTreeByRootID(rootID);
        return NestedSetUtil.toHierachyTree(flatTree,needSort);
    }

    @Caching(
            cacheable = {
                    @Cacheable(value="tree",key = "#rootNode?.getId()+'-ordered'",condition = "#needSort"),
                    @Cacheable(value="tree",key = "#rootNode?.getId()",condition = "!#needSort")
            }
    )
    @Override
    public List<Catalog> getTreeByRootNode(Catalog rootNode, boolean needSort) {
        List<Catalog> flatTree = catalogDao.getTreeByRootNode(rootNode);
        return NestedSetUtil.toHierachyTree(flatTree);
    }


    @Caching(
            evict = {
                    @CacheEvict(value = "tree",allEntries = true),
                    //删除子树，清空treeNode
                    @CacheEvict(value = "treeNode",allEntries = true,condition = "!(T(com.udf.core.orm.nestedSet.support.NestedSetUtil).isLeaf(#node))"),
                    //删除叶子节点，清除单个节点缓存
                    @CacheEvict(value = "treeNode",key = "#node?.getId()",condition = "T(com.udf.core.orm.nestedSet.support.NestedSetUtil).isLeaf(#node)")
            }
    )
    @Override
    public boolean deleteNode(Catalog node){
        if(node.getId()!=null){
            try {
                catalogDao.delete(node);
                return true;
            }catch (Exception e){
                logger.error("EROOR during delete tree Node:\n{}", e.getMessage());
            }
        }
        return false;
    }

    @Override
    @Cacheable(value="treeNode",key="#id")
    public Catalog findNodeByID(Long id){
        return catalogDao.findOne(id);
    }

    @Override
    @Caching(
            evict = {
                //save操作修改了树结构
                @CacheEvict(value ="tree",allEntries = true,condition = "T(com.udf.core.orm.nestedSet.support.NestedSetUtil).isParentChanged(#node)"),
                //save操作修改了节点
                @CacheEvict(value="treeNode",key="#node?.getId()",condition = "!(T(com.udf.core.orm.nestedSet.support.NestedSetUtil).isNewNode(#node))")
            },
            put = {
                //save操作新增了节点
                @CachePut(value="treeNode",key="#node?.getId()",condition = "T(com.udf.core.orm.nestedSet.support.NestedSetUtil).isNewNode(#node)")
            }
    )
    public boolean saveNode(Catalog node) {
        if(node!=null){
            try {
                catalogDao.save(node);
                return true;
            }catch (Exception e){
                logger.error("EROOR during save tree Node:\n{}", e.getMessage());
            }
        }
        return false;
    }


    @CacheEvict(value = "tree",allEntries = true)
    @Override
    public boolean addChildren(Catalog parent, List<Catalog> children) {
        if(parent==null||children==null||children.isEmpty()) return false;
        catalogDao.addChildrenInBatch(parent, children);
        return true;
    }

}
