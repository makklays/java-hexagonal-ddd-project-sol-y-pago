package com.techmatrix18.application.port.out.payment;

import com.techmatrix18.domain.payment.PaymentTransaction;

/**
 * Payment gateway port interface for interacting with external payment providers. This interface defines the contract
 * for sending payment requests to banks or payment processors, allowing for different implementations based on
 * the specific provider (e.g., STRIPE, BIZUM).
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 24.07.2026
 */
public interface PaymentGatewayPort {
    // Метод отправки запроса в банк. Внутри адаптера будет происходить запись в GatewayAuditLog
    boolean charge(PaymentTransaction transaction);
    String getProviderName(); // "STRIPE" или "BIZUM"
}

