-- Customer roles: CUSTOMER (default) or ADMIN.
ALTER TABLE customer ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER';

UPDATE customer SET role = 'ADMIN' WHERE email = 'bobjoe@gmail.com';
