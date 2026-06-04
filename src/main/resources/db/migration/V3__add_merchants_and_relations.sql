CREATE TABLE merchants (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    invite_code VARCHAR(255) UNIQUE
);

ALTER TABLE users ADD COLUMN merchant_id UUID;

ALTER TABLE users
ADD CONSTRAINT fk_users_merchant FOREIGN KEY (merchant_id) REFERENCES merchants (id);

ALTER TABLE products ADD COLUMN merchant_id UUID;

ALTER TABLE products
ADD CONSTRAINT fk_products_merchant FOREIGN KEY (merchant_id) REFERENCES merchants (id);

ALTER TABLE products DROP COLUMN seller_id CASCADE;

CREATE INDEX idx_products_merchant_id ON products (merchant_id);

ALTER TABLE orders ADD COLUMN merchant_id UUID;

ALTER TABLE orders
ADD CONSTRAINT fk_orders_merchant FOREIGN KEY (merchant_id) REFERENCES merchants (id);

CREATE INDEX idx_orders_merchant_id ON orders (merchant_id);

ALTER TABLE order_item DROP COLUMN seller_id CASCADE;