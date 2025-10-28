package io.github.isysdcore.genericAutoCrud.utils;

import io.github.isysdcore.genericAutoCrud.generics.GenericEntity;
import io.github.isysdcore.genericAutoCrud.generics.sql.GenericRestServiceAbstract;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author domingos.fernando
 * @created 26/12/2024 - 16:28
 * @project Generic Auto CRUD
 */
@Component
@Slf4j
public class UtilServiceTests {


    public Object prepareSimpleEntity(Object entityToPrepare){
        try {
            Object newEntityInstance = entityToPrepare.getClass().getDeclaredConstructor().newInstance();
            List<Field> fieldList = Arrays.asList(newEntityInstance.getClass().getDeclaredFields());
            fieldList.forEach(entityField -> {
                entityField.setAccessible(true);
                try {
                    entityField.set(newEntityInstance, generateValueByFieldType(entityField));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
            log.info("Object fulfilled with data: {}", newEntityInstance);
            return newEntityInstance;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException |
                 SecurityException | IllegalArgumentException ex) {
            Logger.getLogger(GenericRestServiceAbstract.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private Object generateValueByFieldType(Field field){
        if(!field.getName().equalsIgnoreCase("id")){
           if(field.getType() == String.class){
               return RandomStringUtils.secure().nextAlphanumeric(10);
           } else if (field.getType() == Integer.class || field.getType() == int.class) {
               return new Random().nextInt();
           } else if (field.getType() == Long.class || field.getType() == long.class) {
               return new Random().nextLong();
           } else if (field.getType() == UUID.class) {
               return UUID.randomUUID();
           } else if (field.getType() == Date.class) {
               return Calendar.getInstance().getTime();
           }else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
               return new Random().nextBoolean();
           }
        }
       return null;
    }

    public <T extends GenericEntity<?>> Field[] collectEntityFields(T entity) {
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
        }
        return fields;
    }

    public <T extends GenericEntity<?>> Object getValueFromField(T entityToGetValue, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = entityToGetValue.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(entityToGetValue);
    }

    public <T extends GenericEntity<?>> Field getFieldFromEntity(T entityToSetValue, String fieldName)   {
        Field  field = null;
        try{
            field = entityToSetValue.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
        } catch (NoSuchFieldException  e) {
            log.warn("No field founded named={} founded on entity={}, trying on super class={}", fieldName, entityToSetValue.getClass().getName(), entityToSetValue.getClass().getSuperclass().getName());
            try{
                field = entityToSetValue.getClass().getSuperclass().getDeclaredField(fieldName);
                field.setAccessible(true);
            } catch (NoSuchFieldException ex) {
                log.error("Was unable to get field with name={}", fieldName);
                throw new RuntimeException(ex);
            }
        }
        return field;
    }

    public void setValueByClassType(Field field, Object entity) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        Random random = new Random();
        field.setAccessible(true);
        if(field.get(entity) instanceof String){
            field.set(entity, RandomStringUtils.secure().nextAlphanumeric(10));
        }else if(field.get(entity) instanceof UUID){
            field.set(entity, UUID.randomUUID());
        }else if(field.get(entity) instanceof Double){
            field.set(entity, random.nextDouble());
        }else if(field.get(entity) instanceof Long){
            field.set(entity, random.nextLong());
        }else if(field.get(entity) instanceof Integer){
            field.set(entity, random.nextInt());
        } else if(field.get(entity) instanceof Instant){
            field.set(entity, Instant.now());
        }else if(field.get(entity) instanceof Date){
            field.set(entity, Calendar.getInstance().getTime());
        }else if(field.get(entity) instanceof Short){
            field.set(entity, random.nextInt(Short.MAX_VALUE + 1));
        }else if(field.get(entity) instanceof Boolean){
            field.set(entity, false);
        }else if(field.get(entity) instanceof Byte){
            field.set(entity, random.nextInt(128));
        }else {
            Constructor<?> constructor = field.getClass().getDeclaredConstructor();
            Object newInstance = constructor.newInstance();
            field.set(entity, newInstance);
        }
    }
}
