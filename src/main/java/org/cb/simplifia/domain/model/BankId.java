package org.cb.simplifia.domain.model;

import java.util.UUID;

public class BankId {

    private final String value;

    private BankId(String value) {
        this.value = value;
    }

    public static BankId generate() {
        return new BankId(UUID.randomUUID().toString());
    }

    public static BankId from(String value) {
        return new BankId(value);
    }

    public String getValue() {
        return value;
    }
}
