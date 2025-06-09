package org.cb.simplifia.infrastructure.web.controller;

import jakarta.validation.Valid;
import org.cb.simplifia.domain.model.Credits;
import org.cb.simplifia.domain.port.input.BankUseCase;
import org.cb.simplifia.infrastructure.web.mapper.BankMapper;
import org.cb.simplifia.infrastructure.web.payload.CreateBankPayload;
import org.cb.simplifia.infrastructure.web.payload.CreditCreditsPayload;
import org.cb.simplifia.infrastructure.web.payload.DebitCreditsPayload;
import org.cb.simplifia.infrastructure.web.response.CreateBankResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/banks")
public class BankController {

    private final BankUseCase bankUseCase;
    private final BankMapper bankMapper;

    public BankController(BankUseCase bankUseCase, BankMapper bankMapper) {
        this.bankUseCase = bankUseCase;
        this.bankMapper = bankMapper;
    }

    @PostMapping
    public CreateBankResponse create(@RequestBody @Validated CreateBankPayload payload) {
        var bank = bankUseCase.createBank(payload.amount());
        return bankMapper.toResponse(bank);
    }

    @PostMapping("/{bankId}/debits")
    public void debit(@PathVariable String bankId, @RequestBody @Valid DebitCreditsPayload payload) {
        var credits = new Credits(payload.amount());
        bankUseCase.debit(bankId, credits, payload.orderId());
    }

    @PostMapping("/{bankId}/credits")
    public void credit(@PathVariable String bankId, @RequestBody @Valid CreditCreditsPayload payload) {
        var credits = new Credits(payload.amount());
        bankUseCase.credit(bankId, credits, payload.orderId());
    }

}
