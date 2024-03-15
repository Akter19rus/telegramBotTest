-- liquibase formatted sql

-- changeset akter:1
create table notification_task
(
    id            bigserial primary key,
    chat_id       bigint,
    message       text,
    date_and_time TIMESTAMP
);