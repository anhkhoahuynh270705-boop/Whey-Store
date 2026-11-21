-- V3: Tăng độ dài image_url trong product_images lên TEXT
-- để hỗ trợ cả URL dài và base64 data URI
ALTER TABLE product_images
    ALTER COLUMN image_url TYPE TEXT;

ALTER TABLE brands
    ALTER COLUMN logo_url TYPE TEXT;
