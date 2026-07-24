package com.techmatrix18.infrastructure.gateways;

import com.techmatrix18.application.port.out.payment.GatewayAuditLogPort;
import com.techmatrix18.application.port.out.payment.PaymentGatewayPort;
import com.techmatrix18.domain.payment.PaymentTransaction;
import org.springframework.stereotype.Component;

/**
 * Adapter class for the Bizum payment gateway. This class implements the PaymentGatewayPort interface and provides
 * the necessary logic to interact with the Bizum payment gateway for processing payments.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 25.07.2026
 */
@Component
public class BizumGatewayAdapter implements PaymentGatewayPort {

    private final GatewayAuditLogPort auditLogPort;
    private static final String BIZUM_URL = "https://bizum.es";

    public BizumGatewayAdapter(GatewayAuditLogPort auditLogPort) {
        this.auditLogPort = auditLogPort;
    }

    @Override
    public boolean charge(PaymentTransaction transaction) {
        // Симулируем тело запроса по спецификации Bizum API
        String rawRequest = String.format(
                "{\"total_amount\": %s, \"concept\": \"Sol y Pago Gateway Order\"}",
                transaction.getAmount()
        );

        System.out.println("--> [HTTP POST] Sending transaction to Bizum API...");

        // --- СИМУЛЯЦИЯ СБОЯ ДЛЯ ТЕСТИРОВАНИЯ FALLBACK ---
        // Если сумма платежа больше 40 евро (но меньше 50, то есть попадает в роутинг Bizum)
        // Имитируем временный сбой на стороне испанского банковского шлюза
        if (transaction.getAmount().doubleValue() > 40.00) {
            int httpStatus = 503;
            String rawResponse = "{\"error\": \"Gateway Timeout\", \"message\": \"Bizum server is temporarily unavailable\"}";

            auditLogPort.logResponse(
                    transaction.getId(), getProviderName(), BIZUM_URL, httpStatus,
                    rawRequest, rawResponse, "BIZUM_SERVER_DOWN"
            );

            // Генерируем исключение, чтобы запустить executePaymentWithResilience в слое Application
            throw new RuntimeException("Bizum banking service is unavailable. Connection failed.");
        }

        // Штатный успешный сценарий Bizum
        int httpStatus = 200;
        String rawResponse = "{\"bizum_id\": \"bz_id_" + transaction.getId() + "\", \"result\": \"ACCEPTED\"}";

        auditLogPort.logResponse(
                transaction.getId(), getProviderName(), BIZUM_URL, httpStatus,
                rawRequest, rawResponse, null
        );

        return true;
    }

    @Override
    public String getProviderName() {
        return "BIZUM";
    }
}

