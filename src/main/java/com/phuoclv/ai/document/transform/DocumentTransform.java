package com.phuoclv.ai.document.transform;

import org.jmolecules.event.types.DomainEvent;
import org.springframework.ai.document.Document;

import java.time.LocalDateTime;
import java.util.Set;

public record DocumentTransform(
        Document document,
        String resourceId,
        String creatorName,
        String creatorId,
        LocalDateTime createdAt,
        Set<String> tags
) implements DomainEvent  {
}
