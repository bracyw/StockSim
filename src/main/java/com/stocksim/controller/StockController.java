package com.stocksim.controller;

import com.stocksim.dto.StockPriceResponse;
import com.stocksim.service.StockService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/{ticker}/download")
    public ResponseEntity<Map<String, String>> downloadStock(@PathVariable String ticker) {
        String result = stockService.downloadStock(ticker.toUpperCase());
        return ResponseEntity.ok(Map.of("message", result));
    }

    @GetMapping("/{ticker}/price")
    public ResponseEntity<StockPriceResponse> getPrice(
            @PathVariable String ticker,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {
        if (date == null) {
            return ResponseEntity.ok(stockService.getMostRecentPrice(ticker.toUpperCase()));
        }
        BigDecimal price = stockService.getPrice(ticker.toUpperCase(), date);
        return ResponseEntity.ok(new StockPriceResponse(
                ticker.toUpperCase(), date, null, null, null, price, null));
    }

    @GetMapping("/{ticker}/gain-loss")
    public ResponseEntity<Map<String, Object>> getGainLoss(
            @PathVariable String ticker,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        BigDecimal gainLoss = stockService.getGainLoss(ticker.toUpperCase(), from, to);
        return ResponseEntity.ok(Map.of(
                "ticker", ticker.toUpperCase(),
                "from", from.toString(),
                "to", to.toString(),
                "gainLoss", gainLoss));
    }

    @GetMapping("/{ticker}/moving-average")
    public ResponseEntity<Map<String, Object>> getMovingAverage(
            @PathVariable String ticker,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "30") int days) {
        BigDecimal avg = stockService.getMovingAverage(ticker.toUpperCase(), date, days);
        return ResponseEntity.ok(Map.of(
                "ticker", ticker.toUpperCase(),
                "date", date.toString(),
                "days", days,
                "movingAverage", avg));
    }

    @GetMapping("/{ticker}/crossovers")
    public ResponseEntity<Map<String, Object>> getCrossovers(
            @PathVariable String ticker,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "30") int days) {
        Set<LocalDate> crossovers = stockService.getCrossovers(
                ticker.toUpperCase(), from, to, days);
        return ResponseEntity.ok(Map.of(
                "ticker", ticker.toUpperCase(),
                "from", from.toString(),
                "to", to.toString(),
                "days", days,
                "crossoverDates", crossovers));
    }

    @GetMapping("/{ticker}/history")
    public ResponseEntity<List<StockPriceResponse>> getHistory(
            @PathVariable String ticker,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(stockService.getHistory(ticker.toUpperCase(), from, to));
    }
}
