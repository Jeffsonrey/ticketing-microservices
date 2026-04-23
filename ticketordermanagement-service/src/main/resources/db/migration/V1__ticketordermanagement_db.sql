

CREATE TABLE IF NOT EXISTS ticket_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_identifier VARCHAR(255) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    event_id VARCHAR(255) NOT NULL,
    ticket_type VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    status VARCHAR(255) NOT NULL
);
