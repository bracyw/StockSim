package com.stocksim.dto;

import jakarta.validation.constraints.NotBlank;

public record CreatePortfolioRequest(
        @NotBlank(message = "Portfolio name is required") String name) {
}
