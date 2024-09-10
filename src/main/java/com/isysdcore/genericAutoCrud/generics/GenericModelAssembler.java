/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.isysdcore.genericAutoCrud.generics;


import com.isysdcore.genericAutoCrud.utils.DefaultSearchParameters;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 *
 * @author domingos.fernando
 * @param <T>
 */
public class GenericModelAssembler<T> implements RepresentationModelAssembler<T, EntityModel<T>> {

    GenericRestController<T, ?> controllerClass;
    DefaultSearchParameters parameters;

    public GenericModelAssembler(GenericRestController<T, ?> controllerClass) {
        this.controllerClass = controllerClass;
        this.parameters = new DefaultSearchParameters();
    }

    @Override
    public EntityModel<T> toModel(Object entity) {
        Object id = null;
        T internalEntity = (T) entity;
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

    public void setParameters(DefaultSearchParameters parameters) {
        this.parameters = parameters;
    }

}
