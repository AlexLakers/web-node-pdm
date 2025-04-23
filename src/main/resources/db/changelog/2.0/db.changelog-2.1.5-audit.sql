--liquibase formatted sql

--changeset lakers:1
ALTER TABLE users
    ADD COLUMN created_at TIMESTAMP;

ALTER TABLE  users
    ADD COLUMN created_by VARCHAR(256);

ALTER TABLE specification
    ADD COLUMN created_at TIMESTAMP;

ALTER TABLE specification
    ADD COLUMN created_by VARCHAR(256);

ALTER TABLE detail
    ADD COLUMN created_at TIMESTAMP;

ALTER TABLE detail
    ADD COLUMN created_by VARCHAR(256);