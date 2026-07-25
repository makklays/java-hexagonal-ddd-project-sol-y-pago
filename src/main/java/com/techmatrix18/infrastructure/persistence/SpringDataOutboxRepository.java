package com.techmatrix18.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

/**
 * SpringDataOutboxRepository
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 26.07.2026
 */
public interface SpringDataOutboxRepository extends JpaRepository<OutboxEventEntity, UUID> {
}

