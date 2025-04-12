package com.alex.web.node.pdm.repository.detail;

import com.alex.web.node.pdm.model.Detail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetailRepository extends JpaRepository<Detail, Long>,
        DetailJdbcRepository{

}
