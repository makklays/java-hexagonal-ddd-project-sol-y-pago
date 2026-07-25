package com.techmatrix18.application.command.payment;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Command object representing a request to process a payment. This record encapsulates all necessary information required to initiate a payment transaction.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 24.07.2026
 */
public record ProcessPaymentCommand(
    UUID merchantId,
    String idempotencyKey,
    String apiKey,
    BigDecimal amount,
    String currency,
    String countryCode,
    String customerEmail,
    String customerPhone,
    String paymentMethodName // Передаем как строку "CARD", "BIZUM" и т.д.
) {
    public ProcessPaymentCommand {
        if (merchantId == null) throw new IllegalArgumentException("Merchant ID is required");

        // Валидация ключа идемпотентности: он не должен быть пустым
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("Idempotency key is required for secure financial transactions");
        }

        if (apiKey == null || apiKey.isBlank()) throw new IllegalArgumentException("API key is required");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Amount must be greater than zero");
        if (currency == null || currency.length() != 3) throw new IllegalArgumentException("Currency must be a 3-letter ISO code");
        if (countryCode == null || countryCode.length() != 2) throw new IllegalArgumentException("Country code must be a 2-letter ISO code");
    }
}

