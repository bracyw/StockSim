package com.stocksim.controller;

import com.stocksim.dto.StockPriceResponse;
import com.stocksim.service.StockService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StockController.class)
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockService stockService;

    @Test
    void downloadStock_returns200() throws Exception {
        when(stockService.downloadStock("AAPL"))
                .thenReturn("Downloaded 100 price records for AAPL");

        mockMvc.perform(post("/api/stocks/AAPL/download"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Downloaded 100 price records for AAPL"));
    }

    @Test
    void getPrice_withDate_returnsPrice() throws Exception {
        when(stockService.getPrice("AAPL", LocalDate.of(2024, 1, 15)))
                .thenReturn(new BigDecimal("185.50"));

        mockMvc.perform(get("/api/stocks/AAPL/price")
                        .param("date", "2024-01-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.close").value(185.50));
    }

    @Test
    void getPrice_withoutDate_returnsMostRecent() throws Exception {
        when(stockService.getMostRecentPrice("AAPL"))
                .thenReturn(new StockPriceResponse("AAPL", LocalDate.of(2024, 6, 3),
                        new BigDecimal("190.00"), new BigDecimal("192.00"),
                        new BigDecimal("189.00"), new BigDecimal("191.50"), 5000000L));

        mockMvc.perform(get("/api/stocks/AAPL/price"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticker").value("AAPL"))
                .andExpect(jsonPath("$.close").value(191.50));
    }

    @Test
    void getGainLoss_returnsResult() throws Exception {
        when(stockService.getGainLoss("AAPL",
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 6, 1)))
                .thenReturn(new BigDecimal("15.50"));

        mockMvc.perform(get("/api/stocks/AAPL/gain-loss")
                        .param("from", "2024-01-01")
                        .param("to", "2024-06-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gainLoss").value(15.50));
    }

    @Test
    void getMovingAverage_returnsResult() throws Exception {
        when(stockService.getMovingAverage("AAPL", LocalDate.of(2024, 6, 1), 30))
                .thenReturn(new BigDecimal("188.2500"));

        mockMvc.perform(get("/api/stocks/AAPL/moving-average")
                        .param("date", "2024-06-01")
                        .param("days", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movingAverage").value(188.25));
    }

    @Test
    void getCrossovers_returnsResult() throws Exception {
        when(stockService.getCrossovers("AAPL",
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 1), 30))
                .thenReturn(Set.of(LocalDate.of(2024, 2, 15)));

        mockMvc.perform(get("/api/stocks/AAPL/crossovers")
                        .param("from", "2024-01-01")
                        .param("to", "2024-03-01")
                        .param("days", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.crossoverDates").isArray());
    }
}
