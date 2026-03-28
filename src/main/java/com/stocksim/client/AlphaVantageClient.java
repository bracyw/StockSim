package com.stocksim.client;

import com.stocksim.entity.StockPrice;
import com.stocksim.exception.ApiRateLimitException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AlphaVantageClient {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String baseUrl;

    public AlphaVantageClient(
            @Value("${alphavantage.api-key}") String apiKey,
            @Value("${alphavantage.base-url}") String baseUrl) {
        this.restTemplate = new RestTemplate();
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    /**
     * Fetches daily stock price data from Alpha Vantage and parses the CSV response
     * into StockPrice entities.
     */
    public List<StockPrice> fetchDailyPrices(String ticker) {
        String url = String.format(
                "%s?function=TIME_SERIES_DAILY&symbol=%s&outputsize=full&apikey=%s&datatype=csv",
                baseUrl, ticker, apiKey);

        String csv = restTemplate.getForObject(url, String.class);
        if (csv == null || csv.isBlank()) {
            throw new RuntimeException("No data returned from Alpha Vantage for " + ticker);
        }
        if (csv.contains("rate limit")) {
            throw new ApiRateLimitException();
        }
        if (csv.contains("Invalid API")) {
            throw new RuntimeException("Invalid API key or stock ticker: " + ticker);
        }

        return parseCsv(ticker, csv);
    }

    private List<StockPrice> parseCsv(String ticker, String csv) {
        List<StockPrice> prices = new ArrayList<>();
        String[] lines = csv.split("\n");

        // Skip header line (timestamp,open,high,low,close,volume)
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split(",");
            if (parts.length < 6) {
                continue;
            }
            try {
                LocalDate date = LocalDate.parse(parts[0].trim());
                BigDecimal open = new BigDecimal(parts[1].trim());
                BigDecimal high = new BigDecimal(parts[2].trim());
                BigDecimal low = new BigDecimal(parts[3].trim());
                BigDecimal close = new BigDecimal(parts[4].trim());
                long volume = Long.parseLong(parts[5].trim());

                prices.add(new StockPrice(ticker, date, open, high, low, close, volume));
            } catch (Exception e) {
                // Skip malformed rows
            }
        }
        return prices;
    }
}
