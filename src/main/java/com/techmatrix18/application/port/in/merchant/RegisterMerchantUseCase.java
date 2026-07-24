package com.techmatrix18.application.port.in.merchant;

import com.techmatrix18.application.command.merchant.RegisterMerchantCommand;
import com.techmatrix18.domain.merchant.Merchant;

/**
 * Use case interface for registering a new merchant.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 24.07.2026
 */
public interface RegisterMerchantUseCase {

    // Метод принимает команду из внешнего пакета command
    Merchant register(RegisterMerchantCommand command);
}

