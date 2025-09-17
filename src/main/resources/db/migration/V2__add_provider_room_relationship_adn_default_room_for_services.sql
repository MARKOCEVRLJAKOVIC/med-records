ALTER TABLE rooms
ADD COLUMN provider_id BIGINT REFERENCES providers(id) ON DELETE CASCADE;

ALTER TABLE services
ADD COLUMN default_room_id BIGINT REFERENCES rooms(id) ON DELETE SET NULL;

CREATE INDEX idx_rooms_provider_id ON rooms(provider_id);
CREATE INDEX idx_services_default_room_id ON services(default_room_id);