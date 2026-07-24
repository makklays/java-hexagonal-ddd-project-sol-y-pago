package com.techmatrix18.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

/**
 * Spring Data JPA repository interface for managing PaymentTransactionEntity instances. This interface provides CRUD operations
 * and query methods for interacting with the underlying database.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 25.07.2026
 */
public interface SpringDataPaymentRepository extends JpaRepository<PaymentTransactionEntity, UUID> {
}

