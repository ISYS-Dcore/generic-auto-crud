/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.isysdcore.genericAutoCrud.generics;


import io.github.isysdcore.genericAutoCrud.ex.ResourceNotFoundException;
import io.github.isysdcore.genericAutoCrud.generics.sql.GenericSqlEntity;
import io.github.isysdcore.genericAutoCrud.utils.Constants;
import io.github.isysdcore.genericAutoCrud.utils.DefaultSearchParameters;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

/**
 * Abstract base REST controller providing generic CRUD endpoints for entity-based APIs for SQL Databases.
 *
 * <p>This class defines a reusable REST controller layer that delegates business logic
 * to a service implementation, enabling consistent CRUD operations across all entities
 * without requiring repetitive controller code.</p>
 *
 * <p>It is designed for entity-based APIs where entities are exposed directly (not DTOs),
 * and acts as a standard foundation for REST controllers in the application.</p>
 *
 * @param <ENTITY> the entity type representing the database model
 * @param <SERVICE> the service implementation responsible for business logic and persistence
 *                  operations for the given entity
 * @param <ID> the identifier type of the entity, must be {@link Serializable}
 *
 * @author domingos.fernando
 */
public abstract class GenericRestControllerAbstract<
        ENTITY extends GenericBaseEntity<ID>,
        SERVICE extends GenericRestService<
                        ENTITY,ID>, ID extends Serializable>
        implements GenericRestController<ENTITY, ID> {

    @Getter
    private final String RESOIRCE_NAME = "";
    @Getter
    private final GenericModelAssembler<ENTITY> assembler;
    @Getter
    private final SERVICE serviceImpl;
    private ENTITY object;

    public GenericRestControllerAbstract(SERVICE serviceImpl) {
        this.assembler = new GenericModelAssembler<>( this);
        this.serviceImpl = serviceImpl;
    }

    public GenericRestControllerAbstract(SERVICE serviceImpl, ENTITY entity) {
        this.assembler = new GenericModelAssembler<>( this);
        this.serviceImpl = serviceImpl;
        this.object = entity;
    }

    @Override
    @GetMapping(value = RESOIRCE_NAME, params = {Constants.PAGE, Constants.SIZE, Constants.SORT})
    public Page<ENTITY> findAll(@RequestParam(value = Constants.PAGE) int page,
                           @RequestParam(value = Constants.SIZE) int size,
                           @RequestParam(value = Constants.SORT) int sort) {
        try {
            assembler.setParameters(new DefaultSearchParameters(page, size, sort, "isDeleted==false"));
            return serviceImpl.findAll(page, size, sort);
        }catch (Exception e){
            throw e;
        }
    }

    @Override
    @GetMapping(value = RESOIRCE_NAME + Constants.RESOURCE_SEARCH,
            params = {Constants.PAGE, Constants.SIZE, Constants.SORT,
                    Constants.QUERY})
    public Page<ENTITY> findByQuery(
            @RequestParam(value = Constants.PAGE) int page,
            @RequestParam(value = Constants.SIZE) int size,
            @RequestParam(value = Constants.SORT) int sort,
            @RequestParam(value = Constants.QUERY, defaultValue = "id==*", required = true) String query) {
        try{
            assembler.setParameters(new DefaultSearchParameters(page, size, sort, query));
            return serviceImpl.findAll(query, page, size, sort);
        }catch (Exception e){
            throw e;
        }
    }

    @Override
    @GetMapping(RESOIRCE_NAME + Constants.RESOURCE_BY_ID)
    public ResponseEntity<EntityModel<ENTITY>> findById(@PathVariable(name = "id") ID id) {
        try{
            ENTITY entity = serviceImpl.findById(id);
            if (entity == null) {
                throw new ResourceNotFoundException("Resource of type " + object.getClass().getSimpleName() + " with id " + id + " not found");
            }
            return ResponseEntity.ok(assembler.toModel(entity));
        }catch (Exception e){
            throw e;
        }
    }

    @Override
    @PostMapping(RESOIRCE_NAME)
    public ResponseEntity<?> create(@RequestBody ENTITY newEntity) {
        try{
            ENTITY saved = serviceImpl.save(newEntity);
            EntityModel<ENTITY> entityModel = assembler.toModel(saved);
            return ResponseEntity //
                    .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                    .body(entityModel);
        }catch (Exception e){
            throw e;
        }
    }

    @Override
    @PutMapping(RESOIRCE_NAME + Constants.RESOURCE_BY_ID)
    public ResponseEntity<?> update(@PathVariable(name = "id") ID id, @RequestBody ENTITY newEntity) {

        try{
            ENTITY entity = serviceImpl.update(id, newEntity);
            if (entity == null) {
                throw new ResourceNotFoundException("Resource of type " + object.getClass().getSimpleName() + " with id " + id + " not found");
            }
            EntityModel<ENTITY> entityModel = assembler.toModel(entity);
            return ResponseEntity //
                    .ok(entityModel);
        }catch (Exception e){
            throw e;
        }

    }

    @Override
    @DeleteMapping(RESOIRCE_NAME + Constants.RESOURCE_BY_ID)
    public ResponseEntity<?> delete(@PathVariable(name = "id") ID id) {
        try{
            EntityModel<ENTITY> entityModel = assembler.toModel(serviceImpl.delete(id));
            return ResponseEntity //
                    .noContent()
                    .build();
        }catch (Exception e){
            throw e;
        }

    }

}
