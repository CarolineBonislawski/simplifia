package org.cb.simplifia.common.exception;

public class NullAmountException extends IllegalArgumentException {

    public NullAmountException() {
        super("Debit amount cannot be null");
    }
}
