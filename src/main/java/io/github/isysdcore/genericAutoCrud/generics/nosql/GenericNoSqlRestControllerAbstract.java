/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.isysdcore.genericAutoCrud.generics.nosql;


import io.github.isysdcore.genericAutoCrud.generics.GenericModelAssembler;
import io.github.isysdcore.genericAutoCrud.generics.GenericRestController;
import io.github.isysdcore.genericAutoCrud.utils.Constants;
import io.github.isysdcore.genericAutoCrud.utils.DefaultSearchParameters;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Abstract base REST controller for MongoDB entities.
 *
 * <p>This class provides a generic implementation of common REST operations,
 * delegating business logic to a service layer and exposing standardized
 * endpoints for CRUD functionality.</p>
 *
 * <p>It is designed to be extended by concrete controllers to reduce boilerplate
 * and enforce consistency across MongoDB-based REST APIs.</p>
 *
 * @param <ENTITY> the entity type representing the MongoDB document
 * @param <SERVICE> the service implementation responsible for business logic
 *                  and persistence operations
 * with a String as ID type for all entities
 *
 * @author domingos.fernando
 */
public abstract class GenericNoSqlRestControllerAbstract<ENTITY extends GenericNoSqlEntity, SERVICE extends GenericNoSqlRestServiceAbstract<ENTITY,?>> implements GenericRestController<ENTITY, String> {

    @Getter
    private final String RESOIRCE_NAME = "";
    @Getter
    private final GenericModelAssembler<ENTITY> assembler;
    @Getter
    private final SERVICE serviceImpl;

    public GenericNoSqlRestControllerAbstract(SERVICE serviceImpl) {
        this.assembler = new GenericModelAssembler<>( this);
        this.serviceImpl = serviceImpl;
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
    public ResponseEntity<EntityModel<ENTITY>> findById(@PathVariable(name = "id") String id) {
        try{
            ENTITY entity = serviceImpl.findById(id);
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
    public ResponseEntity<?> update(@PathVariable(name = "id") String id, @RequestBody ENTITY newEntity) {

        try{
            EntityModel<ENTITY> entityModel = assembler.toModel(serviceImpl.update(id, newEntity));
            return ResponseEntity //
                    .ok(entityModel);
        }catch (Exception e){
            throw e;
        }

    }

    @Override
    @DeleteMapping(RESOIRCE_NAME + Constants.RESOURCE_BY_ID)
    public ResponseEntity<?> delete(@PathVariable(name = "id") String id) {
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
