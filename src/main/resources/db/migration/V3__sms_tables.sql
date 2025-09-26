CREATE TABLE provider_phone_numbers (
    id BIGSERIAL PRIMARY KEY,
    phone_number VARCHAR(20) NOT NULL,
    twilio_sid VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    provider_id BIGINT NOT NULL REFERENCES providers(id) ON DELETE CASCADE

);

CREATE TABLE sms_messages (
    id BIGSERIAL PRIMARY KEY,
    direction VARCHAR(50) NOT NULL CHECK (direction IN ('SENT', 'RECEIVED')),
    from_number VARCHAR(20) NOT NULL,
    to_number VARCHAR(20) NOT NULL,
    body TEXT,
    status VARCHAR(20) DEFAULT 'SENT',
    twilio_sid VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    phone_number_id BIGINT NOT NULL REFERENCES provider_phone_numbers(id) ON DELETE CASCADE,
    provider_id BIGINT NOT NULL REFERENCES providers(id) ON DELETE CASCADE

);

