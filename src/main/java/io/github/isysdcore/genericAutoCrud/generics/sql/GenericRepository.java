package io.github.isysdcore.genericAutoCrud.generics.sql;

import io.github.isysdcore.genericAutoCrud.generics.GenericEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/// GenericRepository is a generic interface that extends JpaRepository and JpaSpecificationExecutor.
/// It provides basic CRUD operations and allows for the execution of JPA specifications.
/// @param <ENTITY> The Entity class that represent the database entity
/// @param <ID> The Class type that represent the id field datatype of entity of type T
@NoRepositoryBean
public interface GenericRepository<
        ENTITY extends GenericEntity<ID>,ID extends Serializable>
        extends JpaRepository<ENTITY, ID>,
        JpaSpecificationExecutor<ENTITY> {
}
