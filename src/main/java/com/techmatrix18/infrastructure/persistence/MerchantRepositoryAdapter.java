package com.techmatrix18.infrastructure.persistence;

import com.techmatrix18.application.port.out.merchant.MerchantRepositoryPort;
import com.techmatrix18.domain.merchant.Merchant;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter class for the MerchantRepositoryPort interface. This class implements the port and provides the necessary
 * logic to interact with the underlying Spring Data JPA repository for merchant-related operations.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 25.07.2026
 */
public class MerchantRepositoryAdapter implements MerchantRepositoryPort {

    private final SpringDataMerchantRepository jpaRepository;

    public MerchantRepositoryAdapter(SpringDataMerchantRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Merchant save(Merchant merchant) {
        MerchantEntity entity = MerchantEntity.fromDomain(merchant);
        MerchantEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Merchant> findById(UUID id) {
        return jpaRepository.findById(id).map(MerchantEntity::toDomain);
    }
}

