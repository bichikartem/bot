--liquibase formatted sql
--changeset bichik:6

DROP TABLE IF EXISTS citations;

--rollback CREATE TABLE IF NOT EXISTS citations (id varchar not null, body text not null);