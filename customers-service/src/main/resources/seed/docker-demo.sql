INSERT INTO customers (id, customer_identifier, first_name, last_name, email_address, phone_number)
VALUES
    (1001, 'customer-demo-1001', 'Alice', 'Anderson', 'alice.demo@example.com', '5145551001'),
    (1002, 'customer-demo-1002', 'Bob', 'Bennett', '5145551002@invalid', '5145551002'),
    (1003, 'customer-demo-1003', 'Ivy', 'Ineligible', 'ivy.demo@example.com', '123')
ON CONFLICT (id) DO NOTHING;

SELECT setval(
    pg_get_serial_sequence('customers', 'id'),
    GREATEST((SELECT COALESCE(MAX(id), 1) FROM customers), 1),
    true
);
