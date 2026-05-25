package io.github.isysdcore.genericAutoCrud.query.mongo;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Custom RSQL visitor that converts RSQL query nodes into MongoDB {@link org.springframework.data.mongodb.core.query.Criteria}.
 *
 * <p>This visitor traverses the RSQL abstract syntax tree and delegates the
 * construction of MongoDB query criteria to {@link MongoRsqlSpecBuilder}.</p>
 *
 * <p>It supports logical (AND, OR) and comparison operations, enabling the
 * translation of RSQL expressions into executable MongoDB queries.</p>
 *
 * @param <T> the entity type representing the MongoDB document
 *
 * @author domingos.fernando
 */
public class MongoRsqlVisitor<T> implements RSQLVisitor<Criteria, Void> {

    private final MongoRsqlSpecBuilder<T> builder;

    /**
     * Constructs a MongoRsqlVisitor with the specified entity class and property resolver.
     *
     * @param entityClass The class of the entity being queried.
     * @param resolver    The resolver to determine the type of the property.
     */
    public MongoRsqlVisitor(Class<T> entityClass, MongoPropertyResolver resolver) {
        this.builder = new MongoRsqlSpecBuilder<>(entityClass, resolver);
    }


    @Override
    public Criteria visit(AndNode node, Void param) {
        return builder.createCriteria(node);
    }

    @Override
    public Criteria visit(OrNode node, Void param) {
        return builder.createCriteria(node);
    }

    @Override
    public Criteria visit(ComparisonNode node, Void param) {
        return builder.createCriteria(node);
    }
}
