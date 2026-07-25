package com.techmatrix18.infrastructure.persistence;

import com.techmatrix18.application.port.out.payment.OutboxEventPort;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.UUID;

/**
 * OutboxRepositoryAdapter
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 26.07.2026
 */
@Component
public class OutboxRepositoryAdapter implements OutboxEventPort {

    private final SpringDataOutboxRepository jpaRepository;

    public OutboxRepositoryAdapter(SpringDataOutboxRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void sendPaymentSuccessEvent(UUID transactionId, String payloadJson) {
        OutboxEventEntity entity = new OutboxEventEntity();
        entity.setId(UUID.randomUUID());
        entity.setAggregateType("PAYMENT");
        entity.setAggregateId(transactionId);
        entity.setEventType("PAYMENT_TRANSACTION_SUCCESS");
        entity.setPayload(payloadJson);
        entity.setStatus("PENDING"); // По умолчанию событие ждет отправки в Kafka фоновым шедулером
        entity.setCreatedAt(Instant.now());

        jpaRepository.save(entity);
    }
}

