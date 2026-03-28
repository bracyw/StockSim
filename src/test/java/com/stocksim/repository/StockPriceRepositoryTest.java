package com.stocksim.repository;

import com.stocksim.entity.StockPrice;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class StockPriceRepositoryTest {

    @Autowired
    private StockPriceRepository repo;

    @BeforeEach
    void setUp() {
        repo.deleteAll();
        repo.save(new StockPrice("AAPL", LocalDate.of(2024, 1, 15),
                bd("180"), bd("182"), bd("179"), bd("181"), 1000000L));
        repo.save(new StockPrice("AAPL", LocalDate.of(2024, 1, 16),
                bd("181"), bd("185"), bd("180"), bd("184"), 1100000L));
        repo.save(new StockPrice("AAPL", LocalDate.of(2024, 1, 17),
                bd("184"), bd("186"), bd("183"), bd("185.50"), 1200000L));
        repo.save(new StockPrice("MSFT", LocalDate.of(2024, 1, 15),
                bd("400"), bd("405"), bd("399"), bd("402"), 500000L));
    }

    @Test
    void findByTickerAndDate() {
        Optional<StockPrice> result = repo.findByTickerAndDate("AAPL",
                LocalDate.of(2024, 1, 15));
        assertTrue(result.isPresent());
        assertEquals(bd("181"), result.get().getClose());
    }

    @Test
    void findMostRecentByTicker() {
        Optional<StockPrice> result = repo.findMostRecentByTicker("AAPL");
        assertTrue(result.isPresent());
        assertEquals(LocalDate.of(2024, 1, 17), result.get().getDate());
    }

    @Test
    void findEarliestByTicker() {
        Optional<StockPrice> result = repo.findEarliestByTicker("AAPL");
        assertTrue(result.isPresent());
        assertEquals(LocalDate.of(2024, 1, 15), result.get().getDate());
    }

    @Test
    void findClosestPriceOnOrBefore() {
        // Ask for Jan 18 (no data), should return Jan 17
        Optional<StockPrice> result = repo.findClosestPriceOnOrBefore("AAPL",
                LocalDate.of(2024, 1, 18));
        assertTrue(result.isPresent());
        assertEquals(LocalDate.of(2024, 1, 17), result.get().getDate());
    }

    @Test
    void findByTickerAndDateBetween() {
        List<StockPrice> results = repo.findByTickerAndDateBetweenOrderByDateDesc("AAPL",
                LocalDate.of(2024, 1, 15), LocalDate.of(2024, 1, 16));
        assertEquals(2, results.size());
        // Should be ordered by date DESC
        assertEquals(LocalDate.of(2024, 1, 16), results.get(0).getDate());
    }

    @Test
    void existsByTicker() {
        assertTrue(repo.existsByTicker("AAPL"));
        assertTrue(repo.existsByTicker("MSFT"));
    }

    private static BigDecimal bd(String val) {
        return new BigDecimal(val);
    }
}
