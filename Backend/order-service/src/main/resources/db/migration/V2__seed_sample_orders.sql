-- Seed sample orders
DO $$
DECLARE
    v_order1 BIGINT;
    v_order2 BIGINT;
BEGIN
    INSERT INTO orders (order_number, user_id, status, total_amount, shipping_address, payment_method, created_at, updated_at)
    VALUES ('ORD-20261115-001', 'customer', 'PENDING', 2500000, '{"fullName":"Nguyễn Văn A","phone":"0912345678","addressLine":"123 Nguyễn Thị Minh Khai","ward":"Phường 6","district":"Quận 3","city":"TP Hồ Chí Minh"}', 'COD', NOW() - INTERVAL '2 hours', NOW() - INTERVAL '2 hours')
    ON CONFLICT (order_number) DO NOTHING
    RETURNING id INTO v_order1;

    IF v_order1 IS NOT NULL THEN
        INSERT INTO order_items (order_id, product_id, product_name, quantity, unit_price, subtotal)
        VALUES 
        (v_order1, 1, 'ON Gold Standard Whey 5lbs', 1, 1050000, 1050000),
        (v_order1, 4, 'Rule 1 Protein Isolate 5lbs', 1, 1450000, 1450000);
    END IF;

    INSERT INTO orders (order_number, user_id, status, total_amount, shipping_address, payment_method, created_at, updated_at)
    VALUES ('ORD-20261115-002', 'customer', 'COMPLETED', 1890000, '{"fullName":"Trần Thị B","phone":"0987654321","addressLine":"456 Lê Duẩn","ward":"Phường Bến Nghé","district":"Quận 1","city":"TP Hồ Chí Minh"}', 'BANK_TRANSFER', NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day')
    ON CONFLICT (order_number) DO NOTHING
    RETURNING id INTO v_order2;

    IF v_order2 IS NOT NULL THEN
        INSERT INTO order_items (order_id, product_id, product_name, quantity, unit_price, subtotal)
        VALUES 
        (v_order2, 6, 'Mutant Mass Gainer 15lbs (6.8kg)', 1, 1890000, 1890000);
    END IF;
END $$;

