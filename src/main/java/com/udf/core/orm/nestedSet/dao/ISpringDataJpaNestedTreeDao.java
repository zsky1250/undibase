package com.udf.core.orm.nestedSet.dao;

import java.io.Serializable;

/**
 * Created by zwr on 2015/9/9.
 * 标记接口：
 * 扩展ITreeDao 为了应用SpringDataJPA增加了 获取domainClass的方法 获取操作的Entity类
 * 从而可以以此类为根，使用Criteria查询。
 */
public interface ISpringDataJpaNestedTreeDao<T,ID extends Serializable> extends ITreeDao<T,ID> {
   public Class<T> getDomainClass();
}
