-- Workflow Database - Initial Schema
-- Version: 1
-- Description: Create custom tables for workflow data (Camunda tables auto-created)

-- Note: Camunda BPM automatically creates its own tables (ACT_* tables)
-- This migration only creates custom business tables for workflow data

-- ============================================
-- Purchase Workflows Table
-- ============================================
CREATE TABLE IF NOT EXISTS purchase_workflows (
    id BIGSERIAL PRIMARY KEY,
    
    -- Process reference
    process_instance_id VARCHAR(100) NOT NULL UNIQUE,
    process_definition_key VARCHAR(100) NOT NULL,
    
    -- Business data
    order_id BIGINT NOT NULL,
    order_number VARCHAR(50) NOT NULL,
    customer_id VARCHAR(100) NOT NULL,
    
    -- Workflow status
    status VARCHAR(50) NOT NULL DEFAULT 'STARTED'
        CHECK (status IN ('STARTED', 'INVENTORY_RESERVED', 'PAYMENT_PROCESSING', 
                         'PAYMENT_COMPLETED', 'ORDER_CONFIRMED', 'COMPLETED', 'FAILED', 'CANCELLED')),
    
    -- Steps status
    inventory_reserved BOOLEAN DEFAULT false,
    payment_completed BOOLEAN DEFAULT false,
    order_confirmed BOOLEAN DEFAULT false,
    notification_sent BOOLEAN DEFAULT false,
    
    -- Error handling
    error_message TEXT,
    retry_count INT DEFAULT 0,
    
    -- Timestamps
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    failed_at TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_purchase_workflows_process ON purchase_workflows(process_instance_id);
CREATE INDEX idx_purchase_workflows_order ON purchase_workflows(order_id);
CREATE INDEX idx_purchase_workflows_customer ON purchase_workflows(customer_id);
CREATE INDEX idx_purchase_workflows_status ON purchase_workflows(status);
CREATE INDEX idx_purchase_workflows_started ON purchase_workflows(started_at DESC);

-- ============================================
-- Workflow Tasks Table
-- ============================================
CREATE TABLE IF NOT EXISTS workflow_tasks (
    id BIGSERIAL PRIMARY KEY,
    workflow_id BIGINT NOT NULL REFERENCES purchase_workflows(id) ON DELETE CASCADE,
    
    -- Task details
    task_key VARCHAR(100) NOT NULL,
    task_name VARCHAR(255) NOT NULL,
    task_type VARCHAR(50) NOT NULL
        CHECK (task_type IN ('SERVICE_TASK', 'USER_TASK', 'MANUAL_TASK', 'BUSINESS_RULE_TASK')),
    
    -- Status
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'FAILED', 'SKIPPED')),
    
    -- Execution
    assignee VARCHAR(100),
    input_data TEXT, -- JSON
    output_data TEXT, -- JSON
    error_message TEXT,
    
    -- Timestamps
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    failed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_workflow_tasks_workflow ON workflow_tasks(workflow_id);
CREATE INDEX idx_workflow_tasks_key ON workflow_tasks(task_key);
CREATE INDEX idx_workflow_tasks_status ON workflow_tasks(status);
CREATE INDEX idx_workflow_tasks_assignee ON workflow_tasks(assignee);

-- ============================================
-- Workflow Events Table
-- ============================================
CREATE TABLE IF NOT EXISTS workflow_events (
    id BIGSERIAL PRIMARY KEY,
    workflow_id BIGINT NOT NULL REFERENCES purchase_workflows(id) ON DELETE CASCADE,
    
    -- Event details
    event_type VARCHAR(100) NOT NULL,
    event_name VARCHAR(255) NOT NULL,
    event_data TEXT, -- JSON
    
    -- Source
    source VARCHAR(100), -- Which service/component triggered
    correlation_id VARCHAR(100),
    
    -- Timestamp
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_workflow_events_workflow ON workflow_events(workflow_id);
CREATE INDEX idx_workflow_events_type ON workflow_events(event_type);
CREATE INDEX idx_workflow_events_correlation ON workflow_events(correlation_id);
CREATE INDEX idx_workflow_events_created ON workflow_events(created_at DESC);

-- ============================================
-- Compensation Actions Table (For Saga Pattern)
-- ============================================
CREATE TABLE IF NOT EXISTS compensation_actions (
    id BIGSERIAL PRIMARY KEY,
    workflow_id BIGINT NOT NULL REFERENCES purchase_workflows(id) ON DELETE CASCADE,
    
    -- Action details
    action_type VARCHAR(100) NOT NULL
        CHECK (action_type IN ('RELEASE_INVENTORY', 'REFUND_PAYMENT', 'CANCEL_ORDER', 'SEND_NOTIFICATION')),
    action_data TEXT, -- JSON
    
    -- Status
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED')),
    
    -- Execution
    executed_at TIMESTAMP,
    error_message TEXT,
    
    -- Timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_compensation_actions_workflow ON compensation_actions(workflow_id);
CREATE INDEX idx_compensation_actions_type ON compensation_actions(action_type);
CREATE INDEX idx_compensation_actions_status ON compensation_actions(status);

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

CREATE TRIGGER update_purchase_workflows_updated_at BEFORE UPDATE ON purchase_workflows
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Log workflow events automatically
CREATE OR REPLACE FUNCTION log_workflow_status_change()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'UPDATE' AND OLD.status != NEW.status) THEN
        INSERT INTO workflow_events (
            workflow_id,
            event_type,
            event_name,
            event_data,
            source
        ) VALUES (
            NEW.id,
            'STATUS_CHANGE',
            'Workflow status changed',
            json_build_object(
                'old_status', OLD.status,
                'new_status', NEW.status,
                'order_id', NEW.order_id
            )::TEXT,
            'SYSTEM'
        );
    END IF;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER track_workflow_status AFTER UPDATE ON purchase_workflows
    FOR EACH ROW EXECUTE FUNCTION log_workflow_status_change();
