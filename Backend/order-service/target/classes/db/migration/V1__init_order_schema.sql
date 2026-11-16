-- Order Service Schema

CREATE TABLE IF NOT EXISTS orders (
    id                  BIGSERIAL PRIMARY KEY,
    order_number        VARCHAR(50)     NOT NULL UNIQUE,
    user_id             VARCHAR(100)    NOT NULL,
    status              VARCHAR(50)     NOT NULL,
    total_amount        NUMERIC(14,0)   NOT NULL,
    shipping_address    VARCHAR(1000),
    payment_method      VARCHAR(100),
    process_instance_id VARCHAR(255),
    cancel_reason       VARCHAR(500),
    created_at          TIMESTAMP       NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP       NOT NULL DEFAULT now(),
    completed_at        TIMESTAMP,
    cancelled_at        TIMESTAMP
);

CREATE INDEX idx_order_user   ON orders (user_id);
CREATE INDEX idx_order_status ON orders (status);

CREATE TABLE IF NOT EXISTS order_items (
    id           BIGSERIAL PRIMARY KEY,
    order_id     BIGINT          NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    product_id   BIGINT          NOT NULL,
    product_name VARCHAR(500)    NOT NULL,
    quantity     INT             NOT NULL,
    unit_price   NUMERIC(14,0)   NOT NULL,
    subtotal     NUMERIC(14,0)   NOT NULL
);

CREATE INDEX idx_order_items_order ON order_items (order_id);

CREATE TABLE IF NOT EXISTS order_status_history (
    id         BIGSERIAL PRIMARY KEY,
    order_id   BIGINT       NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    status     VARCHAR(50)  NOT NULL,
    note       VARCHAR(500),
    created_at TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE INDEX idx_order_history_order ON order_status_history (order_id);
