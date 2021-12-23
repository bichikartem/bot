--liquibase formatted sql
--changeset bichik:5

INSERT INTO phrases(type, text)
SELECT 'QUOTE', body FROM citations;

--TRUNCATE TABLE phrases;