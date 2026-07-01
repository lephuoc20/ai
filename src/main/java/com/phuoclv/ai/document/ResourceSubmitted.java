package com.phuoclv.ai.document;

import org.jmolecules.event.types.DomainEvent;
import org.springframework.core.io.Resource;

import java.time.LocalDateTime;
import java.util.Set;

public record ResourceSubmitted(
        String id,
        Resource resource,
        String creatorName,
        String creatorId,
        LocalDateTime createdAt,
        Set<String> tags
) implements DomainEvent {
}
