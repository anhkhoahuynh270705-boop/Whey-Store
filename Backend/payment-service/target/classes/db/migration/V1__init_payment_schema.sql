-- Payment Service Schema

CREATE TABLE IF NOT EXISTS payments (
    id                BIGSERIAL PRIMARY KEY,
    order_id          VARCHAR(255)    NOT NULL UNIQUE,
    payment_method    VARCHAR(100)    NOT NULL,
    amount            NUMERIC(14,0)   NOT NULL,
    status            VARCHAR(50)     NOT NULL,
    transaction_id    VARCHAR(255),
    provider_response VARCHAR(2000),
    created_at        TIMESTAMP       NOT NULL DEFAULT now(),
    updated_at        TIMESTAMP       NOT NULL DEFAULT now()
);

CREATE INDEX idx_payment_order  ON payments (order_id);
CREATE INDEX idx_payment_status ON payments (status);
