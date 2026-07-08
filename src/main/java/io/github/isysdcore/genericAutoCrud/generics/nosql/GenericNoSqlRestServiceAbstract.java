/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.isysdcore.genericAutoCrud.generics.nosql;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import io.github.isysdcore.genericAutoCrud.ex.ResourceNotFoundException;
import io.github.isysdcore.genericAutoCrud.generics.GenericRestService;
import io.github.isysdcore.genericAutoCrud.query.mongo.MongoPropertyResolver;
import io.github.isysdcore.genericAutoCrud.query.mongo.MongoRsqlVisitor;
import io.github.isysdcore.genericAutoCrud.utils.DefaultSearchParameters;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract base service for MongoDB entities.
 *
 * <p>This class provides a generic implementation of common REST service operations,
 * delegating data access to a repository layer and providing standardized
 * business logic for MongoDB-based applications.</p>
 *
 * @param <ENTITY> the entity type representing the MongoDB document
 * @param <REPOSITORY> the repository implementation responsible for data access
 * With String as ID datatype
 *
 * @author domingos.fernando
 */
@RequiredArgsConstructor
public abstract class GenericNoSqlRestServiceAbstract<ENTITY extends GenericNoSqlEntity, REPOSITORY extends GenericNoSqlRepository<ENTITY>>
implements GenericRestService<ENTITY, String> {

    @Autowired
    public REPOSITORY repository;
    @Autowired
    private MongoTemplate mongoTemplate;
    private final Class<ENTITY> entityClass;
    @Autowired
    private MongoPropertyResolver mongoPropertyResolver;

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
            Logger.getLogger(GenericNoSqlRestServiceAbstract.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param id The unique main primary key that identify the database entity
     * @return An optional object of type ENTITY
     */
    public ENTITY findById(String id) {
        return repository.findByIdAndDeletedFalse(id).orElseThrow(() -> {
            Logger.getLogger(GenericNoSqlRestServiceAbstract.class.getName()).log(Level.SEVERE, null, new RuntimeException("Error accessing find entity  of type "+ entityClass.getName() +" by id "));
            return new EntityNotFoundException("Error was unable to find entity with id: " + id + " on database.");
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
        return repository.findAllByDeletedFalse(DefaultSearchParameters.preparePages(page, size, sort));
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
        Criteria criteria = rootNode.accept(new MongoRsqlVisitor<>(entityClass, mongoPropertyResolver));
        Pageable pageable = DefaultSearchParameters.preparePages(page, size, sort);
        Query finalQuery = new Query(criteria)
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize());
        List<ENTITY> content = mongoTemplate.find(finalQuery, entityClass);
        long total = mongoTemplate.count(new Query(criteria),entityClass);
        return new PageImpl<>(content, pageable, total);
    }
    /**
     *
     * @param condToCount Query to use as a condition to count follow the syntax "fieldName==value"
     * @return The amount of entities of type ENTITY that match de condition
     */
    public long count(String condToCount) {
        condToCount = "deleted==FALSE;(" + condToCount + ")";
        Node rootNode = new RSQLParser().parse(condToCount);
        Criteria criteria = rootNode.accept(new MongoRsqlVisitor<>(entityClass, mongoPropertyResolver));
        return mongoTemplate.count(new Query(criteria),entityClass);
    }
    /**
     *
     * @param id The unique main primary key that identify the database entity
     * @param newEntity The new entity registry of type ENTITY that will be used to update the old entity registry
     * @return Updated registry of type ENTITY
     */
    public ENTITY update(String id, ENTITY newEntity) {
        return repository.findByIdAndDeletedFalse(id) //
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
                                Logger.getLogger(GenericNoSqlRestServiceAbstract.class.getName()).log(Level.SEVERE, null, e);
                            }
                        });
                        newEntity.setCreatedAt(oldEntity.getCreatedAt());
                        newEntity.setUpdatedBy(oldEntity.getUpdatedBy());
                        newEntity.setUpdatedAt(Instant.now());
                        newEntity.setId(id);
                    } catch (SecurityException | IllegalArgumentException ex) {
                        Logger.getLogger(GenericNoSqlRestServiceAbstract.class.getName()).log(Level.SEVERE, null, ex);
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
    public ENTITY update(String id, ENTITY newEntity, String updatedBy) {
        return repository.findByIdAndDeletedFalse(id) //
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
                                Logger.getLogger(GenericNoSqlRestServiceAbstract.class.getName()).log(Level.SEVERE, null, e);
                            }
                        });
                        newEntity.setCreatedAt(oldEntity.getCreatedAt());
                        newEntity.setUpdatedBy(updatedBy);
                        newEntity.setUpdatedAt(Instant.now());
                        newEntity.setId(id);
                    } catch (SecurityException | IllegalArgumentException ex) {
                        Logger.getLogger(GenericNoSqlRestServiceAbstract.class.getName()).log(Level.SEVERE, null, ex);
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
    public ENTITY delete(String id) {
        return repository.findByIdAndDeletedFalse(id) //
                .map(oldEntity -> {
                    try {
                        oldEntity.setDeletedAt(Instant.now());
                        oldEntity.setDeleted(Boolean.TRUE);
                    } catch (Exception ex) {
                        Logger.getLogger(GenericNoSqlRestServiceAbstract.class.getName()).log(Level.SEVERE, null, ex);
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
    public ENTITY delete(String id, String deletedBy) {
        return repository.findByIdAndDeletedFalse(id) //
                .map(oldEntity -> {
                    try {
                        oldEntity.setDeletedAt(Instant.now());
                        oldEntity.setDeleted(Boolean.TRUE);
                        oldEntity.setDeletedBy(deletedBy);
                    } catch (Exception ex) {
                        Logger.getLogger(GenericNoSqlRestServiceAbstract.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return repository.save(oldEntity);
                }) //
                .orElseThrow(() -> new ResourceNotFoundException(id.toString()));
    }

}
