/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.isysdcore.genericAutoCrud.generics;


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
 * Generic HATEOAS model assembler that converts entities into
 * {@link org.springframework.hateoas.EntityModel EntityModel} representations.
 *
 * <p>This assembler uses reflection to extract the entity identifier and
 * automatically builds hypermedia links, including:</p>
 * <ul>
 *   <li>Self link for the individual resource</li>
 *   <li>Collection link for retrieving all resources of the same type</li>
 * </ul>
 *
 * <p>It is designed to reduce boilerplate in REST APIs by providing a
 * reusable mechanism for wrapping entities with HATEOAS-compliant
 * representations.</p>
 *
 * @param <ENTITY> the entity type representing the database model
 *
 * @author domingos.fernando
 */
public class GenericModelAssembler<ENTITY> implements RepresentationModelAssembler<ENTITY, EntityModel<ENTITY>> {

    GenericRestController<ENTITY, ?> controllerClass;
    @Setter
    DefaultSearchParameters parameters;

    public GenericModelAssembler(GenericRestController<ENTITY, ?> controllerClass) {
        this.controllerClass = controllerClass;
        this.parameters = new DefaultSearchParameters();
    }

    @Override
    public EntityModel<ENTITY> toModel(Object entity) {
        Object id = null;
        ENTITY internalEntity = (ENTITY) entity;
        try {
            Method method = internalEntity.getClass().getMethod("getId");
            id = method.invoke(internalEntity);
            return EntityModel.of(internalEntity,
                    linkTo(methodOn(controllerClass.getClass()).findById(id)).withSelfRel(),
                    linkTo(methodOn(controllerClass.getClass()).findAll(parameters.getPage(), parameters.getSize(), parameters.getSort())).withRel(internalEntity.getClass().getSimpleName().toLowerCase()));


        } catch (NoSuchMethodException | SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
            Logger.getLogger(GenericModelAssembler.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

}
