package com.phuoclv.ai.etl.transform;

import org.jmolecules.event.types.DomainEvent;
import org.springframework.ai.document.Document;

import java.time.OffsetDateTime;
import java.util.Collection;

public record DocumentTransform(
        Document document,
        Long resourceId,
        String creatorName,
        String creatorId,
        OffsetDateTime createdAt,
        Collection<?> tags
) implements DomainEvent  {
}
