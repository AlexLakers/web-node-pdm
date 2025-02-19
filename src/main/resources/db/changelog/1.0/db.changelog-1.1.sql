--liquibase formatted sql

--changeset alex:1
ALTER TABLE users ADD password VARCHAR(255);

--changeset alex:2
ALTER TABLE users
    ADD COLUMN provider VARCHAR(128)




