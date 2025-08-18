/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.isysdcore.genericAutoCrud.query.sql;

import io.github.isysdcore.genericAutoCrud.query.QuerySearchOperation;
import io.github.isysdcore.genericAutoCrud.utils.DateUtils;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.CollectionAttribute;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.MapAttribute;
import jakarta.persistence.metamodel.PluralAttribute;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.metamodel.mapping.ordering.ast.PluralAttributePath;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/// @author domingos.fernando
/// @param <T> Entity type that this specification will be applied to
/// This class implements a generic RSQL specification for JPA queries.
@AllArgsConstructor
@Slf4j
public class GenericRsqlSpec<T> implements Specification<T> {

    private final String property;
    private final ComparisonOperator operator;
    private final List<String> arguments;

    //    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query,
                                 CriteriaBuilder builder) {
        Path<String> propertyExpression = parseProperty(root);
        List<Object> args = castArguments(propertyExpression);
        Object argument = args.get(0);
        query.distinct(true);

        switch (Objects.requireNonNull(QuerySearchOperation.getSimpleOperator(operator))) {
            case EQUAL -> {
                if (propertyExpression.getModel() instanceof PluralAttribute && argument.equals("empty")) {
                    return builder.isEmpty(root.get(property));
                } else if (argument == null || argument.equals("null")) {
                    return builder.isNull(propertyExpression);
                } else if (argument instanceof String) {
                    return builder.like(builder.lower(propertyExpression),
                            argument.toString().replace('*', '%').toLowerCase());
                }  else {
                    return builder.equal(propertyExpression, argument);
                }
            }
            case NOT_EQUAL -> {
                if (propertyExpression instanceof PluralAttributePath && argument.equals("empty")) {
                    return builder.isNotEmpty(root.get(property));
                } else if (argument == null || argument.equals("null")) {
                    return builder.isNotNull(propertyExpression);
                } else if (argument instanceof String) {
                    return builder.notLike(builder.lower(propertyExpression),
                            argument.toString().replace('*', '%').toLowerCase());
                }  else {
                    return builder.notEqual(propertyExpression, argument);
                }
            }
            case GREATER_THAN -> {
                if (argument instanceof Date) {
                    return builder.greaterThan(root.get(property), (Date) argument);
                }
                return builder.greaterThan(propertyExpression,
                        argument.toString());
            }
            case GREATER_THAN_OR_EQUAL -> {
                if (argument instanceof Date) {
                    return builder.greaterThanOrEqualTo(root.get(property), (Date) argument);
                }
                return builder.greaterThanOrEqualTo(propertyExpression,
                        argument.toString());
            }
            case LESS_THAN -> {
                if (argument instanceof Date) {
                    return builder.lessThan(root.get(property), (Date) argument);
                }
                return builder.lessThan(propertyExpression,
                        argument.toString());
            }
            case LESS_THAN_OR_EQUAL -> {
                if (argument instanceof Date) {
                    return builder.lessThanOrEqualTo(root.get(property), (Date) argument);
                }
                return builder.lessThanOrEqualTo(propertyExpression,
                        argument.toString());
            }
            case IN -> {
                return propertyExpression.in(args);
            }
            case NOT_IN -> {
                return builder.not(propertyExpression.in(args));
            }
        }
        return null;
    }

    // This method will help us diving deep into nested property using the dot convention
    // The originial tutorial did not have this, so it can only parse the shallow properties.
    // New Version of dipper attribute in concern with hibernate 6
    private Path<String> parseProperty(Root<T> root) {
        Path<String> path;
        if (property.contains(".")) {
            // Handle nested properties
            String[] pathSteps = property.split("\\.");
            String step = pathSteps[0];
            path = root.get(step);
            From<?, ?> lastFrom = root;

            for (int i = 1; i < pathSteps.length; i++) {
                // Determine if the current path is a plural or singular attribute
                if (path.getModel() instanceof PluralAttribute) {
                    // Handle collection attributes
                    PluralAttribute<?, ?, ?> attr = (PluralAttribute<?, ?, ?>) path.getModel();
                    Join<?, ?> join = getJoin(attr, lastFrom);
                    path = join.get(pathSteps[i]);
                    lastFrom = join;
                } else if (path.getModel() instanceof SingularAttribute) {
                    // Handle singular attributes
                    SingularAttribute attr = (SingularAttribute<?, ?>) path.getModel();
                    if (attr.getPersistentAttributeType() != Attribute.PersistentAttributeType.BASIC) {
                        // Join non-basic types
                        Join<?, ?> join = lastFrom.join(attr, JoinType.LEFT);
                        path = join.get(pathSteps[i]);
                        lastFrom = join;
                    } else {
                        // Basic attribute, no join needed
                        path = path.get(pathSteps[i]);
                    }
                } else {
                    // Default path handling
                    path = path.get(pathSteps[i]);
                }
            }
        } else {
            // Single property, no nesting
            path = root.get(property);
        }
        return path;
    }

    private Join getJoin(PluralAttribute attr, From from) {
        return switch (attr.getCollectionType()) {
            case COLLECTION -> from.join((CollectionAttribute) attr);
            case SET -> from.join((SetAttribute) attr);
            case LIST -> from.join((ListAttribute) attr);
            case MAP -> from.join((MapAttribute) attr);
            default -> null;
        };
    }

    private List<Object> castArguments(Path<?> propertyExpression) {
        Class<?> type = propertyExpression.getJavaType();
        return arguments.stream().map(arg -> {
            if (type.equals(Integer.class)) {
                return Integer.valueOf(arg);
            } else if (type.equals(Long.class)) {
                return Long.valueOf(arg);
            } else if (type.equals(Date.class)) {
                return DateUtils.strToDate(arg);
            } else if (type.equals(Byte.class)) {
                return Byte.valueOf(arg);
            } else if (type.equals(Boolean.class)) {
                return Boolean.valueOf(arg);
            } else if (type.equals(Double.class)) {
                return Double.valueOf(arg);
            } else if (type.equals(Short.class)) {
                return Short.valueOf(arg);
            }else if (type.equals(UUID.class)) {
                return UUID.fromString(arg);
            } else if (type.equals(Object.class)) {
                return getClassTypeFromValue(arg);
            }else {
                return arg;
            }
        }).collect(Collectors.toList());
    }

    private Object getClassTypeFromValue(String arg){
        if( arg.matches("\\d+(\\.\\d+)?")){
            // Check if it is an Integer
            try {
                return Integer.parseInt(arg);
            } catch (NumberFormatException e) {
                // Not an Integer
                log.info("Object value is not parsable to Integer: {}", arg);
            }
            try {
                return Long.parseLong(arg);
            } catch (NumberFormatException e) {
                // Not a Long
                log.info("Object value is not parsable to Long: {}", arg);
            }
            // Check if it is a Double
            try {
                return Double.parseDouble(arg);
            } catch (NumberFormatException e) {
                // Not a Double
                log.info("Object value is not parsable to Double: {}", arg);
            }
        }else if (arg.equalsIgnoreCase("true") || arg.equalsIgnoreCase("false")) {
            return Boolean.valueOf(arg);
        } else {
            // Check if it is a UUID
            try {
                return UUID.fromString(arg);
            } catch (IllegalArgumentException e) {
                // Not a UUID
                log.info("Object value is not parsable to UUID: {}", arg);
            }
        }
        // Default: String
        return arg;
    }
}
