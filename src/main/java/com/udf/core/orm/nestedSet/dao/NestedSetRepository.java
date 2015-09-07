package com.udf.core.orm.nestedSet.dao;

import com.udf.core.orm.nestedSet.entity.NestedSetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * Created by 张未然 on 2015/8/21.
 * Spring Data JPA - 方式使用的树操作接口。
 *
 */
@NoRepositoryBean
public interface NestedSetRepository<T extends NestedSetEntity,ID extends Serializable> extends JpaRepository<T,ID>,ITreeDao<T,ID> {

}
