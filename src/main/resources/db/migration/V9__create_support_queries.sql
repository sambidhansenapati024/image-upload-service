CREATE TABLE support_queries (

    id BIGSERIAL PRIMARY KEY,

    query_id BIGINT NOT NULL UNIQUE,

    user_id BIGINT NOT NULL,

    query TEXT NOT NULL,

    status VARCHAR(30) NOT NULL,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_support_queries_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE

);

CREATE INDEX idx_support_query_id
    ON support_queries(query_id);

CREATE INDEX idx_support_query_user_id
    ON support_queries(user_id);