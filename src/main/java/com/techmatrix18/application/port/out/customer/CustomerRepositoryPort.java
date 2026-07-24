package com.techmatrix18.application.port.out.customer;

import com.techmatrix18.domain.customer.Customer;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port interface for customer-related operations. This interface defines the contract for saving and
 * retrieving customer data from the underlying data store.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 24.07.2026
 */
public interface CustomerRepositoryPort {
    Customer save(Customer customer);
    Optional<Customer> findById(UUID id);
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByPhoneNumber(String phoneNumber);
}

