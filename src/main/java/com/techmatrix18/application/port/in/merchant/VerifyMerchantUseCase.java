package com.techmatrix18.application.port.in.merchant;

import java.util.UUID;

/**
 * Use case interface for verifying a merchant's identity or status.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 24.07.2026
 */
public interface VerifyMerchantUseCase {

    // Проверяет, активен ли мерчант и совпадает ли хэш присланного API-ключа
    boolean isValid(UUID merchantId, String rawApiKey);
}

