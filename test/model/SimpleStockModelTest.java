package model;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * Test the {@code SimpleStockModel} class.
 */
public class SimpleStockModelTest extends AbstractedModelTests {
  @Override
  protected StockModel instantiateModel() {
    return new SimpleStockModel();
  }

  @Test
  public void totalValue() {
    assertThrows(NullPointerException.class, () -> {
      forTests.totalValue(LocalDate.parse("2024-03-16"));
    });
    forTests.createPortfolio("f");
    assertThrows(IllegalArgumentException.class, () -> {
      forTests.totalValue(LocalDate.parse("2024-06-17"));
    });


    forTests.buy("AAPL", 50);
    assertEquals(forTests.totalValue(LocalDate.parse("2023-03-17")), 7750.0, .01);
    assertEquals(forTests.totalValue(LocalDate.parse("2021-03-17")), 6238.0, .01);
    assertEquals(forTests.totalValue(LocalDate.parse("2002-12-27")), 703.0, .01);
    assertEquals(forTests.totalValue(LocalDate.parse("2024-03-07")), 8450.0, .01);
    assertEquals(forTests.totalValue(LocalDate.parse("2000-03-17")), 6250.0, .01);
    assertEquals(forTests.totalValue(LocalDate.parse("2024-03-17")), 8631.0, .01);
    assertEquals(forTests.totalValue(LocalDate.parse("1998-03-17")), 0, .01);
  }

  @Test
  public void buy() {
    assertThrows(IllegalArgumentException.class, () -> {
      forTests.buy("AAPL", 50);
    });
    forTests.createPortfolio("f");

    assertThrows(IllegalArgumentException.class, () -> {
      forTests.buy("AAPLa", 50);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      forTests.buy("AAPL", -50);
    });
    forTests.buy("AAPL", 50);
    assertEquals(forTests.getPortfolio(), "Portfolio is currently holding:\n" +
            "Ticker   Shares  Dollar Value\n" +
            "AAPL    50.0000    " + formatTickerAmount("AAPL", 50));
    forTests.buy("AAPL", 50);
    forTests.buy("COST", 50);
  }

  @Test
  public void sell() {
    // Ensure that an exception is thrown for an invalid portfolio
    assertThrows(IllegalArgumentException.class, () -> {
      forTests.sell("AAPL", 50);
    });
    forTests.createPortfolio("f");

    // Ensure that an exception is thrown for invalid ticker or negative amount
    assertThrows(IllegalArgumentException.class, () -> {
      forTests.sell("AAPLa", 50);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      forTests.sell("AAPL", -50);
    });

    // Setup initial buys to have stocks to sell
    forTests.buy("AAPL", 50);
    forTests.buy("COST", 50);

    // Check portfolio composition after initial buys
    assertEquals("Portfolio is currently holding:\n" +
                    "Ticker   Shares  Dollar Value\n" +
                    "AAPL    50.0000    " + formatTickerAmount("AAPL", 50) + "\n" +
                    "COST    50.0000    " + formatTickerAmount("COST", 50),
            forTests.getPortfolio());

    // Sell part of AAPL stock
    forTests.sell("AAPL", 20);
    assertEquals("Portfolio is currently holding:\n" +
                    "Ticker   Shares  Dollar Value\n" +
                    "AAPL    30.0000     " + formatTickerAmount("AAPL", 30) + "\n" +
                    "COST    50.0000    " + formatTickerAmount("COST", 50),
            forTests.getPortfolio());

    // Sell all remaining AAPL stock
    forTests.sell("AAPL", 30);
    assertEquals("Portfolio is currently holding:\n" +
                    "Ticker   Shares  Dollar Value\n" +
                    "COST    50.0000    " + formatTickerAmount("COST", 50),
            forTests.getPortfolio());

    // Sell off all COST stock
    forTests.sell("COST", 50);
    assertEquals("Portfolio is empty",
            forTests.getPortfolio());
  }


}