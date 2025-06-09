package org.cb.simplifia.infrastructure.web.mapper;

import org.cb.simplifia.infrastructure.web.response.CreateBankResponse;
import org.cb.simplifia.domain.model.Bank;
import org.springframework.stereotype.Component;

@Component
public class BankMapper {

    public CreateBankResponse toResponse(Bank bank) {
        return new CreateBankResponse(bank.getId().getValue());
    }
}
