package org.cb.simplifia.infrastructure.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cb.simplifia.domain.model.event.BankCreated;
import org.cb.simplifia.domain.model.event.BankCredited;
import org.cb.simplifia.domain.model.event.BankDebited;
import org.cb.simplifia.domain.model.event.DomainEvent;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class EventStoreMapper {

    private final ObjectMapper objectMapper;

    public EventStoreMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public EventStoreEntity toEntity(DomainEvent event) {
        try {
            return new EventStoreEntity(
                UUID.randomUUID(),
                event.getAggregateId(),
                event.getEventType(),
                objectMapper.writeValueAsString(event),
                event.getVersion(),
                event.getOccurredAt(),
                Instant.now()
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public DomainEvent fromEntity(EventStoreEntity entity) {
        try {
            var data = entity.getEventData();
            return switch (entity.getEventType()) {
                case CREATED -> objectMapper.readValue(data, BankCreated.class);
                case DEBITED -> objectMapper.readValue(data, BankDebited.class);
                case CREDITED -> objectMapper.readValue(data, BankCredited.class);
            };
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
