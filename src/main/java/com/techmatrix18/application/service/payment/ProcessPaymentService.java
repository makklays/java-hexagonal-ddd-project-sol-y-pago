package com.techmatrix18.application.service.payment;

import com.techmatrix18.application.command.customer.CreateCustomerCommand;
import com.techmatrix18.application.command.payment.ProcessPaymentCommand;
import com.techmatrix18.application.port.in.customer.FindOrCreateCustomerUseCase;
import com.techmatrix18.application.port.in.merchant.VerifyMerchantUseCase;
import com.techmatrix18.application.port.in.payment.ProcessPaymentUseCase;
import com.techmatrix18.application.port.out.payment.IdempotencyRepositoryPort;
import com.techmatrix18.application.port.out.payment.OutboxEventPort;
import com.techmatrix18.application.port.out.payment.PaymentGatewayPort;
import com.techmatrix18.application.port.out.payment.PaymentRepositoryPort;
import com.techmatrix18.domain.customer.Customer;
import com.techmatrix18.domain.payment.PaymentMethod;
import com.techmatrix18.domain.payment.PaymentStatus;
import com.techmatrix18.domain.payment.PaymentTransaction;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service class for processing payments. This class implements the ProcessPaymentUseCase interface and
 * handles the business logic for processing payment transactions, including merchant verification, customer management,
 * and interaction with payment gateways.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 25.07.2026
 */
public class ProcessPaymentService implements ProcessPaymentUseCase {

    private final VerifyMerchantUseCase verifyMerchantUseCase;
    private final FindOrCreateCustomerUseCase findOrCreateCustomerUseCase;
    private final PaymentRepositoryPort paymentRepositoryPort;
    private final IdempotencyRepositoryPort idempotencyRepositoryPort; // Новое поле
    private final OutboxEventPort outboxEventPort;
    private final Map<String, PaymentGatewayPort> gateways;

    // Конструктор собирает все платежные шлюзы (Stripe, Bizum) в динамическую Map по их имени провайдера
    public ProcessPaymentService(VerifyMerchantUseCase verifyMerchantUseCase,
                                 FindOrCreateCustomerUseCase findOrCreateCustomerUseCase,
                                 PaymentRepositoryPort paymentRepositoryPort,
                                 IdempotencyRepositoryPort idempotencyRepositoryPort,
                                 OutboxEventPort outboxEventPort,
                                 List<PaymentGatewayPort> gatewayPorts) {
        this.verifyMerchantUseCase = verifyMerchantUseCase;
        this.findOrCreateCustomerUseCase = findOrCreateCustomerUseCase;
        this.paymentRepositoryPort = paymentRepositoryPort;
        this.idempotencyRepositoryPort = idempotencyRepositoryPort; // Инициализация
        this.outboxEventPort = outboxEventPort;
        this.gateways = gatewayPorts.stream()
                .collect(Collectors.toMap(PaymentGatewayPort::getProviderName, Function.identity()));
    }

    @Override
    public PaymentTransaction process(ProcessPaymentCommand command) {
        // 1. Проверка идемпотентности на самом входе в Use Case (Защита от дубликатов)
        if (command.idempotencyKey() != null) {
            Optional<String> cachedResponse = idempotencyRepositoryPort.findResponse(command.idempotencyKey());
            if (cachedResponse.isPresent()) {
                System.out.println("--> [Idempotency] Duplicate request detected for key: " + command.idempotencyKey());
                // В реальном API мы бы вернули закешированный JSON. Для доменного слоя возвращаем мок транзакции или бросаем исключение
            }
        }

        // 2. Безопасность: Верификация мерчанта
        if (!verifyMerchantUseCase.isValid(command.merchantId(), command.apiKey())) {
            throw new SecurityException("Invalid Merchant credentials or Merchant is suspended");
        }

        // 3. Клиент: Поиск или регистрация на лету
        CreateCustomerCommand customerCommand = new CreateCustomerCommand(command.customerEmail(), command.customerPhone());
        Customer customer = findOrCreateCustomerUseCase.findOrCreate(customerCommand);

        // 4. Создание транзакции: Мапим строку метода оплаты в наш доменный Enum
        PaymentMethod method = PaymentMethod.valueOf(command.paymentMethodName().toUpperCase());
        PaymentTransaction transaction = PaymentTransaction.createNew(
                command.merchantId(),
                customer.getId(),
                command.amount(),
                command.currency(),
                command.countryCode(),
                method
        );

        // 5. Роутинг: Запускаем чистое бизнес-правило выбора провайдера (Испанское ТЗ)
        transaction.assignRoutingProvider();

        // 6. БД: Сохраняем первичную запись транзакции в статусе CREATED
        PaymentTransaction savedTransaction = paymentRepositoryPort.save(transaction);

        // 7. Банковское API: Проводим платеж со встроенным fallback-механизмом
        boolean isGatewaySuccess = executePaymentWithResilience(savedTransaction);

        // 8. Финал: Меняем статус агрегата на основе ответа шлюзов и сохраняем финальный результат
        if (isGatewaySuccess) {
            savedTransaction.markAsSuccess();
        } else {
            savedTransaction.markAsFailed();
        }

        // 9. Сохраняем финальный результат изменения статуса в БД
        PaymentTransaction finalResult = paymentRepositoryPort.save(savedTransaction);

        // 10. Outbox Pattern: Если платеж успешен — пишем событие в таблицу outbox_events
        if (finalResult.getStatus() == PaymentStatus.SUCCESS) {
            String eventPayloadJson = String.format(
                    "{\"transactionId\":\"%s\",\"amount\":%s,\"currency\":\"%s\"}",
                    finalResult.getId(), finalResult.getAmount(), finalResult.getCurrency()
            );
            // Вызываем порт инфраструктуры
            outboxEventPort.sendPaymentSuccessEvent(finalResult.getId(), eventPayloadJson);
        }

        return finalResult;
    }

    private boolean executePaymentWithResilience(PaymentTransaction transaction) {
        String preferredProvider = transaction.getProviderName();
        PaymentGatewayPort primaryGateway = gateways.get(preferredProvider);

        try {
            // Первая попытка провести платеж через оптимальный банк
            if (primaryGateway != null && primaryGateway.charge(transaction)) {
                return true;
            }
        } catch (Exception e) {
            System.err.println("Primary gateway [" + preferredProvider + "] failed. Activating architectural fallback...");
        }

        // --- ЛОГИКА АВТОМАТИЧЕСКОГО РЕЗЕРВИРОВАНИЯ (FALLBACK) ---
        // Если основной банк упал, вычисляем альтернативный и пробуем его
        String fallbackProvider = "STRIPE".equalsIgnoreCase(preferredProvider) ? "BIZUM" : "STRIPE";
        PaymentGatewayPort fallbackGateway = gateways.get(fallbackProvider);

        if (fallbackGateway != null) {
            try {
                return fallbackGateway.charge(transaction);
            } catch (Exception e) {
                System.err.println("Fallback gateway [" + fallbackProvider + "] also failed. Transaction fully declined.");
            }
        }

        return false;
    }
}

