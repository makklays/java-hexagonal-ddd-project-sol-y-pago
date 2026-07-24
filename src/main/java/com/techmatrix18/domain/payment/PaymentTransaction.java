package com.techmatrix18.domain.payment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Class representing a payment transaction.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 24.07.2026
 */
public class PaymentTransaction {
    private final UUID id;
    private final UUID merchantId;
    private final UUID customerId;
    private final BigDecimal amount;
    private final String currency;
    private final String countryCode;
    private PaymentStatus status;
    private final PaymentMethod paymentMethod;
    private String providerName; // STRIPE или BIZUM
    private final Instant createdAt;

    private PaymentTransaction(UUID id, UUID merchantId, UUID customerId, BigDecimal amount,
                               String currency, String countryCode, PaymentMethod paymentMethod) {
        this.id = id;
        this.merchantId = merchantId;
        this.customerId = customerId;
        this.amount = amount;
        this.currency = currency;
        this.countryCode = countryCode;
        this.status = PaymentStatus.CREATED;
        this.paymentMethod = paymentMethod;
        this.createdAt = Instant.now();
    }

    // Фабричный метод создания транзакции с валидацией бизнес-параметров
    public static PaymentTransaction createNew(UUID merchantId, UUID customerId, BigDecimal amount,
                                               String currency, String countryCode, PaymentMethod paymentMethod) {
        if (merchantId == null) {
            throw new IllegalArgumentException("Merchant ID cannot be null");
        }
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency cannot be empty");
        }
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method must be specified");
        }
        return new PaymentTransaction(UUID.randomUUID(), merchantId, customerId, amount, currency, countryCode, paymentMethod);
    }

    // Интеллектуальный роутинг платежей по Испании (Бизнес-логика ядра)
    public void assignRoutingProvider() {
        if (this.status != PaymentStatus.CREATED) {
            throw new IllegalStateException("Can only route transaction in CREATED status");
        }

        // Если клиент выбрал Bizum принудительно, шлюз переключается на Bizum
        if (this.paymentMethod == PaymentMethod.BIZUM) {
            this.providerName = "BIZUM";
            return;
        }

        // Автоматическое умное правило для карт/переводов:
        // Если сумма < 50.00 EUR и клиент из Испании (ES) -> пускаем через дешевый Bizum, иначе Stripe
        if (this.amount.compareTo(new BigDecimal("50.00")) < 0 && "ES".equalsIgnoreCase(this.countryCode)) {
            this.providerName = "BIZUM";
        } else {
            this.providerName = "STRIPE";
        }
    }

    // Методы управления состояниями транзакции
    public void markAsSuccess() {
        this.status = PaymentStatus.SUCCESS;
    }

    public void markAsFailed() {
        this.status = PaymentStatus.FAILED;
    }

    // Геттеры для чтения инфраструктурным слоем
    public UUID getId() { return id; }
    public UUID getMerchantId() { return merchantId; }
    public UUID getCustomerId() { return customerId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getCountryCode() { return countryCode; }
    public PaymentStatus getStatus() { return status; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public String getProviderName() { return providerName; }
    public Instant getCreatedAt() { return createdAt; }
}

