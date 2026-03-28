package com.stocksim.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StockPriceResponse(
        String ticker,
        LocalDate date,
        BigDecimal open,
        BigDecimal high,
        BigDecimal low,
        BigDecimal close,
        Long volume) {
}
