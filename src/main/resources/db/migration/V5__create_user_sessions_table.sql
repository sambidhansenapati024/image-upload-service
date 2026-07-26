CREATE TABLE user_sessions (

    id BIGSERIAL PRIMARY KEY,

    session_id UUID NOT NULL UNIQUE,

    user_id BIGINT NOT NULL,

    device VARCHAR(255),

    browser VARCHAR(255),

    operating_system VARCHAR(255),

    ip_address VARCHAR(100),

    location VARCHAR(255),

    login_time TIMESTAMP NOT NULL,

    last_activity TIMESTAMP,

    logout_time TIMESTAMP,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_user_sessions_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_user_sessions_user
ON user_sessions(user_id);