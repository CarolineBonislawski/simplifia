package org.cb.simplifia.domain.port.output;

import org.cb.simplifia.domain.model.Bank;
import org.cb.simplifia.domain.model.BankId;

import java.util.Optional;

public interface BankRepository {

    Optional<Bank> findById(BankId bankId);
    void save(Bank bank);
    boolean exists(BankId bankId);

}
