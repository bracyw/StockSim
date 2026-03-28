package com.stocksim.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "stock_prices",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ticker", "date"}),
        indexes = @Index(columnList = "ticker, date"))
public class StockPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ticker;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "open_price", precision = 12, scale = 4)
    private BigDecimal open;

    @Column(name = "high_price", precision = 12, scale = 4)
    private BigDecimal high;

    @Column(name = "low_price", precision = 12, scale = 4)
    private BigDecimal low;

    @Column(name = "close_price", precision = 12, scale = 4, nullable = false)
    private BigDecimal close;

    private Long volume;

    public StockPrice() {
    }

    public StockPrice(String ticker, LocalDate date, BigDecimal open, BigDecimal high,
                      BigDecimal low, BigDecimal close, Long volume) {
        this.ticker = ticker;
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    public Long getId() {
        return id;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }
}
