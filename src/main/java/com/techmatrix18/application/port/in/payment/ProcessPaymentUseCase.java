package com.techmatrix18.application.port.in.payment;

import com.techmatrix18.application.command.payment.ProcessPaymentCommand;
import com.techmatrix18.domain.payment.PaymentTransaction;

/**
 * Use case interface for processing payments. This interface defines the contract for handling payment transactions,
 * including initiating, validating, and completing payments within the system.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 24.07.2026
 */
public interface ProcessPaymentUseCase {
    // Метод принимает команду из внешнего пакета command
    PaymentTransaction process(ProcessPaymentCommand command);
}
