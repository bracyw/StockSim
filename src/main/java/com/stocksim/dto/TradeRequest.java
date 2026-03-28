package com.stocksim.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record TradeRequest(
        @NotBlank(message = "Ticker is required") String ticker,
        @Positive(message = "Shares must be positive") double shares,
        @NotNull(message = "Date is required") LocalDate date) {
}
