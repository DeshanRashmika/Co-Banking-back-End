package edu.icet.banking.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidOperationException extends BankingException {
    public InvalidOperationException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_OPERATION");
    }
}

