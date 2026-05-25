/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.isysdcore.genericAutoCrud.generics.mongo.dto;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import io.github.isysdcore.genericAutoCrud.generics.GenericEntity;
import io.github.isysdcore.genericAutoCrud.generics.dto.GenericDTOMapper;
import io.github.isysdcore.genericAutoCrud.generics.dto.GenericDto;
import io.github.isysdcore.genericAutoCrud.generics.mongo.MongoGenericRepository;
import io.github.isysdcore.genericAutoCrud.generics.sql.GenericRestServiceAbstract;
import io.github.isysdcore.genericAutoCrud.query.mongo.MongoPropertyResolver;
import io.github.isysdcore.genericAutoCrud.query.mongo.MongoRsqlVisitor;
import io.github.isysdcore.genericAutoCrud.utils.DefaultSearchParameters;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
* This abstract class provide a Generic Rest Api operations using GeneticRepository and GenericDTOMapper for MongoDB entities.
* @author domingos.fernando
* @param <ENTITY> The Entity class that represent the database entity
* @param <DTO> The Entity class that represent Output DTO
* @param <REPOSITORY> The generic Repository modified by entity and id datatype injected
* @param <ID> The Class type that represent the id field datatype of entity of type ENTITY
*
* */

@RequiredArgsConstructor
@Transactional
public abstract class MongoGenericRestServiceAbstractDto<
        ENTITY extends GenericEntity<ID>,
        DTO extends GenericDto<ID>,
        REPOSITORY extends MongoGenericRepository<ENTITY,ID>,
        MAPPER extends GenericDTOMapper<DTO, ENTITY>,
        ID extends Serializable>{

    @Autowired
    public REPOSITORY repository;
    protected MAPPER mapper;
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
    public DTO save(DTO newEntity) {
       try{
           ENTITY entity = mapper.toEntity(newEntity);
           entity.setCreatedAt(Instant.now());
           ENTITY savedEntity = repository.save(entity);
           return mapper.toDto(savedEntity);
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
    public DTO findById(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null for search on database");
        }
        return repository.findById(id).map(mapper::toDto).orElseThrow(() -> {
            Logger.getLogger(GenericRestServiceAbstract.class.getName()).log(Level.SEVERE, null, new RuntimeException("Error accessing find entity  of type "+ entityClass.getName() +" by id "));
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
    public Page<DTO> findAll(int page, int size, int sort) {
        return repository.findAll(DefaultSearchParameters.preparePages(page, size, sort)).map(mapper::toDto);
    }
    /**
     *
     * @param query Query to search in database follow the syntax "fieldName==value"
     * @param page The page counter start by 0
     * @param size The amount of items per page
     * @param sort The order of result 1 for ASC and -1 for DESC, default 0
     * @return Pageable object of type ENTITY
     */
    public Page<DTO> findAll(String query, int page, int size, int sort) {
        String defaultQuery = "deleted==false;(" + query + ")";
        Node rootNode = new RSQLParser().parse(defaultQuery);
        Criteria criteria = rootNode.accept(new MongoRsqlVisitor<>(entityClass, mongoPropertyResolver));
        Pageable pageable = DefaultSearchParameters.preparePages(page, size, sort);
        Query finalQuery = new Query(criteria)
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize());
        List<DTO> content = mapper.toDtoList(mongoTemplate.find(finalQuery, entityClass));
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
    public DTO update(ID id, DTO newEntity) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null for update");
        }

        ENTITY updatedEntity = mapper.toEntity(newEntity);
        return repository.findById(id) //
                .map(oldEntity -> {
                    try {
                        List<Field> fieldList = Arrays.asList(oldEntity.getClass().getDeclaredFields());
                        fieldList.forEach(oldField -> {
                            oldField.setAccessible(true);
                            try {
                                Field fd = updatedEntity.getClass().getDeclaredField(oldField.getName());
                                fd.setAccessible(true);
                                if(fd.get(updatedEntity) == null)
                                {
                                    fd.set(updatedEntity, oldField.get(oldEntity));
                                }
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                Logger.getLogger(GenericRestServiceAbstract.class.getName()).log(Level.SEVERE, null, e);
                            }
                        });
                        updatedEntity.setCreatedAt(oldEntity.getCreatedAt());
                        updatedEntity.setUpdatedAt(Instant.now());
                        updatedEntity.setId(id);
                    } catch (SecurityException | IllegalArgumentException ex) {
                        Logger.getLogger(GenericRestServiceAbstract.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return mapper.toDto(repository.save(updatedEntity));
                }) //
                .orElseThrow( () -> new EntityNotFoundException(
                        "Entity with id " + id + " not found to update, ABORT")
                );
    }
    /**
     *
     * @param id The unique main primary key that identify the database entity
     * @param newEntity The new entity registry of type ENTITY that will be used to update the old entity registry
     * @param updatedBy The primary key from the user or identity that perform this update
     * @return Updated registry of type ENTITY
     */
    public DTO update(ID id, DTO newEntity, ID updatedBy) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null for update");
        }

        ENTITY updatedEntity = mapper.toEntity(newEntity);
        return repository.findById(id) //
                .map(oldEntity -> {
                    try {
                        List<Field> fieldList = Arrays.asList(oldEntity.getClass().getDeclaredFields());
                        fieldList.forEach(oldField -> {
                            oldField.setAccessible(true);
                            try {
                                Field fd = updatedEntity.getClass().getDeclaredField(oldField.getName());
                                fd.setAccessible(true);
                                if(fd.get(updatedEntity) == null)
                                {
                                    fd.set(updatedEntity, oldField.get(oldEntity));
                                }
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                Logger.getLogger(GenericRestServiceAbstract.class.getName()).log(Level.SEVERE, null, e);
                            }
                        });
                        updatedEntity.setCreatedAt(oldEntity.getCreatedAt());
                        updatedEntity.setUpdatedBy(updatedBy);
                        updatedEntity.setUpdatedAt(Instant.now());
                        updatedEntity.setId(id);
                    } catch (SecurityException | IllegalArgumentException ex) {
                        Logger.getLogger(GenericRestServiceAbstract.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return mapper.toDto(repository.save(updatedEntity));
                }) //
                .orElseThrow( () -> new EntityNotFoundException(
                        "Entity with id " + id + " not found to update, ABORT")
                );
    }
    /**
     *
     * @param id The unique main primary key that identify the database registry entity
     * @return The entity founded in database or not found exception
     */
    public DTO delete(ID id) {
        return repository.findById(id) //
                .map(oldEntity -> {
                    try {
                        oldEntity.setDeletedAt(Instant.now());
                        oldEntity.setDeleted(Boolean.TRUE);
                    } catch (Exception ex) {
                        Logger.getLogger(GenericRestServiceAbstract.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return mapper.toDto(repository.save(oldEntity));
                }) //
                .orElseThrow(() -> new EntityNotFoundException(
                        "Entity with id " + id + " not found to update, ABORT")
                );
    }
    /**
     *
     * @param id The unique main primary key that identify the database registry entity
     * @param deletedBy The primary key from person or entity that perform the deletion
     * @return The entity founded in database or not found exception
     */
    public DTO delete(ID id, ID deletedBy) {
        return repository.findById(id) //
                .map(oldEntity -> {
                    try {
                        oldEntity.setDeletedAt(Instant.now());
                        oldEntity.setDeleted(Boolean.TRUE);
                        oldEntity.setDeletedBy(deletedBy);
                    } catch (Exception ex) {
                        Logger.getLogger(GenericRestServiceAbstract.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return mapper.toDto(repository.save(oldEntity));
                }) //
                .orElseThrow(() -> new EntityNotFoundException(
                        "Entity with id " + id + " not found to update, ABORT")
                );
    }

}
