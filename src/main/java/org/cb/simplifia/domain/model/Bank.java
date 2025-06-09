package org.cb.simplifia.domain.model;

import org.cb.simplifia.common.exception.InsufficientCreditsException;
import org.cb.simplifia.common.StringFormatter;
import org.cb.simplifia.common.exception.NullAmountException;
import org.cb.simplifia.domain.model.event.BankCreated;
import org.cb.simplifia.domain.model.event.BankCredited;
import org.cb.simplifia.domain.model.event.BankDebited;
import org.cb.simplifia.domain.model.event.DomainEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.cb.simplifia.common.StringFormatter.buildReason;

public class Bank {

    private BankId id;
    private Credits balance;
    private Long version;
    private final List<DomainEvent> uncommittedEvents = new ArrayList<>();

    private Bank() {
        this.balance = Credits.zero();
        this.version = 0L;
    }

    public Bank(Credits initialCredits) {
        create(initialCredits);
        var event = new BankCreated(UUID.randomUUID(), Instant.now(), this.id, initialCredits, this.version);
        applyAndAddEvent(event);
    }

    public static Bank from(List<DomainEvent> events) {
        var bank = new Bank();
        events.forEach(bank::apply);
        return bank;
    }

    private void create(Credits initialCredits) {
        this.id = BankId.generate();
        this.balance = initialCredits;
        this.version = 1L;
    }

    public void credits(Credits credits, String orderId) {
        validateCredit(credits);
        var reason = buildReason(orderId, StringFormatter.OrderType.CREDIT);
        var event = new BankCredited(
            UUID.randomUUID(),
            Instant.now(),
            this.id,
            credits,
            balance.add(credits.amount()),
            reason,
            orderId,
            version + 1
        );
        applyAndAddEvent(event);
    }

    public void debits(Credits credits, String orderId) {
        validateDebit(credits);
        var reason = buildReason(orderId, StringFormatter.OrderType.DEBIT);
        var event = new BankDebited(
            UUID.randomUUID(),
            Instant.now(),
            this.id,
            credits,
            balance.subtract(credits.amount()),
            reason,
            orderId,
            version + 1
        );
        applyAndAddEvent(event);
    }

    /**
     * Replay event in order to update bank values
     */
    private void apply(DomainEvent domainEvent) {
        switch (domainEvent) {
            case BankCreated created -> {
                this.id = created.bankAccountId();
                this.balance = created.initialCredits();
                this.version = created.version();
            }
            case BankCredited credited -> {
                this.balance = credited.balanceAfter();
                this.version = credited.version();
            }
            case BankDebited debited -> {
                this.balance = debited.balanceAfter();
                this.version = debited.version();
            }
            default -> throw new IllegalStateException("Unexpected value: " + domainEvent);
        }
    }

    public void markEventsAsCommitted() {
        uncommittedEvents.clear();
    }

    public List<DomainEvent> getUncommittedEvents() {
        return Collections.unmodifiableList(uncommittedEvents);
    }

    public boolean hasUncommittedEvents() {
        return !uncommittedEvents.isEmpty();
    }

    private void validateCredit(Credits amount) {
        if (amount == null) {
            throw new NullAmountException();
        }
    }

    private void validateDebit(Credits amount) {
        if (amount == null) {
            throw new NullAmountException();
        }
        if (!balance.isGreaterThanOrEqual(amount)) {
            throw new InsufficientCreditsException("Insufficient credits. Current balance: " + balance + ", requested: " + amount);
        }
    }

    private void applyAndAddEvent(DomainEvent event) {
        apply(event);
        uncommittedEvents.add(event);
    }

    public BankId getId() {
        return id;
    }

    public Credits getBalance() {
        return balance;
    }

    public Long getVersion() {
        return version;
    }
}
