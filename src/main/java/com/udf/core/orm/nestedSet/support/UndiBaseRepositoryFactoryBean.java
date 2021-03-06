package com.udf.core.orm.nestedSet.support;

import com.udf.core.orm.nestedSet.entity.NestedSetEntity;
import com.udf.core.orm.nestedSet.dao.NestedSetRepository;
import com.udf.core.orm.nestedSet.dao.NestedSetRepositoryImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * 实现RepositoryFacotryBean 供XML配置使用
 * 参考 《Spring-data-jpa-reference-1.8.2》中的 3.6.2节 Adding custom behavior to all repositories
 * Created by 张未然 on 2015/8/21.
 *
 * 需要在XML配置文件的JPA bean中 加上factory-class="com.udf.core.orm.nestedSet.support.UndiBaseRepositoryFactoryBean"
 * Deperated from Spring Data JPA 1.9 because its new base-class xml config attribute
 */
public class UndiBaseRepositoryFactoryBean<R extends JpaRepository<T, I>, T, I extends Serializable>
        extends JpaRepositoryFactoryBean<R, T, I> {

    protected RepositoryFactorySupport createRepositoryFactory(EntityManager em) {
        return new UndiBaseRepositoryFactory(em);
    }

    private static class UndiBaseRepositoryFactory<T, I extends Serializable> extends JpaRepositoryFactory {

        private final EntityManager em;

        public UndiBaseRepositoryFactory(EntityManager em) {
            super(em);
            this.em = em;
        }

        protected Object getTargetRepository(RepositoryInformation metadata) {
            if(isNestedSetRepository(metadata)){
                return new NestedSetRepositoryImpl<NestedSetEntity,I>((Class<NestedSetEntity>) metadata.getDomainType(),em);
            }
            return super.getTargetRepository(metadata);
        }

        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            if (isNestedSetRepository(metadata)) {
                return NestedSetRepositoryImpl.class;
            }

            return super.getRepositoryBaseClass(metadata);
        }

        private boolean isNestedSetRepository(RepositoryMetadata metadata){
            Class<?> repositoryInterface = metadata.getRepositoryInterface();
            return NestedSetRepository.class.isAssignableFrom(repositoryInterface)&&
                    NestedSetEntity.class.isAssignableFrom((metadata.getDomainType()));
        }
    }
}
