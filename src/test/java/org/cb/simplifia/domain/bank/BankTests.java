package org.cb.simplifia.domain.bank;

import org.cb.simplifia.common.exception.NullAmountException;
import org.cb.simplifia.domain.model.Credits;
import org.cb.simplifia.common.exception.InsufficientCreditsException;
import org.cb.simplifia.domain.model.event.BankCreated;
import org.cb.simplifia.domain.model.event.BankCredited;
import org.cb.simplifia.domain.model.event.BankDebited;
import org.cb.simplifia.domain.model.event.DomainEvent;
import org.cb.simplifia.domain.model.Bank;
import org.cb.simplifia.domain.model.BankId;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BankTests {

    @Test
    void shouldCreateBankWithInitialCredits() {
        // Given
        var initialCredits = new Credits(100);

        // When
        var account = new Bank(initialCredits);

        // Then
        assertNotNull(account.getId());
        assertEquals(initialCredits, account.getBalance());
        assertEquals(1, account.getVersion());
        assertFalse(account.getUncommittedEvents().isEmpty());
        assertEquals(1, account.getUncommittedEvents().size());

        var event = account.getUncommittedEvents().getFirst();
        assertInstanceOf(BankCreated.class, event);
    }

    @Test
    void shouldThrowExceptionWhenBankNotExists() {
        // Given
        var initialCredits = new Credits(50);
        var account = new Bank(initialCredits);

        // When & Then
        assertThrows(NullAmountException.class, () -> account.debits(null, "ORDER-123"));
    }

    @Test
    void shouldThrowExceptionWhenInsufficientCredits() {
        // Given
        var initialCredits = new Credits(50);
        var account = new Bank(initialCredits);
        var debitAmount = new Credits(100);

        // When & Then
        assertThrows(InsufficientCreditsException.class, () -> account.debits(debitAmount, "ORDER-123"));
    }

    @Test
    void shouldThrowExceptionWhenAmountNull() {
        // Given
        var initialCredits = new Credits(50);
        var account = new Bank(initialCredits);

        // When & Then
        assertThrows(NullAmountException.class, () -> account.debits(null, "ORDER-123"));
    }

    @Test
    void shouldCreditCreditsSuccessfully() {
        // Given
        var initialCredits = new Credits(100);
        var account = new Bank(initialCredits);
        account.markEventsAsCommitted();

        var creditAmount = new Credits(50);
        var orderId = "ORDER-123";

        // When
        account.credits(creditAmount, orderId);

        // Then
        assertEquals(new Credits(150), account.getBalance());
        assertEquals(2, account.getVersion());
        assertTrue(account.hasUncommittedEvents());

        var event = account.getUncommittedEvents().getFirst();
        assertInstanceOf(BankCredited.class, event);
        var creditedEvent = (BankCredited) event;
        assertEquals(creditAmount, creditedEvent.creditedAmount());
        assertEquals(new Credits(150), creditedEvent.balanceAfter());
        assertEquals(orderId, creditedEvent.orderId());
    }

    @Test
    void shouldReconstructFromEvents() {
        // Given
        var id = BankId.generate();
        var initialCredits = new Credits(100);

        List<DomainEvent> events = List.of(
            new BankCreated(id, initialCredits, 1),
            new BankDebited(id, new Credits(30), new Credits(70), "Order created", "ORDER-1", 2),
            new BankCredited(id, new Credits(20), new Credits(90), "Order cancelled", "ORDER-2", 3)
        );

        // When
        var account = Bank.from(events);

        // Then
        assertEquals(id, account.getId());
        assertEquals(new Credits(90), account.getBalance());
        assertEquals(3, account.getVersion());
        assertFalse(account.hasUncommittedEvents());
    }

    @Test
    void shouldHandleScenario1() {
        // Given
        var initialCredits = new Credits(100);
        var account = new Bank(initialCredits);
        account.markEventsAsCommitted();

        // When
        account.debits(new Credits(1), "C1");

        // Then
        assertEquals(new Credits(99), account.getBalance());
    }

    @Test
    void shouldHandleScenario2() {
        // Given
        var initialCredits = new Credits(100);
        var account = new Bank(initialCredits);
        account.markEventsAsCommitted();

        // When
        account.debits(new Credits(1), "C1");
        assertEquals(new Credits(99), account.getBalance());

        account.credits(new Credits(1), "C1");

        // Then
        assertEquals(new Credits(100), account.getBalance());
    }
}
