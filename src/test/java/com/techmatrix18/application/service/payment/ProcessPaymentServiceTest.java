package com.techmatrix18.application.service.payment;

import com.techmatrix18.application.command.payment.ProcessPaymentCommand;
import com.techmatrix18.application.port.in.customer.FindOrCreateCustomerUseCase;
import com.techmatrix18.application.port.in.merchant.VerifyMerchantUseCase;
import com.techmatrix18.application.port.out.payment.PaymentGatewayPort;
import com.techmatrix18.application.port.out.payment.PaymentRepositoryPort;
import com.techmatrix18.application.port.out.payment.IdempotencyRepositoryPort;
import com.techmatrix18.application.port.out.payment.OutboxEventPort;
import com.techmatrix18.domain.customer.Customer;
import com.techmatrix18.domain.payment.PaymentStatus;
import com.techmatrix18.domain.payment.PaymentTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ProcessPaymentService class, focusing on the fallback mechanism when the primary payment gateway fails.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 25.07.2026
 */
@ExtendWith(MockitoExtension.class)
class ProcessPaymentServiceTest {

    @Mock private VerifyMerchantUseCase verifyMerchantUseCase;
    @Mock private FindOrCreateCustomerUseCase findOrCreateCustomerUseCase;
    @Mock private PaymentRepositoryPort paymentRepositoryPort;
    @Mock private IdempotencyRepositoryPort idempotencyRepositoryPort; // Добавили Mock нового порта
    @Mock private OutboxEventPort outboxEventPort;
    @Mock private PaymentGatewayPort stripeGateway;
    @Mock private PaymentGatewayPort bizumGateway;

    private ProcessPaymentService processPaymentService;

    private final UUID merchantId = UUID.randomUUID();
    private final String apiKey = "valid_api_key";
    private final String idempotencyKey = UUID.randomUUID().toString();
    private final Customer customer = Customer.createNew("test@customer.com", "+34611223344");

    @BeforeEach
    void setUp() {
        // Настраиваем имена провайдеров для заглушек шлюзов
        lenient().when(stripeGateway.getProviderName()).thenReturn("STRIPE");
        lenient().when(bizumGateway.getProviderName()).thenReturn("BIZUM");

        // По умолчанию симулируем, что ключ уникальный и в базе его нет
        lenient().when(idempotencyRepositoryPort.findResponse(any())).thenReturn(Optional.empty());

        // Передаем моки шлюзов списком в наш сервис приложения
        processPaymentService = new ProcessPaymentService(
                verifyMerchantUseCase,
                findOrCreateCustomerUseCase,
                paymentRepositoryPort,
                idempotencyRepositoryPort,
                outboxEventPort,
                List.of(stripeGateway, bizumGateway)
        );
    }

    @Test
    @DisplayName("Should successfully execute fallback to STRIPE when primary BIZUM gateway fails")
    void shouldFallbackToStripeWhenBizumFails() {
        // Передаем idempotencyKey вторым параметром в команду
        ProcessPaymentCommand command = new ProcessPaymentCommand(
                merchantId, idempotencyKey, apiKey, new BigDecimal("25.00"), "EUR", "ES",
                customer.getEmail(), customer.getPhoneNumber(), "CARD"
        );

        when(verifyMerchantUseCase.isValid(merchantId, apiKey)).thenReturn(true);
        when(findOrCreateCustomerUseCase.findOrCreate(any())).thenReturn(customer);

        // Мокаем сохранение в репозиторий: просто возвращаем то, что передали
        when(paymentRepositoryPort.save(any(PaymentTransaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Симулируем жесткое падение основного шлюза Bizum (Exception)
        when(bizumGateway.charge(any(PaymentTransaction.class))).thenThrow(new RuntimeException("Bizum API Down"));

        // Симулируем, что резервный шлюз Stripe успешно принимает платеж
        when(stripeGateway.charge(any(PaymentTransaction.class))).thenReturn(true);

        // When
        PaymentTransaction result = processPaymentService.process(command);

        // Then
        assertNotNull(result);
        assertEquals(PaymentStatus.SUCCESS, result.getStatus()); // Транзакция в итоге успешна
        verify(bizumGateway, times(1)).charge(any()); // Попытка через Bizum была
        verify(stripeGateway, times(1)).charge(any()); // Автоматический Fallback на Stripe сработал!
        verify(paymentRepositoryPort, times(2)).save(any()); // Сохранили при создании и при финале
        // Проверяем, что если платеж завершился успехом, была попытка сохранить ключ
        verify(idempotencyRepositoryPort, atLeastOnce()).findResponse(any());
    }

    @Test
    @DisplayName("Should throw SecurityException if merchant verification fails")
    void shouldThrowExceptionWhenMerchantInvalid() {
        // Given
        ProcessPaymentCommand command = new ProcessPaymentCommand(
                merchantId, idempotencyKey, apiKey, new BigDecimal("25.00"), "EUR", "ES",
                customer.getEmail(), customer.getPhoneNumber(), "CARD"
        );

        when(verifyMerchantUseCase.isValid(merchantId, apiKey)).thenReturn(false);

        // When & Then
        assertThrows(SecurityException.class, () -> processPaymentService.process(command));
        verifyNoInteractions(findOrCreateCustomerUseCase);
        verifyNoInteractions(paymentRepositoryPort);
    }
}

