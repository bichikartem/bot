--liquibase formatted sql

--changeset bstrdn:3
CREATE TABLE IF NOT EXISTS citations
(
    id varchar not null,
    body text not null
);

COMMENT ON TABLE citations IS 'Таблица для хранения фраз и текстовых блоков';
COMMENT ON COLUMN citations.id IS 'Идентификатор записи';
COMMENT ON COLUMN citations.body IS 'Текст';
--rollback DROP TABLE citations;