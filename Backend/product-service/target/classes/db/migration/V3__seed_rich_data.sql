-- V3__seed_rich_data.sql: Seed comprehensive gym categories, brands, and real products

-- 1. Insert/Update Categories
INSERT INTO categories (slug, name, description) VALUES
    ('whey-protein',     'Whey Protein',             'Đạm whey cao cấp hấp thu nhanh hỗ trợ phát triển và phục hồi cơ bắp'),
    ('mass-gainer',      'Sữa tăng cân Mass Gainer', 'Công thức giàu calorie và protein hỗ trợ tăng cân tăng cơ hiệu quả'),
    ('bcaa-eaa',         'BCAA & EAA Phục hồi',      'Axit amin thiết yếu chống dị hóa cơ và giảm mệt mỏi khi tập'),
    ('creatine',         'Creatine Tăng sức mạnh',   'Hỗ trợ bùng nổ sức mạnh, tăng thể tích tế bào cơ bắp'),
    ('pre-workout',      'Pre-Workout & Năng lượng', 'Bùng nổ năng lượng, tỉnh táo và tăng sức bền tối đa'),
    ('vitamins-minerals','Vitamin & Khoáng chất',    'Bổ sung vi chất thiết yếu, tăng cường đề kháng và chuyển hóa'),
    ('fat-burner',       'Đốt mỡ - Giảm cân',        'Đốt cháy calo dư thừa, hỗ trợ siết cơ và giảm cân an toàn'),
    ('gym-accessories',  'Phụ kiện tập Gym',         'Bình lắc shakers, đai lưng, găng tay và phụ kiện hỗ trợ tập'),
    ('healthy-snacks',   'Bánh Protein & Snacks',    'Bánh protein bar, snack dinh dưỡng cho bữa phụ tiện lợi')
ON CONFLICT (slug) DO UPDATE 
SET name = EXCLUDED.name, description = EXCLUDED.description;

-- 2. Insert/Update Brands
INSERT INTO brands (slug, name, description, logo_url) VALUES
    ('optimum-nutrition', 'Optimum Nutrition (ON)', 'Thương hiệu thực phẩm bổ sung số 1 thế giới từ Mỹ', 'https://images.unsplash.com/photo-1579722821273-0f6c7d44362f?w=200'),
    ('rule1',             'Rule 1 Proteins',        'Nguồn Whey Isolate & Hydrolyzed siêu tinh khiết từ Mỹ', 'https://images.unsplash.com/photo-1593095948071-474c5cc2989d?w=200'),
    ('muscletech',        'MuscleTech',             'Dẫn đầu khoa học thể hình với các nghiên cứu lâm sàng đỉnh cao', 'https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=200'),
    ('applied-nutrition', 'Applied Nutrition',      'Thương hiệu dinh dưỡng thể thao danh tiếng đến từ Anh Quốc', 'https://images.unsplash.com/photo-1579722820308-d74e571900a9?w=200'),
    ('dymatize',          'Dymatize',               'Nổi tiếng toàn cầu với dòng ISO 100 đoạt giải Protein của năm', 'https://images.unsplash.com/photo-1517838277536-f5f99be501cd?w=200'),
    ('myprotein',         'MyProtein',              'Thương hiệu bổ sung thể hình số 1 Châu Âu, chất lượng và đa dạng', 'https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=200'),
    ('bpi-sports',        'BPI Sports',             'Chuyên gia về phục hồi cơ và các dòng BCAA hương vị tuyệt hảo', 'https://images.unsplash.com/photo-1540420773420-3366772f4999?w=200'),
    ('mutant',            'Mutant',                 'Dòng sản phẩm thể hình hardcore dành cho gymer muốn bứt phá giới hạn', 'https://images.unsplash.com/photo-1583454110551-21f2fa2afe61?w=200'),
    ('nutrex-research',   'Nutrex Research',        'Tiên phong trong các dòng giảm mỡ Lipo-6 và hiệu suất cao', 'https://images.unsplash.com/photo-1574680096145-d05b474e2155?w=200')
ON CONFLICT (slug) DO UPDATE 
SET name = EXCLUDED.name, description = EXCLUDED.description;

