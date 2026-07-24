package com.techmatrix18.application.port.out.payment;

import java.util.UUID;

/**
 * Port interface for logging HTTP exchanges with external banking APIs. This interface defines the contract for
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 24.07.2026
 */
public interface GatewayAuditLogPort {
    // Сохраняет технический лог HTTP обмена со сторонними API банков
    void logResponse(UUID transactionId, String providerName, String url, int httpStatus, String requestBody, String responseBody, String errorCode);
}

