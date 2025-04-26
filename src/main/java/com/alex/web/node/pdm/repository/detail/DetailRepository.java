package com.alex.web.node.pdm.repository.detail;

import com.alex.web.node.pdm.model.Detail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

public interface DetailRepository extends
        JpaRepository<Detail, Long>,
        DetailJdbcRepository,
        RevisionRepository<Detail, Long, Integer> {

}
