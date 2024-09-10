/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.isysdcore.genericAutoCrud.generics;


import com.isysdcore.genericAutoCrud.utils.ApiError;
import com.isysdcore.genericAutoCrud.utils.Constants;
import com.isysdcore.genericAutoCrud.utils.DefaultSearchParameters;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author domingos.fernando
 * @param <T>
 * @param <S>
 */
public abstract class GenericRestControllerAbstract<T, S extends GenericRestServiceAbstract<T,?,K>, K> implements GenericRestController<T, K> {

    @Getter
    private final String RESOIRCE_NAME = "";
    @Getter
    private final GenericModelAssembler<T> assembler;
    private final S serviceImpl;
    private T object;

    public GenericRestControllerAbstract(S serviceImpl) {
        this.assembler = new GenericModelAssembler<>( this);
        this.serviceImpl = serviceImpl;
    }

    public GenericRestControllerAbstract(S serviceImpl, T entity) {
        this.assembler = new GenericModelAssembler<>( this);
        this.serviceImpl = serviceImpl;
        this.object = entity;
    }

    @Override
    @GetMapping(value = RESOIRCE_NAME, params = {Constants.PAGE, Constants.SIZE, Constants.SORT})
    public Page<T> findAll(@RequestParam(value = Constants.PAGE) int page,
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
    public Page<T> findByQuery(
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
    public ResponseEntity<EntityModel<T>> findById(@PathVariable K id) {
        try{
            T entity = serviceImpl.findById(id) //
                    .orElseThrow(() -> new ResourceNotFoundException(object.getClass().getSimpleName() + " with id " + id));
            return ResponseEntity.ok(assembler.toModel(entity));
        }catch (Exception e){
            throw e;
        }
//        T entity = serviceImpl.findById(id) //
//                    .orElseThrow(() -> new EntityNotFoundException(object.getClass().getName() , id));
//        return ResponseEntity.ok(entity);
//        return ResponseEntity.ok(assembler.toModel(entity));
    }

    @Override
    @PostMapping(RESOIRCE_NAME)
    public ResponseEntity<?> create(@RequestBody T newEntity) {
        try{
            T saved = serviceImpl.save(newEntity);
            EntityModel<T> entityModel = assembler.toModel(saved);
            return ResponseEntity //
                    .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                    .body(entityModel);
        }catch (Exception e){
            throw e;
        }
//        return ResponseEntity.internalServerError().body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro ao salvar a entidade", newEntity.toString()));
    }

    @Override
    @PutMapping(RESOIRCE_NAME + Constants.RESOURCE_BY_ID)
    public ResponseEntity<?> update(@PathVariable K id, @RequestBody T newEntity) {

        try{
            EntityModel<T> entityModel = assembler.toModel(serviceImpl.update(id, newEntity));
            return ResponseEntity //
                    .ok(entityModel);
        }catch (Exception e){
            throw e;
        }

    }

    @Override
    @DeleteMapping(RESOIRCE_NAME + Constants.RESOURCE_BY_ID)
    public ResponseEntity<?> delete(@PathVariable K id) {
        try{
            EntityModel<T> entityModel = assembler.toModel(serviceImpl.delete(id));
            return ResponseEntity //
                    .noContent()
                    .build();
        }catch (Exception e){
            throw e;
        }

    }

    public S getServiceImpl() {
        return serviceImpl;
    }
}
