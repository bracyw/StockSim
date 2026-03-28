package com.stocksim.exception;

public class StockNotFoundException extends RuntimeException {
    public StockNotFoundException(String ticker) {
        super("Stock not found: " + ticker);
    }
}
