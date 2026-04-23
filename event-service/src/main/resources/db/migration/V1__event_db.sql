


CREATE TABLE IF NOT EXISTS events (
                        id VARCHAR(255) PRIMARY KEY,

                        title VARCHAR(255) NOT NULL,
                        description VARCHAR(2000) NOT NULL,

                        venue_id VARCHAR(255) NOT NULL,
                        venue_name VARCHAR(255) NOT NULL,
                        venue_address VARCHAR(255) NOT NULL,
                        venue_city VARCHAR(255) NOT NULL,

                        start_date_time TIMESTAMP NOT NULL,
                        end_date_time TIMESTAMP NOT NULL,

                        total_capacity INT NOT NULL,
                        available_tickets INT NOT NULL,

                        status VARCHAR(50) NOT NULL
);

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
) VALUES (
             'event-001',
             'Jazz Night',
             'Live jazz concert in Montreal',
             'venue-001',
             'Place des Arts',
             '175 Sainte-Catherine St W',
             'Montreal',
             '2026-05-01 19:00:00',
             '2026-05-01 22:00:00',
             500,
             500,
             'INACTIVE'
         );