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
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepo;

    @Mock
    private TransactionRepository transactionRepo;

    @Mock
    private StockService stockService;

    @InjectMocks
    private PortfolioService portfolioService;

    private Portfolio testPortfolio;
    private static final LocalDate DATE = LocalDate.of(2024, 1, 15);
    private static final BigDecimal PRICE = new BigDecimal("150.00");

    @BeforeEach
    void setUp() {
        testPortfolio = new Portfolio("TestPortfolio");
        // Simulate having an ID set
        try {
            var idField = Portfolio.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(testPortfolio, 1L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createPortfolio_success() {
        when(portfolioRepo.existsByName("NewPort")).thenReturn(false);
        when(portfolioRepo.save(any(Portfolio.class))).thenReturn(testPortfolio);

        PortfolioResponse response = portfolioService.createPortfolio("NewPort");
        assertNotNull(response);
        assertEquals("TestPortfolio", response.name());
    }

    @Test
    void createPortfolio_duplicateName_throws() {
        when(portfolioRepo.existsByName("Existing")).thenReturn(true);
        assertThrows(IllegalArgumentException.class,
                () -> portfolioService.createPortfolio("Existing"));
    }

    @Test
    void getPortfolio_notFound_throws() {
        when(portfolioRepo.findById(999L)).thenReturn(Optional.empty());
        assertThrows(PortfolioNotFoundException.class,
                () -> portfolioService.getPortfolio(999L));
    }

    @Test
    void buy_createsTransaction() {
        when(portfolioRepo.findById(1L)).thenReturn(Optional.of(testPortfolio));
        when(stockService.getClosestPrice("AAPL", DATE)).thenReturn(PRICE);
        when(transactionRepo.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        TradeResponse response = portfolioService.buy(1L, "AAPL", 10, DATE);

        assertEquals("AAPL", response.ticker());
        assertEquals("BUY", response.type());
        assertEquals(10, response.shares());
        assertEquals(PRICE, response.pricePerShare());
        assertEquals(0, new BigDecimal("1500.00").compareTo(response.totalCost()));
    }

    @Test
    void buy_negativeShares_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> portfolioService.buy(1L, "AAPL", -5, DATE));
    }

    @Test
    void sell_withSufficientShares_succeeds() {
        when(portfolioRepo.findById(1L)).thenReturn(Optional.of(testPortfolio));
        when(stockService.getClosestPrice("AAPL", DATE)).thenReturn(PRICE);
        when(transactionRepo.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        // Set up existing buy transaction
        Transaction buyTx = new Transaction("AAPL", TransactionType.BUY, 20,
                PRICE, DATE.minusDays(5));
        when(transactionRepo.findByPortfolioIdAndDateLessThanEqualOrderByDateAscIdAsc(1L, DATE))
                .thenReturn(List.of(buyTx));

        TradeResponse response = portfolioService.sell(1L, "AAPL", 10, DATE);
        assertEquals("SELL", response.type());
        assertEquals(10, response.shares());
    }

    @Test
    void sell_insufficientShares_throws() {
        when(portfolioRepo.findById(1L)).thenReturn(Optional.of(testPortfolio));

        Transaction buyTx = new Transaction("AAPL", TransactionType.BUY, 5,
                PRICE, DATE.minusDays(5));
        when(transactionRepo.findByPortfolioIdAndDateLessThanEqualOrderByDateAscIdAsc(1L, DATE))
                .thenReturn(List.of(buyTx));

        assertThrows(InsufficientSharesException.class,
                () -> portfolioService.sell(1L, "AAPL", 10, DATE));
    }

    @Test
    void getComposition_replaysTransactionsCorrectly() {
        when(portfolioRepo.findById(1L)).thenReturn(Optional.of(testPortfolio));

        Transaction buy1 = new Transaction("AAPL", TransactionType.BUY, 10, PRICE, DATE);
        Transaction buy2 = new Transaction("MSFT", TransactionType.BUY, 5,
                new BigDecimal("400.00"), DATE);
        Transaction sell1 = new Transaction("AAPL", TransactionType.SELL, 3, PRICE, DATE);
        when(transactionRepo.findByPortfolioIdAndDateLessThanEqualOrderByDateAscIdAsc(1L, DATE))
                .thenReturn(List.of(buy1, buy2, sell1));

        List<CompositionEntry> composition = portfolioService.getComposition(1L, DATE);

        assertEquals(2, composition.size());
        CompositionEntry aaplEntry = composition.stream()
                .filter(e -> e.ticker().equals("AAPL")).findFirst().orElseThrow();
        assertEquals(7, aaplEntry.shares(), 0.001);
        CompositionEntry msftEntry = composition.stream()
                .filter(e -> e.ticker().equals("MSFT")).findFirst().orElseThrow();
        assertEquals(5, msftEntry.shares(), 0.001);
    }

    @Test
    void getTotalValue_computesCorrectly() {
        when(portfolioRepo.findById(1L)).thenReturn(Optional.of(testPortfolio));

        Transaction buyAapl = new Transaction("AAPL", TransactionType.BUY, 10, PRICE, DATE);
        Transaction buyMsft = new Transaction("MSFT", TransactionType.BUY, 5,
                new BigDecimal("400.00"), DATE);
        when(transactionRepo.findByPortfolioIdAndDateLessThanEqualOrderByDateAscIdAsc(1L, DATE))
                .thenReturn(List.of(buyAapl, buyMsft));
        when(stockService.getClosestPrice("AAPL", DATE)).thenReturn(new BigDecimal("155.00"));
        when(stockService.getClosestPrice("MSFT", DATE)).thenReturn(new BigDecimal("420.00"));

        BigDecimal value = portfolioService.getTotalValue(1L, DATE);
        // 10 * 155 + 5 * 420 = 1550 + 2100 = 3650
        assertEquals(0, new BigDecimal("3650.00").compareTo(value));
    }

    @Test
    void buyBalanced_weightsNotHundred_throws() {
        Map<String, Double> weights = Map.of("AAPL", 50.0, "MSFT", 30.0);
        assertThrows(IllegalArgumentException.class,
                () -> portfolioService.buyBalanced(1L, weights, 10000, DATE));
    }

    @Test
    void buyBalanced_validWeights_buysCorrectly() {
        when(portfolioRepo.findById(1L)).thenReturn(Optional.of(testPortfolio));
        when(stockService.getClosestPrice("AAPL", DATE)).thenReturn(new BigDecimal("150.00"));
        when(stockService.getClosestPrice("MSFT", DATE)).thenReturn(new BigDecimal("400.00"));
        when(transactionRepo.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        Map<String, Double> weights = Map.of("AAPL", 60.0, "MSFT", 40.0);
        Map<String, TradeResponse> results = portfolioService.buyBalanced(
                1L, weights, 10000, DATE);

        assertEquals(2, results.size());
        assertTrue(results.containsKey("AAPL"));
        assertTrue(results.containsKey("MSFT"));
        // AAPL: 10000 * 0.60 / 150 = 40 shares
        assertEquals(40.0, results.get("AAPL").shares(), 0.01);
        // MSFT: 10000 * 0.40 / 400 = 10 shares
        assertEquals(10.0, results.get("MSFT").shares(), 0.01);
    }

    @Test
    void listAll_returnsAllPortfolios() {
        Portfolio p2 = new Portfolio("SecondPort");
        when(portfolioRepo.findAll()).thenReturn(List.of(testPortfolio, p2));

        List<PortfolioResponse> all = portfolioService.listAll();
        assertEquals(2, all.size());
    }
}
