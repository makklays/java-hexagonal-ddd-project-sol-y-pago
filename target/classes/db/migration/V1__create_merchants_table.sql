-- V1__create_merchant_table.sql

-- Таблица Мерчантов (Интернет-магазинов)
CREATE TABLE merchants (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    api_key_hash VARCHAR(64) NOT NULL,       -- Хэш ключа для аутентификации запросов магазина
    status VARCHAR(20) NOT NULL,             -- ACTIVE, SUSPENDED
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

