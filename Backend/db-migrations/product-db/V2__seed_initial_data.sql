-- Product Database - Seed Initial Data
-- Version: 2
-- Description: Insert initial categories, brands, and sample products

-- ============================================
-- Categories
-- ============================================
INSERT INTO categories (name, slug, description) VALUES
    ('Protein', 'protein', 'Protein supplements for muscle building and recovery'),
    ('Pre-Workout', 'pre-workout', 'Energy and performance boosting supplements'),
    ('Post-Workout', 'post-workout', 'Recovery and muscle repair supplements'),
    ('Vitamins & Minerals', 'vitamins-minerals', 'Essential vitamins and minerals'),
    ('Weight Loss', 'weight-loss', 'Fat burning and weight management products'),
    ('Creatine', 'creatine', 'Creatine supplements for strength and power'),
    ('BCAAs', 'bcaas', 'Branched-chain amino acids for muscle recovery'),
    ('Mass Gainers', 'mass-gainers', 'High-calorie supplements for muscle mass')
ON CONFLICT (slug) DO NOTHING;

-- ============================================
-- Brands
-- ============================================
INSERT INTO brands (name, slug, description) VALUES
    ('Optimum Nutrition', 'optimum-nutrition', 'Leading sports nutrition brand'),
    ('MuscleTech', 'muscletech', 'Science-driven supplements for athletes'),
    ('BSN', 'bsn', 'Premium sports nutrition products'),
    ('Dymatize', 'dymatize', 'High-quality protein and supplements'),
    ('Cellucor', 'cellucor', 'Performance and lifestyle supplements'),
    ('Universal Nutrition', 'universal-nutrition', 'Trusted bodybuilding supplements'),
    ('MyProtein', 'myprotein', 'Affordable quality sports nutrition'),
    ('Scitec Nutrition', 'scitec-nutrition', 'European sports nutrition leader')
ON CONFLICT (slug) DO NOTHING;

-- ============================================
-- Sample Products
-- ============================================
-- Get category and brand IDs
DO $$
DECLARE
    v_protein_cat_id BIGINT;
    v_pre_workout_cat_id BIGINT;
    v_on_brand_id BIGINT;
    v_muscletech_brand_id BIGINT;
BEGIN
    SELECT id INTO v_protein_cat_id FROM categories WHERE slug = 'protein';
    SELECT id INTO v_pre_workout_cat_id FROM categories WHERE slug = 'pre-workout';
    SELECT id INTO v_on_brand_id FROM brands WHERE slug = 'optimum-nutrition';
    SELECT id INTO v_muscletech_brand_id FROM brands WHERE slug = 'muscletech';

    -- Insert sample products
    INSERT INTO products (
        name, slug, description, short_description,
        price, sale_price, status, category_id, brand_id,
        primary_image, is_featured
    ) VALUES
    (
        'Optimum Nutrition Gold Standard 100% Whey 5lbs',
        'on-gold-standard-whey-5lbs',
        'Gold Standard 100% Whey is the world''s best-selling whey protein powder. It provides 24g of protein per serving.',
        '24g Protein, 5.5g BCAAs, 4g Glutamine',
        1200000,
        1050000,
        'ACTIVE',
        v_protein_cat_id,
        v_on_brand_id,
        'https://placehold.co/400x400/1a73e8/white?text=ON+Whey',
        true
    ),
    (
        'MuscleTech NitroTech 100% Whey Gold 5.5lbs',
        'muscletech-nitrotech-whey-gold-5-5lbs',
        'Premium whey protein powder with 24g of protein and 5.5g of BCAAs per serving.',
        '24g Protein, 5.5g BCAAs, Enhanced with Creatine',
        1300000,
        NULL,
        'ACTIVE',
        v_protein_cat_id,
        v_muscletech_brand_id,
        'https://placehold.co/400x400/e81a1a/white?text=MT+NitroTech',
        true
    ),
    (
        'Cellucor C4 Original Pre-Workout',
        'cellucor-c4-original-pre-workout',
        'America''s #1 selling pre-workout brand. Explosive energy and enhanced performance.',
        'Explosive Energy, Enhanced Focus, Maximum Performance',
        800000,
        720000,
        'ACTIVE',
        v_pre_workout_cat_id,
        (SELECT id FROM brands WHERE slug = 'cellucor'),
        'https://placehold.co/400x400/ffa500/white?text=C4+Pre',
        true
    )
    ON CONFLICT (slug) DO NOTHING;
END $$;
