package com.stocksim.exception;

public class PortfolioNotFoundException extends RuntimeException {
    public PortfolioNotFoundException(String identifier) {
        super("Portfolio not found: " + identifier);
    }
}
