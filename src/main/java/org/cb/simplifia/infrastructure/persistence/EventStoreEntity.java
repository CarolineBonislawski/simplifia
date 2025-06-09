package org.cb.simplifia.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import org.cb.simplifia.domain.model.event.EventType;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.UUID;

@Entity
public class EventStoreEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String aggregateId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    @Column(nullable = false, columnDefinition = "JSON")
    private String eventData;

    @Column(nullable = false)
    private Long version;

    @Column(nullable = false)
    private Instant occurredAt;

    @Column(nullable = false)
    private Instant createdAt;

    public EventStoreEntity() {

    }

    public EventStoreEntity(
        UUID id,
        String aggregateId,
        EventType eventType,
        String eventData,
        Long version,
        Instant occurredAt,
        Instant createdAt
    ) {
        this.id = id;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.eventData = eventData;
        this.version = version;
        this.occurredAt = occurredAt;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getEventData() {
        return eventData;
    }

    public Long getVersion() {
        return version;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
