package com.alex.web.node.pdm.repository;

import com.alex.web.node.pdm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Optional;

/**
 * This interface contains all the necessary methods for interaction app with database(table 'users').
 */

public interface UserRepository extends
        JpaRepository<User, Long>,
        RevisionRepository<User,Long,Integer> {

    /*  @EntityGraph(attributePaths = {"roles"},type = EntityGraph.EntityGraphType.FETCH)*/
    @Query("select u from User u " +
            "join fetch u.roles r where u.id=:id")
    Optional<User> findUserById(Long id);

    Optional<User> findByUsername(String username);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM users u " +
            "WHERE UPPER(u.username)=UPPER(:username))" +
            " LIMIT 1", nativeQuery = true)
    boolean existsUserByUsername(String username);
}
