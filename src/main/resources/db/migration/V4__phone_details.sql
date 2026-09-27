-- Details shown on the phone page.
ALTER TABLE phones
    ADD COLUMN description VARCHAR(1000),
    ADD COLUMN storage     VARCHAR(50),
    ADD COLUMN screen      VARCHAR(100),
    ADD COLUMN camera      VARCHAR(100),
    ADD COLUMN battery     VARCHAR(50);

-- Sample details for the seeded phones (matched by photo, since ids can differ between databases).
UPDATE phones SET
    description = 'A big, bright display and a fast-charging battery in a customizable design with water-repellent coating.',
    storage = '32 GB', screen = '5.7" LCD, 1440 x 2560', camera = '21 MP rear, 5 MP front', battery = '3000 mAh'
WHERE img_src = 'blackMotoX.jpg';

UPDATE phones SET
    description = 'Google''s own phone with a class-leading camera, fast updates and unlimited original-quality photo storage.',
    storage = '32 GB', screen = '5.5" AMOLED, 1440 x 2560', camera = '12.3 MP rear, 8 MP front', battery = '3450 mAh'
WHERE img_src = 'blackgooglepixelXL.jpg';

UPDATE phones SET
    description = 'An edge-to-edge Infinity Display in a slim, water-resistant body with wireless charging.',
    storage = '64 GB', screen = '5.8" AMOLED, 1440 x 2960', camera = '12 MP rear, 8 MP front', battery = '3000 mAh'
WHERE img_src = 'blacks8.jpg';

UPDATE phones SET
    description = 'A pre-owned iPhone 7 in great condition, tested and fully working. Water resistant with stereo speakers.',
    storage = '32 GB', screen = '4.7" Retina HD, 750 x 1334', camera = '12 MP rear, 7 MP front', battery = '1960 mAh'
WHERE img_src = 'goldiPhone7.jpg';

UPDATE phones SET
    description = 'The special edition red iPhone 7, with a water-resistant design, stereo speakers and a fast A10 Fusion chip.',
    storage = '128 GB', screen = '4.7" Retina HD, 750 x 1334', camera = '12 MP rear, 7 MP front', battery = '1960 mAh'
WHERE img_src = 'rediphone.jpg';
