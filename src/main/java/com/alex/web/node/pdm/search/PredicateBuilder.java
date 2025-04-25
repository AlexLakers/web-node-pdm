package com.alex.web.node.pdm.search;

import com.alex.web.node.pdm.service.SpecificationService;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * This class describes functional for creating one predicate by several predicates.
 * It is used for search data by several criteria.
 *
 * @see SpecificationService#findAll(SpecificationSearchDto) findAllSpecifications.
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PredicateBuilder {
    private List<Predicate> predicates=new ArrayList<>();

    public static PredicateBuilder builder() {
        return new PredicateBuilder();
    }
    public <T>PredicateBuilder add(T param, Function<T,Predicate> function) {
        if(param!=null) {
            predicates.add(function.apply(param));
        }
        return this;
    }
    public Predicate and(){

        return Optional.ofNullable(ExpressionUtils.allOf(predicates)).orElse(Expressions.TRUE);
    }
   /* Optional.ofNullable(ExpressionUtils.allOf(predicates))
            .orElseGet(() -> Expressions.asBoolean(true).isTrue());*/
    public Predicate or(){
        return Optional.ofNullable(ExpressionUtils.anyOf(predicates)).orElse(Expressions.TRUE);
    }

}
