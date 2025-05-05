package io.github.isysdcore.genericAutoCrud.query.mongo;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * @author domingos.fernando
 */
public class MongoRsqlVisitor<T> implements RSQLVisitor<Criteria, Void> {

    private final MongoRsqlSpecBuilder<T> builder;

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
