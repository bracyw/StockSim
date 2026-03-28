package com.stocksim.service;

import com.stocksim.dto.CompositionEntry;
import com.stocksim.dto.DistributionEntry;
import com.stocksim.dto.PortfolioResponse;
import com.stocksim.dto.TradeResponse;
import com.stocksim.entity.Portfolio;
import com.stocksim.entity.Transaction;
import com.stocksim.entity.TransactionType;
import com.stocksim.exception.InsufficientSharesException;
import com.stocksim.exception.PortfolioNotFoundException;
import com.stocksim.repository.PortfolioRepository;
import com.stocksim.repository.TransactionRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepo;
    private final TransactionRepository transactionRepo;
    private final StockService stockService;

    public PortfolioService(PortfolioRepository portfolioRepo,
                            TransactionRepository transactionRepo,
                            StockService stockService) {
        this.portfolioRepo = portfolioRepo;
        this.transactionRepo = transactionRepo;
        this.stockService = stockService;
    }

    @Transactional
    public PortfolioResponse createPortfolio(String name) {
        if (portfolioRepo.existsByName(name)) {
            throw new IllegalArgumentException("Portfolio already exists: " + name);
        }
        Portfolio portfolio = portfolioRepo.save(new Portfolio(name));
        return toResponse(portfolio);
    }

    public List<PortfolioResponse> listAll() {
        return portfolioRepo.findAll().stream().map(this::toResponse).toList();
    }

    public PortfolioResponse getPortfolio(Long id) {
        return toResponse(findPortfolio(id));
    }

    public PortfolioResponse getPortfolioByName(String name) {
        Portfolio p = portfolioRepo.findByName(name)
                .orElseThrow(() -> new PortfolioNotFoundException(name));
        return toResponse(p);
    }

    /**
     * Buys shares of a stock on a given date.
     * Mirrors BetterPortfolioImpl.buyDate().
     */
    @Transactional
    public TradeResponse buy(Long portfolioId, String ticker, double shares, LocalDate date) {
        if (shares <= 0) {
            throw new IllegalArgumentException("Shares must be positive");
        }
        Portfolio portfolio = findPortfolio(portfolioId);
        BigDecimal price = stockService.getClosestPrice(ticker, date);

        Transaction tx = new Transaction(ticker, TransactionType.BUY, shares, price, date);
        portfolio.addTransaction(tx);
        transactionRepo.save(tx);

        BigDecimal totalCost = price.multiply(BigDecimal.valueOf(shares));
        return new TradeResponse(ticker, "BUY", shares, price, totalCost, date);
    }

    /**
     * Sells shares of a stock on a given date.
     * Mirrors BetterPortfolioImpl.sellDate() - validates sufficient shares.
     */
    @Transactional
    public TradeResponse sell(Long portfolioId, String ticker, double shares, LocalDate date) {
        if (shares <= 0) {
            throw new IllegalArgumentException("Shares must be positive");
        }
        Portfolio portfolio = findPortfolio(portfolioId);

        // Check holdings at this date
        Map<String, Double> holdings = computeHoldings(portfolioId, date);
        double available = holdings.getOrDefault(ticker, 0.0);
        if (available < shares) {
            throw new InsufficientSharesException(ticker, shares, available);
        }

        BigDecimal price = stockService.getClosestPrice(ticker, date);
        Transaction tx = new Transaction(ticker, TransactionType.SELL, shares, price, date);
        portfolio.addTransaction(tx);
        transactionRepo.save(tx);

        BigDecimal totalProceeds = price.multiply(BigDecimal.valueOf(shares));
        return new TradeResponse(ticker, "SELL", shares, price, totalProceeds, date);
    }

    /**
     * Computes portfolio composition (holdings) at a given date by replaying transactions.
     * Mirrors BetterPortfolioImpl.getPortfolio(date) -> composition().
     */
    public List<CompositionEntry> getComposition(Long portfolioId, LocalDate date) {
        findPortfolio(portfolioId); // validate exists
        Map<String, Double> holdings = computeHoldings(portfolioId, date);
        return holdings.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .map(e -> new CompositionEntry(e.getKey(), e.getValue()))
                .toList();
    }

    /**
     * Computes portfolio distribution (holdings * prices) at a given date.
     * Mirrors BetterPortfolioImpl.distribution().
     */
    public List<DistributionEntry> getDistribution(Long portfolioId, LocalDate date) {
        findPortfolio(portfolioId);
        Map<String, Double> holdings = computeHoldings(portfolioId, date);
        return holdings.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .map(e -> {
                    BigDecimal price = stockService.getClosestPrice(e.getKey(), date);
                    BigDecimal value = price.multiply(BigDecimal.valueOf(e.getValue()));
                    return new DistributionEntry(e.getKey(), e.getValue(), value);
                })
                .toList();
    }

    /**
     * Computes total portfolio value at a given date.
     * Mirrors BetterPortfolioImpl.totalValue().
     */
    public BigDecimal getTotalValue(Long portfolioId, LocalDate date) {
        List<DistributionEntry> dist = getDistribution(portfolioId, date);
        return dist.stream()
                .map(DistributionEntry::value)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Buys multiple stocks weighted by percentage.
     * Mirrors BetterPortfolioImpl.balance().
     */
    @Transactional
    public Map<String, TradeResponse> buyBalanced(Long portfolioId, Map<String, Double> weights,
                                                   double amount, LocalDate date) {
        validateWeights(weights);
        findPortfolio(portfolioId);

        Map<String, TradeResponse> results = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : weights.entrySet()) {
            String ticker = entry.getKey();
            double percentage = entry.getValue();
            BigDecimal price = stockService.getClosestPrice(ticker, date);
            double sharesToBuy = (amount * percentage * 0.01) / price.doubleValue();
            TradeResponse trade = buy(portfolioId, ticker, sharesToBuy, date);
            results.put(ticker, trade);
        }
        return results;
    }

    /**
     * Rebalances portfolio holdings according to target weights.
     * Mirrors BetterPortfolioImpl.reBalance().
     */
    @Transactional
    public Map<String, TradeResponse> rebalance(Long portfolioId, Map<String, Double> weights,
                                                 LocalDate date) {
        validateWeights(weights);
        findPortfolio(portfolioId);

        Map<String, Double> holdings = computeHoldings(portfolioId, date);

        // Calculate total value of stocks being rebalanced
        BigDecimal totalValue = BigDecimal.ZERO;
        for (String ticker : weights.keySet()) {
            double held = holdings.getOrDefault(ticker, 0.0);
            if (held <= 0) {
                throw new IllegalArgumentException(
                        "Cannot rebalance " + ticker + ": not held in portfolio");
            }
            BigDecimal price = stockService.getClosestPrice(ticker, date);
            totalValue = totalValue.add(price.multiply(BigDecimal.valueOf(held)));
        }

        if (totalValue.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("No value in specified stocks to rebalance");
        }

        // First sell all shares of rebalanced stocks, then buy target amounts
        Map<String, TradeResponse> results = new LinkedHashMap<>();
        for (String ticker : weights.keySet()) {
            double held = holdings.getOrDefault(ticker, 0.0);
            if (held > 0) {
                sell(portfolioId, ticker, held, date);
            }
        }
        for (Map.Entry<String, Double> entry : weights.entrySet()) {
            String ticker = entry.getKey();
            double targetPct = entry.getValue();
            BigDecimal price = stockService.getClosestPrice(ticker, date);
            BigDecimal targetValue = totalValue.multiply(BigDecimal.valueOf(targetPct * 0.01));
            double targetShares = targetValue.divide(price, 10, RoundingMode.HALF_UP).doubleValue();
            TradeResponse trade = buy(portfolioId, ticker, targetShares, date);
            results.put(ticker, trade);
        }
        return results;
    }

    /**
     * Gets portfolio value over time for plotting.
     * Mirrors BetterPortfolioImpl.plot().
     */
    public Map<LocalDate, BigDecimal> getValueHistory(Long portfolioId,
                                                       LocalDate from, LocalDate to) {
        findPortfolio(portfolioId);
        int interval = StockService.computeInterval(from, to);
        Map<LocalDate, BigDecimal> data = new LinkedHashMap<>();

        LocalDate active = from;
        while (active.isBefore(to)) {
            try {
                data.put(active, getTotalValue(portfolioId, active));
            } catch (Exception e) {
                // skip dates with no data
            }
            active = active.plusDays(interval);
        }
        try {
            data.put(to, getTotalValue(portfolioId, to));
        } catch (Exception e) {
            // skip
        }
        return data;
    }

    /**
     * Core method: replays transactions up to a date to compute holdings.
     * This replaces the command-replay system in BetterPortfolioImpl.
     */
    private Map<String, Double> computeHoldings(Long portfolioId, LocalDate date) {
        List<Transaction> txs = transactionRepo
                .findByPortfolioIdAndDateLessThanEqualOrderByDateAscIdAsc(portfolioId, date);

        Map<String, Double> holdings = new LinkedHashMap<>();
        for (Transaction tx : txs) {
            double current = holdings.getOrDefault(tx.getTicker(), 0.0);
            if (tx.getType() == TransactionType.BUY) {
                holdings.put(tx.getTicker(), current + tx.getShares());
            } else {
                double newAmount = current - tx.getShares();
                if (newAmount <= 0) {
                    holdings.remove(tx.getTicker());
                } else {
                    holdings.put(tx.getTicker(), newAmount);
                }
            }
        }
        return holdings;
    }

    private Portfolio findPortfolio(Long id) {
        return portfolioRepo.findById(id)
                .orElseThrow(() -> new PortfolioNotFoundException(id.toString()));
    }

    private void validateWeights(Map<String, Double> weights) {
        double total = weights.values().stream().mapToDouble(Double::doubleValue).sum();
        if (Math.abs(total - 100.0) > 0.01) {
            throw new IllegalArgumentException(
                    "Weights must add up to 100%, got " + total + "%");
        }
        for (Map.Entry<String, Double> e : weights.entrySet()) {
            if (e.getValue() < 0) {
                throw new IllegalArgumentException("Cannot have negative weight for " + e.getKey());
            }
        }
    }

    private PortfolioResponse toResponse(Portfolio p) {
        return new PortfolioResponse(p.getId(), p.getName(), p.getCreatedAt());
    }
}
