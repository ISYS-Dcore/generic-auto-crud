/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.isysdcore.genericAutoCrud.query;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.RSQLOperators;

import java.util.Arrays;

/**
 *
 * @author domingos.fernando
 */
public enum QuerySearchOperation
{
    EQUAL(RSQLOperators.EQUAL),
    NOT_EQUAL(RSQLOperators.NOT_EQUAL),
    GREATER_THAN(RSQLOperators.GREATER_THAN),
    GREATER_THAN_OR_EQUAL(RSQLOperators.GREATER_THAN_OR_EQUAL),
    LESS_THAN(RSQLOperators.LESS_THAN),
    LESS_THAN_OR_EQUAL(RSQLOperators.LESS_THAN_OR_EQUAL),
    IN(RSQLOperators.IN),
    NOT_IN(RSQLOperators.NOT_IN),
    REGEX(new ComparisonOperator("=re=", false)),
    EXISTS(new ComparisonOperator("=ex=", false));

    private ComparisonOperator operator;

    private QuerySearchOperation(ComparisonOperator operator)
    {
        this.operator = operator;
    }

    public static QuerySearchOperation getSimpleOperator(ComparisonOperator operator)
    {
        return Arrays.stream(values()).filter(value -> value.operator.equals(operator)).findFirst().orElse(null);
    }

    public ComparisonOperator getOperator()
    {
        return operator;
    }
}
