package com.techmatrix18.infrastructure.persistence;

import com.techmatrix18.application.port.out.payment.PaymentRepositoryPort;
import com.techmatrix18.domain.payment.PaymentMethod;
import com.techmatrix18.domain.payment.PaymentTransaction;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter class for the PaymentRepositoryPort interface. This class implements the port and provides the necessary
 * logic to interact with the underlying Spring Data JPA repository for payment-related operations.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 25.07.2026
 */
@Component
public class PaymentRepositoryAdapter implements PaymentRepositoryPort {

    private final SpringDataPaymentRepository jpaRepository;

    public PaymentRepositoryAdapter(SpringDataPaymentRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PaymentTransaction save(PaymentTransaction domain) {
        PaymentTransactionEntity entity = new PaymentTransactionEntity();
        entity.setId(domain.getId());
        entity.setMerchantId(domain.getMerchantId());
        entity.setCustomerId(domain.getCustomerId());
        entity.setAmount(domain.getAmount());
        entity.setCurrency(domain.getCurrency());
        entity.setCountryCode(domain.getCountryCode());
        entity.setStatus(domain.getStatus().name());
        entity.setPaymentMethod(domain.getPaymentMethod().name());
        entity.setProviderName(domain.getProviderName());
        entity.setCreatedAt(domain.getCreatedAt());

        jpaRepository.save(entity);
        return domain; // Возвращаем исходный Агрегат для дальнейшей цепочки
    }

    @Override
    public Optional<PaymentTransaction> findById(UUID id) {
        // Метод поиска для демо-режима возвращает пустой Optional или мапит обратно, если потребуется расширение
        return jpaRepository.findById(id).map(entity ->
                PaymentTransaction.createNew(
                        entity.getMerchantId(), entity.getCustomerId(), entity.getAmount(),
                        entity.getCurrency(), entity.getCountryCode(), PaymentMethod.valueOf(entity.getPaymentMethod())
                )
        );
    }
}

