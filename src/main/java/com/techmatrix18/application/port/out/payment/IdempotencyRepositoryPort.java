package com.techmatrix18.application.port.out.payment;

import java.util.Optional;

/**
 * IdempotencyRepositoryPort is an interface that defines methods for handling idempotent payment processing.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 25.07.2026
 */
public interface IdempotencyRepositoryPort {
    // Ищет закешированный JSON-ответ, если платеж уже был успешно обработан
    Optional<String> findResponse(String key);

    // Сохраняет финальный JSON-ответ в базу данных со статусом 'COMPLETED'
    void saveResponse(String key, String responseBody);
}

