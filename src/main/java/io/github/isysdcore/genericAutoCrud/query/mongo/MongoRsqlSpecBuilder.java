package io.github.isysdcore.genericAutoCrud.query.mongo;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.LogicalNode;
import cz.jirutka.rsql.parser.ast.LogicalOperator;
import cz.jirutka.rsql.parser.ast.Node;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/// @author domingos.fernando
/// @param <T> The Entity class that represents the MongoDB entity
/// This class is responsible for building MongoDB Criteria from RSQL query nodes.
/// It handles logical operations (AND, OR) and comparison operations, converting them into appropriate Mongo
public class MongoRsqlSpecBuilder<T> {

    private final Class<T> entityClass;
    private final MongoPropertyResolver resolver;

    public MongoRsqlSpecBuilder(Class<T> entityClass, MongoPropertyResolver resolver) {
        this.entityClass = entityClass;
        this.resolver = resolver;
    }

        /**
         * Creates a Criteria from an RSQL query represented by a Node.
         *
         * @param node The RSQL query node
         * @return A Criteria that can be used in MongoDB queries, or an empty Criteria if the node is not recognized.
         */
        public Criteria createCriteria(Node node) {
            if (node instanceof LogicalNode) {
                return createCriteria((LogicalNode) node);
            }
            if (node instanceof ComparisonNode) {
                return createCriteria((ComparisonNode) node);
            }
            return new Criteria(); // Empty criteria fallback
        }

        /**
         * Creates a Criteria from a LogicalNode, which can be an AND or OR operation.
         *
         * @param logicalNode The LogicalNode representing the logical operation
         * @return A Criteria that combines the criteria of the child nodes using the logical operator.
         */
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

        /**
         * Creates a Criteria from a ComparisonNode, which represents a comparison operation.
         *
         * @param node The ComparisonNode representing the comparison operation
         * @return A Criteria that represents the comparison operation.
         */
        public Criteria createCriteria(ComparisonNode node) {
            return new MongoRsqlSpec(node.getSelector(), node.getOperator(), node.getArguments(),
                    entityClass,
                    resolver).toCriteria();
        }
}
