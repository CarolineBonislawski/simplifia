package org.cb.simplifia.domain.model.event;

import org.cb.simplifia.domain.model.BankId;
import org.cb.simplifia.domain.model.Credits;

import java.time.Instant;
import java.util.UUID;

public record BankCredited(
    UUID eventId,
    Instant occurredAt,
    BankId bankAccountId,
    Credits creditedAmount,
    Credits balanceAfter,
    String reason,
    String orderId,
    long version
) implements DomainEvent {

    public BankCredited(BankId bankId, Credits creditedAmount, Credits balanceAfter, String reason, String orderId, long version) {
        this(UUID.randomUUID(), Instant.now(), bankId, creditedAmount, balanceAfter, reason, orderId, version);
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
        return EventType.CREDITED;
    }

    @Override
    public long getVersion() {
        return version;
    }
}
