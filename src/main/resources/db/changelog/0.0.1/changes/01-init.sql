--liquibase formatted sql

--changeset bstrdn:1
CREATE TABLE IF NOT EXISTS users
(
    id        varchar not null
    constraint user_pkey primary key,
    username  varchar,
    first_name varchar,
    last_name  varchar
);
--rollback DROP TABLE users;