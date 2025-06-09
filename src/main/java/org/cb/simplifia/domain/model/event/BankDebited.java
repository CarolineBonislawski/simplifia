package org.cb.simplifia.domain.model.event;

import org.cb.simplifia.domain.model.BankId;
import org.cb.simplifia.domain.model.Credits;

import java.time.Instant;
import java.util.UUID;

public record BankDebited(
    UUID eventId,
    Instant occurredAt,
    BankId bankAccountId,
    Credits debitedAmount,
    Credits balanceAfter,
    String reason,
    String orderId,
    long version
) implements DomainEvent {

    public BankDebited(BankId bankId, Credits debitedAmount, Credits balanceAfter, String reason, String orderId, long version) {
        this(UUID.randomUUID(), Instant.now(), bankId, debitedAmount, balanceAfter, reason, orderId, version);
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
        return EventType.DEBITED;
    }

    @Override
    public long getVersion() {
        return version;
    }
}
