package com.techmatrix18.infrastructure.persistence;

import com.techmatrix18.application.port.out.payment.GatewayAuditLogPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.UUID;

/**
 * Adapter class for the GatewayAuditLogPort interface. This class implements the port and provides the necessary
 * logic to interact with the underlying database for gateway audit log operations.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 25.07.2026
 */
@Component
public class GatewayAuditLogAdapter implements GatewayAuditLogPort {

    private final JdbcTemplate jdbcTemplate;

    public GatewayAuditLogAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void logResponse(UUID transactionId, String providerName, String url, int httpStatus,
                            String requestBody, String responseBody, String errorCode) {
        String sql = """
            INSERT INTO gateway_audit_logs 
            (id, transaction_id, provider_name, request_url, http_status, raw_request, raw_response, error_code, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(sql,
                UUID.randomUUID(), transactionId, providerName, url, httpStatus,
                requestBody, responseBody, errorCode, Instant.now()
        );
    }
}

