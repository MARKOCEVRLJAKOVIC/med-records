CREATE TABLE calendar_slots (
    id BIGSERIAL PRIMARY KEY,
    provider_id BIGINT NOT NULL REFERENCES providers(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status VARCHAR(20) CHECK (status IN ('AVAILABLE','BOOKED','CANCELLED')) NOT NULL,
    appointment_id BIGINT NULL REFERENCES appointments(id) ON DELETE SET NULL,
    client_name VARCHAR(255) NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_calendar_slots_provider_date
    ON calendar_slots(provider_id, date);