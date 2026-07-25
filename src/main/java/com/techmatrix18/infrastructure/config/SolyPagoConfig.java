package com.techmatrix18.infrastructure.config;

import com.techmatrix18.application.port.in.customer.FindOrCreateCustomerUseCase;
import com.techmatrix18.application.port.in.merchant.RegisterMerchantUseCase;
import com.techmatrix18.application.port.in.merchant.VerifyMerchantUseCase;
import com.techmatrix18.application.port.in.payment.ProcessPaymentUseCase;
import com.techmatrix18.application.port.out.customer.CustomerRepositoryPort;
import com.techmatrix18.application.port.out.merchant.MerchantRepositoryPort;
import com.techmatrix18.application.port.out.payment.IdempotencyRepositoryPort;
import com.techmatrix18.application.port.out.payment.OutboxEventPort;
import com.techmatrix18.application.port.out.payment.PaymentGatewayPort;
import com.techmatrix18.application.port.out.payment.PaymentRepositoryPort;
import com.techmatrix18.application.service.customer.FindOrCreateCustomerService;
import com.techmatrix18.application.service.merchant.RegisterMerchantService;
import com.techmatrix18.application.service.merchant.VerifyMerchantService;
import com.techmatrix18.application.service.payment.ProcessPaymentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration class for SolyPago application. This class defines beans for various use cases and services related to merchants, customers, and payments.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 25.07.2026
 */
@Configuration
public class SolyPagoConfig {

    // 1. Собираем бин для регистрации мерчантов
    @Bean
    public RegisterMerchantUseCase registerMerchantUseCase(MerchantRepositoryPort merchantRepositoryPort) {
        return new RegisterMerchantService(merchantRepositoryPort);
    }

    // 2. Собираем бин для верификации мерчантов
    @Bean
    public VerifyMerchantUseCase verifyMerchantUseCase(MerchantRepositoryPort merchantRepositoryPort) {
        return new VerifyMerchantService(merchantRepositoryPort);
    }

    // 3. Собираем бин для Just-In-Time работы с покупателями
    @Bean
    public FindOrCreateCustomerUseCase findOrCreateCustomerUseCase(CustomerRepositoryPort customerRepositoryPort) {
        return new FindOrCreateCustomerService(customerRepositoryPort);
    }

    // 4. Главный оркестратор платежей.
    // Spring Boot автоматически найдет все реализации PaymentGatewayPort (Stripe и Bizum) и передаст их списком List
    @Bean
    public ProcessPaymentUseCase processPaymentUseCase(
            VerifyMerchantUseCase verifyMerchantUseCase,
            FindOrCreateCustomerUseCase findOrCreateCustomerUseCase,
            PaymentRepositoryPort paymentRepositoryPort,
            IdempotencyRepositoryPort idempotencyRepositoryPort, // Добавили новый порт сюда
            OutboxEventPort outboxEventPort,
            List<PaymentGatewayPort> gatewayPorts) {

        return new ProcessPaymentService(
                verifyMerchantUseCase,
                findOrCreateCustomerUseCase,
                paymentRepositoryPort,
                idempotencyRepositoryPort, // Передали в конструктор сервиса
                outboxEventPort,
                gatewayPorts
        );
    }
}

