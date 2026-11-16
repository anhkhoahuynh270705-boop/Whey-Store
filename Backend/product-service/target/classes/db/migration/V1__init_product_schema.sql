-- Product Service Schema

CREATE TABLE IF NOT EXISTS categories (
    id          BIGSERIAL    PRIMARY KEY,
    slug        VARCHAR(200) NOT NULL UNIQUE,
    name        VARCHAR(200) NOT NULL UNIQUE,
    description VARCHAR(2000),
    created_at  TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS brands (
    id          BIGSERIAL    PRIMARY KEY,
    slug        VARCHAR(200) NOT NULL UNIQUE,
    name        VARCHAR(200) NOT NULL UNIQUE,
    description VARCHAR(2000),
    logo_url    VARCHAR(500),
    created_at  TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS products (
    id           BIGSERIAL      PRIMARY KEY,
    name         VARCHAR(200)   NOT NULL,
    slug         VARCHAR(200)   NOT NULL UNIQUE,
    description  VARCHAR(5000),
    category_id  BIGINT         REFERENCES categories (id),
    brand_id     BIGINT         REFERENCES brands (id),
    price        NUMERIC(12, 0) NOT NULL,
    sale_price   NUMERIC(12, 0),
    weight_grams INT,
    flavor       VARCHAR(100),
    serving_size INT,
    status       VARCHAR(50)    NOT NULL DEFAULT 'ACTIVE',
    created_at   TIMESTAMP      NOT NULL DEFAULT now(),
    updated_at   TIMESTAMP      NOT NULL DEFAULT now()
);

CREATE INDEX idx_product_slug     ON products (slug);
CREATE INDEX idx_product_category ON products (category_id);
CREATE INDEX idx_product_brand    ON products (brand_id);
CREATE INDEX idx_product_status   ON products (status);

CREATE TABLE IF NOT EXISTS product_images (
    id         BIGSERIAL    PRIMARY KEY,
    product_id BIGINT       NOT NULL REFERENCES products (id) ON DELETE CASCADE,
    image_url  VARCHAR(500) NOT NULL,
    is_primary BOOLEAN      NOT NULL DEFAULT false
);

CREATE INDEX idx_product_images_product ON product_images (product_id);
