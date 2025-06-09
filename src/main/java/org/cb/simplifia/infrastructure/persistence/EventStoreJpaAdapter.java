package org.cb.simplifia.infrastructure.persistence;

import org.cb.simplifia.domain.port.output.DomainEventRepository;
import org.cb.simplifia.domain.model.event.DomainEvent;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class EventStoreJpaAdapter implements DomainEventRepository {

    private final EventStoreJpaRepository eventStoreJpaRepository;
    private final EventStoreMapper eventStoreMapper;

    public EventStoreJpaAdapter(EventStoreJpaRepository eventStoreJpaRepository, EventStoreMapper eventStoreMapper) {
        this.eventStoreJpaRepository = eventStoreJpaRepository;
        this.eventStoreMapper = eventStoreMapper;
    }

    @Override
    @Transactional
    public void saveAllEvents(List<DomainEvent> events) {
        var eventStore = events.stream().map(eventStoreMapper::toEntity).toList();
        eventStoreJpaRepository.saveAll(eventStore);
    }

    @Override
    public List<DomainEvent> getEvents(String aggregateId) {
        var events = eventStoreJpaRepository.findAllByAggregateIdOrderByVersionAsc(aggregateId);
        return events.stream().map(eventStoreMapper::fromEntity).toList();
    }
}
