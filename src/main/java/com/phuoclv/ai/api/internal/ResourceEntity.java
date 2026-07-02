package com.phuoclv.ai.api.internal;

import java.time.OffsetDateTime;
import java.util.Collection;

public record ResourceEntity(Long id, String name, String fileName, String creatorId, String creatorName, Collection<?> tags, OffsetDateTime createdAt) {
}
