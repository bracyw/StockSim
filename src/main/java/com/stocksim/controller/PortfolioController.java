package com.stocksim.controller;

import com.stocksim.dto.BuyBalancedRequest;
import com.stocksim.dto.CompositionEntry;
import com.stocksim.dto.CreatePortfolioRequest;
import com.stocksim.dto.DistributionEntry;
import com.stocksim.dto.PortfolioResponse;
import com.stocksim.dto.PortfolioValueResponse;
import com.stocksim.dto.RebalanceRequest;
import com.stocksim.dto.TradeRequest;
import com.stocksim.dto.TradeResponse;
import com.stocksim.service.PortfolioService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @PostMapping
    public ResponseEntity<PortfolioResponse> createPortfolio(
            @Valid @RequestBody CreatePortfolioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(portfolioService.createPortfolio(request.name()));
    }

    @GetMapping
    public ResponseEntity<List<PortfolioResponse>> listAll() {
        return ResponseEntity.ok(portfolioService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PortfolioResponse> getPortfolio(@PathVariable Long id) {
        return ResponseEntity.ok(portfolioService.getPortfolio(id));
    }

    @PostMapping("/{id}/buy")
    public ResponseEntity<TradeResponse> buy(
            @PathVariable Long id,
            @Valid @RequestBody TradeRequest request) {
        return ResponseEntity.ok(portfolioService.buy(
                id, request.ticker().toUpperCase(), request.shares(), request.date()));
    }

    @PostMapping("/{id}/sell")
    public ResponseEntity<TradeResponse> sell(
            @PathVariable Long id,
            @Valid @RequestBody TradeRequest request) {
        return ResponseEntity.ok(portfolioService.sell(
                id, request.ticker().toUpperCase(), request.shares(), request.date()));
    }

    @GetMapping("/{id}/value")
    public ResponseEntity<PortfolioValueResponse> getTotalValue(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        BigDecimal value = portfolioService.getTotalValue(id, date);
        return ResponseEntity.ok(new PortfolioValueResponse(id, date, value));
    }

    @GetMapping("/{id}/composition")
    public ResponseEntity<List<CompositionEntry>> getComposition(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(portfolioService.getComposition(id, date));
    }

    @GetMapping("/{id}/distribution")
    public ResponseEntity<List<DistributionEntry>> getDistribution(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(portfolioService.getDistribution(id, date));
    }

    @PostMapping("/{id}/buy-balanced")
    public ResponseEntity<Map<String, TradeResponse>> buyBalanced(
            @PathVariable Long id,
            @Valid @RequestBody BuyBalancedRequest request) {
        return ResponseEntity.ok(portfolioService.buyBalanced(
                id, request.weights(), request.amount(), request.date()));
    }

    @PostMapping("/{id}/rebalance")
    public ResponseEntity<Map<String, TradeResponse>> rebalance(
            @PathVariable Long id,
            @Valid @RequestBody RebalanceRequest request) {
        return ResponseEntity.ok(portfolioService.rebalance(
                id, request.weights(), request.date()));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<Map<LocalDate, BigDecimal>> getValueHistory(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(portfolioService.getValueHistory(id, from, to));
    }
}
