package com.phuoclv.ai.etl;

import org.jmolecules.event.types.DomainEvent;

import java.time.OffsetDateTime;
import java.util.Collection;

public record ResourceSubmitted(
        Long id,
        String filePath,
        String creatorName,
        String creatorId,
        OffsetDateTime createdAt,
        Collection<?> tags
) implements DomainEvent {
}
