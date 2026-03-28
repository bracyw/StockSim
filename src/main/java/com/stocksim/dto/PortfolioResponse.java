package com.stocksim.dto;

import java.time.LocalDateTime;

public record PortfolioResponse(Long id, String name, LocalDateTime createdAt) {
}
