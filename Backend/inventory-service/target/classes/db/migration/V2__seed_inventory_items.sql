-- V2__seed_inventory_items.sql: Seed initial stock for products
INSERT INTO inventory_items (product_id, sku, available_qty, reserved_qty, version) VALUES
(1, 'SKU-ON-WHEY-5', 50, 0, 0),
(2, 'SKU-MT-NITRO-55', 40, 0, 0),
(3, 'SKU-CC-C4-ORIG', 75, 0, 0),
(4, 'SKU-R1-ISO-5', 60, 0, 0),
(5, 'SKU-DY-ISO100-5', 45, 0, 0),
(6, 'SKU-MU-MASS-15', 30, 0, 0),
(7, 'SKU-AN-ABE-315', 80, 0, 0),
(8, 'SKU-BPI-BCAA-30', 90, 0, 0),
(9, 'SKU-MT-CREATINE-400', 100, 0, 0),
(10, 'SKU-NU-LIPO6-60', 65, 0, 0),
(11, 'SKU-MYP-IMPACT-25', 55, 0, 0),
(12, 'SKU-ON-OPTI-90', 70, 0, 0)
ON CONFLICT (product_id) DO UPDATE SET available_qty = EXCLUDED.available_qty;

