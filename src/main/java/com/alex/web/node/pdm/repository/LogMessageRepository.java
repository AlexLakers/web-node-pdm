package com.alex.web.node.pdm.repository;


import com.alex.web.node.pdm.model.LogMessage;
import org.springframework.data.repository.CrudRepository;

/**
 * This interface contains only default methods for interaction app with database(table 'log_message').
 */

public interface LogMessageRepository extends CrudRepository<LogMessage, Long> { }
