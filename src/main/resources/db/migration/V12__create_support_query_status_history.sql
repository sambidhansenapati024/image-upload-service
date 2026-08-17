CREATE TABLE support_query_status_history (

    id BIGSERIAL PRIMARY KEY,

    support_query_id BIGINT NOT NULL,

    status VARCHAR(30) NOT NULL,

    changed_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_support_query_status_history_query
        FOREIGN KEY (support_query_id)
        REFERENCES support_queries(id)
        ON DELETE CASCADE

);