package com.techmatrix18.application.port.in.customer;

import com.techmatrix18.application.command.customer.CreateCustomerCommand;
import com.techmatrix18.domain.customer.Customer;

/**
 * Use case interface for finding or creating a customer. This interface defines the contract for searching for
 * an existing customer by email or phone number, or creating a new customer if one does not already exist in the system.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 24.07.2026
 */
public interface FindOrCreateCustomerUseCase {

    // Ищет по email/телефону или создает, если клиента еще нет в системе
    Customer findOrCreate(CreateCustomerCommand command);
}

