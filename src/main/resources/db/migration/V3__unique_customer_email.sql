-- Each email can only belong to one customer.
ALTER TABLE customer ADD UNIQUE KEY email (email);
