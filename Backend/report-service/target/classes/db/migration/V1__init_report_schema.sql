-- Report Service Schema
-- report-service dùng DataSource để query trực tiếp qua JasperReports (JDBC).
-- Bảng này lưu lịch sử các báo cáo đã xuất.

CREATE TABLE IF NOT EXISTS report_history (
    id           BIGSERIAL PRIMARY KEY,
    report_type  VARCHAR(100) NOT NULL,
    parameters   TEXT,
    format       VARCHAR(20)  NOT NULL DEFAULT 'pdf',
    generated_by VARCHAR(255),
    generated_at TIMESTAMP    NOT NULL DEFAULT now(),
    file_size    INTEGER
);

CREATE INDEX idx_report_history_type ON report_history (report_type);
CREATE INDEX idx_report_history_date ON report_history (generated_at);
