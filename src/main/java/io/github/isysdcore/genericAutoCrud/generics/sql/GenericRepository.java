package io.github.isysdcore.genericAutoCrud.generics.sql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * Generic JPA repository interface providing standard CRUD operations and
 * support for JPA Specifications.
 *
 * <p>This repository extends both {@link org.springframework.data.jpa.repository.JpaRepository}
 * and {@link org.springframework.data.jpa.repository.JpaSpecificationExecutor},
 * enabling basic persistence operations as well as dynamic query निर्माण using
 * JPA Criteria-based specifications.</p>
 *
 * <p>It serves as a base abstraction for all repository interfaces in the
 * application, reducing boilerplate and enabling consistent data access patterns.</p>
 *
 * @param <ENTITY> the entity type representing the database model
 * @param <ID> the identifier type of the entity
 *
 * @author domingos.fernando
 */
@NoRepositoryBean
public interface GenericRepository<
        ENTITY extends GenericEntity<ID>,ID extends Serializable>
        extends JpaRepository<ENTITY, ID>,
        JpaSpecificationExecutor<ENTITY> {
}
