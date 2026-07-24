package com.techmatrix18.infrastructure.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Entity class representing a Payment Transaction in the database. This class is used for persistence and mapping between
 * the domain model and the database schema.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 25.07.2026
 */
@Table(name = "payment_transactions")
public class PaymentTransactionEntity {
    @Id
    private UUID id;
    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;
    @Column(name = "customer_id", nullable = false)
    private UUID customerId;
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    @Column(nullable = false, length = 3)
    private String currency;
    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;
    @Column(nullable = false, length = 2)
    private String status;
    @Column(name = "payment_method", nullable = false, length = 30)
    private String paymentMethod;
    @Column(name = "provider_name", length = 50)
    private String providerName;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    // Геттеры и сеттеры для Hibernate
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getMerchantId() { return merchantId; }
    public void setMerchantId(UUID merchantId) { this.merchantId = merchantId; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

