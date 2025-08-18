package io.github.isysdcore.genericAutoCrud.query.mongo;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import org.springframework.data.mongodb.core.query.Criteria;

/// @author domingos.fernando
/// @param <T> The Entity class that represents the MongoDB entity
/// This class implements a custom RSQL visitor that builds MongoDB Criteria
/// from RSQL query nodes. It uses a MongoRsqlSpecBuilder to create the criteria
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
