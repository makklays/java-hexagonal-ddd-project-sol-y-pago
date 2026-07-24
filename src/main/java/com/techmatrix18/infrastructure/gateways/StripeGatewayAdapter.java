package com.techmatrix18.infrastructure.gateways;

import com.techmatrix18.application.port.out.payment.GatewayAuditLogPort;
import com.techmatrix18.application.port.out.payment.PaymentGatewayPort;
import com.techmatrix18.domain.payment.PaymentTransaction;
import org.springframework.stereotype.Component;

/**
 * Adapter class for the Stripe payment gateway. This class implements the PaymentGatewayPort interface and provides
 * the necessary logic to interact with the Stripe payment gateway for processing payments.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 25.07.2026
 */
@Component
public class StripeGatewayAdapter implements PaymentGatewayPort {

    private final GatewayAuditLogPort auditLogPort;
    private static final String STRIPE_URL = "https://stripe.com";

    public StripeGatewayAdapter(GatewayAuditLogPort auditLogPort) {
        this.auditLogPort = auditLogPort;
    }

    @Override
    public boolean charge(PaymentTransaction transaction) {
        // Симулируем тело JSON-запроса к API Stripe
        String rawRequest = String.format(
                "{\"amount\": %s, \"currency\": \"%s\", \"source\": \"tok_visa\"}",
                transaction.getAmount(), transaction.getCurrency()
        );

        System.out.println("--> [HTTP POST] Sending transaction to Stripe API...");

        // Имитируем успешный 200 OK ответ от серверов Stripe
        String rawResponse = "{\"id\": \"ch_stripe_" + transaction.getId() + "\", \"captured\": true, \"status\": \"succeeded\"}";
        int httpStatus = 200;

        // Сохраняем технический след обмена данными в таблицу gateway_audit_logs
        auditLogPort.logResponse(
                transaction.getId(), getProviderName(), STRIPE_URL, httpStatus,
                rawRequest, rawResponse, null
        );

        return true; // Платеж через Stripe всегда успешен в нашем демо
    }

    @Override
    public String getProviderName() {
        return "STRIPE";
    }
}

