package com.techmatrix18.application.port.out.payment;

import java.util.UUID;

/**
 * OutboxEventPort
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 26.07.2026
 */
public interface OutboxEventPort {
    void sendPaymentSuccessEvent(UUID transactionId, String payloadJson);
}

