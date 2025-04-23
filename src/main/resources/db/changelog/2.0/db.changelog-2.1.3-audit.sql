--liquibase formatted sql

--changeset lakers:1
CREATE TABLE IF NOT EXISTS specification_aud
(
    id      BIGINT,
    rev     INT REFERENCES revision (id),
    revtype SMALLINT,
    code    VARCHAR(255),
    amount  INT,
    user_id BIGINT
);
--rollback DROP TABLE specification_aud;

--changeset lakers:2
CREATE TABLE IF NOT EXISTS detail_aud
(
    id               BIGINT,
    name             VARCHAR(255),
    specification_id BIGINT
);
--rollback DROP TABLE detail_aud;