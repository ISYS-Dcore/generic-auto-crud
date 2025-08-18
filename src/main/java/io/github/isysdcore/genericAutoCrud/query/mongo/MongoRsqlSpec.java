package io.github.isysdcore.genericAutoCrud.query.mongo;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import io.github.isysdcore.genericAutoCrud.query.QuerySearchOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/// @author domingos.fernando
/// @param <T> The Entity class that represents the MongoDB entity
/// This class is responsible for converting RSQL specifications into MongoDB Criteria.
/// It handles various comparison operators and argument types, converting them into appropriate MongoDB queries.
public class MongoRsqlSpec<T> {

    private final String property;
    private final ComparisonOperator operator;
    private final List<String> arguments;
    private final Class<T> entityClass;
    private final MongoPropertyResolver resolver;

    /**
     * Constructs a MongoRsqlSpec with the specified property, operator, arguments, entity class, and property resolver.
     *
     * @param property  The property to filter on.
     * @param operator  The comparison operator to use.
     * @param arguments The list of arguments for the comparison.
     * @param entityClass The class of the entity being queried.
     * @param resolver  The resolver to determine the type of the property.
     */
    public MongoRsqlSpec(String property, ComparisonOperator operator, List<String> arguments,
                         Class<T> entityClass, MongoPropertyResolver resolver) {
        this.property = property;
        this.operator = operator;
        this.arguments = arguments;
        this.entityClass = entityClass;
        this.resolver = resolver;
    }

    /**
     * Converts the RSQL specification into a MongoDB Criteria object.
     *
     * @return A Criteria object that can be used in MongoDB queries.
     */
    public Criteria toCriteria() {
        List<Object> args = castArguments();
        Object arg = args.isEmpty() ? null : args.get(0);

        QuerySearchOperation operation = QuerySearchOperation.getSimpleOperator(operator);
        Criteria criteria = Criteria.where(property);

        switch (operation) {
            case EQUAL -> {
                if ("null".equalsIgnoreCase(String.valueOf(arg))) {
                    return criteria.is(null);
                }
                if (arg instanceof String str && str.contains("*")) {
                    String regex = str.replace("*", ".*");
                    return criteria.regex("^" + regex + "$", "i");
                }
                return criteria.is(arg);
            }
            case NOT_EQUAL -> {
                if ("null".equalsIgnoreCase(String.valueOf(arg))) {
                    return criteria.ne(null);
                }
                if (arg instanceof String str && str.contains("*")) {
                    String regex = str.replace("*", ".*");
                    return new Criteria().not().regex("^" + regex + "$", "i");
                }
                return criteria.ne(arg);
            }
            case GREATER_THAN -> {
                return criteria.gt(arg);
            }
            case GREATER_THAN_OR_EQUAL -> {
                return criteria.gte(arg);
            }
            case LESS_THAN -> {
                return criteria.lt(arg);
            }
            case LESS_THAN_OR_EQUAL -> {
                return criteria.lte(arg);
            }
            case IN -> {
                return criteria.in(args);
            }
            case NOT_IN -> {
                return criteria.nin(args);
            }
            default -> {
                return new Criteria(); // fallback to match all
            }
        }
    }
    /**
     * Converts the arguments to the appropriate types based on the property type.
     *
     * @return A list of arguments cast to their appropriate types.
     */
    private List<Object> castArguments() {
        Class<?> targetType = resolver.resolvePropertyType(entityClass, property).orElse(String.class);
        return arguments.stream().map(arg -> convert(arg, targetType)).toList();
    }

    /**
     * Converts a string argument to the specified type.
     *
     * @param arg  The string argument to convert.
     * @param type The target type to convert to.
     * @return The converted object, or the original string if conversion fails.
     */
    private Object convert(String arg, Class<?> type) {
        try {
            if (type.equals(Integer.class)) return Integer.valueOf(arg);
            if (type.equals(Long.class)) return Long.valueOf(arg);
            if (type.equals(Double.class)) return Double.valueOf(arg);
            if (type.equals(Boolean.class)) return Boolean.valueOf(arg);
            if (type.equals(java.util.Date.class)) return java.sql.Date.valueOf(arg);
            if (type.equals(java.util.UUID.class)) return java.util.UUID.fromString(arg);
            return arg;
        } catch (Exception e) {
            return arg;
        }
    }

    /**
     * Parses a string argument to its appropriate type.
     *
     * @param arg The string argument to parse.
     * @return The parsed object, or the original string if parsing fails.
     */
    private Object parseValue(String arg) {
        try {
            if (arg.matches("\\d+")) return Integer.valueOf(arg);
            if (arg.matches("\\d+\\.\\d+")) return Double.valueOf(arg);
            if (arg.equalsIgnoreCase("true") || arg.equalsIgnoreCase("false")) return Boolean.valueOf(arg);
            if (arg.matches("\\d{4}-\\d{2}-\\d{2}")) return java.sql.Date.valueOf(arg);
            return UUID.fromString(arg); // try UUID
        } catch (Exception ignored) {}
        return arg; // fallback to string
    }
}
