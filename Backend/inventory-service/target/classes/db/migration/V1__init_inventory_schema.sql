-- Inventory Service Schema

CREATE TABLE IF NOT EXISTS inventory_items (
    id            BIGSERIAL PRIMARY KEY,
    product_id    BIGINT      NOT NULL UNIQUE,
    sku           VARCHAR(100) NOT NULL,
    available_qty INT         NOT NULL DEFAULT 0,
    reserved_qty  INT         NOT NULL DEFAULT 0,
    version       BIGINT      NOT NULL DEFAULT 0,
    created_at    TIMESTAMP   NOT NULL DEFAULT now(),
    updated_at    TIMESTAMP   NOT NULL DEFAULT now()
);

CREATE INDEX idx_inventory_product ON inventory_items (product_id);
CREATE INDEX idx_inventory_sku     ON inventory_items (sku);

CREATE TABLE IF NOT EXISTS stock_movements (
    id         BIGSERIAL PRIMARY KEY,
    product_id BIGINT      NOT NULL,
    order_id   VARCHAR(255),
    quantity   INT         NOT NULL,
    type       VARCHAR(50) NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT now()
);

CREATE INDEX idx_movement_product ON stock_movements (product_id);
CREATE INDEX idx_movement_order   ON stock_movements (order_id);

CREATE TABLE IF NOT EXISTS stock_reservations (
    id         BIGSERIAL PRIMARY KEY,
    order_id   VARCHAR(255) NOT NULL,
    product_id BIGINT       NOT NULL,
    quantity   INT          NOT NULL,
    status     VARCHAR(50)  NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE INDEX idx_reservation_order ON stock_reservations (order_id);
