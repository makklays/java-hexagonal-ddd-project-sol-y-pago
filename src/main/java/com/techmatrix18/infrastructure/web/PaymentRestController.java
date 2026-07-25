package com.techmatrix18.infrastructure.web;

import com.techmatrix18.application.command.payment.ProcessPaymentCommand;
import com.techmatrix18.application.port.in.payment.ProcessPaymentUseCase;
import com.techmatrix18.domain.payment.PaymentTransaction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * REST controller for handling payment-related operations. This controller provides endpoints for processing payments and managing payment transactions.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 25.07.2026
 */
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentRestController {

    private final ProcessPaymentUseCase processPaymentUseCase;

    // Внедряем входящий порт через конструктор Spring
    public PaymentRestController(ProcessPaymentUseCase processPaymentUseCase) {
        this.processPaymentUseCase = processPaymentUseCase;
    }

    /**
     * Эндпоинт для инициализации и обработки платежа интернет-магазинами.
     * Принимает JSON, конвертирует в CQRS-команду и возвращает результат транзакции.
     */
    @PostMapping
    public ResponseEntity<PaymentResponseDto> processPayment(
            @RequestHeader("X-Merchant-Id") UUID merchantId,
            @RequestHeader("X-Merchant-Key") String apiKey,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey, // Перехватываем заголовок
            @RequestBody PaymentRequestDto request) {

        // 1. Создаем иммутабельную бизнес-команду из HTTP-данных
        ProcessPaymentCommand command = new ProcessPaymentCommand(
                merchantId,
                idempotencyKey,
                apiKey,
                request.amount(),
                request.currency(),
                request.countryCode(),
                request.customerEmail(),
                request.customerPhone(),
                request.paymentMethod()
        );

        // 2. Передаем команду во входящий порт гексагона
        PaymentTransaction transaction = processPaymentUseCase.process(command);

        // 3. Формируем чистый HTTP-ответ наружу
        PaymentResponseDto response = new PaymentResponseDto(
                transaction.getId(),
                transaction.getStatus().name(),
                transaction.getProviderName(),
                transaction.getAmount(),
                transaction.getCurrency()
        );

        if ("FAILED".equals(response.status())) {
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(response);
        }

        return ResponseEntity.ok(response);
    }

    // --- Внутренние DTO контракты для веб-слоя (Records Java 17+) ---

    public record PaymentRequestDto(
            BigDecimal amount,
            String currency,
            String countryCode,
            String customerEmail,
            String customerPhone,
            String paymentMethod // "CARD" или "BIZUM"
    ) {}

    public record PaymentResponseDto(
            UUID transactionId,
            String status,
            String processedBy, // STRIPE или BIZUM
            BigDecimal amount,
            String currency
    ) {}
}

