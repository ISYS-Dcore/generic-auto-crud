package io.github.isysdcore.genericAutoCrud.test.utils;

import io.github.isysdcore.genericAutoCrud.generics.GenericBaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

///**
// * @author domingos.fernando
// * @created 26/12/2024 - 16:28
// * @project Generic Auto CRUD
// */
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
            Logger.getLogger(UtilServiceTests.class.getName()).log(Level.SEVERE, null, ex);
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

    public <T extends GenericBaseEntity<?>> Field[] collectEntityFields(T entity) {
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
        }
        return fields;
    }

    public <T extends GenericBaseEntity<?>> Object getValueFromField(T entityToGetValue, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = entityToGetValue.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(entityToGetValue);
    }

    public <T extends GenericBaseEntity<?>> Field getFieldFromEntity(T entityToSetValue, String fieldName)   {
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

    public <T extends GenericBaseEntity<?>> Class<?> getIdClass(T entityToSetValue)   {
        try{
            return entityToSetValue.getId().getClass();
        } catch (Exception e) {
            log.error("Was unable to get ID class for entity={}", entityToSetValue.getClass().getName());
            throw new RuntimeException(e);
        }
    }

    public static void setValueByClassType(Field field, Object entity) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException, NoSuchFieldException {
        field.setAccessible(true);
        Class<?> fieldType = field.getType();
        field.set(entity, generateValueFromDatatype(fieldType));
    }

    public static Object generateValueFromDatatype(Class<?> fieldType) {
        Random random = new Random();
        if (fieldType.equals(String.class)) {
            return RandomStringUtils.secure().nextAlphanumeric(10);
        } else if (fieldType.equals(UUID.class)) {
            return UUID.randomUUID();
        } else if (fieldType.equals(Double.class) || fieldType.equals(double.class)) {
            return random.nextDouble();
        } else if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
            return random.nextLong();
        } else if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
            return random.nextInt();
        } else if (fieldType.equals(LocalDateTime.class)) {
            return LocalDateTime.now();
        } else if (fieldType.equals(Instant.class)) {
            return Instant.now();
        } else if (fieldType.equals(Date.class)) {
            return Calendar.getInstance().getTime();
        } else if (fieldType.equals(Short.class) || fieldType.equals(short.class)) {
            return (short) random.nextInt(Short.MAX_VALUE + 1);
        } else if (fieldType.equals(Boolean.class) || fieldType.equals(boolean.class)) {
            return false;
        } else if (fieldType.equals(Byte.class) || fieldType.equals(byte.class)) {
            return (byte) random.nextInt(128);
        } else if (fieldType.isEnum()) {
            Object[] enumConstants = fieldType.getEnumConstants();
            if (enumConstants.length > 0) {
                return enumConstants[random.nextInt(enumConstants.length)];
            }
            return null;
        }else if (Map.class.isAssignableFrom(fieldType)){
            return new HashMap<>();
        } else if (Collection.class.isAssignableFrom(fieldType)) {
            return new ArrayList<>();
        } else {
            try {
                Constructor<?> constructor = fieldType.getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor.newInstance();
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                log.error(ex.getMessage(), ex);
                throw new RuntimeException("Was unable to generate value to field of type: " + fieldType.getName(), ex);
            }
        }
    }
}
