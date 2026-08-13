ALTER TABLE user_sessions
    ADD COLUMN refresh_token_hash VARCHAR(512),
    ADD COLUMN refresh_token_expires_at TIMESTAMP,
    ADD COLUMN refresh_token_jti VARCHAR(255),
    ADD COLUMN refresh_token_rotation_count INTEGER DEFAULT 0;

CREATE INDEX idx_user_sessions_refresh_token_jti
    ON user_sessions(refresh_token_jti);