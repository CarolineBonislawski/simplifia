package org.cb.simplifia.infrastructure.persistence;

import org.cb.simplifia.domain.model.Bank;
import org.cb.simplifia.domain.model.BankId;
import org.cb.simplifia.domain.port.output.BankRepository;
import org.cb.simplifia.domain.port.output.DomainEventRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class EventSourcedBankAdapter implements BankRepository {

    private final DomainEventRepository domainEventRepository;

    public EventSourcedBankAdapter(DomainEventRepository domainEventRepository) {
        this.domainEventRepository = domainEventRepository;
    }

    @Override
    public Optional<Bank> findById(BankId bankId) {
        var events = domainEventRepository.getEvents(bankId.getValue());
        if (events.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(Bank.from(events));
    }

    @Override
    public void save(Bank bank) {
        if (bank.getUncommittedEvents().isEmpty()) {
            return;
        }
        domainEventRepository.saveAllEvents(bank.getUncommittedEvents());
    }

    @Override
    public boolean exists(BankId bankId) {
        return !domainEventRepository.getEvents(bankId.getValue()).isEmpty();
    }

}
