--liquibase formatted sql

--changeset lakers:1
ALTER TABLE users
    ADD COLUMN modified_at TIMESTAMP;

ALTER TABLE users
    ADD COLUMN modified_by VARCHAR(256);

ALTER TABLE specification
    ADD COLUMN modified_at TIMESTAMP;

ALTER TABLE specification
    ADD COLUMN modified_by VARCHAR(256);

ALTER TABLE detail
    ADD COLUMN modified_at TIMESTAMP;

ALTER TABLE detail
    ADD COLUMN modified_by VARCHAR(256);