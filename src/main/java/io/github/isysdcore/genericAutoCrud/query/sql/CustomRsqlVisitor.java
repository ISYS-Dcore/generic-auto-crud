/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.isysdcore.genericAutoCrud.query.sql;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import org.springframework.data.jpa.domain.Specification;

/**
 * Custom RSQL visitor that converts RSQL query nodes into JPA {@link org.springframework.data.jpa.domain.Specification}.
 *
 * <p>This visitor traverses the RSQL abstract syntax tree and delegates the
 * construction of JPA Specifications to {@link GenericRsqlSpecBuilder}.</p>
 *
 * <p>It supports logical operations (AND, OR) and comparison operators, enabling
 * dynamic query generation for JPA-based persistence layers.</p>
 *
 * @param <T> the entity type representing the JPA persistence model
 *
 * @author domingos.fernando
 */
public class CustomRsqlVisitor<T> implements RSQLVisitor<Specification<T>, Void> {

    private final GenericRsqlSpecBuilder<T> builder;

    public CustomRsqlVisitor() {
        builder = new GenericRsqlSpecBuilder<>();
    }

    @Override
    public Specification<T> visit(AndNode node, Void param) {
        return builder.createSpecification(node);
    }

    @Override
    public Specification<T> visit(OrNode node, Void param) {
        return builder.createSpecification(node);
    }

    @Override
    public Specification<T> visit(ComparisonNode node, Void params) {
        return builder.createSpecification(node);
    }

}
