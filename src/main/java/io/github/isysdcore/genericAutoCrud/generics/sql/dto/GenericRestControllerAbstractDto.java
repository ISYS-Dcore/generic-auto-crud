/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.isysdcore.genericAutoCrud.generics.sql.dto;


import io.github.isysdcore.genericAutoCrud.generics.GenericEntity;
import io.github.isysdcore.genericAutoCrud.generics.dto.GenericDto;
import io.github.isysdcore.genericAutoCrud.generics.dto.GenericModelAssemblerDto;
import io.github.isysdcore.genericAutoCrud.generics.dto.GenericRestControllerDto;
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
 *
 * @param <ENTITY> The Entity class that represent the database entity
 * @param <ID> The Class type that represent the id field datatype of entity of type ENTITY
 */

/// @author domingos.fernando
/// @param <ENTITY> The Entity class that represent the database entity
/// @param <SERVICE> The service Implementation that already modified by entity injection
/// @param <ID> The Class type that represent the id field datatype of entity of type ENTITY
public abstract class GenericRestControllerAbstractDto<
        ENTITY extends GenericEntity<ID>,
        DTO extends GenericDto<ID>,
        SERVICE extends GenericRestServiceAbstractDto<ENTITY, DTO,?, ?,ID>,
        ID extends Serializable> implements GenericRestControllerDto<DTO, ID> {

    @Getter
    private final String RESOURCE_NAME = "";
    @Getter
    private final GenericModelAssemblerDto<DTO> assembler;
    private final SERVICE serviceImpl;
    private ENTITY object;

    public GenericRestControllerAbstractDto(SERVICE serviceImpl) {
        this.assembler = new GenericModelAssemblerDto<>( this);
        this.serviceImpl = serviceImpl;
    }

    public GenericRestControllerAbstractDto(SERVICE serviceImpl, ENTITY entity) {
        this.assembler = new GenericModelAssemblerDto <>( this);
        this.serviceImpl = serviceImpl;
        this.object = entity;
    }

    @Override
    @GetMapping(value = RESOURCE_NAME, params = {Constants.PAGE, Constants.SIZE, Constants.SORT})
    public Page<DTO> findAll(@RequestParam(value = Constants.PAGE) int page,
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
    @GetMapping(value = RESOURCE_NAME + Constants.RESOURCE_SEARCH,
            params = {Constants.PAGE, Constants.SIZE, Constants.SORT,
                    Constants.QUERY})
    public Page<DTO> findByQuery(
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
    @GetMapping(RESOURCE_NAME + Constants.RESOURCE_BY_ID)
    public ResponseEntity<EntityModel<DTO>> findById(@PathVariable(name = "id") ID id) {
        try{
            DTO entity = serviceImpl.findById(id);
            return ResponseEntity.ok(assembler.toModel(entity));
        }catch (Exception e){
            throw e;
        }
    }

    @Override
    @PostMapping(RESOURCE_NAME)
    public ResponseEntity<?> create(@RequestBody DTO newEntity) {
        try{
            DTO saved = serviceImpl.save(newEntity);
            EntityModel<DTO> entityModel = assembler.toModel(saved);
            return ResponseEntity //
                    .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                    .body(entityModel);
        }catch (Exception e){
            throw e;
        }
    }

    @Override
    @PutMapping(RESOURCE_NAME + Constants.RESOURCE_BY_ID)
    public ResponseEntity<?> update(@PathVariable(name = "id") ID id, @RequestBody DTO newEntity) {

        try{
            EntityModel<DTO> entityModel = assembler.toModel(serviceImpl.update(id, newEntity));
            return ResponseEntity //
                    .ok(entityModel);
        }catch (Exception e){
            throw e;
        }

    }

    @Override
    @DeleteMapping(RESOURCE_NAME + Constants.RESOURCE_BY_ID)
    public ResponseEntity<?> delete(@PathVariable(name = "id") ID id) {
        try{
            EntityModel<DTO> entityModel = assembler.toModel(serviceImpl.delete(id));
            return ResponseEntity //
                    .noContent()
                    .build();
        }catch (Exception e){
            throw e;
        }

    }

    public SERVICE getServiceImpl() {
        return serviceImpl;
    }
}
