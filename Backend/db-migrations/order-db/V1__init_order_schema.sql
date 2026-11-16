-- Order Database - Initial Schema
-- Version: 1
-- Description: Create tables for orders and order items

-- Orders Table
CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    
    -- Customer info
    customer_id VARCHAR(100) NOT NULL,
    customer_email VARCHAR(255),
    customer_name VARCHAR(255),
    customer_phone VARCHAR(20),
    
    -- Shipping address
    shipping_address_line1 VARCHAR(500),
    shipping_address_line2 VARCHAR(500),
    shipping_city VARCHAR(100),
    shipping_state VARCHAR(100),
    shipping_postal_code VARCHAR(20),
    shipping_country VARCHAR(100) DEFAULT 'Vietnam',
    
    -- Billing address (if different)
    billing_address_line1 VARCHAR(500),
    billing_city VARCHAR(100),
    billing_postal_code VARCHAR(20),
    
    -- Order status
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'REFUNDED')),
    
    -- Pricing
    subtotal DECIMAL(12,2) NOT NULL CHECK (subtotal >= 0),
    shipping_fee DECIMAL(10,2) DEFAULT 0 CHECK (shipping_fee >= 0),
    tax DECIMAL(10,2) DEFAULT 0 CHECK (tax >= 0),
    discount DECIMAL(10,2) DEFAULT 0 CHECK (discount >= 0),
    total DECIMAL(12,2) NOT NULL CHECK (total >= 0),
    
    -- Payment
    payment_method VARCHAR(50),
    payment_status VARCHAR(50) DEFAULT 'PENDING'
        CHECK (payment_status IN ('PENDING', 'PAID', 'FAILED', 'REFUNDED')),
    paid_at TIMESTAMP,
    
    -- Shipping
    shipping_method VARCHAR(50),
    tracking_number VARCHAR(100),
    shipped_at TIMESTAMP,
    delivered_at TIMESTAMP,
    
    -- Notes
    customer_notes TEXT,
    admin_notes TEXT,
    
    -- Timestamps
    cancelled_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_orders_number ON orders(order_number);
CREATE INDEX idx_orders_customer ON orders(customer_id);
CREATE INDEX idx_orders_email ON orders(customer_email);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_payment_status ON orders(payment_status);
CREATE INDEX idx_orders_created ON orders(created_at DESC);

-- ============================================
-- Order Items Table
-- ============================================
CREATE TABLE IF NOT EXISTS order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    
    -- Product info (snapshot at time of order)
    product_id BIGINT NOT NULL,
    product_name VARCHAR(500) NOT NULL,
    product_sku VARCHAR(100),
    product_image VARCHAR(500),
    
    -- Pricing
    unit_price DECIMAL(10,2) NOT NULL CHECK (unit_price >= 0),
    quantity INT NOT NULL CHECK (quantity > 0),
    subtotal DECIMAL(10,2) NOT NULL CHECK (subtotal >= 0),
    discount DECIMAL(10,2) DEFAULT 0 CHECK (discount >= 0),
    total DECIMAL(10,2) NOT NULL CHECK (total >= 0),
    
    -- Timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_product ON order_items(product_id);

-- ============================================
-- Order Status History Table
-- ============================================
CREATE TABLE IF NOT EXISTS order_status_history (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    
    -- Status change
    old_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    
    -- Details
    notes TEXT,
    changed_by VARCHAR(100),
    
    -- Timestamp
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_order_status_history_order ON order_status_history(order_id);
CREATE INDEX idx_order_status_history_created ON order_status_history(created_at DESC);

-- ============================================
-- Functions
-- ============================================

-- Update timestamp trigger
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_orders_updated_at BEFORE UPDATE ON orders
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Track status changes
CREATE OR REPLACE FUNCTION track_order_status_change()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'UPDATE' AND OLD.status != NEW.status) THEN
        INSERT INTO order_status_history (
            order_id,
            old_status,
            new_status,
            notes,
            changed_by
        ) VALUES (
            NEW.id,
            OLD.status,
            NEW.status,
            'Status changed automatically',
            'SYSTEM'
        );
    END IF;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER track_order_status AFTER UPDATE ON orders
    FOR EACH ROW EXECUTE FUNCTION track_order_status_change();

-- Generate order number
CREATE OR REPLACE FUNCTION generate_order_number()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.order_number IS NULL THEN
        NEW.order_number := 'ORD' || TO_CHAR(CURRENT_TIMESTAMP, 'YYYYMMDD') || 
                           LPAD(NEW.id::TEXT, 6, '0');
    END IF;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER set_order_number BEFORE INSERT ON orders
    FOR EACH ROW EXECUTE FUNCTION generate_order_number();
