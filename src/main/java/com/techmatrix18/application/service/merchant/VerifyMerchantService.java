package com.techmatrix18.application.service.merchant;

import com.techmatrix18.application.port.in.merchant.VerifyMerchantUseCase;
import com.techmatrix18.application.port.out.merchant.MerchantRepositoryPort;

import java.util.UUID;

/**
 * Service class for verifying the validity of a merchant. This class implements the VerifyMerchantUseCase interface and
 * handles the business logic for checking if a merchant is active and if the provided API key matches the stored hash.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 24.07.2026
 */
public class VerifyMerchantService implements VerifyMerchantUseCase {

    private final MerchantRepositoryPort merchantRepositoryPort;

    public VerifyMerchantService(MerchantRepositoryPort merchantRepositoryPort) {
        this.merchantRepositoryPort = merchantRepositoryPort;
    }

    @Override
    public boolean isValid(UUID merchantId, String rawApiKey) {
        return merchantRepositoryPort.findById(merchantId)
                .map(merchant -> merchant.isActive() && merchant.getApiKeyHash().equals(rawApiKey))
                .orElse(false);
    }
}

