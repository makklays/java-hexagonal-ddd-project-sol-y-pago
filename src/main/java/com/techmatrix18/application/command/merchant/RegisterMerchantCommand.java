package com.techmatrix18.application.command.merchant;

/**
 * Command class for registering a new merchant. This class is used to encapsulate the data required for the registration process.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 24.07.2026
 */
public record RegisterMerchantCommand(
    String name,
    String rawApiKey
) {
    // Прямо здесь в конструкторе record можно делать базовую валидацию синтаксиса
    public RegisterMerchantCommand {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Merchant name in command cannot be empty");
        }
        if (rawApiKey == null || rawApiKey.isBlank()) {
            throw new IllegalArgumentException("API key in command cannot be empty");
        }
    }
}