-- 3. Seed Realistic Products
DO $$
DECLARE
    v_whey_id       BIGINT;
    v_mass_id       BIGINT;
    v_bcaa_id       BIGINT;
    v_creatine_id   BIGINT;
    v_pre_id        BIGINT;
    v_vit_id        BIGINT;
    v_fat_id        BIGINT;
    v_acc_id        BIGINT;
    v_snack_id      BIGINT;

    v_on_id         BIGINT;
    v_r1_id         BIGINT;
    v_mt_id         BIGINT;
    v_an_id         BIGINT;
    v_dy_id         BIGINT;
    v_my_id         BIGINT;
    v_bpi_id        BIGINT;
    v_mu_id         BIGINT;
    v_nu_id         BIGINT;

    v_pid           BIGINT;
BEGIN
    SELECT id INTO v_whey_id     FROM categories WHERE slug = 'whey-protein';
    SELECT id INTO v_mass_id     FROM categories WHERE slug = 'mass-gainer';
    SELECT id INTO v_bcaa_id     FROM categories WHERE slug = 'bcaa-eaa';
    SELECT id INTO v_creatine_id FROM categories WHERE slug = 'creatine';
    SELECT id INTO v_pre_id      FROM categories WHERE slug = 'pre-workout';
    SELECT id INTO v_vit_id      FROM categories WHERE slug = 'vitamins-minerals';
    SELECT id INTO v_fat_id      FROM categories WHERE slug = 'fat-burner';
    SELECT id INTO v_acc_id      FROM categories WHERE slug = 'gym-accessories';
    SELECT id INTO v_snack_id    FROM categories WHERE slug = 'healthy-snacks';

    SELECT id INTO v_on_id       FROM brands WHERE slug = 'optimum-nutrition';
    SELECT id INTO v_r1_id       FROM brands WHERE slug = 'rule1';
    SELECT id INTO v_mt_id       FROM brands WHERE slug = 'muscletech';
    SELECT id INTO v_an_id       FROM brands WHERE slug = 'applied-nutrition';
    SELECT id INTO v_dy_id       FROM brands WHERE slug = 'dymatize';
    SELECT id INTO v_my_id       FROM brands WHERE slug = 'myprotein';
    SELECT id INTO v_bpi_id      FROM brands WHERE slug = 'bpi-sports';
    SELECT id INTO v_mu_id       FROM brands WHERE slug = 'mutant';
    SELECT id INTO v_nu_id       FROM brands WHERE slug = 'nutrex-research';

    -- P1: Rule 1 Protein 5lbs
    INSERT INTO products (name, slug, description, category_id, brand_id, price, sale_price, weight_grams, flavor, serving_size, status)
    VALUES ('Rule 1 Protein Isolate 5lbs', 'rule-1-protein-isolate-5lbs',
            '100% Whey Isolate & Hydrolyzed siêu tinh khiết. 25g protein mỗi liều dùng, 0 đường, 0 chất béo.',
            v_whey_id, v_r1_id, 1650000, 1450000, 2270, 'Chocolate Fudge', 76, 'ACTIVE')
    ON CONFLICT (slug) DO UPDATE SET price = EXCLUDED.price, sale_price = EXCLUDED.sale_price, status = 'ACTIVE'
    RETURNING id INTO v_pid;
    IF v_pid IS NOT NULL THEN
        DELETE FROM product_images WHERE product_id = v_pid;
        INSERT INTO product_images (product_id, image_url, is_primary) VALUES
        (v_pid, 'https://images.unsplash.com/photo-1593095948071-474c5cc2989d?w=600&auto=format&fit=crop&q=80', true);
    END IF;

    -- P2: ISO 100 5lbs Dymatize
    INSERT INTO products (name, slug, description, category_id, brand_id, price, sale_price, weight_grams, flavor, serving_size, status)
    VALUES ('Dymatize ISO 100 Hydrolyzed 5lbs', 'dymatize-iso-100-5lbs',
            'Đạt giải thưởng Whey Protein Isolate của năm. 25g Hydrolyzed Isolate hấp thu siêu nhanh, vị ngon đỉnh cao.',
            v_whey_id, v_dy_id, 1950000, 1790000, 2300, 'Gourmet Chocolate', 71, 'ACTIVE')
    ON CONFLICT (slug) DO UPDATE SET price = EXCLUDED.price, sale_price = EXCLUDED.sale_price, status = 'ACTIVE'
    RETURNING id INTO v_pid;
    IF v_pid IS NOT NULL THEN
        DELETE FROM product_images WHERE product_id = v_pid;
        INSERT INTO product_images (product_id, image_url, is_primary) VALUES
        (v_pid, 'https://images.unsplash.com/photo-1579722821273-0f6c7d44362f?w=600&auto=format&fit=crop&q=80', true);
    END IF;

    -- P3: Mutant Mass 15lbs
    INSERT INTO products (name, slug, description, category_id, brand_id, price, sale_price, weight_grams, flavor, serving_size, status)
    VALUES ('Mutant Mass Gainer 15lbs (6.8kg)', 'mutant-mass-gainer-15lbs',
            'Sữa tăng cân tăng cơ siêu khủng. Cung cấp 1100 calo, 56g protein chất lượng cao cùng yến mạch và hạt lanh.',
            v_mass_id, v_mu_id, 2100000, 1890000, 6800, 'Triple Chocolate', 280, 'ACTIVE')
    ON CONFLICT (slug) DO UPDATE SET price = EXCLUDED.price, sale_price = EXCLUDED.sale_price, status = 'ACTIVE'
    RETURNING id INTO v_pid;
    IF v_pid IS NOT NULL THEN
        DELETE FROM product_images WHERE product_id = v_pid;
        INSERT INTO product_images (product_id, image_url, is_primary) VALUES
        (v_pid, 'https://images.unsplash.com/photo-1583454110551-21f2fa2afe61?w=600&auto=format&fit=crop&q=80', true);
    END IF;

    -- P4: Applied Nutrition ABE Pre-Workout
    INSERT INTO products (name, slug, description, category_id, brand_id, price, sale_price, weight_grams, flavor, serving_size, status)
    VALUES ('Applied Nutrition ABE Pre-Workout 315g', 'applied-nutrition-abe-pre-workout',
            'Dòng pre-workout bán chạy nhất Anh Quốc. Bổ sung TeaCrine, Tri-Creatine, Citrulline Malate và Beta-Alanine bùng nổ sức mạnh.',
            v_pre_id, v_an_id, 650000, 580000, 315, 'Energy Flavour', 30, 'ACTIVE')
    ON CONFLICT (slug) DO UPDATE SET price = EXCLUDED.price, sale_price = EXCLUDED.sale_price, status = 'ACTIVE'
    RETURNING id INTO v_pid;
    IF v_pid IS NOT NULL THEN
        DELETE FROM product_images WHERE product_id = v_pid;
        INSERT INTO product_images (product_id, image_url, is_primary) VALUES
        (v_pid, 'https://images.unsplash.com/photo-1517838277536-f5f99be501cd?w=600&auto=format&fit=crop&q=80', true);
    END IF;

    -- P5: BPI Best BCAA 30 servings
    INSERT INTO products (name, slug, description, category_id, brand_id, price, sale_price, weight_grams, flavor, serving_size, status)
    VALUES ('BPI Sports Best BCAA 30 Servings', 'bpi-sports-best-bcaa-30-servings',
            'Công nghệ BCAA liên kết Oligopeptide độc quyền giúp hấp thu tối ưu. Bổ sung CLA hỗ trợ chuyển hóa mỡ.',
            v_bcaa_id, v_bpi_id, 550000, 490000, 300, 'Watermelon Ice', 30, 'ACTIVE')
    ON CONFLICT (slug) DO UPDATE SET price = EXCLUDED.price, sale_price = EXCLUDED.sale_price, status = 'ACTIVE'
    RETURNING id INTO v_pid;
    IF v_pid IS NOT NULL THEN
        DELETE FROM product_images WHERE product_id = v_pid;
        INSERT INTO product_images (product_id, image_url, is_primary) VALUES
        (v_pid, 'https://images.unsplash.com/photo-1540420773420-3366772f4999?w=600&auto=format&fit=crop&q=80', true);
    END IF;

    -- P6: MuscleTech Platinum 100% Creatine
    INSERT INTO products (name, slug, description, category_id, brand_id, price, sale_price, weight_grams, flavor, serving_size, status)
    VALUES ('MuscleTech Platinum 100% Creatine 400g', 'muscletech-platinum-creatine-400g',
            'Creatine Monohydrate siêu vi hạt HPLC tinh khiết nhất. Giúp tăng sức mạnh, sức bền và phát triển cơ bắp không mỡ.',
            v_creatine_id, v_mt_id, 450000, 390000, 400, 'Unflavored', 80, 'ACTIVE')
    ON CONFLICT (slug) DO UPDATE SET price = EXCLUDED.price, sale_price = EXCLUDED.sale_price, status = 'ACTIVE'
    RETURNING id INTO v_pid;
    IF v_pid IS NOT NULL THEN
        DELETE FROM product_images WHERE product_id = v_pid;
        INSERT INTO product_images (product_id, image_url, is_primary) VALUES
        (v_pid, 'https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=600&auto=format&fit=crop&q=80', true);
    END IF;

    -- P7: Nutrex Lipo-6 Black Ultra Concentrate
    INSERT INTO products (name, slug, description, category_id, brand_id, price, sale_price, weight_grams, flavor, serving_size, status)
    VALUES ('Nutrex Lipo-6 Black Ultra Concentrate 60 viên', 'nutrex-lipo-6-black-ultra-concentrate',
            'Viên đốt mỡ mạnh mẽ hàng đầu thế giới. Kích hoạt chuyển hóa, tăng sinh nhiệt và ức chế cảm giác thèm ăn.',
            v_fat_id, v_nu_id, 620000, 540000, 150, 'Capsules', 60, 'ACTIVE')
    ON CONFLICT (slug) DO UPDATE SET price = EXCLUDED.price, sale_price = EXCLUDED.sale_price, status = 'ACTIVE'
    RETURNING id INTO v_pid;
    IF v_pid IS NOT NULL THEN
        DELETE FROM product_images WHERE product_id = v_pid;
        INSERT INTO product_images (product_id, image_url, is_primary) VALUES
        (v_pid, 'https://images.unsplash.com/photo-1574680096145-d05b474e2155?w=600&auto=format&fit=crop&q=80', true);
    END IF;

    -- P8: MyProtein Impact Whey Protein 2.5kg
    INSERT INTO products (name, slug, description, category_id, brand_id, price, sale_price, weight_grams, flavor, serving_size, status)
    VALUES ('MyProtein Impact Whey Protein 2.5kg', 'myprotein-impact-whey-2-5kg',
            'Whey Protein bán chạy nhất Châu Âu. 21g protein mỗi serving từ bò ăn cỏ tự nhiên, giá thành tối ưu cho sinh viên gymer.',
            v_whey_id, v_my_id, 1400000, 1250000, 2500, 'Milk Tea', 100, 'ACTIVE')
    ON CONFLICT (slug) DO UPDATE SET price = EXCLUDED.price, sale_price = EXCLUDED.sale_price, status = 'ACTIVE'
    RETURNING id INTO v_pid;
    IF v_pid IS NOT NULL THEN
        DELETE FROM product_images WHERE product_id = v_pid;
        INSERT INTO product_images (product_id, image_url, is_primary) VALUES
        (v_pid, 'https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=600&auto=format&fit=crop&q=80', true);
    END IF;

    -- P9: ON Opti-Men Daily Multivitamin 90 viên
    INSERT INTO products (name, slug, description, category_id, brand_id, price, sale_price, weight_grams, flavor, serving_size, status)
    VALUES ('Optimum Nutrition Opti-Men Multivitamin 90 viên', 'on-opti-men-multivitamin-90-tablets',
            'Hệ thống tối ưu hóa dinh dưỡng gồm hơn 75 thành phần vi lượng thiết yếu dành riêng cho nam giới vận động cao.',
            v_vit_id, v_on_id, 580000, 520000, 200, 'Tablets', 30, 'ACTIVE')
    ON CONFLICT (slug) DO UPDATE SET price = EXCLUDED.price, sale_price = EXCLUDED.sale_price, status = 'ACTIVE'
    RETURNING id INTO v_pid;
    IF v_pid IS NOT NULL THEN
        DELETE FROM product_images WHERE product_id = v_pid;
        INSERT INTO product_images (product_id, image_url, is_primary) VALUES
        (v_pid, 'https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=600&auto=format&fit=crop&q=80', true);
    END IF;
END $$;

