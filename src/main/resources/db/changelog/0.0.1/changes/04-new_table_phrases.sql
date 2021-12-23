--liquibase formatted sql
--changeset bichik:4

CREATE TABLE IF NOT EXISTS phrases
(
    id       uuid default gen_random_uuid() not null
        constraint text_pkey primary key,
    type varchar not null,
    text text not null
);

COMMENT ON TABLE phrases IS 'Таблица для хранения фраз и текстовых блоков';
COMMENT ON COLUMN phrases.id IS 'Идентификатор записи';
COMMENT ON COLUMN phrases.type IS 'Тип текстового блока';
COMMENT ON COLUMN phrases.text IS 'Текст';

--rollback DROP TABLE phrases;