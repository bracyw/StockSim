package com.stocksim.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PortfolioValueResponse(Long portfolioId, LocalDate date, BigDecimal totalValue) {
}
