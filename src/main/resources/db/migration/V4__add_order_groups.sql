CREATE TABLE order_groups (
    id UUID PRIMARY KEY,
    buyer_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    total_amount DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_order_groups_buyer FOREIGN KEY (buyer_id) REFERENCES users (id)
);

ALTER TABLE orders RENAME COLUMN checkout_group_id TO order_group_id;

ALTER TABLE orders
ADD CONSTRAINT fk_orders_order_group FOREIGN KEY (order_group_id) REFERENCES order_groups (id);