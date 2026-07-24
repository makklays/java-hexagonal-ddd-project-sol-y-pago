package com.techmatrix18.infrastructure.persistence;

import com.techmatrix18.application.port.out.customer.CustomerRepositoryPort;
import com.techmatrix18.domain.customer.Customer;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Adapter class for the CustomerRepositoryPort interface. This class implements the port and provides the necessary
 * logic to interact with the underlying Spring Data JPA repository for customer-related operations.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 25.07.2026
 */
@Component // Этот адаптер мы можем сразу пометить как бин Spring
public class CustomerRepositoryAdapter implements CustomerRepositoryPort {

    private final SpringDataCustomerRepository jpaRepository;

    public CustomerRepositoryAdapter(SpringDataCustomerRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Customer save(Customer customer) {
        CustomerEntity entity = CustomerEntity.fromDomain(customer);
        CustomerEntity savedEntity = jpaRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return jpaRepository.findById(id).map(CustomerEntity::toDomain);
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(CustomerEntity::toDomain);
    }

    @Override
    public Optional<Customer> findByPhoneNumber(String phoneNumber) {
        return jpaRepository.findByPhoneNumber(phoneNumber).map(CustomerEntity::toDomain);
    }
}

