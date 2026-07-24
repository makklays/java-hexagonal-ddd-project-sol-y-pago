-- V3__create_payment_transactions_table.sql

-- Таблица Транзакций (Связана с Merchant и Customer)
CREATE TABLE payment_transactions (
    id UUID PRIMARY KEY,
    merchant_id UUID NOT NULL,
    customer_id UUID NOT NULL,

    amount NUMERIC(15, 2) NOT NULL,                       -- Строгий тип для денег
    currency VARCHAR(3) NOT NULL,                         -- EUR, USD
    country_code VARCHAR(2) NOT NULL,                     -- ES, UA

    status VARCHAR(20) NOT NULL,                          -- CREATED, SUCCESS, FAILED
    payment_method VARCHAR(30) NOT NULL DEFAULT 'CARD',   -- CARD, BANK_TRANSFER, PAYPAL

    provider_name VARCHAR(50),                            -- STRIPE, BIZUM
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    -- Внешние ключи для обеспечения целостности данных
    CONSTRAINT fk_transaction_merchant FOREIGN KEY (merchant_id) REFERENCES merchants(id),
    CONSTRAINT fk_transaction_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
);

-- Индексы для высокой производительности (Highload-кейсы)
CREATE INDEX idx_transactions_merchant ON payment_transactions(merchant_id);
CREATE INDEX idx_transactions_customer ON payment_transactions(customer_id);
CREATE INDEX idx_transactions_status ON payment_transactions(status);

