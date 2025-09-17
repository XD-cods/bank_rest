-- initial-db.sql
-- Заполнение базы данных тестовыми данными

-- Очистка существующих данных (опционально, если нужно перезаполнить)
-- DELETE FROM cards;
-- DELETE FROM users;

-- Вставка тестовых пользователей
INSERT INTO users (id, first_name, last_name, email, encrypted_password, role, is_active, created_at, updated_at) VALUES
-- Пароль для всех пользователей: "Password123!"
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Иван', 'Иванов', 'ivan@example.com', '$2a$10$rOzJq5Q1U6UQ6UQ6UQ6UQOe6UQ6UQ6UQ6UQ6UQ6UQ6UQ6UQ6UQ6U', 1, true, NOW(), NOW()),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Петр', 'Петров', 'petr@example.com', '$2a$10$rOzJq5Q1U6UQ6UQ6UQ6UQOe6UQ6UQ6UQ6UQ6UQ6UQ6UQ6UQ6UQ6U', 0, true, NOW(), NOW()),
('c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Мария', 'Сидорова', 'maria@example.com', '$2a$10$rOzJq5Q1U6UQ6UQ6UQ6UQOe6UQ6UQ6UQ6UQ6UQ6UQ6UQ6UQ6UQ6U', 0, true, NOW(), NOW()),
('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'Анна', 'Кузнецова', 'anna@example.com', '$2a$10$rOzJq5Q1U6UQ6UQ6UQ6UQOe6UQ6UQ6UQ6UQ6UQ6UQ6UQ6UQ6UQ6U', 0, false, NOW(), NOW());

-- Вставка тестовых банковских карт (только валидные UUID с символами a-f)
INSERT INTO cards (id, masked_card_number, card_number_hash, expiry_date, owner_id, card_status, balance, created_at, updated_at) VALUES
-- Карты для Ивана (администратора)
('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', '4111********1111', 'hash_4111111111111111', '12/2025', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 0, 15000.50, NOW(), NOW()),
('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', '5500********0001', 'hash_5500000000000001', '03/2026', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 0, 27500.00, NOW(), NOW()),

-- Карты для Петра
('a6eebc99-9c0b-4ef8-bb6d-6bb9bd380a23', '3782********0005', 'hash_378282246310005', '09/2024', 'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 0, 8500.75, NOW(), NOW()),
('b7eebc99-9c0b-4ef8-bb6d-6bb9bd380a24', '6011********1111', 'hash_6011111111111111', '01/2025', 'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 1, 1200.00, NOW(), NOW()),

-- Карты для Марии
('c8eebc99-9c0b-4ef8-bb6d-6bb9bd380a25', '3056********0002', 'hash_30569309025904', '06/2024', 'c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 0, 45000.25, NOW(), NOW()),
('d9eebc99-9c0b-4ef8-bb6d-6bb9bd380a26', '4111********2222', 'hash_4111111111112222', '11/2025', 'c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 2, 0.00, NOW(), NOW()),

-- Карта для Анны (неактивный пользователь)
('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a27', '5105********1234', 'hash_5105105105101234', '08/2024', 'd3eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 0, 9800.00, NOW(), NOW());

-- Дополнительные карты для разнообразия
INSERT INTO cards (id, masked_card_number, card_number_hash, expiry_date, owner_id, card_status, balance, created_at, updated_at) VALUES
('f1eebc99-9c0b-4ef8-bb6d-6bb9bd380a28', '3714********0000', 'hash_371449635398431', '02/2026', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 0, 50000.00, NOW(), NOW()),
('a2eebc99-9c0b-4ef8-bb6d-6bb9bd380a29', '3600********1111', 'hash_3600000000001111', '05/2025', 'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 0, 1500.50, NOW(), NOW()),
('b3eebc99-9c0b-4ef8-bb6d-6bb9bd380a30', '4111********3333', 'hash_4111111111113333', '12/2024', 'c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 1, 750.25, NOW(), NOW());

