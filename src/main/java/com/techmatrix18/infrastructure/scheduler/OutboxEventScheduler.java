package com.techmatrix18.infrastructure.scheduler;

import com.techmatrix18.infrastructure.persistence.OutboxEventEntity;
import com.techmatrix18.infrastructure.persistence.SpringDataOutboxRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * OutboxEventScheduler
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 26.07.2026
 */
@Component
@EnableScheduling // Включаем поддержку фоновых задач Spring
public class OutboxEventScheduler {

    private final SpringDataOutboxRepository outboxRepository;

    public OutboxEventScheduler(SpringDataOutboxRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    /**
     * Фоновый метод. Выполняется автоматически каждые 5000 миллисекунд (5 секунд).
     * Ищет неотправленные финтех-события в Postgres и гарантированно "доставляет" их.
     */
    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processOutboxEvents() {
        // Запрос к БД (инфраструктурный JPA репозиторий мы создадим в следующем шаге)
        // Для демо просто вытаскиваем первые 10 записей со статусом PENDING
        List<OutboxEventEntity> pendingEvents = outboxRepository.findAll().stream()
                .filter(e -> "PENDING".equals(e.getStatus()))
                .limit(10)
                .toList();

        if (pendingEvents.isEmpty()) {
            return;
        }

        System.out.println("--> [Outbox Scheduler] Found " + pendingEvents.size() + " pending events to process.");

        for (OutboxEventEntity event : pendingEvents) {
            try {
                // --- СИМУЛЯЦИЯ ОТПРАВКИ В BROKER (Apache Kafka / RabbitMQ) ---
                System.out.println("🚀 [Kafka Producer] Successfully published event [" + event.getEventType() + "] for aggregate ID: " + event.getAggregateId());

                // Если отправка прошла успешно, обновляем статус записи в БД, чтобы не отправить повторно
                event.setStatus("PROCESSED");
                event.setProcessedAt(Instant.now());
                outboxRepository.save(event);

            } catch (Exception e) {
                System.err.println("❌ [Outbox Scheduler] Failed to publish event " + event.getId() + ". Will retry in next cycle.");
                event.setStatus("FAILED");
                outboxRepository.save(event);
            }
        }
    }
}

