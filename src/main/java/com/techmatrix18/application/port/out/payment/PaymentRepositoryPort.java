package com.techmatrix18.application.port.out.payment;

import com.techmatrix18.domain.payment.PaymentTransaction;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port interface for payment-related operations. This interface defines the contract for persisting
 * and retrieving payment transactions within the system.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 24.07.2026
 */
public interface PaymentRepositoryPort {
    PaymentTransaction save(PaymentTransaction transaction);
    Optional<PaymentTransaction> findById(UUID id);
}

