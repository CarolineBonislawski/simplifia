CREATE TABLE event_store (
    id uuid primary key,
    aggregate_id uuid,
    event_type text,
    event_data json,
    version bigint,
    occurred_at date,
    created_at date
);

CREATE INDEX event_store_aggregate_id_index ON event_store(aggregate_id);