package com.stocksim.repository;

import com.stocksim.entity.StockPrice;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {

    List<StockPrice> findByTickerOrderByDateDesc(String ticker);

    Optional<StockPrice> findByTickerAndDate(String ticker, LocalDate date);

    List<StockPrice> findByTickerAndDateBetweenOrderByDateDesc(String ticker,
                                                                LocalDate from, LocalDate to);

    boolean existsByTicker(String ticker);

    @Query("SELECT sp FROM StockPrice sp WHERE sp.ticker = :ticker ORDER BY sp.date DESC LIMIT 1")
    Optional<StockPrice> findMostRecentByTicker(String ticker);

    @Query("SELECT sp FROM StockPrice sp WHERE sp.ticker = :ticker ORDER BY sp.date ASC LIMIT 1")
    Optional<StockPrice> findEarliestByTicker(String ticker);

    @Query("SELECT sp FROM StockPrice sp WHERE sp.ticker = :ticker AND sp.date <= :date " +
            "ORDER BY sp.date DESC LIMIT 1")
    Optional<StockPrice> findClosestPriceOnOrBefore(String ticker, LocalDate date);

    @Query("SELECT sp FROM StockPrice sp WHERE sp.ticker = :ticker AND sp.date <= :date " +
            "ORDER BY sp.date DESC")
    List<StockPrice> findByTickerOnOrBeforeDate(String ticker, LocalDate date);

    void deleteByTicker(String ticker);
}
