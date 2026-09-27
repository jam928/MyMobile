-- Brand, used to filter the phone list.
ALTER TABLE phones ADD COLUMN brand VARCHAR(50);

UPDATE phones SET brand = 'Apple'    WHERE name LIKE 'iPhone%';
UPDATE phones SET brand = 'Samsung'  WHERE name LIKE 'Samsung%';
UPDATE phones SET brand = 'Google'   WHERE name LIKE 'Google%';
UPDATE phones SET brand = 'OnePlus'  WHERE name LIKE 'OnePlus%';
UPDATE phones SET brand = 'Motorola' WHERE name LIKE 'Moto%';
UPDATE phones SET brand = 'Nothing'  WHERE name LIKE 'Nothing%';
UPDATE phones SET brand = 'Sony'     WHERE name LIKE 'Sony%';
UPDATE phones SET brand = 'Other'    WHERE brand IS NULL;

ALTER TABLE phones MODIFY brand VARCHAR(50) NOT NULL, ADD INDEX idx_phones_brand (brand);
