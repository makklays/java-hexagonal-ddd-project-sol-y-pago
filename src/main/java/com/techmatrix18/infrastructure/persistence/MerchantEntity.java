package com.techmatrix18.infrastructure.persistence;

import com.techmatrix18.domain.merchant.Merchant;
import com.techmatrix18.domain.merchant.MerchantStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Entity class representing a Merchant in the database. This class is used for persistence and mapping between the domain model and the database schema.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 25.07.2026
 */
@Entity
@Table(name = "merchants")
public class MerchantEntity {
    @Id
    private UUID id;
    private String name;
    @Column(name = "api_key_hash")
    private String apiKeyHash;
    private String status;
    @Column(name = "created_at")
    private Instant createdAt;

    // Фабричный метод для маппинга из доменной модели в сущность БД
    public static MerchantEntity fromDomain(Merchant merchant) {
        MerchantEntity entity = new MerchantEntity();
        entity.id = merchant.getId();
        entity.name = merchant.getName();
        entity.apiKeyHash = merchant.getApiKeyHash();
        entity.status = merchant.getStatus().name();
        entity.createdAt = merchant.getCreatedAt();
        return entity;
    }

    // Метод обратного конвертирования в чистый домен
    public Merchant toDomain() {
        // Используем Reflection или фабричный метод домена через обход приватного конструктора (для демо упрощено через рефлексию)
        return Merchant.createNew(this.name, this.apiKeyHash);
    }
}

