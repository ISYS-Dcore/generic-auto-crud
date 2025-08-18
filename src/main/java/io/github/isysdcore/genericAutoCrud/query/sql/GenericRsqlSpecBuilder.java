/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.isysdcore.genericAutoCrud.query.sql;

import cz.jirutka.rsql.parser.ast.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/// @author domingos.fernando
/// @param <T> The entity type for which the specification is created
/// This class is responsible for building a JPA Specification from an RSQL query.
public class GenericRsqlSpecBuilder<T>
{

    /// Creates a Specification from an RSQL query represented by a Node.
    /// @param node The RSQL query node
    /// @return A Specification that can be used in JPA queries, or null if the node is not recognized.
    /// @throws IllegalArgumentException if the node type is not supported
    public Specification<T> createSpecification(Node node)
    {
        if (node instanceof LogicalNode) {
            return createSpecification((LogicalNode) node);
        }
        if (node instanceof ComparisonNode) {
            return createSpecification((ComparisonNode) node);
        }
        return null;
    }

    /// Creates a Specification from a LogicalNode, which can be an AND or OR operation.
    /// @param logicalNode The LogicalNode representing the logical operation
    /// @return A Specification that combines the specifications of the child nodes using the logical operator.
    /// @throws IllegalArgumentException if the logical node has no children
    /// or if the operator is not recognized
    public Specification<T> createSpecification(LogicalNode logicalNode)
    {
        List<Specification> specs = logicalNode.getChildren()
                .stream()
                .map(node -> createSpecification(node))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Specification<T> result = specs.get(0);
        if (logicalNode.getOperator() == LogicalOperator.AND) {
            for (int i = 1; i < specs.size(); i++) {
                result = Specification.where(result).and(specs.get(i));
            }
        }
        else if (logicalNode.getOperator() == LogicalOperator.OR) {
            for (int i = 1; i < specs.size(); i++) {
                result = Specification.where(result).or(specs.get(i));
            }
        }

        return result;
    }

    /// Creates a Specification from a ComparisonNode, which represents a comparison operation.
    /// @param comparisonNode The ComparisonNode representing the comparison operation
    /// @return A Specification that applies the comparison operation to the specified field with the given arguments
    /// @throws IllegalArgumentException if the comparison node is not recognized
    /// or if the operator is not supported
    public Specification<T> createSpecification(ComparisonNode comparisonNode)
    {
        Specification<T> result = Specification.where(
                new GenericRsqlSpec<T>(
                        comparisonNode.getSelector(),
                        comparisonNode.getOperator(),
                        comparisonNode.getArguments()
                )
        );
        return result;
    }

}
