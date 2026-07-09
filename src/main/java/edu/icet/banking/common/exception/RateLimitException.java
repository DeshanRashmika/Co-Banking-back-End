package edu.icet.banking.common.exception;

import org.springframework.http.HttpStatus;

public class RateLimitException extends BankingException {

    private final long retryAfterSeconds;

    public RateLimitException(String message, long retryAfterSeconds) {
        super(message, HttpStatus.TOO_MANY_REQUESTS, "RATE_LIMIT_EXCEEDED");
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
