-- V2__create_merchant_table.sql

-- Таблица Клиентов (Покупателей)
CREATE TABLE customers (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE,               -- Уникальный email покупателя
    phone_number VARCHAR(30) UNIQUE,         -- Уникальный телефон для платежей Bizum
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

