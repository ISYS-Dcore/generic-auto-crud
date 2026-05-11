/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.isysdcore.genericAutoCrud.generics.dto;


import io.github.isysdcore.genericAutoCrud.generics.GenericRestController;
import io.github.isysdcore.genericAutoCrud.utils.DefaultSearchParameters;
import lombok.Setter;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/// This class is a generic model assembler that converts an entity of type T
/// into an EntityModel<T>. It uses reflection to get the ID of the entity and
/// creates links for self and collection retrieval.
/// @author domingos.fernando
/// @param <DTO> The Entity class that represent the database entity or DTO
public class GenericModelAssemblerDto<DTO> implements RepresentationModelAssembler<DTO, EntityModel<DTO>> {

    GenericRestControllerDto<DTO, ?> controllerClass;
    @Setter
    DefaultSearchParameters parameters;

    public GenericModelAssemblerDto(GenericRestControllerDto<DTO, ?> controllerClass) {
        this.controllerClass = controllerClass;
        this.parameters = new DefaultSearchParameters();
    }

    @Override
    public EntityModel<DTO> toModel(Object entity) {
        Object id = null;
        DTO internalEntity = (DTO) entity;
        try {
            Method method = internalEntity.getClass().getMethod("getId");
            id = method.invoke(internalEntity);
            return EntityModel.of(internalEntity,
                    linkTo(methodOn(controllerClass.getClass()).findById(id)).withSelfRel(),
                    linkTo(methodOn(controllerClass.getClass()).findAll(parameters.getPage(), parameters.getSize(), parameters.getSort())).withRel(internalEntity.getClass().getSimpleName().toLowerCase()));


        } catch (NoSuchMethodException | SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
            Logger.getLogger(GenericModelAssemblerDto.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

}
