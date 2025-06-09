package org.cb.simplifia.application.service;

import org.cb.simplifia.common.exception.BankNotExistsException;
import org.cb.simplifia.domain.model.Bank;
import org.cb.simplifia.domain.model.BankId;
import org.cb.simplifia.domain.model.Credits;
import org.cb.simplifia.domain.port.input.BankUseCase;
import org.cb.simplifia.domain.port.output.BankRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BankService implements BankUseCase {

    private final BankRepository bankRepository;

    public BankService(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    @Override
    @Transactional
    public Bank createBank(int initialAmount) {
        var bank = new Bank(new Credits(initialAmount));
        bankRepository.save(bank);
        return bank;
    }

    @Override
    @Transactional
    public void credit(String bankId, Credits credits, String orderId) {
        var bank = bankRepository.findById(BankId.from(bankId)).orElseThrow(BankNotExistsException::new);
        bank.credits(credits, orderId);
        bankRepository.save(bank);
    }

    @Override
    @Transactional
    public void debit(String bankId, Credits credits, String orderId) {
        var bank = bankRepository.findById(BankId.from(bankId)).orElseThrow(BankNotExistsException::new);
        bank.debits(credits, orderId);
        bankRepository.save(bank);
    }

    public boolean accountExists(BankId bankId) {
        return bankRepository.exists(bankId);
    }

    public Credits getBalance(BankId accountId) {
        var bankAccount = bankRepository.findById(accountId).orElseThrow(BankNotExistsException::new);
        return bankAccount.getBalance();
    }
}
