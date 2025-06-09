package org.cb.simplifia.domain.model.event;

import org.cb.simplifia.domain.model.BankId;
import org.cb.simplifia.domain.model.Credits;

import java.time.Instant;
import java.util.UUID;

public record BankCreated(
    UUID eventId,
    Instant occurredAt,
    BankId bankAccountId,
    Credits initialCredits,
    long version
)  implements DomainEvent {

    public BankCreated(BankId bankId, Credits initialCredits, long version) {
        this(UUID.randomUUID(), Instant.now(), bankId, initialCredits, version);
    }

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String getAggregateId() {
        return bankAccountId.getValue();
    }

    @Override
    public EventType getEventType() {
        return EventType.CREATED;
    }

    @Override
    public long getVersion() {
        return version();
    }
}
