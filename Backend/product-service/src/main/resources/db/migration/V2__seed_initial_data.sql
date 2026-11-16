-- Product Service - Seed Initial Data

INSERT INTO categories (slug, name, description) VALUES
    ('protein',          'Protein',           'Protein supplements for muscle building and recovery'),
    ('pre-workout',      'Pre-Workout',        'Energy and performance boosting supplements'),
    ('post-workout',     'Post-Workout',       'Recovery and muscle repair supplements'),
    ('vitamins',         'Vitamins & Minerals','Essential vitamins and minerals'),
    ('weight-loss',      'Weight Loss',        'Fat burning and weight management products'),
    ('creatine',         'Creatine',           'Creatine supplements for strength and power'),
    ('bcaas',            'BCAAs',              'Branched-chain amino acids for muscle recovery'),
    ('mass-gainers',     'Mass Gainers',       'High-calorie supplements for muscle mass')
ON CONFLICT (slug) DO NOTHING;

INSERT INTO brands (slug, name, description) VALUES
    ('optimum-nutrition', 'Optimum Nutrition',  'Leading sports nutrition brand'),
    ('muscletech',        'MuscleTech',          'Science-driven supplements for athletes'),
    ('bsn',               'BSN',                 'Premium sports nutrition products'),
    ('dymatize',          'Dymatize',            'High-quality protein and supplements'),
    ('cellucor',          'Cellucor',            'Performance and lifestyle supplements'),
    ('universal',         'Universal Nutrition', 'Trusted bodybuilding supplements'),
    ('myprotein',         'MyProtein',           'Affordable quality sports nutrition'),
    ('scitec',            'Scitec Nutrition',    'European sports nutrition leader')
ON CONFLICT (slug) DO NOTHING;

-- Sample products
DO $$
DECLARE
    v_protein_id     BIGINT;
    v_pre_workout_id BIGINT;
    v_on_id          BIGINT;
    v_mt_id          BIGINT;
    v_cc_id          BIGINT;
    v_prod1          BIGINT;
    v_prod2          BIGINT;
    v_prod3          BIGINT;
BEGIN
    SELECT id INTO v_protein_id     FROM categories WHERE slug = 'protein';
    SELECT id INTO v_pre_workout_id FROM categories WHERE slug = 'pre-workout';
    SELECT id INTO v_on_id          FROM brands     WHERE slug = 'optimum-nutrition';
    SELECT id INTO v_mt_id          FROM brands     WHERE slug = 'muscletech';
    SELECT id INTO v_cc_id          FROM brands     WHERE slug = 'cellucor';

    INSERT INTO products (name, slug, description, category_id, brand_id, price, sale_price, status)
    VALUES (
        'ON Gold Standard Whey 5lbs',
        'on-gold-standard-whey-5lbs',
        '24g protein per serving. The world best-selling whey protein.',
        v_protein_id, v_on_id, 1200000, 1050000, 'ACTIVE'
    ) ON CONFLICT (slug) DO NOTHING
    RETURNING id INTO v_prod1;

    IF v_prod1 IS NOT NULL THEN
        INSERT INTO product_images (product_id, image_url, is_primary) VALUES
        (v_prod1, 'https://placehold.co/400x400/1a73e8/white?text=ON+Whey', true);
    END IF;

    INSERT INTO products (name, slug, description, category_id, brand_id, price, status)
    VALUES (
        'MuscleTech NitroTech Whey Gold 5.5lbs',
        'muscletech-nitrotech-whey-gold-5-5lbs',
        '24g protein with 5.5g BCAAs per serving.',
        v_protein_id, v_mt_id, 1300000, 'ACTIVE'
    ) ON CONFLICT (slug) DO NOTHING
    RETURNING id INTO v_prod2;

    IF v_prod2 IS NOT NULL THEN
        INSERT INTO product_images (product_id, image_url, is_primary) VALUES
        (v_prod2, 'https://placehold.co/400x400/e81a1a/white?text=MT+NitroTech', true);
    END IF;

    INSERT INTO products (name, slug, description, category_id, brand_id, price, sale_price, status)
    VALUES (
        'Cellucor C4 Original Pre-Workout',
        'cellucor-c4-original-pre-workout',
        'America #1 selling pre-workout. Explosive energy and enhanced performance.',
        v_pre_workout_id, v_cc_id, 800000, 720000, 'ACTIVE'
    ) ON CONFLICT (slug) DO NOTHING
    RETURNING id INTO v_prod3;

    IF v_prod3 IS NOT NULL THEN
        INSERT INTO product_images (product_id, image_url, is_primary) VALUES
        (v_prod3, 'https://placehold.co/400x400/ffa500/white?text=C4+Pre', true);
    END IF;
END $$;
