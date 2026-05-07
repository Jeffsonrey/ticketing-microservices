INSERT INTO events (
    id,
    title,
    description,
    venue_id,
    venue_name,
    venue_address,
    venue_city,
    start_date_time,
    end_date_time,
    total_capacity,
    available_tickets,
    status
)
VALUES
    (
        'event-demo-active',
        'Montreal Jazz Demo',
        'Active seeded event for successful orchestration',
        'venue-demo-1001',
        'Place des Arts',
        '175 Sainte-Catherine St W',
        'Montreal',
        '2026-07-01 19:00:00',
        '2026-07-01 22:00:00',
        250,
        120,
        'ACTIVE'
    ),
    (
        'event-demo-cancelled',
        'Cancelled Demo Event',
        'Cancelled seeded event for negative eligibility checks',
        'venue-demo-1002',
        'Olympia',
        '1004 Sainte-Catherine St E',
        'Montreal',
        '2026-08-15 20:00:00',
        '2026-08-15 23:00:00',
        150,
        0,
        'CANCELLED'
    )
ON CONFLICT (id) DO NOTHING;
