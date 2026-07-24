package com.techmatrix18.domain.merchant;

import java.time.Instant;
import java.util.UUID;

/**
 * Class representing a merchant entity.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 24.07.2026
 */
public class Merchant {
    private final UUID id;
    private final String name;
    private final String apiKeyHash;
    private MerchantStatus status;
    private final Instant createdAt;

    private Merchant(UUID id, String name, String apiKeyHash, MerchantStatus status, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.apiKeyHash = apiKeyHash;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Фабричный метод для онбординга нового мерчанта
    public static Merchant createNew(String name, String apiKeyHash) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Merchant name cannot be empty");
        }
        if (apiKeyHash == null || apiKeyHash.isBlank()) {
            throw new IllegalArgumentException("API key hash cannot be empty");
        }
        return new Merchant(UUID.randomUUID(), name, apiKeyHash, MerchantStatus.ACTIVE, Instant.now());
    }

    // Бизнес-методы изменения состояния
    public void suspend() {
        this.status = MerchantStatus.SUSPENDED;
    }

    public void activate() {
        this.status = MerchantStatus.ACTIVE;
    }

    public boolean isActive() {
        return this.status == MerchantStatus.ACTIVE;
    }

    // Геттеры для инфраструктурного слоя (чтение/сохранение в БД)
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getApiKeyHash() { return apiKeyHash; }
    public MerchantStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}

