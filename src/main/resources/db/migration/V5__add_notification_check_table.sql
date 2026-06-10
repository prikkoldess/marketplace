CREATE TABLE notification_check (
    id BIGSERIAL PRIMARY KEY,
    notification_key VARCHAR(255) NOT NULL UNIQUE,
    sent_at TIMESTAMP
);