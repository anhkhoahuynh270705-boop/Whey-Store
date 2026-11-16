-- Notification Service Schema

CREATE TABLE IF NOT EXISTS email_logs (
    id            BIGSERIAL PRIMARY KEY,
    message_id    VARCHAR(255) NOT NULL UNIQUE,
    order_id      VARCHAR(255),
    user_id       VARCHAR(255),
    email         VARCHAR(255),
    subject       VARCHAR(500),
    template_name VARCHAR(100),
    status        VARCHAR(50),
    retry_count   INT          NOT NULL DEFAULT 0,
    error_message TEXT,
    sent_at       TIMESTAMP,
    created_at    TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX idx_email_log_message_id ON email_logs (message_id);
CREATE INDEX        idx_email_log_order_id   ON email_logs (order_id);
