package io.github.isysdcore.genericAutoCrud.query.mongo;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.LogicalNode;
import cz.jirutka.rsql.parser.ast.LogicalOperator;
import cz.jirutka.rsql.parser.ast.Node;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author domingos.fernando
 */
public class MongoRsqlSpecBuilder<T> {

    private final Class<T> entityClass;
    private final MongoPropertyResolver resolver;

    public MongoRsqlSpecBuilder(Class<T> entityClass, MongoPropertyResolver resolver) {
        this.entityClass = entityClass;
        this.resolver = resolver;
    }

        public Criteria createCriteria(Node node) {
            if (node instanceof LogicalNode) {
                return createCriteria((LogicalNode) node);
            }
            if (node instanceof ComparisonNode) {
                return createCriteria((ComparisonNode) node);
            }
            return new Criteria(); // Empty criteria fallback
        }

        public Criteria createCriteria(LogicalNode logicalNode) {
            List<Criteria> childCriteria = logicalNode.getChildren().stream()
                    .map(this::createCriteria)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (logicalNode.getOperator() == LogicalOperator.AND) {
                return new Criteria().andOperator(childCriteria.toArray(new Criteria[0]));
            } else if (logicalNode.getOperator() == LogicalOperator.OR) {
                return new Criteria().orOperator(childCriteria.toArray(new Criteria[0]));
            }

            return new Criteria();
        }

        public Criteria createCriteria(ComparisonNode node) {
            return new MongoRsqlSpec(node.getSelector(), node.getOperator(), node.getArguments(),
                    entityClass,
                    resolver).toCriteria();
        }
}
