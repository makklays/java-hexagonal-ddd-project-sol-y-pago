package com.techmatrix18.application.service.payment;

import com.techmatrix18.application.command.customer.CreateCustomerCommand;
import com.techmatrix18.application.command.payment.ProcessPaymentCommand;
import com.techmatrix18.application.port.in.customer.FindOrCreateCustomerUseCase;
import com.techmatrix18.application.port.in.merchant.VerifyMerchantUseCase;
import com.techmatrix18.application.port.in.payment.ProcessPaymentUseCase;
import com.techmatrix18.application.port.out.payment.PaymentGatewayPort;
import com.techmatrix18.application.port.out.payment.PaymentRepositoryPort;
import com.techmatrix18.domain.customer.Customer;
import com.techmatrix18.domain.payment.PaymentMethod;
import com.techmatrix18.domain.payment.PaymentTransaction;

import java.util.List;
import java.util.Map;
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
    private final Map<String, PaymentGatewayPort> gateways;

    // Конструктор собирает все платежные шлюзы (Stripe, Bizum) в динамическую Map по их имени провайдера
    public ProcessPaymentService(VerifyMerchantUseCase verifyMerchantUseCase,
                                 FindOrCreateCustomerUseCase findOrCreateCustomerUseCase,
                                 PaymentRepositoryPort paymentRepositoryPort,
                                 List<PaymentGatewayPort> gatewayPorts) {
        this.verifyMerchantUseCase = verifyMerchantUseCase;
        this.findOrCreateCustomerUseCase = findOrCreateCustomerUseCase;
        this.paymentRepositoryPort = paymentRepositoryPort;
        this.gateways = gatewayPorts.stream()
                .collect(Collectors.toMap(PaymentGatewayPort::getProviderName, Function.identity()));
    }

    @Override
    public PaymentTransaction process(ProcessPaymentCommand command) {
        // 1. Безопасность: Верификация мерчанта
        if (!verifyMerchantUseCase.isValid(command.merchantId(), command.apiKey())) {
            throw new SecurityException("Invalid Merchant credentials or Merchant is suspended");
        }

        // 2. Клиент: Поиск или регистрация на лету
        CreateCustomerCommand customerCommand = new CreateCustomerCommand(command.customerEmail(), command.customerPhone());
        Customer customer = findOrCreateCustomerUseCase.findOrCreate(customerCommand);

        // 3. Создание транзакции: Мапим строку метода оплаты в наш доменный Enum
        PaymentMethod method = PaymentMethod.valueOf(command.paymentMethodName().toUpperCase());
        PaymentTransaction transaction = PaymentTransaction.createNew(
                command.merchantId(),
                customer.getId(),
                command.amount(),
                command.currency(),
                command.countryCode(),
                method
        );

        // 4. Роутинг: Запускаем чистое бизнес-правило выбора провайдера (Испанское ТЗ)
        transaction.assignRoutingProvider();

        // 5. БД: Сохраняем первичную запись транзакции в статусе CREATED
        PaymentTransaction savedTransaction = paymentRepositoryPort.save(transaction);

        // 6. Банковское API: Проводим платеж со встроенным fallback-механизмом
        boolean isGatewaySuccess = executePaymentWithResilience(savedTransaction);

        // 7. Финал: Меняем статус агрегата на основе ответа шлюзов и сохраняем финальный результат
        if (isGatewaySuccess) {
            savedTransaction.markAsSuccess();
        } else {
            savedTransaction.markAsFailed();
        }

        return paymentRepositoryPort.save(savedTransaction);
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

