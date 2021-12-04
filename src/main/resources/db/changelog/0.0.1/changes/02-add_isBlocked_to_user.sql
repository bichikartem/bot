--liquibase formatted sql

--changeset bstrdn:2
ALTER TABLE users ADD COLUMN IF NOT EXISTS is_blocked BOOLEAN NOT NULL DEFAULT false;
--rollback ALTER TABLE users DROP COLUMN is_blocked;