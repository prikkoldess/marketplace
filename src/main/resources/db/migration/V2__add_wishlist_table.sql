CREATE TABLE wishlist (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products (id),
    buyer_id BIGINT NOT NULL REFERENCES users (id)
);