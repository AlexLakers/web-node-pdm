--liquibase formatted sql

--changeset lakers:1
CREATE TABLE IF NOT EXISTS revision
(
    id        BIGSERIAL PRIMARY KEY,
    timestamp BIGINT NOT NULL
);
--rollback DROP TABLE revision;

--changeset lakers:2
CREATE TABLE IF NOT EXISTS users_aud
(
    id        BIGINT,
    rev       INT REFERENCES revision (id),
    revtype   SMALLINT,
    firstname VARCHAR(255),
    lastname  VARCHAR(255),
    username  VARCHAR(255),
    birth_day DATE,
    password  VARCHAR(255),
    provider  VARCHAR(128)
)
--rollback DROP TABLE users_aud;