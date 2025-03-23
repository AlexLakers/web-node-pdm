--liquibase formatted sql

--changeset lakers:1
INSERT INTO specification(id,code, amount, description, user_id)
VALUES (1,'testCode',1000,'desc',1),
       (2,'testCode1',1000,'desc',1);

--changeset lakers:2
INSERT INTO detail(id,name, amount, specification_id)
VALUES (1,'testDetail', 500,1),
       (2,'testDetail2', 600,1);