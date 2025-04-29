package io.github.isysdcore.genericAutoCrud.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author domingos.fernando
 *
 */
@Slf4j
public class CsvUtils<T> {

    public List<T> parseCsv(MultipartFile file, Class<T> clazz) {
        Set<String> entityFields = getEntityFields(clazz);
        log.info("Entity fields: {}", entityFields);
        List<T> result = new ArrayList<>();
        try (Reader reader = new InputStreamReader(file.getInputStream());
             CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader)) {

            Set<String> csvHeaders = parser.getHeaderMap().keySet();
            // Validate headers
            List<String> invalidHeaders = csvHeaders.stream()
                    .filter(header -> entityFields.stream().noneMatch(f -> f.equalsIgnoreCase(header)))
                    .toList();

            if (!invalidHeaders.isEmpty()) {
                throw new IllegalArgumentException("Invalid headers: " + invalidHeaders);
            }
            // Map records to entity instances
            for (CSVRecord record : parser) {
                T entity = clazz.getDeclaredConstructor().newInstance();

                for (String header : csvHeaders) {
                    String value = record.get(header);
                    Field field = getFieldIgnoreCase(clazz, header);
                    if (field != null) {
                        field.setAccessible(true);
                        Object typedValue = convertValue(field.getType(), value);
                        field.set(entity, typedValue);
                    }
                }
                result.add(entity);
            }
            return result;

        } catch (IOException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static Set<String> getEntityFields(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toSet());
    }


    private Field getFieldIgnoreCase(Class<?> clazz, String fieldName) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> f.getName().equalsIgnoreCase(fieldName))
                .findFirst().orElse(null);
    }

    private Object convertValue(Class<?> type, String value) {
        if (type == String.class) return value;
        if (type == int.class || type == Integer.class) return Integer.parseInt(value);
        if (type == long.class || type == Long.class) return Long.parseLong(value);
        if (type == double.class || type == Double.class) return Double.parseDouble(value);
        if (type == boolean.class || type == Boolean.class) return Boolean.parseBoolean(value);
        return value; // fallback as String
    }
}
