package com.stocksim.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.Map;

public record BuyBalancedRequest(
        @NotEmpty(message = "Weights map is required") Map<String, Double> weights,
        @Positive(message = "Amount must be positive") double amount,
        @NotNull(message = "Date is required") LocalDate date) {
}
