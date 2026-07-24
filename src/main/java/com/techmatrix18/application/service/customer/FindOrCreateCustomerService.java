package com.techmatrix18.application.service.customer;

import com.techmatrix18.application.command.customer.CreateCustomerCommand;
import com.techmatrix18.application.port.in.customer.FindOrCreateCustomerUseCase;
import com.techmatrix18.application.port.out.customer.CustomerRepositoryPort;
import com.techmatrix18.domain.customer.Customer;

import java.util.Optional;

/**
 * Service class for finding or creating a customer. This class implements the FindOrCreateCustomerUseCase interface and
 * handles the business logic for searching for an existing customer by email or phone number, or creating a new customer
 * if one does not already exist in the system.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 24.07.2026
 */
public class FindOrCreateCustomerService implements FindOrCreateCustomerUseCase {

    private final CustomerRepositoryPort customerRepositoryPort;

    public FindOrCreateCustomerService(CustomerRepositoryPort customerRepositoryPort) {
        this.customerRepositoryPort = customerRepositoryPort;
    }

    @Override
    public Customer findOrCreate(CreateCustomerCommand command) {
        // Поочередно пытаемся найти клиента по Email или Номеру телефона
        Optional<Customer> existingCustomer = Optional.empty();

        if (command.email() != null && !command.email().isBlank()) {
            existingCustomer = customerRepositoryPort.findByEmail(command.email());
        }

        if (existingCustomer.isEmpty() && command.phoneNumber() != null && !command.phoneNumber().isBlank()) {
            existingCustomer = customerRepositoryPort.findByPhoneNumber(command.phoneNumber());
        }

        // Если нашли — возвращаем, если нет — создаем новый доменный объект и сохраняем
        return existingCustomer.orElseGet(() -> {
            Customer newCustomer = Customer.createNew(command.email(), command.phoneNumber());
            return customerRepositoryPort.save(newCustomer);
        });
    }
}

