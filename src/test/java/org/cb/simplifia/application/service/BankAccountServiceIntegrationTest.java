package org.cb.simplifia.application.service;

import org.cb.simplifia.common.exception.InsufficientCreditsException;
import org.cb.simplifia.domain.model.Credits;
import org.cb.simplifia.domain.model.event.DomainEvent;
import org.cb.simplifia.domain.model.event.EventType;
import org.cb.simplifia.domain.port.output.DomainEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BankAccountServiceIntegrationTest {

    @Autowired
    private BankService bankService;

    @Autowired
    private DomainEventRepository domainEventRepository;

    @Test
    void shouldCreateBankAndPersistEvents() {
        // Given
        var initialAmount = 100;
        var initialCredits = new Credits(initialAmount);

        // When
        var accountId = bankService.createBank(initialAmount).getId();

        // Then
        assertNotNull(accountId);
        assertTrue(bankService.accountExists(accountId));
        assertEquals(initialCredits, bankService.getBalance(accountId));

        // Verify events are persisted
        List<DomainEvent> events = domainEventRepository.getEvents(accountId.getValue());
        assertEquals(1, events.size());
        assertEquals(EventType.CREATED, events.getFirst().getEventType());
    }

    @Test
    void shouldDebitCreditsAndPersistEvents() {
        // Given
        var initialAmount = 100;
        var accountId = bankService.createBank(initialAmount).getId();
        var orderId = "ORDER-123";
        var debitAmount = new Credits(30);

        // When
        bankService.debit(accountId.getValue(), debitAmount, orderId);

        // Then
        assertEquals(new Credits(70), bankService.getBalance(accountId));

        // Verify events are persisted
        List<DomainEvent> events = domainEventRepository.getEvents(accountId.getValue());
        assertEquals(2, events.size());
        assertEquals(EventType.CREATED, events.get(0).getEventType());
        assertEquals(EventType.DEBITED, events.get(1).getEventType());
    }

    @Test
    void shouldCreditCreditsAndPersistEvents() {
        // Given
        var initialAmount = 100;
        var accountId = bankService.createBank(initialAmount).getId();
        var orderId = "ORDER-123";
        var creditAmount = new Credits(50);

        // When
        bankService.credit(accountId.getValue(), creditAmount, orderId);

        // Then
        assertEquals(new Credits(150), bankService.getBalance(accountId));

        // Verify events are persisted
        List<DomainEvent> events = domainEventRepository.getEvents(accountId.getValue());
        assertEquals(2, events.size());
        assertEquals(EventType.CREATED, events.getFirst().getEventType());
        assertEquals(EventType.CREDITED, events.get(1).getEventType());
    }

    @Test
    void shouldThrowExceptionForInsufficientCredits() {
        // Given
        var initialAmount = 50;
        var initialCredits = new Credits(initialAmount);
        var accountId = bankService.createBank(50).getId();
        String orderId = "ORDER-123";
        Credits debitAmount = new Credits(100);

        // When & Then
        assertThrows(InsufficientCreditsException.class, () ->
            bankService.debit(accountId.getValue(), debitAmount, orderId));

        // Balance should remain unchanged
        assertEquals(initialCredits, bankService.getBalance(accountId));
    }

    @Test
    void shouldHandleCompleteScenario() {
        // Complete scenario: Create account, debit for order, credit for cancellation

        // Given
        var initialAmount = 100;
        var initialCredits = new Credits(initialAmount);
        var accountId = bankService.createBank(initialAmount).getId();
        var orderId = "ORDER-123";
        var orderCost = new Credits(25);

        // When - Create order (debit)
        bankService.debit(accountId.getValue(), orderCost, orderId);
        Credits balanceAfterDebit = bankService.getBalance(accountId);

        // Then
        assertEquals(new Credits(75), balanceAfterDebit);

        // When - Cancel order (credit)
        bankService.credit(accountId.getValue(), orderCost, orderId);
        Credits finalBalance = bankService.getBalance(accountId);

        // Then
        assertEquals(initialCredits, finalBalance);

        // Verify complete event history
        List<DomainEvent> events = domainEventRepository.getEvents(accountId.getValue());
        assertEquals(3, events.size());
        assertEquals(EventType.CREATED, events.getFirst().getEventType());
        assertEquals(EventType.DEBITED, events.get(1).getEventType());
        assertEquals(EventType.CREDITED, events.get(2).getEventType());
    }

    @Test
    void shouldReconstructAccountFromEvents() {
        // Given - Create account and perform operations
        var initialAmount = 100;
        var accountId = bankService.createBank(initialAmount).getId();
        var accountIdValue = accountId.getValue();

        bankService.debit(accountIdValue, new Credits(30), "ORDER-1");
        bankService.debit(accountIdValue, new Credits(20), "ORDER-2");
        bankService.credit(accountIdValue, new Credits(10), "ORDER-1");

        Credits expectedBalance = new Credits(60); // 100 - 30 - 20 + 10

        // When - Get balance (which reconstructs from events)
        Credits actualBalance = bankService.getBalance(accountId);

        // Then
        assertEquals(expectedBalance, actualBalance);

        // Verify event history
        List<DomainEvent> events = domainEventRepository.getEvents(accountId.getValue());
        assertEquals(4, events.size()); // Created + 2 Debits + 1 Credit
    }
}