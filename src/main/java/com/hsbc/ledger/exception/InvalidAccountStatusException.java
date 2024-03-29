package com.hsbc.ledger.exception;

public class InvalidAccountStatusException extends RuntimeException {

    public InvalidAccountStatusException(String message) {
        super(message);
    }
}
