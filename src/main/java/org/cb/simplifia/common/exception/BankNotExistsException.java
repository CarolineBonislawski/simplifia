package org.cb.simplifia.common.exception;

import java.util.NoSuchElementException;

public class BankNotExistsException extends NoSuchElementException {

    public BankNotExistsException() {
        super("Bank not found");
    }
}
