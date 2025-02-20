--liquibase formatted sql

--changeset alex:1
INSERT INTO users(id,firstname, lastname, username, birth_day,password,provider)
VALUES(1,'admin','admin','admin@mail.com','1993-11-01','{noop}admin','DAO_LOCAL');
--changeset lakers:2
INSERT INTO users_roles(user_id, name) VALUES(1,'ADMIN');

