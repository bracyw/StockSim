package model.portfolio;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import model.OODStocks;
import model.stock.SimpleStock;
import model.stock.Stock;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@code SimplePortfolio} class.
 */
public class SimplePortfolioTest {
  Stock mSFTStock;
  Stock vStock;
  Stock aAPLStock;
  Stock gOOGStock;
  LocalDate mockCurrentDate;
  Map<String, Stock> stocks;
  List<Stock> stockList;
  Portfolio port1;
  Portfolio port2;
  Portfolio port3;

  @Before
  public void setup() {
    stocks = OODStocks.getStocks();
    stockList = new ArrayList<>(stocks.values());
    for (Stock stock : stockList) {
      OODStocks.removeStock(stock.getTicker());
    }
    mockCurrentDate = LocalDate.of(2024, 6, 3);
    // the below dates will be assigned individually in each test based on the specific stock
    mSFTStock = new SimpleStock("MSFT");
    vStock = new SimpleStock("V");
    aAPLStock = new SimpleStock("AAPL");
    gOOGStock = new SimpleStock("GOOG");
    stocks = OODStocks.getStocks();
    stockList = new ArrayList<>(stocks.values());
    port1 = new SimplePortfolio();
    port2 = new SimplePortfolio();
    port2.buy("MSFT", 15);
    port3 = new SimplePortfolio();
    port3.buy("AAPL", 10);
    port3.buy("GOOG", 1000);
  }

  @After
  public void teardown() throws IOException {
    for (Stock stock : stockList) {
      OODStocks.removeStock(stock.getTicker());
    }
  }

  @Test
  public void testTotalValue() {
    assertEquals(0, port1.totalValue(mockCurrentDate), .001);
    assertEquals(6202.799999999999, port2.totalValue(mockCurrentDate), .001);
  }

  @Test
  public void testGetValue() {
    assertEquals(6202.799999999999, port2.getValue("MSFT", mockCurrentDate),
            .001);
    assertEquals(6360.15, port2.buy("MSFT", 15), .001);
    assertEquals(12405.599999999999, port2.getValue("MSFT", mockCurrentDate),
            .001);
    assertEquals(4055.7, port2.buy("V", 15), .001);
    assertEquals(17250.0, port2.buy("NVDA", 15), .001);

    assertEquals(4055.7, port2.getValue("V", mockCurrentDate), .001);
    assertEquals(17250.0, port2.getValue("NVDA", mockCurrentDate), .001);
  }

  @Test
  public void testBuy() {
    assertEquals(6202.799999999999, port2.getValue("MSFT", mockCurrentDate),
            .001);
    port2.buy("MSFT", 3);
    assertEquals(7443.36, port2.getValue("MSFT", mockCurrentDate), .001);
    port1.buy("AAPL", 3000);
    assertEquals(582090.0, port1.getValue("AAPL", mockCurrentDate), .001);
  }


  @Test
  public void testSell() {
    port2.buy("GOOG", 3000);
    assertEquals(523260, port2.sell("GOOG", 3000), .001);
    port2.buy("GOOG", 3000);
    assertEquals(261630, port2.sell("GOOG", 1500), .001);
    assertEquals(261630, port2.sell("GOOG", 1500), .001);
  }

  @Test
  public void testToString() {
    assertEquals("Portfolio is empty", port1.toString());
    assertEquals("Portfolio is currently holding:\n" +
            "Ticker   Shares  Dollar Value\n" +
            "MSFT    15.0000     6360.1500", port2.toString());
    assertEquals("Portfolio is currently holding:\n" +
            "Ticker     Shares  Dollar Value\n" +
            "AAPL      10.0000     2142.4000\n" +
            "GOOG    1000.0000   174420.0000", port3.toString());
  }
}