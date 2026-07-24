package com.techmatrix18.infrastructure.persistence;

import com.techmatrix18.domain.customer.Customer;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Entity class representing a Customer in the database. This class is used for persistence and mapping between
 * the domain model and the database schema.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 25.07.2026
 */
@Entity
@Table(name = "customers")
public class CustomerEntity {
    @Id
    private UUID id;

    @Column(unique = true)
    private String email;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "created_at")
    private Instant createdAt;

    // Маппинг из чистой доменной модели в сущность базы данных
    public static CustomerEntity fromDomain(Customer customer) {
        CustomerEntity entity = new CustomerEntity();
        entity.id = customer.getId();
        entity.email = customer.getEmail();
        entity.phoneNumber = customer.getPhoneNumber();
        entity.createdAt = customer.getCreatedAt();
        return entity;
    }

    // Маппинг из сущности базы данных обратно в доменную модель DDD
    public Customer toDomain() {
        return Customer.createNew(this.email, this.phoneNumber);
    }

    // Геттеры и сеттеры для Hibernate
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

