package com.stocksim.dto;

import java.math.BigDecimal;

public record DistributionEntry(String ticker, double shares, BigDecimal value) {
}
