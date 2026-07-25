package com.techmatrix18.infrastructure.persistence;

import com.techmatrix18.application.port.out.payment.IdempotencyRepositoryPort;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.util.Optional;

/**
 * PostgresIdempotencyAdapter is an implementation of the IdempotencyRepositoryPort interface that interacts with
 * a PostgreSQL database to handle idempotent payment processing.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 25.07.2026
 */
@Component
public class PostgresIdempotencyAdapter implements IdempotencyRepositoryPort {

    private final JdbcTemplate jdbcTemplate;

    public PostgresIdempotencyAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<String> findResponse(String key) {
        // Извлекаем кэш ответа только для успешно завершенных ('COMPLETED') операций
        String sql = "SELECT response_body FROM idempotency_records WHERE idempotency_key = ? AND status = 'COMPLETED'";
        try {
            String response = jdbcTemplate.queryForObject(sql, String.class, key);
            return Optional.ofNullable(response);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty(); // Ключ не найден, запрос уникальный
        }
    }

    @Override
    public void saveResponse(String key, String responseBody) {
        // Записываем финальный ответ. Если запись со статусом PROCESSING уже была, обновляем её до COMPLETED
        String sql = """
            INSERT INTO idempotency_records (idempotency_key, request_payload_hash, status, response_code, response_body) 
            VALUES (?, 'computed_payload_hash_placeholder', 'COMPLETED', 200, ?)
            ON CONFLICT (idempotency_key) 
            DO UPDATE SET status = 'COMPLETED', response_code = 200, response_body = EXCLUDED.response_body;
        """;
        jdbcTemplate.update(sql, key, responseBody);
    }
}

