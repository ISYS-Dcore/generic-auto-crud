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

/**
 * Generic HATEOAS model assembler that converts objects into
 * {@link org.springframework.hateoas.EntityModel EntityModel} representations.
 *
 * <p>This implementation uses reflection to obtain the object's identifier
 * and automatically generates hypermedia links such as self and collection
 * links.</p>
 *
 * <p>The assembler is designed to work with entities or Data Transfer Objects
 * (DTOs), reducing the need for dedicated assemblers for each resource type.</p>
 *
 * @param <DTO> the entity or DTO type to be converted into an {@code EntityModel}
 * @author Domingos Fernando
 */
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
