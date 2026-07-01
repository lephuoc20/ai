package com.phuoclv.ai.document;

import org.jmolecules.event.types.DomainEvent;

public record ResourceStored(String documentId, String creatorId, String creatorName) implements DomainEvent {
}
