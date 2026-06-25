/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.isysdcore.genericAutoCrud.generics.nosql.dto;


import io.github.isysdcore.genericAutoCrud.generics.dto.GenericModelAssemblerDto;
import io.github.isysdcore.genericAutoCrud.generics.dto.GenericRestControllerDto;
import io.github.isysdcore.genericAutoCrud.generics.nosql.GenericNoSqlEntity;
import io.github.isysdcore.genericAutoCrud.utils.Constants;
import io.github.isysdcore.genericAutoCrud.utils.DefaultSearchParameters;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Abstract REST controller for MongoDB-based entities that provides
 * standard CRUD endpoints and integrates with a generic service layer.
 *
 * <p>This controller serves as a base implementation for REST resources,
 * delegating business operations to a service component and handling the
 * conversion between entities and Data Transfer Objects (DTOs).</p>
 *
 * <p>Implementations typically extend this class to expose CRUD operations
 * for a specific resource while reusing the common controller behavior
 * provided by the framework.</p>
 *
 * @param <ENTITY> the entity type representing the MongoDB document
 * @param <DTO> the Data Transfer Object (DTO) type used for request and
 *              response payloads
 * @param <SERVICE> the service implementation responsible for business and
 *                  persistence operations on {@code ENTITY}
 * With String as the identifier type used by {@code ENTITY}

 *
 * @author Domingos Fernando
 */
public abstract class GenericNoSqlRestControllerAbstractDto<
        ENTITY extends GenericNoSqlEntity,
        DTO,
        SERVICE extends GenericNoSqlRestServiceAbstractDto<ENTITY, DTO,?, ?>> implements GenericRestControllerDto<DTO, String> {

    @Getter
    private final String RESOIRCE_NAME = "";
    @Getter
    private final GenericModelAssemblerDto<DTO> assembler;
    private final SERVICE serviceImpl;
    private ENTITY object;

    public GenericNoSqlRestControllerAbstractDto(SERVICE serviceImpl) {
        this.assembler = new GenericModelAssemblerDto<>( this);
        this.serviceImpl = serviceImpl;
    }

    public GenericNoSqlRestControllerAbstractDto(SERVICE serviceImpl, ENTITY entity) {
        this.assembler = new GenericModelAssemblerDto<>( this);
        this.serviceImpl = serviceImpl;
        this.object = entity;
    }

    @Override
    @GetMapping(value = RESOIRCE_NAME, params = {Constants.PAGE, Constants.SIZE, Constants.SORT})
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
    @GetMapping(value = RESOIRCE_NAME + Constants.RESOURCE_SEARCH,
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
    @GetMapping(RESOIRCE_NAME + Constants.RESOURCE_BY_ID)
    public ResponseEntity<EntityModel<DTO>> findById(@PathVariable(name = "id") String id) {
        try{
            DTO entity = serviceImpl.findById(id);
            return ResponseEntity.ok(assembler.toModel(entity));
        }catch (Exception e){
            throw e;
        }
    }

    @Override
    @PostMapping(RESOIRCE_NAME)
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
    @PutMapping(RESOIRCE_NAME + Constants.RESOURCE_BY_ID)
    public ResponseEntity<?> update(@PathVariable(name = "id") String id, @RequestBody DTO newEntity) {

        try{
            EntityModel<DTO> entityModel = assembler.toModel(serviceImpl.update(id, newEntity));
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
