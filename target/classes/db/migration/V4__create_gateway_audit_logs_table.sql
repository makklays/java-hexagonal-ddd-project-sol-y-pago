-- V4__create_gateway_audit_logs_table.sql

-- Создаем таблицу технических логов взаимодействия с банками/шлюзами
CREATE TABLE gateway_audit_logs (
    id UUID PRIMARY KEY,
    transaction_id UUID NOT NULL,
    provider_name VARCHAR(50) NOT NULL,      -- STRIPE, BIZUM
    request_url VARCHAR(512) NOT NULL,       -- Куда отправляли запрос
    http_status INT NOT NULL,                -- 200, 400, 500
    raw_request TEXT NOT NULL,               -- Полный JSON запроса к Stripe/Bizum
    raw_response TEXT NOT NULL,              -- Полный JSON ответа от Stripe/Bizum
    error_code VARCHAR(100),                 -- Код ошибки от банка, если платеж зафейлился
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    -- Связь с транзакцией. Если транзакции нет — лог не имеет смысла
    CONSTRAINT fk_audit_log_transaction FOREIGN KEY (transaction_id) REFERENCES payment_transactions(id) ON DELETE CASCADE
);

-- Индекс по transaction_id для мгновенного поиска логов по конкретному платежу
CREATE INDEX idx_gateway_audit_logs_transaction ON gateway_audit_logs(transaction_id);

