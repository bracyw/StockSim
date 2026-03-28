package com.stocksim.exception;

public class ApiRateLimitException extends RuntimeException {
    public ApiRateLimitException() {
        super("Alpha Vantage API rate limit reached. Please try again later.");
    }
}
