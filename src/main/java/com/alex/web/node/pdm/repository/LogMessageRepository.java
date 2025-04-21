package com.alex.web.node.pdm.repository;


import com.alex.web.node.pdm.model.LogMessage;
import org.springframework.data.repository.CrudRepository;

public interface LogMessageRepository extends CrudRepository<LogMessage, Long> { }
