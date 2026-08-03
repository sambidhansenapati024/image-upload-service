CREATE TABLE activity_log (
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,

    action_type VARCHAR(50) NOT NULL,

    message VARCHAR(255) NOT NULL,

    reference_id BIGINT,

    is_read BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_activity_log_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);