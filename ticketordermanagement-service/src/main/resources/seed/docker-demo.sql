INSERT INTO ticket_orders (
    id,
    order_identifier,
    customer_id,
    event_id,
    ticket_type,
    quantity,
    total_price,
    status
)
VALUES
    (
        4001,
        'order-demo-4001',
        1001,
        'event-demo-active',
        'VIP',
        2,
        199.98,
        'CONFIRMED'
    )
ON CONFLICT (id) DO NOTHING;

SELECT setval(
    pg_get_serial_sequence('ticket_orders', 'id'),
    GREATEST((SELECT COALESCE(MAX(id), 1) FROM ticket_orders), 1),
    true
);
