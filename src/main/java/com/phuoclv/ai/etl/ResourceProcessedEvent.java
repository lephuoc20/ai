package com.phuoclv.ai.etl;

import org.jmolecules.event.types.DomainEvent;

public record ResourceProcessedEvent(Long resourceId, String creatorId, String creatorName) implements DomainEvent {
}
