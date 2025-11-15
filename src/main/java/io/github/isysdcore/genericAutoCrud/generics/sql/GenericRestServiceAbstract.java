/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.isysdcore.genericAutoCrud.generics.sql;

import io.github.isysdcore.genericAutoCrud.ex.ResourceNotFoundException;
import io.github.isysdcore.genericAutoCrud.generics.GenericEntity;
import io.github.isysdcore.genericAutoCrud.query.sql.CustomRsqlVisitor;
import io.github.isysdcore.genericAutoCrud.utils.DefaultSearchParameters;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/// @author domingos.fernando
/// @param <T> The Entity class that represent the database entity
/// @param <K> The Class type that represent the id field datatype of entity of type T
/// @param <R> The generic Repository modified by entity and id datatype injected
public abstract class GenericRestServiceAbstract<T extends GenericEntity<K>, R extends GenericRepository<T,K>, K>{

    @Autowired
    public R repository;
    /**
     *
     * @param newEntity The new entity registry of type T to store in database
     * @return A database saved entity of type T
     */
    public T save(T newEntity) {
       try{
           newEntity.setCreatedAt(Instant.now());
           return repository.save(newEntity);
        } catch (Exception ex) {
            Logger.getLogger(GenericRestServiceAbstract.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param id The unique main primary key that identify the database entity
     * @return An optional object of type T
     */
    public T findById(K id) {
        return repository.findById(id).orElseThrow(() -> {
            Logger.getLogger(GenericRestServiceAbstract.class.getName()).log(Level.SEVERE, null, new RuntimeException("Error accessing find entity of type by id "));
            return new EntityNotFoundException("Error was unable to find entity with id: " + id.toString() + " on database.");
        });
    }

    /**
     *
     * @param page The page counter start by 0
     * @param size The amount of items per page
     * @param sort The order of result 1 for ASC and -1 for DESC, default 0
     * @return Pageable object of type T
     */
    public Page<T> findAll(int page, int size, int sort) {
        String defaultQuery = "deleted==false";
        Node rootNode = new RSQLParser().parse(defaultQuery);
        Specification<T> spec = rootNode.accept(new CustomRsqlVisitor<>());
        return repository.findAll(spec, DefaultSearchParameters.preparePages(page, size, sort));
    }
    /**
     *
     * @param query Query to search in database follow the syntax "fieldName==value"
     * @param page The page counter start by 0
     * @param size The amount of items per page
     * @param sort The order of result 1 for ASC and -1 for DESC, default 0
     * @return Pageable object of type T
     */
    public Page<T> findAll(String query, int page, int size, int sort) {
        String defaultQuery = "deleted==false;(" + query + ")";
        Node rootNode = new RSQLParser().parse(defaultQuery);
        Specification<T> spec = rootNode.accept(new CustomRsqlVisitor<>());
        return repository.findAll(spec, DefaultSearchParameters.preparePages(page, size, sort));
    }
    /**
     *
     * @param condToCount Query to use as a condition to count follow the syntax "fieldName==value"
     * @return The amount of entities of type T that match de condition
     */
    public long count(String condToCount) {
        condToCount = "deleted==FALSE;(" + condToCount + ")";
        Node rootNode = new RSQLParser().parse(condToCount);
        Specification<T> spec = rootNode.accept(new CustomRsqlVisitor<>());
        return repository.count(spec);
    }
    /**
     *
     * @param id The unique main primary key that identify the database entity
     * @param newEntity The new entity registry of type T that will be used to update the old entity registry
     * @return Updated registry of type T
     */
    public T update(K id, T newEntity) {
        return repository.findById(id) //
                .map(oldEntity -> {
                    try {
                        List<Field> fieldList = Arrays.asList(oldEntity.getClass().getDeclaredFields());
                        fieldList.forEach(oldField -> {
                            oldField.setAccessible(true);
                            try {
                                Field fd = newEntity.getClass().getDeclaredField(oldField.getName());
                                fd.setAccessible(true);
                                if(fd.get(newEntity) == null)
                                {
                                    fd.set(newEntity, oldField.get(oldEntity));
                                }
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                Logger.getLogger(GenericRestServiceAbstract.class.getName()).log(Level.SEVERE, null, e);
                            }
                        });
                        newEntity.setCreatedAt(oldEntity.getCreatedAt());
                        newEntity.setUpdatedBy(oldEntity.getUpdatedBy());
                        newEntity.setUpdatedAt(Instant.now());
                        newEntity.setId(id);
                    } catch (SecurityException | IllegalArgumentException ex) {
                        Logger.getLogger(GenericRestServiceAbstract.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return repository.save(newEntity);
                }) //
                .orElseThrow( () -> new ResourceNotFoundException(id.toString()));
    }
    /**
     *
     * @param id The unique main primary key that identify the database entity
     * @param newEntity The new entity registry of type T that will be used to update the old entity registry
     * @param updatedBy The primary key from the user or identity that perform this update
     * @return Updated registry of type T
     */
    public T update(K id, T newEntity, K updatedBy) {
        return repository.findById(id) //
                .map(oldEntity -> {
                    try {
                        List<Field> fieldList = Arrays.asList(oldEntity.getClass().getDeclaredFields());
                        fieldList.forEach(oldField -> {
                            oldField.setAccessible(true);
                            try {
                                Field fd = newEntity.getClass().getDeclaredField(oldField.getName());
                                fd.setAccessible(true);
                                if(fd.get(newEntity) == null)
                                {
                                    fd.set(newEntity, oldField.get(oldEntity));
                                }
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                Logger.getLogger(GenericRestServiceAbstract.class.getName()).log(Level.SEVERE, null, e);
                            }
                        });
                        newEntity.setCreatedAt(oldEntity.getCreatedAt());
                        newEntity.setUpdatedBy(updatedBy);
                        newEntity.setUpdatedAt(Instant.now());
                        newEntity.setId(id);
                    } catch (SecurityException | IllegalArgumentException ex) {
                        Logger.getLogger(GenericRestServiceAbstract.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return repository.save(newEntity);
                }) //
                .orElseThrow( () -> new ResourceNotFoundException(id.toString()));
    }
    /**
     *
     * @param id The unique main primary key that identify the database registry entity
     * @return The entity founded in database or not found exception
     */
    public T delete(K id) {
        return repository.findById(id) //
                .map(oldEntity -> {
                    try {
                        oldEntity.setDeletedAt(Instant.now());
                        oldEntity.setDeleted(Boolean.TRUE);
                    } catch (Exception ex) {
                        Logger.getLogger(GenericRestServiceAbstract.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return repository.save(oldEntity);
                }) //
                .orElseThrow(() -> new ResourceNotFoundException(id.toString()));
    }
    /**
     *
     * @param id The unique main primary key that identify the database registry entity
     * @param deletedBy The primary key from person or entity that perform the deletion
     * @return The entity founded in database or not found exception
     */
    public T delete(K id, K deletedBy) {
        return repository.findById(id) //
                .map(oldEntity -> {
                    try {
                        oldEntity.setDeletedAt(Instant.now());
                        oldEntity.setDeleted(Boolean.TRUE);
                        oldEntity.setDeletedBy(deletedBy);
                    } catch (Exception ex) {
                        Logger.getLogger(GenericRestServiceAbstract.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return repository.save(oldEntity);
                }) //
                .orElseThrow(() -> new ResourceNotFoundException(id.toString()));
    }

}
