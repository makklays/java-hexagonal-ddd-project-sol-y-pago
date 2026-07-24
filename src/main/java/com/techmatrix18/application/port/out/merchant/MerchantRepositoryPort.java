package com.techmatrix18.application.port.out.merchant;

import com.techmatrix18.domain.merchant.Merchant;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port interface for merchant-related operations.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 24.07.2026
 */
public interface MerchantRepositoryPort {
    Merchant save(Merchant merchant);
    Optional<Merchant> findById(UUID id);
}

