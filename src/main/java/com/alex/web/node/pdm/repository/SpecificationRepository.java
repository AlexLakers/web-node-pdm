package com.alex.web.node.pdm.repository;


import com.alex.web.node.pdm.model.Specification;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.List;
import java.util.Optional;

/**
 * This interface contains all the necessary methods for interaction app with database(table 'specifications').
 */

public interface SpecificationRepository extends
        JpaRepository<Specification, Long>,
        QuerydslPredicateExecutor<Specification>,
        RevisionRepository<Specification, Long, Integer> {
    Optional<Specification> findByCode(String code);

    /*@Query(value = "SELECT u FROM public.specification s " +
            "LEFT JOIN public.detail d ON s.id=d.specification_id" +
            " WHERE user_id=:userId",nativeQuery = true)*/
    @EntityGraph(attributePaths = "details", type = EntityGraph.EntityGraphType.FETCH)
    List<Specification> findAllByUserId(Long userId);

    Page<Specification> findAll(Predicate predicate, Pageable page);

    @Query(value = "SELECT EXISTS(SELECT 1  FROM specification s WHERE s.code=:code) LIMIT 1", nativeQuery = true)
    boolean existsByCode(String code);
}
