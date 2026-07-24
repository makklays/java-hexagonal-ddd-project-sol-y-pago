package com.techmatrix18.application.command.customer;

/**
 * Command class for creating a new customer. This class is used to encapsulate the data required for the customer creation process.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 24.07.2026
 */
public record CreateCustomerCommand(
    String email,
    String phoneNumber)
{
    public CreateCustomerCommand {
        if ((email == null || email.isBlank()) && (phoneNumber == null || phoneNumber.isBlank())) {
            throw new IllegalArgumentException("Customer command must provide either email or phone number");
        }
    }
}

