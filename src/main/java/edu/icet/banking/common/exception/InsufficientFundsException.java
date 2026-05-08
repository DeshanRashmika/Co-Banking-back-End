package edu.icet.banking.common.exception;

import org.springframework.http.HttpStatus;

public class InsufficientFundsException extends BankingException {
    public InsufficientFundsException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INSUFFICIENT_FUNDS");
    }

    public InsufficientFundsException(String accountId, String requiredAmount) {
        super(
            String.format("Insufficient funds in account %s. Required: %s", accountId, requiredAmount),
            HttpStatus.BAD_REQUEST,
            "INSUFFICIENT_FUNDS"
        );
    }
}

