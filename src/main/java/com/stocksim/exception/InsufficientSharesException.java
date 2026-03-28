package com.stocksim.exception;

public class InsufficientSharesException extends RuntimeException {
    public InsufficientSharesException(String ticker, double requested, double available) {
        super(String.format("Cannot sell %.4f shares of %s, only %.4f available",
                requested, ticker, available));
    }
}
