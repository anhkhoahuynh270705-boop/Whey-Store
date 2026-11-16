-- Payment Database - Initial Schema
-- Version: 1
-- Description: Create tables for payments and transactions

-- ============================================
-- Payments Table
-- ============================================
CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    payment_reference VARCHAR(100) NOT NULL UNIQUE,
    
    -- Order reference
    order_id BIGINT NOT NULL,
    order_number VARCHAR(50) NOT NULL,
    
    -- Customer
    customer_id VARCHAR(100) NOT NULL,
    customer_email VARCHAR(255),
    
    -- Payment details
    amount DECIMAL(12,2) NOT NULL CHECK (amount > 0),
    currency VARCHAR(3) DEFAULT 'VND',
    
    -- Status
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED', 'REFUNDED')),
    
    -- Payment method
    payment_method VARCHAR(50) NOT NULL
        CHECK (payment_method IN ('CREDIT_CARD', 'DEBIT_CARD', 'BANK_TRANSFER', 'COD', 'MOMO', 'VNPAY', 'ZALOPAY')),
    
    -- Provider details
    provider_transaction_id VARCHAR(200),
    provider_name VARCHAR(100),
    provider_response TEXT, -- JSON response from provider
    
    -- Card details (masked)
    card_last_four VARCHAR(4),
    card_brand VARCHAR(50),
    
    -- Timestamps
    paid_at TIMESTAMP,
    failed_at TIMESTAMP,
    refunded_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_payments_reference ON payments(payment_reference);
CREATE INDEX idx_payments_order ON payments(order_id);
CREATE INDEX idx_payments_customer ON payments(customer_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_method ON payments(payment_method);
CREATE INDEX idx_payments_provider_txn ON payments(provider_transaction_id);
CREATE INDEX idx_payments_created ON payments(created_at DESC);

-- ============================================
-- Payment Transactions Table (Audit Log)
-- ============================================
CREATE TABLE IF NOT EXISTS payment_transactions (
    id BIGSERIAL PRIMARY KEY,
    payment_id BIGINT NOT NULL REFERENCES payments(id) ON DELETE CASCADE,
    
    -- Transaction details
    transaction_type VARCHAR(50) NOT NULL
        CHECK (transaction_type IN ('AUTHORIZE', 'CAPTURE', 'VOID', 'REFUND', 'CHARGEBACK')),
    amount DECIMAL(12,2) NOT NULL,
    
    -- Status
    status VARCHAR(50) NOT NULL
        CHECK (status IN ('SUCCESS', 'FAILED', 'PENDING')),
    
    -- Provider response
    provider_transaction_id VARCHAR(200),
    provider_response TEXT, -- JSON response
    error_code VARCHAR(50),
    error_message TEXT,
    
    -- Metadata
    metadata TEXT, -- JSON for additional data
    
    -- Timestamp
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_payment_txn_payment ON payment_transactions(payment_id);
CREATE INDEX idx_payment_txn_type ON payment_transactions(transaction_type);
CREATE INDEX idx_payment_txn_status ON payment_transactions(status);
CREATE INDEX idx_payment_txn_created ON payment_transactions(created_at DESC);

-- ============================================
-- Refunds Table
-- ============================================
CREATE TABLE IF NOT EXISTS refunds (
    id BIGSERIAL PRIMARY KEY,
    payment_id BIGINT NOT NULL REFERENCES payments(id) ON DELETE CASCADE,
    refund_reference VARCHAR(100) NOT NULL UNIQUE,
    
    -- Refund details
    amount DECIMAL(12,2) NOT NULL CHECK (amount > 0),
    reason VARCHAR(500),
    
    -- Status
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED')),
    
    -- Provider
    provider_refund_id VARCHAR(200),
    provider_response TEXT,
    
    -- Metadata
    requested_by VARCHAR(100),
    approved_by VARCHAR(100),
    notes TEXT,
    
    -- Timestamps
    completed_at TIMESTAMP,
    failed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_refunds_payment ON refunds(payment_id);
CREATE INDEX idx_refunds_reference ON refunds(refund_reference);
CREATE INDEX idx_refunds_status ON refunds(status);
CREATE INDEX idx_refunds_created ON refunds(created_at DESC);

-- ============================================
-- Functions
-- ============================================

-- Update timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_payments_updated_at BEFORE UPDATE ON payments
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_refunds_updated_at BEFORE UPDATE ON refunds
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Generate payment reference
CREATE OR REPLACE FUNCTION generate_payment_reference()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.payment_reference IS NULL THEN
        NEW.payment_reference := 'PAY' || TO_CHAR(CURRENT_TIMESTAMP, 'YYYYMMDD') || 
                                LPAD(NEW.id::TEXT, 8, '0');
    END IF;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER set_payment_reference BEFORE INSERT ON payments
    FOR EACH ROW EXECUTE FUNCTION generate_payment_reference();

-- Track payment transactions automatically
CREATE OR REPLACE FUNCTION track_payment_status_change()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'UPDATE' AND OLD.status != NEW.status) THEN
        INSERT INTO payment_transactions (
            payment_id,
            transaction_type,
            amount,
            status,
            provider_transaction_id,
            metadata
        ) VALUES (
            NEW.id,
            'AUTHORIZE',
            NEW.amount,
            CASE 
                WHEN NEW.status = 'COMPLETED' THEN 'SUCCESS'
                WHEN NEW.status = 'FAILED' THEN 'FAILED'
                ELSE 'PENDING'
            END,
            NEW.provider_transaction_id,
            json_build_object('old_status', OLD.status, 'new_status', NEW.status)::TEXT
        );
    END IF;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER track_payment_status AFTER UPDATE ON payments
    FOR EACH ROW EXECUTE FUNCTION track_payment_status_change();
