package org.cb.simplifia.domain.port.input;

import org.cb.simplifia.domain.model.Bank;
import org.cb.simplifia.domain.model.Credits;

public interface BankUseCase {

    Bank createBank(int initialAmount);
    void credit(String bankId, Credits credits, String orderId);
    void debit(String bankId, Credits credits, String orderId);

}
