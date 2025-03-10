--liquibase formatted sql

--changeset lakers:1
INSERT INTO users(id, firstname, lastname, username, birth_day, password, provider)
VALUES (1, 'testUser', 'testUser', 'testUser@mail.com', '1993-01-21', '{noop}testUser123', 'DAO_LOCAL'),
       (2, 'testAdmin', 'testAdmin', 'testAdmin@mail.com', '1993-01-21', '{noop}testAdmin123', 'OAUTH2_GOOGLE');

--changeset lakers:2
INSERT INTO users_roles(user_id, name)
VALUES (1, 'USER'),
       (2, 'ADMIN');