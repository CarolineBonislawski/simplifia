package org.cb.simplifia.domain.model.event;

import java.time.Instant;
import java.util.UUID;

public interface DomainEvent {

    UUID getEventId();
    Instant getOccurredAt();
    String getAggregateId();
    EventType getEventType();
    long getVersion();
}
