INSERT INTO ticket_inventory (
    id,
    inventory_identifier,
    event_id,
    ticket_type,
    total_tickets,
    available_tickets
)
VALUES
    (
        3001,
        'inventory-demo-3001',
        'event-demo-active',
        'VIP',
        120,
        80
    ),
    (
        3002,
        'inventory-demo-3002',
        'event-demo-active',
        'STANDARD',
        200,
        5
    ),
    (
        3003,
        'inventory-demo-3003',
        'event-demo-cancelled',
        'VIP',
        50,
        0
    )
ON CONFLICT (id) DO NOTHING;

SELECT setval(
    pg_get_serial_sequence('ticket_inventory', 'id'),
    GREATEST((SELECT COALESCE(MAX(id), 1) FROM ticket_inventory), 1),
    true
);
