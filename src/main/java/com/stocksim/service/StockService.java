package com.stocksim.service;

import com.stocksim.client.AlphaVantageClient;
import com.stocksim.dto.StockPriceResponse;
import com.stocksim.entity.StockPrice;
import com.stocksim.exception.StockNotFoundException;
import com.stocksim.repository.StockPriceRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {

    private final StockPriceRepository stockPriceRepo;
    private final AlphaVantageClient alphaVantageClient;

    public StockService(StockPriceRepository stockPriceRepo,
                        AlphaVantageClient alphaVantageClient) {
        this.stockPriceRepo = stockPriceRepo;
        this.alphaVantageClient = alphaVantageClient;
    }

    /**
     * Downloads stock data from Alpha Vantage and saves to database.
     * Replaces existing data for the ticker.
     */
    @Transactional
    public String downloadStock(String ticker) {
        List<StockPrice> prices = alphaVantageClient.fetchDailyPrices(ticker);
        if (prices.isEmpty()) {
            throw new RuntimeException("No price data returned for " + ticker);
        }
        stockPriceRepo.deleteByTicker(ticker);
        stockPriceRepo.saveAll(prices);
        return "Downloaded " + prices.size() + " price records for " + ticker;
    }

    /**
     * Gets the closing price of a stock on a specific date.
     * Mirrors SimpleStock.getPrice(date).
     */
    public BigDecimal getPrice(String ticker, LocalDate date) {
        validateStockExists(ticker);
        StockPrice sp = stockPriceRepo.findByTickerAndDate(ticker, date)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No price data for " + ticker + " on " + date));
        return sp.getClose();
    }

    /**
     * Gets the closest closing price on or before the given date.
     * Mirrors SimpleStock.fetchClosestStockPrice(date).
     */
    public BigDecimal getClosestPrice(String ticker, LocalDate date) {
        validateStockExists(ticker);
        return stockPriceRepo.findClosestPriceOnOrBefore(ticker, date)
                .map(StockPrice::getClose)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No price data for " + ticker + " on or before " + date));
    }

    /**
     * Calculates gain/loss between two dates.
     * Mirrors SimpleStock.gainLoss(from, to).
     */
    public BigDecimal getGainLoss(String ticker, LocalDate from, LocalDate to) {
        validateStockExists(ticker);
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Start date is after end date");
        }
        validateDateRange(ticker, from, to);

        BigDecimal fromPrice = getClosestPrice(ticker, from);
        BigDecimal toPrice = getClosestPrice(ticker, to);
        return toPrice.subtract(fromPrice);
    }

    /**
     * Calculates X-day moving average ending on the given date.
     * Mirrors SimpleStock.xDayAvg(date, days).
     */
    public BigDecimal getMovingAverage(String ticker, LocalDate date, int days) {
        validateStockExists(ticker);
        List<StockPrice> prices = stockPriceRepo.findByTickerOnOrBeforeDate(ticker, date);

        if (prices.isEmpty()) {
            throw new IllegalArgumentException("No data for " + ticker + " on or before " + date);
        }

        int count = Math.min(days, prices.size());
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < count; i++) {
            sum = sum.add(prices.get(i).getClose());
        }

        if (count < days) {
            throw new IllegalArgumentException(
                    String.format("Only %d days available. Running average is %.4f",
                            count, sum.divide(BigDecimal.valueOf(count), 4, RoundingMode.HALF_UP)
                                    .doubleValue()));
        }

        return sum.divide(BigDecimal.valueOf(count), 4, RoundingMode.HALF_UP);
    }

    /**
     * Finds X-day crossover dates in a range.
     * Mirrors SimpleStock.xDayCrossover(from, to, days).
     */
    public Set<LocalDate> getCrossovers(String ticker, LocalDate from, LocalDate to, int days) {
        validateStockExists(ticker);
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Start date is after end date");
        }
        validateDateRange(ticker, from, to);

        List<StockPrice> prices = stockPriceRepo
                .findByTickerAndDateBetweenOrderByDateDesc(ticker, from, to);

        Set<LocalDate> crossovers = new HashSet<>();
        for (StockPrice sp : prices) {
            try {
                BigDecimal avg = getMovingAverage(ticker, sp.getDate(), days);
                if (sp.getClose().compareTo(avg) > 0) {
                    crossovers.add(sp.getDate());
                }
            } catch (IllegalArgumentException e) {
                // Not enough data for this date's moving average, skip
            }
        }
        return crossovers;
    }

    /**
     * Gets stock price history between two dates (for plotting/charting).
     */
    public List<StockPriceResponse> getHistory(String ticker, LocalDate from, LocalDate to) {
        validateStockExists(ticker);
        return stockPriceRepo.findByTickerAndDateBetweenOrderByDateDesc(ticker, from, to)
                .stream()
                .map(sp -> new StockPriceResponse(sp.getTicker(), sp.getDate(),
                        sp.getOpen(), sp.getHigh(), sp.getLow(), sp.getClose(), sp.getVolume()))
                .toList();
    }

    /**
     * Gets the most recent price data for a stock.
     */
    public StockPriceResponse getMostRecentPrice(String ticker) {
        StockPrice sp = stockPriceRepo.findMostRecentByTicker(ticker)
                .orElseThrow(() -> new StockNotFoundException(ticker));
        return new StockPriceResponse(sp.getTicker(), sp.getDate(),
                sp.getOpen(), sp.getHigh(), sp.getLow(), sp.getClose(), sp.getVolume());
    }

    /**
     * Returns a bar graph of stock prices over time (preserves legacy plot feature).
     */
    public Map<LocalDate, BigDecimal> getPlotData(String ticker, LocalDate from, LocalDate to) {
        validateStockExists(ticker);
        int interval = computeInterval(from, to);
        Map<LocalDate, BigDecimal> data = new LinkedHashMap<>();

        LocalDate active = from;
        while (active.isBefore(to)) {
            try {
                data.put(active, getClosestPrice(ticker, active));
            } catch (IllegalArgumentException e) {
                // skip dates with no data
            }
            active = active.plusDays(interval);
        }
        try {
            data.put(to, getClosestPrice(ticker, to));
        } catch (IllegalArgumentException e) {
            // skip
        }
        return data;
    }

    public boolean stockExists(String ticker) {
        return stockPriceRepo.existsByTicker(ticker);
    }

    private void validateStockExists(String ticker) {
        if (!stockPriceRepo.existsByTicker(ticker)) {
            throw new StockNotFoundException(ticker);
        }
    }

    private void validateDateRange(String ticker, LocalDate from, LocalDate to) {
        StockPrice earliest = stockPriceRepo.findEarliestByTicker(ticker)
                .orElseThrow(() -> new StockNotFoundException(ticker));
        StockPrice latest = stockPriceRepo.findMostRecentByTicker(ticker)
                .orElseThrow(() -> new StockNotFoundException(ticker));

        if (from.isBefore(earliest.getDate())) {
            throw new IllegalArgumentException("Date " + from + " is before earliest data ("
                    + earliest.getDate() + ") for " + ticker);
        }
        if (to.isAfter(latest.getDate())) {
            throw new IllegalArgumentException("Date " + to + " is after latest data ("
                    + latest.getDate() + ") for " + ticker);
        }
    }

    static int computeInterval(LocalDate from, LocalDate to) {
        long days = java.time.temporal.ChronoUnit.DAYS.between(from, to);
        if (days <= 10) return 1;
        if (days <= 58) return 2;
        if (days <= 175) return 7;
        if (days <= 870) return 30;
        if (days <= 5475) return 365;
        return 365 * 3;
    }
}
