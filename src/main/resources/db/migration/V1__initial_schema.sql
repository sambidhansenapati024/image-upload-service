-- =====================================================
-- CloudVault
-- Version : V1
-- Description : Initial database schema
-- =====================================================

CREATE TABLE users
(
    id BIGSERIAL PRIMARY KEY,

    full_name VARCHAR(255) NOT NULL,

    email VARCHAR(255) NOT NULL,

    password VARCHAR(255) NOT NULL,

    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE image_metadata
(
    id BIGSERIAL PRIMARY KEY,

    original_file_name VARCHAR(255),

    s3_key VARCHAR(255) NOT NULL,

    image_url TEXT,

    file_size BIGINT,

    content_type VARCHAR(255),

    uploaded_at TIMESTAMP,

    user_id BIGINT NOT NULL,

    CONSTRAINT uk_image_metadata_s3_key
        UNIQUE (s3_key),

    CONSTRAINT fk_image_metadata_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- =====================================================
-- Indexes
-- =====================================================

CREATE INDEX idx_image_metadata_user
    ON image_metadata(user_id);

CREATE INDEX idx_image_metadata_uploaded_at
    ON image_metadata(uploaded_at DESC);

CREATE INDEX idx_image_metadata_original_file_name
    ON image_metadata(original_file_name);

CREATE INDEX idx_users_email
    ON users(email);