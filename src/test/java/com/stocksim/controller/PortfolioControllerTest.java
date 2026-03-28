package com.stocksim.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stocksim.dto.CompositionEntry;
import com.stocksim.dto.CreatePortfolioRequest;
import com.stocksim.dto.DistributionEntry;
import com.stocksim.dto.PortfolioResponse;
import com.stocksim.dto.PortfolioValueResponse;
import com.stocksim.dto.TradeRequest;
import com.stocksim.dto.TradeResponse;
import com.stocksim.service.PortfolioService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PortfolioController.class)
class PortfolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PortfolioService portfolioService;

    private static final LocalDate DATE = LocalDate.of(2024, 1, 15);

    @Test
    void createPortfolio_returns201() throws Exception {
        when(portfolioService.createPortfolio("MyPort"))
                .thenReturn(new PortfolioResponse(1L, "MyPort", LocalDateTime.now()));

        mockMvc.perform(post("/api/portfolios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreatePortfolioRequest("MyPort"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("MyPort"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createPortfolio_blankName_returns400() throws Exception {
        mockMvc.perform(post("/api/portfolios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listAll_returnsPortfolios() throws Exception {
        when(portfolioService.listAll()).thenReturn(List.of(
                new PortfolioResponse(1L, "Port1", LocalDateTime.now()),
                new PortfolioResponse(2L, "Port2", LocalDateTime.now())));

        mockMvc.perform(get("/api/portfolios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void buy_returnsTradeResponse() throws Exception {
        TradeResponse trade = new TradeResponse("AAPL", "BUY", 10,
                new BigDecimal("150.00"), new BigDecimal("1500.00"), DATE);
        when(portfolioService.buy(eq(1L), eq("AAPL"), eq(10.0), eq(DATE)))
                .thenReturn(trade);

        mockMvc.perform(post("/api/portfolios/1/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new TradeRequest("AAPL", 10, DATE))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticker").value("AAPL"))
                .andExpect(jsonPath("$.shares").value(10))
                .andExpect(jsonPath("$.totalCost").value(1500.00));
    }

    @Test
    void getComposition_returnsEntries() throws Exception {
        when(portfolioService.getComposition(1L, DATE))
                .thenReturn(List.of(
                        new CompositionEntry("AAPL", 10),
                        new CompositionEntry("MSFT", 5)));

        mockMvc.perform(get("/api/portfolios/1/composition")
                        .param("date", "2024-01-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].ticker").value("AAPL"));
    }

    @Test
    void getDistribution_returnsEntries() throws Exception {
        when(portfolioService.getDistribution(1L, DATE))
                .thenReturn(List.of(
                        new DistributionEntry("AAPL", 10, new BigDecimal("1500.00"))));

        mockMvc.perform(get("/api/portfolios/1/distribution")
                        .param("date", "2024-01-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].value").value(1500.00));
    }

    @Test
    void getTotalValue_returnsValue() throws Exception {
        when(portfolioService.getTotalValue(1L, DATE))
                .thenReturn(new BigDecimal("5000.00"));

        mockMvc.perform(get("/api/portfolios/1/value")
                        .param("date", "2024-01-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalValue").value(5000.00));
    }
}
