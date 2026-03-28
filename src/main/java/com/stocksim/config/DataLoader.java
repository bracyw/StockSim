package com.stocksim.config;

import com.stocksim.entity.Portfolio;
import com.stocksim.entity.StockPrice;
import com.stocksim.entity.Transaction;
import com.stocksim.entity.TransactionType;
import com.stocksim.repository.PortfolioRepository;
import com.stocksim.repository.StockPriceRepository;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final StockPriceRepository stockPriceRepo;
    private final PortfolioRepository portfolioRepo;

    @Value("${stocksim.import.stock-dir:StockInfo}")
    private String stockDir;

    @Value("${stocksim.import.portfolio-dir:PortfolioInfo}")
    private String portfolioDir;

    public DataLoader(StockPriceRepository stockPriceRepo, PortfolioRepository portfolioRepo) {
        this.stockPriceRepo = stockPriceRepo;
        this.portfolioRepo = portfolioRepo;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (stockPriceRepo.count() == 0) {
            importStockData();
        } else {
            log.info("Stock data already present, skipping import.");
        }
        if (portfolioRepo.count() == 0) {
            importPortfolioData();
        } else {
            log.info("Portfolio data already present, skipping import.");
        }
    }

    private void importStockData() {
        Path dir = Paths.get(stockDir);
        if (!Files.isDirectory(dir)) {
            log.warn("Stock data directory not found: {}. Skipping stock import.", dir);
            return;
        }

        log.info("Importing stock data from {}...", dir);
        int totalRecords = 0;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.txt")) {
            for (Path file : stream) {
                String fileName = file.getFileName().toString();
                String ticker = fileName.substring(0, fileName.lastIndexOf('.'));
                try {
                    List<StockPrice> prices = parseCsvFile(ticker, file);
                    stockPriceRepo.saveAll(prices);
                    totalRecords += prices.size();
                    log.info("  Imported {} records for {}", prices.size(), ticker);
                } catch (Exception e) {
                    log.warn("  Failed to import {}: {}", ticker, e.getMessage());
                }
            }
        } catch (IOException e) {
            log.error("Error reading stock directory: {}", e.getMessage());
        }

        log.info("Stock import complete: {} total records.", totalRecords);
    }

    private List<StockPrice> parseCsvFile(String ticker, Path file) throws IOException {
        List<String> lines = Files.readAllLines(file);
        List<StockPrice> prices = new ArrayList<>();

        // Skip header line
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split(",");
            if (parts.length < 6) continue;

            try {
                LocalDate date = LocalDate.parse(parts[0].trim());
                BigDecimal open = new BigDecimal(parts[1].trim());
                BigDecimal high = new BigDecimal(parts[2].trim());
                BigDecimal low = new BigDecimal(parts[3].trim());
                BigDecimal close = new BigDecimal(parts[4].trim());
                long volume = Long.parseLong(parts[5].trim());
                prices.add(new StockPrice(ticker, date, open, high, low, close, volume));
            } catch (Exception e) {
                // skip malformed lines
            }
        }
        return prices;
    }

    private void importPortfolioData() {
        Path dir = Paths.get(portfolioDir);
        if (!Files.isDirectory(dir)) {
            log.warn("Portfolio directory not found: {}. Skipping portfolio import.", dir);
            return;
        }

        log.info("Importing portfolios from {}...", dir);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.txt")) {
            for (Path file : stream) {
                try {
                    importPortfolioFile(file);
                } catch (Exception e) {
                    log.warn("  Failed to import portfolio {}: {}",
                            file.getFileName(), e.getMessage());
                }
            }
        } catch (IOException e) {
            log.error("Error reading portfolio directory: {}", e.getMessage());
        }
    }

    private void importPortfolioFile(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file);
        if (lines.isEmpty()) return;

        String name = lines.get(0).trim();
        if (name.isBlank()) return;

        if (portfolioRepo.existsByName(name)) {
            log.info("  Portfolio '{}' already exists, skipping.", name);
            return;
        }

        Portfolio portfolio = new Portfolio(name);

        // Parse alternating date/command lines
        int i = 1;
        while (i + 1 < lines.size()) {
            LocalDate date;
            try {
                date = LocalDate.parse(lines.get(i).trim());
            } catch (Exception e) {
                i++;
                continue;
            }
            String commandLine = lines.get(i + 1).trim();
            i += 2;

            String[] commands = commandLine.split(",");
            for (String cmd : commands) {
                String[] parts = cmd.trim().split("\\s+");
                if (parts.length < 3) continue;
                String action = parts[0];
                String ticker = parts[1];
                double shares;
                try {
                    shares = Double.parseDouble(parts[2]);
                } catch (NumberFormatException e) {
                    continue;
                }

                TransactionType type;
                if ("buy".equalsIgnoreCase(action)) {
                    type = TransactionType.BUY;
                } else if ("sell".equalsIgnoreCase(action)) {
                    type = TransactionType.SELL;
                } else {
                    continue; // skip rebalance commands for now (complex to replay)
                }

                Transaction tx = new Transaction(ticker, type, shares,
                        BigDecimal.ZERO, date); // price unknown at import time
                portfolio.addTransaction(tx);
            }
        }

        portfolioRepo.save(portfolio);
        log.info("  Imported portfolio '{}' with {} transactions.",
                name, portfolio.getTransactions().size());
    }
}
