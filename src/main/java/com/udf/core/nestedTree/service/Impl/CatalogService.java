package com.udf.core.nestedTree.service.Impl;

import com.udf.common.orm.NestedSetUtil;
import com.udf.core.entity.Catalog;
import com.udf.core.nestedTree.dao.ICatalogDao;
import com.udf.core.nestedTree.service.ICatalogService;
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
        List<Catalog> flatTree = catalogDao.getTreeByRootID(rootID);
        return NestedSetUtil.toHierachyTree(flatTree);
    }

    @Override
    @Cacheable(value = "tree",key = "#rootNode.getId()")
    public List<Catalog> getTreeByRootNode(Catalog rootNode){
        List<Catalog> flatTree = catalogDao.getTreeByRootNode(rootNode);
        return NestedSetUtil.toHierachyTree(flatTree);
    }

    @Caching(
            evict = {
                @CacheEvict(value = "tree",allEntries = true),
                @CacheEvict(value = "treeNode",key = "id")
            }
    )
    @Override
    public boolean deleteLeafNode(Long id){
        if(id!=null){
            try {
                Catalog node = catalogDao.findOne(id);
                if(NestedSetUtil.isLeaf(node)){
                    catalogDao.delete(node);
                    return true;
                }else{
                    throw new Exception("节点非空，不能删除！");
                }
            }catch (Exception e){
                logger.error("EROOR during delete tree Node:\n{}", e.getMessage());
            }
        }
        return false;
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "tree",allEntries = true),
                    @CacheEvict(value = "treeNode",allEntries = true)
            }
    )
    public boolean deleteSubTree(Long id){
        if(id!=null){
            try {
                catalogDao.delete(id);
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
                @CacheEvict(value ="tree",allEntries = true,condition = "T(com.udf.common.orm.NestedSetUtil).isParentChanged(#node)"),
                //save操作修改了节点
                @CacheEvict(value="treeNode",key="#node.getId()",condition = "!(T(com.udf.common.orm.NestedSetUtil).isNewNode(#node))")
            },
            put = {
                //save操作新增了节点
                @CachePut(value="treeNode",key="#node.getId()",condition = "T(com.udf.common.orm.NestedSetUtil).isNewNode(#node)")
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

}
