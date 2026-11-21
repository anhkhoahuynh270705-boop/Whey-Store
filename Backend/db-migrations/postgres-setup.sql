-- Create application users first
CREATE USER product_user   WITH PASSWORD 'product_pass';
CREATE USER inventory_user WITH PASSWORD 'inventory_pass';
CREATE USER order_user     WITH PASSWORD 'order_pass';
CREATE USER payment_user   WITH PASSWORD 'payment_pass';
CREATE USER workflow_user  WITH PASSWORD 'workflow_pass';
CREATE USER keycloak_user  WITH PASSWORD 'keycloak_pass';
CREATE USER notification_user WITH PASSWORD 'notification_pass';
CREATE USER report_user    WITH PASSWORD 'report_pass';

-- Create databases owned by their respective users
CREATE DATABASE product_db   OWNER product_user;
CREATE DATABASE inventory_db OWNER inventory_user;
CREATE DATABASE order_db     OWNER order_user;
CREATE DATABASE payment_db   OWNER payment_user;
CREATE DATABASE workflow_db  OWNER workflow_user;
CREATE DATABASE keycloak_db  OWNER keycloak_user;
CREATE DATABASE notification_db OWNER notification_user;
CREATE DATABASE report_db    OWNER report_user;

-- product_db: grant schema privileges

\c product_db
-- PostgreSQL 15+: grant CREATE on schema public explicitly
GRANT ALL ON SCHEMA public TO product_user;
ALTER SCHEMA public OWNER TO product_user;

-- inventory_db

\c inventory_db
GRANT ALL ON SCHEMA public TO inventory_user;
ALTER SCHEMA public OWNER TO inventory_user;

-- order_db

\c order_db
GRANT ALL ON SCHEMA public TO order_user;
ALTER SCHEMA public OWNER TO order_user;

-- payment_db

\c payment_db
GRANT ALL ON SCHEMA public TO payment_user;
ALTER SCHEMA public OWNER TO payment_user;

-- workflow_db

\c workflow_db
GRANT ALL ON SCHEMA public TO workflow_user;
ALTER SCHEMA public OWNER TO workflow_user;

-- keycloak_db

\c keycloak_db
GRANT ALL ON SCHEMA public TO keycloak_user;
ALTER SCHEMA public OWNER TO keycloak_user;

-- Gateway user account profile table
CREATE TABLE IF NOT EXISTS app_users (
    id         VARCHAR(64)  PRIMARY KEY,
    username   VARCHAR(100) NOT NULL UNIQUE,
    email      VARCHAR(255) NOT NULL UNIQUE,
    full_name  VARCHAR(255),
    phone      VARCHAR(20),
    role       VARCHAR(50)  NOT NULL DEFAULT 'CUSTOMER',
    status     VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- notification_db

\c notification_db
GRANT ALL ON SCHEMA public TO notification_user;
ALTER SCHEMA public OWNER TO notification_user;

-- report_db

\c report_db
GRANT ALL ON SCHEMA public TO report_user;
ALTER SCHEMA public OWNER TO report_user;

-- Done
\c postgres
SELECT 'Database setup completed!' AS status;
