package com.stocksim.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Map;

public record RebalanceRequest(
        @NotEmpty(message = "Weights map is required") Map<String, Double> weights,
        @NotNull(message = "Date is required") LocalDate date) {
}
