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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract service layer providing generic CRUD operations for entities for SQL Databases.
 *
 * <p>This class implements a reusable service abstraction that encapsulates
 * business logic and persistence operations, delegating data access to a
 * generic repository layer.</p>
 *
 * <p>It is intended to be extended by concrete service implementations in order
 * to reduce boilerplate code and enforce consistent service behavior across
 * all entity types.</p>
 *
 * @param <ENTITY> the entity type representing the database model
 * @param <REPOSITORY> the repository responsible for persistence operations
 *                     on the given entity
 * @param <ID> the identifier type of the entity, must be {@link java.io.Serializable}
 *
 * @author domingos.fernando
 */
public abstract class GenericRestServiceAbstract<
        ENTITY extends GenericEntity<ID>, 
        REPOSITORY extends GenericRepository<ENTITY,ID>, 
        ID extends Serializable>{

    @Autowired
    public REPOSITORY repository;
    /**
     *
     * @param newEntity The new entity registry of type ENTITY to store in database
     * @return A database saved entity of type ENTITY
     */
    public ENTITY save(ENTITY newEntity) {
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
     * @return An optional object of type ENTITY
     */
    public ENTITY findById(ID id) {
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
     * @return Pageable object of type ENTITY
     */
    public Page<ENTITY> findAll(int page, int size, int sort) {
        String defaultQuery = "deleted==false";
        Node rootNode = new RSQLParser().parse(defaultQuery);
        Specification<ENTITY> spec = rootNode.accept(new CustomRsqlVisitor<>());
        return repository.findAll(spec, DefaultSearchParameters.preparePages(page, size, sort));
    }
    /**
     *
     * @param query Query to search in database follow the syntax "fieldName==value"
     * @param page The page counter start by 0
     * @param size The amount of items per page
     * @param sort The order of result 1 for ASC and -1 for DESC, default 0
     * @return Pageable object of type ENTITY
     */
    public Page<ENTITY> findAll(String query, int page, int size, int sort) {
        String defaultQuery = "deleted==false;(" + query + ")";
        Node rootNode = new RSQLParser().parse(defaultQuery);
        Specification<ENTITY> spec = rootNode.accept(new CustomRsqlVisitor<>());
        return repository.findAll(spec, DefaultSearchParameters.preparePages(page, size, sort));
    }
    /**
     *
     * @param condToCount Query to use as a condition to count follow the syntax "fieldName==value"
     * @return The amount of entities of type ENTITY that match de condition
     */
    public long count(String condToCount) {
        condToCount = "deleted==FALSE;(" + condToCount + ")";
        Node rootNode = new RSQLParser().parse(condToCount);
        Specification<ENTITY> spec = rootNode.accept(new CustomRsqlVisitor<>());
        return repository.count(spec);
    }
    /**
     *
     * @param id The unique main primary key that identify the database entity
     * @param newEntity The new entity registry of type ENTITY that will be used to update the old entity registry
     * @return Updated registry of type ENTITY
     */
    public ENTITY update(ID id, ENTITY newEntity) {
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
     * @param newEntity The new entity registry of type ENTITY that will be used to update the old entity registry
     * @param updatedBy The primary key from the user or identity that perform this update
     * @return Updated registry of type ENTITY
     */
    public ENTITY update(ID id, ENTITY newEntity, ID updatedBy) {
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
    public ENTITY delete(ID id) {
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
    public ENTITY delete(ID id, ID deletedBy) {
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
