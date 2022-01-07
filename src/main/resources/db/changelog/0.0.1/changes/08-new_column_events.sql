--liquibase formatted sql
--changeset bstrd:8

ALTER TABLE events
    ADD COLUMN IF NOT EXISTS id uuid default gen_random_uuid()
        constraint events_pkey primary key;
COMMENT ON COLUMN events.id IS 'Идентификатор';

--rollback ALTER TABLE events DROP COLUMN id;