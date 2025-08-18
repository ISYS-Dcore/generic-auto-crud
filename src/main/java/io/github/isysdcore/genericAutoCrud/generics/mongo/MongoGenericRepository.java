package io.github.isysdcore.genericAutoCrud.generics.mongo;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

/// GenericRepository is a generic interface that extends MongoRepository.
/// It provides basic CRUD operations and allows for the execution of MongoDB queries.
/// @param <T> The Entity class that represent the database entity
/// @param <K> The Class type that represent the id field datatype of entity of type T
@NoRepositoryBean
public interface MongoGenericRepository<T,K> extends MongoRepository<T, K>  {

    @NotNull
    @Override
    @Query("{ 'deleted' : false }")
    Page<T> findAll(@NotNull Pageable pageable);

    @NotNull
    @Override
    @Query("{ 'deleted' : false, 'id' : ?0 }")
    Optional<T> findById(@NotNull K entityId);
}
