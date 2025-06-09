package org.cb.simplifia.domain.port.output;

import org.cb.simplifia.domain.model.event.DomainEvent;

import java.util.List;

public interface DomainEventRepository {

    void saveAllEvents(List<DomainEvent> events);
    List<DomainEvent> getEvents(String aggregateId);
}
