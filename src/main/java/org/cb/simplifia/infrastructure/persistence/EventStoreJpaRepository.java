package org.cb.simplifia.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventStoreJpaRepository extends JpaRepository<EventStoreEntity, String> {

    List<EventStoreEntity> findAllByAggregateIdOrderByVersionAsc(String aggregateId);

}
