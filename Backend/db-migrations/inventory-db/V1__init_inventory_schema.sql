-- Inventory Database - Initial Schema
-- Version: 1
-- Description: Create tables for inventory, stock movements, and reservations

-- ============================================
-- Inventory Table
-- ============================================
CREATE TABLE IF NOT EXISTS inventory (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL UNIQUE,
    
    -- Stock quantities
    quantity_available INT NOT NULL DEFAULT 0 CHECK (quantity_available >= 0),
    quantity_reserved INT NOT NULL DEFAULT 0 CHECK (quantity_reserved >= 0),
    quantity_total INT GENERATED ALWAYS AS (quantity_available + quantity_reserved) STORED,
    
    -- Thresholds
    reorder_level INT DEFAULT 10,
    reorder_quantity INT DEFAULT 50,
    
    -- Location
    warehouse_location VARCHAR(100),
    
    -- Timestamps
    last_restocked_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_inventory_product ON inventory(product_id);
CREATE INDEX idx_inventory_low_stock ON inventory(quantity_available) 
    WHERE quantity_available <= reorder_level;

-- ============================================
-- Stock Movements Table
-- ============================================
CREATE TABLE IF NOT EXISTS stock_movements (
    id BIGSERIAL PRIMARY KEY,
    inventory_id BIGINT NOT NULL REFERENCES inventory(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL,
    
    -- Movement details
    movement_type VARCHAR(50) NOT NULL 
        CHECK (movement_type IN ('RESTOCK', 'SALE', 'RETURN', 'ADJUSTMENT', 'RESERVATION', 'RELEASE')),
    quantity INT NOT NULL,
    quantity_before INT NOT NULL,
    quantity_after INT NOT NULL,
    
    -- Reference
    reference_type VARCHAR(50), -- ORDER, RETURN, MANUAL, etc.
    reference_id VARCHAR(100),
    
    -- Details
    notes TEXT,
    performed_by VARCHAR(100),
    
    -- Timestamp
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_stock_movements_inventory ON stock_movements(inventory_id);
CREATE INDEX idx_stock_movements_product ON stock_movements(product_id);
CREATE INDEX idx_stock_movements_type ON stock_movements(movement_type);
CREATE INDEX idx_stock_movements_reference ON stock_movements(reference_type, reference_id);
CREATE INDEX idx_stock_movements_created ON stock_movements(created_at DESC);

-- ============================================
-- Reservations Table
-- ============================================
CREATE TABLE IF NOT EXISTS reservations (
    id BIGSERIAL PRIMARY KEY,
    inventory_id BIGINT NOT NULL REFERENCES inventory(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL,
    
    -- Reservation details
    quantity INT NOT NULL CHECK (quantity > 0),
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE'
        CHECK (status IN ('ACTIVE', 'FULFILLED', 'CANCELLED', 'EXPIRED')),
    
    -- Reference
    reference_type VARCHAR(50) NOT NULL, -- ORDER, CART
    reference_id VARCHAR(100) NOT NULL,
    customer_id VARCHAR(100),
    
    -- Timestamps
    expires_at TIMESTAMP NOT NULL,
    fulfilled_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_reservations_inventory ON reservations(inventory_id);
CREATE INDEX idx_reservations_product ON reservations(product_id);
CREATE INDEX idx_reservations_status ON reservations(status);
CREATE INDEX idx_reservations_reference ON reservations(reference_type, reference_id);
CREATE INDEX idx_reservations_expires ON reservations(expires_at) WHERE status = 'ACTIVE';
CREATE INDEX idx_reservations_customer ON reservations(customer_id);

-- ============================================
-- Update Timestamp Function
-- ============================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Triggers
CREATE TRIGGER update_inventory_updated_at BEFORE UPDATE ON inventory
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_reservations_updated_at BEFORE UPDATE ON reservations
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- Function: Record Stock Movement
-- ============================================
CREATE OR REPLACE FUNCTION record_stock_movement()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'UPDATE' AND OLD.quantity_available != NEW.quantity_available) THEN
        INSERT INTO stock_movements (
            inventory_id,
            product_id,
            movement_type,
            quantity,
            quantity_before,
            quantity_after,
            notes
        ) VALUES (
            NEW.id,
            NEW.product_id,
            'ADJUSTMENT',
            NEW.quantity_available - OLD.quantity_available,
            OLD.quantity_available,
            NEW.quantity_available,
            'Auto-recorded stock change'
        );
    END IF;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER track_inventory_changes AFTER UPDATE ON inventory
    FOR EACH ROW EXECUTE FUNCTION record_stock_movement();
