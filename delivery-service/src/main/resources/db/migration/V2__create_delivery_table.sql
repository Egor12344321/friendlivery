SET search_path TO delivery_schema;

CREATE TABLE deliveries (
    id BIGSERIAL PRIMARY KEY,
    tracking_number VARCHAR(50) UNIQUE NOT NULL,
    sender_id BIGINT NOT NULL,
    courier_id BIGINT,

    from_airport VARCHAR(3) NOT NULL CHECK (length(from_airport) = 3),
    to_airport VARCHAR(3) NOT NULL CHECK (length(to_airport) = 3),

    desired_date DATE NOT NULL,
    delivery_deadline DATE NOT NULL,

    description TEXT,
    weight DOUBLE PRECISION NOT NULL CHECK (weight > 0), -- У тебя Double, не Decimal

    courier_accepted_at TIMESTAMP,
    sender_accepted_at TIMESTAMP,

    status VARCHAR(50) NOT NULL DEFAULT 'CREATED',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    price DECIMAL(15, 2) NOT NULL DEFAULT 0.00 CHECK (price >= 0),
    dimensions VARCHAR(50),

    CONSTRAINT chk_dates_valid CHECK (desired_date <= delivery_deadline),
    CONSTRAINT chk_airports_different CHECK (from_airport <> to_airport)
);
