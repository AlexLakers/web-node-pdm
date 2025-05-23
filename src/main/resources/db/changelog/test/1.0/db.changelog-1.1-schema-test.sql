--liquibase formatted sql

--changeset alex:1
CREATE table log_message(
                            id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                            created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                            message VARCHAR(1024) NOT NULL,
                            CONSTRAINT pk_log_message_id PRIMARY KEY (id));
--rollback DROP TABLE log_message;