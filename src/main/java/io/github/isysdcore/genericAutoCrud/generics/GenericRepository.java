package io.github.isysdcore.genericAutoCrud.generics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 *
 * @param <T> The Entity class that represent the database entity
 * @param <K> The Class type that represent the id field datatype of entity of type T
 */
@NoRepositoryBean
public interface GenericRepository<T,K> extends JpaRepository<T, K>, JpaSpecificationExecutor<T> {
}
