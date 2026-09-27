-- Sample catalog data. img_src values are object keys uploaded to MinIO from docker/minio/seed.

INSERT INTO phones (`condition`, name, rating, price, color, img_src, alt, quantity) VALUES
    ('New',  'Moto X',          4, 399.99, 'Black', 'blackMotoX.jpg',         'Black Moto X',          10),
    ('New',  'Google Pixel XL', 5, 769.00, 'Black', 'blackgooglepixelXL.jpg', 'Black Google Pixel XL', 8),
    ('New',  'Samsung Galaxy S8', 5, 724.99, 'Black', 'blacks8.jpg',          'Black Samsung Galaxy S8', 12),
    ('Used', 'iPhone 7',        4, 549.00, 'Gold',  'goldiPhone7.jpg',        'Gold iPhone 7',         5),
    ('New',  'iPhone 7',        5, 749.00, 'Red',   'rediphone.jpg',          'Red iPhone 7',          7);

INSERT INTO phoneplans (numberOfLines, monthlyRate, description) VALUES
    (1, 40.00,  'Single line with unlimited talk & text and 5GB of data'),
    (2, 70.00,  'Two lines with unlimited talk & text and 10GB of shared data'),
    (4, 120.00, 'Family plan: up to four lines with unlimited talk, text and data');
