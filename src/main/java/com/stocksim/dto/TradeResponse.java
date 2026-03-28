package com.stocksim.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TradeResponse(
        String ticker,
        String type,
        double shares,
        BigDecimal pricePerShare,
        BigDecimal totalCost,
        LocalDate date) {
}
