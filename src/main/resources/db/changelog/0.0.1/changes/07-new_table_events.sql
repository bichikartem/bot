--liquibase formatted sql
--changeset bichik:7

CREATE TABLE IF NOT EXISTS events
(
    user_id varchar not null
        references users(id),
    events_type varchar not null,
    count integer
);

--rollback DROP TABLE events;