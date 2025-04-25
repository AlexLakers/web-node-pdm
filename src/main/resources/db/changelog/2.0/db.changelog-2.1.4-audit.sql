--liquibase formatted sql

--changeset lakers:1
ALTER TABLE specification_aud
    ADD COLUMN description VARCHAR(255)