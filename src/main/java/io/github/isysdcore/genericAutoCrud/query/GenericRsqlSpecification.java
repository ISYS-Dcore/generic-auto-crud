/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.isysdcore.genericAutoCrud.query;

import io.github.isysdcore.genericAutoCrud.utils.DateUtils;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.*;
import lombok.AllArgsConstructor;
import org.hibernate.metamodel.mapping.ordering.ast.PluralAttributePath;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 * @author domingos.fernando
 * @param <T>
 */
@AllArgsConstructor
public class GenericRsqlSpecification<T> implements Specification<T> {

    private final String property;
    private final ComparisonOperator operator;
    private final List<String> arguments;

    //    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query,
                                 CriteriaBuilder builder) {
        Path<String> propertyExpression = parsePropertyOneLevel(root);
//        Path<String> propertyExpression = parseProperty(root);
        List<Object> args = castArguments(propertyExpression);
        Object argument = args.get(0);
        query.distinct(true);

        switch (Objects.requireNonNull(RsqlSearchOperation.getSimpleOperator(operator))) {
            case EQUAL -> {
                if (propertyExpression instanceof PluralAttributePath && argument.equals("empty")) {
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

    private Path<String> parsePropertyOneLevel(Root<T> root) {
        Path<String> path;
        if (property.contains(".")) {
            // Nested properties
            String[] pathSteps = property.split("\\.");
            String step = pathSteps[0];
            path = root.get(step);
            for (int i = 1; i <= pathSteps.length - 1; i++) {
                path = path.get(pathSteps[i]);
            }
        } else {
            path = root.get(property);
        }
        return path;
    }

    // This method will help us diving deep into nested property using the dot convention
    // The originial tutorial did not have this, so it can only parse the shallow properties.
//    private Path<String> parseProperty(Root<T> root) {
//        Path<String> path;
//        if (property.contains(".")) {
//            // Nested properties
//            String[] pathSteps = property.split("\\.");
//            String step = pathSteps[0];
//            path = root.get(step);
//            From lastFrom = root;
//
//            for (int i = 1; i <= pathSteps.length - 1; i++) {
//                if (path instanceof PluralAttributePath) {
//                    PluralAttribute attr = ((PluralAttributePath) path).get;
//                    Join join = getJoin(attr, lastFrom);
//                    path = join.get(pathSteps[i]);
//                    lastFrom = join;
//                } else if (path instanceof SingularAttributePath) {
//                    SingularAttribute attr = ((SingularAttributePath) path).getAttribute();
//                    if (attr.getPersistentAttributeType() != Attribute.PersistentAttributeType.BASIC) {
//                        Join join = lastFrom.join(attr, JoinType.LEFT);
//                        path = join.get(pathSteps[i]);
//                        lastFrom = join;
//                    } else {
//                        path = path.get(pathSteps[i]);
//                    }
//                } else {
//                    path = path.get(pathSteps[i]);
//                }
//            }
//        } else {
//            path = root.get(property);
//        }
//        return path;
//    }

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
            } else {
                return arg;
            }
        }).collect(Collectors.toList());
    }
}
