CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    role VARCHAR(50),
    status VARCHAR(50),
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255)
);

CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    price NUMERIC(19, 2) NOT NULL,
    quantity INTEGER NOT NULL,
    status VARCHAR(50),
    seller_id BIGINT REFERENCES users (id)
);

CREATE TABLE basket (
    id BIGSERIAL PRIMARY KEY,
    buyer_id BIGINT UNIQUE REFERENCES users (id)
);

CREATE TABLE basket_items (
    id BIGSERIAL PRIMARY KEY,
    basket_id BIGINT REFERENCES basket (id),
    product_id BIGINT REFERENCES products (id),
    quantity INTEGER NOT NULL
);

CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    buyer_id BIGINT REFERENCES users (id),
    checkout_group_id UUID,
    status VARCHAR(50),
    total_amount NUMERIC(19, 2)
);

CREATE TABLE order_item (
    id BIGSERIAL PRIMARY KEY,
    quantity INTEGER NOT NULL,
    price_at_purchase NUMERIC(19, 2) NOT NULL,
    product_id BIGINT REFERENCES products (id),
    seller_id BIGINT REFERENCES users (id),
    order_id BIGINT REFERENCES orders (id)
);

CREATE INDEX idx_products_seller_id ON products (seller_id);

CREATE INDEX idx_items_basket_id ON basket_items (basket_id);

CREATE INDEX idx_orders_buyer_id ON orders (buyer_id);

CREATE INDEX idx_order_item_order_id ON order_item (order_id);