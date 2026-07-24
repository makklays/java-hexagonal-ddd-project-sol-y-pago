package com.techmatrix18.domain.customer;

import java.time.Instant;
import java.util.UUID;

/**
 * Class representing a customer entity.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 24.07.2026
 */
public class Customer {
    private final UUID id;
    private final String email;
    private final String phoneNumber;
    private final Instant createdAt;

    private Customer(UUID id, String email, String phoneNumber, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
    }

    // Фабричный метод для создания нового клиента
    public static Customer createNew(String email, String phoneNumber) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Customer email cannot be empty");
        }
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Customer phone number cannot be empty");
        }
        return new Customer(UUID.randomUUID(), email, phoneNumber, Instant.now());
    }

    // Геттеры
    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public Instant getCreatedAt() { return createdAt; }
}

