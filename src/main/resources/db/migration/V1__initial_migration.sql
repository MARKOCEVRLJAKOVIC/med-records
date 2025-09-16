CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) CHECK (role IN ('ADMIN','RECEPTIONIST','CLIENT','PROVIDER')) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE clients (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    date_of_birth DATE,
    gender VARCHAR(20),
    address TEXT,
    allergies TEXT,
    medical_notes TEXT,
    user_id BIGINT UNIQUE REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE providers (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    specialty VARCHAR(255),
    license_number VARCHAR(100),
    employment_start DATE,
    employment_end DATE,
    user_id BIGINT UNIQUE REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE services (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2),
    duration_minutes INT,
    provider_id BIGINT NOT NULL REFERENCES providers(id) ON DELETE CASCADE
);

CREATE TABLE rooms (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(255),
    type VARCHAR(50) DEFAULT 'ROOM',
    capacity INT DEFAULT 1,
    is_active BOOLEAN DEFAULT TRUE
);


CREATE TABLE appointments (
    id BIGSERIAL PRIMARY KEY,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    status VARCHAR(50) CHECK (status IN ('UNCONFIRMED','CONFIRMED','ARRIVED','COMPLETED','CANCELED','NO_SHOW')) NOT NULL,
    notes TEXT,
    client_id BIGINT REFERENCES clients(id) ON DELETE CASCADE,
    provider_id BIGINT REFERENCES providers(id) ON DELETE RESTRICT,
    service_id BIGINT REFERENCES services(id) ON DELETE SET NULL,
    room_id BIGINT REFERENCES rooms(id) ON DELETE SET NULL,
    recurrence_rule VARCHAR(255),  -- e.g., iCal RRULE
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Pivot table: multiple services per appointment
CREATE TABLE appointment_services (
    appointment_id BIGINT REFERENCES appointments(id) ON DELETE CASCADE,
    service_id BIGINT REFERENCES services(id) ON DELETE CASCADE,
    duration_override INT,
    price_override DECIMAL(10,2),
    PRIMARY KEY (appointment_id, service_id)
);

-- Appointment notifications/reminders
CREATE TABLE appointment_notifications (
    id BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT REFERENCES appointments(id) ON DELETE CASCADE,
    status VARCHAR(50) CHECK (status IN ('PENDING','SENT','FAILED')) DEFAULT 'PENDING',
    notification_type VARCHAR(50) DEFAULT 'EMAIL', -- or SMS, PUSH
    sent_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE memberships (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT REFERENCES clients(id) ON DELETE CASCADE,
    type VARCHAR(100) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    status VARCHAR(50) CHECK (status IN ('ACTIVE','PAUSED','EXPIRED','CANCELED')),
    monthly_fee DECIMAL(10,2),
    included_services JSONB,
    discount_rate DECIMAL(5,2)
);

CREATE TABLE medical_records (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT REFERENCES clients(id) ON DELETE CASCADE,
    provider_id BIGINT REFERENCES providers(id),
    service_id BIGINT REFERENCES services(id),
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    treatment_type VARCHAR(255),
    medications_used TEXT,
    notes TEXT,
    follow_up_required BOOLEAN DEFAULT FALSE
);

CREATE TABLE photos (
    id BIGSERIAL PRIMARY KEY,
    public_id VARCHAR(255),
    type VARCHAR(50) CHECK (type IN ('BEFORE','AFTER','SIDE','FRONT','PROFILE')),
    taken_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    medical_record_id BIGINT REFERENCES medical_records(id) ON DELETE CASCADE,
    client_id BIGINT REFERENCES clients(id) ON DELETE CASCADE
);

CREATE TABLE inventory (
    id BIGSERIAL PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    product_type VARCHAR(50),
    lot_number VARCHAR(100),
    supplier VARCHAR(255),
    expiry_date DATE,
    quantity INT NOT NULL DEFAULT 0,
    low_stock_threshold INT DEFAULT 10,
    unit VARCHAR(20)
);

CREATE TABLE client_analytics (
    client_id BIGINT PRIMARY KEY REFERENCES clients(id) ON DELETE CASCADE,
    show_rate DECIMAL(5,2),
    avg_revisit_weeks DECIMAL(5,2),
    avg_visit_value DECIMAL(10,2),
    total_visits INT DEFAULT 0,
    total_spent DECIMAL(10,2) DEFAULT 0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_appointments_client_id ON appointments(client_id);
CREATE INDEX idx_appointments_provider_id ON appointments(provider_id);
CREATE INDEX idx_appointment_services_appointment_id ON appointment_services(appointment_id);
CREATE INDEX idx_appointment_services_service_id ON appointment_services(service_id);
CREATE INDEX idx_appointment_notifications_appointment_id ON appointment_notifications(appointment_id);
CREATE INDEX idx_appointments_room_id ON appointments(room_id);
