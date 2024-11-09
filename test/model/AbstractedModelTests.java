package model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;

import model.portfolio.Portfolio;
import model.portfolio.SimplePortfolio;
import model.stock.Stock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

/**
 * Abstracted tests for both models.
 */
public abstract class AbstractedModelTests {
  StockModel forTests;
  Portfolio test1;
  Portfolio test2;

  @Before
  public void startup() {
    forTests = this.instantiateModel();
    test1 = new SimplePortfolio();
    test2 = new SimplePortfolio();
    test2.buy("MFST", 15);
  }

  @After // added tear down so abstraction works
  public void teardown() throws IOException {
    for (Stock entry : OODStocks.getStocks().values()) {
      OODStocks.removeStock(entry.getTicker());
    }
  }

  // formats the current stock price for the ticker
  protected String formatTickerAmount(String ticker, double amount) {
    return String.format("%.4f", OODStocks.getStock(ticker).getPrice() * amount);
  }

  protected String formatTickerAmount(String ticker, double amount, LocalDate date) {
    return String.format("%.4f", OODStocks.getStock(ticker).getPrice(date) * amount);
  }

  protected abstract StockModel instantiateModel();

  @Test
  public void createStock() {
    forTests.createStock("NVDA");
    assertEquals(OODStocks.getStock("NVDA").getTicker(), "NVDA");
    assertThrows(IllegalArgumentException.class, () -> {
      forTests.createStock("NVDA");
    });
    assertThrows(IllegalArgumentException.class, () -> {
      forTests.createStock("PG123");
    });
    forTests.createStock("TSLA");
    assertEquals(OODStocks.getStock("TSLA").getTicker(), "TSLA");
    forTests.createStock("WFC");
    assertEquals(OODStocks.getStock("WFC").getTicker(), "WFC");
  }

  @Test
  public void getXDaysAverage() {
    assertEquals(forTests.getXDaysAverage("AAPL", LocalDate.parse("2024-03-17"),
            15), 174.828, .01);

    assertEquals(forTests.getXDaysAverage("COST", LocalDate.parse("2024-04-17"),
            50), 730.3094, .01);


    assertEquals(forTests.getXDaysAverage("COST", LocalDate.parse("2001-12-17"),
            300), 38.5372, .01);
    assertThrows(IllegalArgumentException.class, () -> {
      forTests.getXDaysAverage("NVDA", LocalDate.parse("2024-03-17"), 150000);
    });

    assertThrows(IllegalArgumentException.class, () -> {
      forTests.getXDaysAverage("COST", LocalDate.parse("2025-03-17"), 15);
    });

    assertThrows(IllegalArgumentException.class, () -> {
      forTests.getXDaysAverage("COST", LocalDate.parse("2000-03-17"), 1500);
    });


  }

  @Test
  public void getDefDayCrossover() {
    HashSet<LocalDate> dates = new HashSet<>();
    dates.add(LocalDate.parse("2024-04-29"));
    dates.add(LocalDate.parse("2024-05-16"));
    dates.add(LocalDate.parse("2024-05-15"));
    dates.add(LocalDate.parse("2024-04-15"));
    dates.add(LocalDate.parse("2024-05-14"));
    dates.add(LocalDate.parse("2024-05-13"));
    dates.add(LocalDate.parse("2024-04-12"));
    dates.add(LocalDate.parse("2024-04-11"));
    dates.add(LocalDate.parse("2024-05-10"));
    dates.add(LocalDate.parse("2024-05-09"));
    dates.add(LocalDate.parse("2024-05-08"));
    dates.add(LocalDate.parse("2024-05-07"));
    dates.add(LocalDate.parse("2024-05-06"));
    dates.add(LocalDate.parse("2024-05-03"));
    dates.add(LocalDate.parse("2024-05-02"));
    HashSet<LocalDate> dates2 = new HashSet<>();


    assertThrows(IllegalArgumentException.class, () -> {
      forTests.getDefDayCrossover("AAPL", LocalDate.parse("2024-03-17"),
              LocalDate.parse("2024-02-16"));
    });
    assertThrows(IllegalArgumentException.class, () -> {
      forTests.getDefDayCrossover("AAPL", LocalDate.parse("2022-03-17"),
              LocalDate.parse("2025-02-16"));
    });
    assertThrows(IllegalArgumentException.class, () -> {
      forTests.getDefDayCrossover("AAPL", LocalDate.parse("1998-03-17"),
              LocalDate.parse("2024-02-16"));
    });
    assertThrows(IllegalArgumentException.class, () -> {
      forTests.getDefDayCrossover("APPL", LocalDate.parse("1998-03-17"),
              LocalDate.parse("2024-02-16"));
    });
    assertEquals(forTests.getDefDayCrossover("AAPL", LocalDate.parse("2024-03-17"),
            LocalDate.parse("2024-05-16")), dates);


    assertEquals(forTests.getDefDayCrossover("AAPL", LocalDate.parse("2022-03-17"),
            LocalDate.parse("2022-03-18")), dates2);

  }

  @Test
  public void getXDayCrossover() {
    HashSet<LocalDate> dates = new HashSet<>();
    dates.add(LocalDate.parse("2024-04-29"));
    dates.add(LocalDate.parse("2024-05-16"));
    dates.add(LocalDate.parse("2024-05-15"));
    dates.add(LocalDate.parse("2024-04-15"));
    dates.add(LocalDate.parse("2024-05-14"));
    dates.add(LocalDate.parse("2024-05-13"));
    dates.add(LocalDate.parse("2024-04-12"));
    dates.add(LocalDate.parse("2024-04-11"));
    dates.add(LocalDate.parse("2024-05-10"));
    dates.add(LocalDate.parse("2024-05-09"));
    dates.add(LocalDate.parse("2024-05-08"));
    dates.add(LocalDate.parse("2024-05-07"));
    dates.add(LocalDate.parse("2024-05-06"));
    dates.add(LocalDate.parse("2024-05-03"));
    dates.add(LocalDate.parse("2024-05-02"));
    HashSet<LocalDate> dates2 = new HashSet<>();


    assertThrows(IllegalArgumentException.class, () -> {
      forTests.getXDayCrossover("AAPL", LocalDate.parse("2024-03-17"),
              LocalDate.parse("2024-02-16"), 41);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      forTests.getXDayCrossover("AAPL", LocalDate.parse("2022-03-17"),
              LocalDate.parse("2025-02-16"), 45);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      forTests.getXDayCrossover("AAPL", LocalDate.parse("1998-03-17"),
              LocalDate.parse("2024-02-16"), 20);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      forTests.getXDayCrossover("APPL", LocalDate.parse("1998-03-17"),
              LocalDate.parse("2024-02-16"), 20);
    });
    assertEquals(forTests.getXDayCrossover("AAPL", LocalDate.parse("2024-03-17"),
            LocalDate.parse("2024-05-16"), 30), dates);


    assertEquals(forTests.getXDayCrossover("AAPL", LocalDate.parse("2022-03-17"),
            LocalDate.parse("2022-03-18"), 30), dates2);

  }


  @Test
  public void gainLoss() {
    assertThrows(IllegalArgumentException.class, () -> {
      forTests.gainLoss("AAPL", LocalDate.parse("2024-03-17"),
              LocalDate.parse("2024-02-16"));
    });
    assertThrows(IllegalArgumentException.class, () -> {
      forTests.gainLoss("AAPL", LocalDate.parse("2022-03-17"),
              LocalDate.parse("2025-02-16"));
    });
    assertThrows(IllegalArgumentException.class, () -> {
      forTests.gainLoss("AAPL", LocalDate.parse("1998-03-17"),
              LocalDate.parse("2024-02-16"));
    });
    assertThrows(IllegalArgumentException.class, () -> {
      forTests.gainLoss("APPL", LocalDate.parse("1998-03-17"),
              LocalDate.parse("2024-02-16"));
    });

    assertEquals(forTests.gainLoss("AAPL", LocalDate.parse("2005-03-17"),
            LocalDate.parse("2022-07-26")), 109.35, .01);

    assertEquals(forTests.gainLoss("AAPL", LocalDate.parse("2004-03-17"),
            LocalDate.parse("2020-06-06")), 305.31, .01);

    assertEquals(forTests.gainLoss("AAPL", LocalDate.parse("2023-03-17"),
            LocalDate.parse("2024-05-16")), 34.84, .01);

    assertEquals(forTests.gainLoss("COST", LocalDate.parse("2024-03-17"),
            LocalDate.parse("2024-06-02")), 84.26, .01);
  }

  @Test
  public void getPrice() {
    assertThrows(IllegalArgumentException.class, () -> {
      forTests.getPrice("AAPL", LocalDate.parse("2024-03-17"));
    });
    assertThrows(IllegalArgumentException.class, () -> {
      forTests.getPrice("AAPL", LocalDate.parse("2666-03-17"));
    });
    assertThrows(IllegalArgumentException.class, () -> {
      forTests.getPrice("AAPL", LocalDate.parse("1998-03-17"));
    });
    assertThrows(IllegalArgumentException.class, () -> {
      forTests.getPrice("APPL", LocalDate.parse("1998-03-17"));
    });

    assertEquals(forTests.getPrice("AAPL", LocalDate.parse("2005-03-17")),
            42.25, .01);

    assertEquals(forTests.getPrice("AAPL", LocalDate.parse("2004-03-17")),
            26.19, .01);

    assertEquals(forTests.getPrice("AAPL", LocalDate.parse("2023-03-17")),
            155.0, .01);

    assertEquals(forTests.getPrice("COST", LocalDate.parse("2024-03-19")),
            732.17, .01);


  }

  @Test
  public void setPortfolio() {
    assertNull(forTests.getPortfolio());
    forTests.createPortfolio("f");
    assertEquals("Portfolio is empty", forTests.getPortfolio());

    forTests.logout();
    assertNull(forTests.getPortfolio());

    forTests.setPortfolio("f");
    assertEquals(forTests.getPortfolio(), "Portfolio is empty");
    assertThrows(IllegalArgumentException.class, () -> {
      forTests.setPortfolio("g");
    });
  }

  @Test
  public void getPortfolio() {
    assertNull(forTests.getPortfolio());
    forTests.createPortfolio("f");
    assertEquals(forTests.getPortfolio(), "Portfolio is empty");

    forTests.logout();
    assertNull(forTests.getPortfolio());

    forTests.setPortfolio("f");
    assertEquals(forTests.getPortfolio(), "Portfolio is empty");
    assertThrows(IllegalArgumentException.class, () -> {
      forTests.setPortfolio("g");
    });
  }

  @Test
  public void createPortfolio() {
    assertNull(forTests.getPortfolio());
    forTests.createPortfolio("f");
    assertEquals(forTests.getPortfolio(), "Portfolio is empty");
    forTests.logout();
    assertThrows(IllegalArgumentException.class, () -> {
      forTests.createPortfolio("f");
    });
  }

  @Test
  public void logout() {
    forTests.logout();
    assertNull(forTests.getPortfolio());

    forTests.createPortfolio("f");
    assertEquals(forTests.getPortfolio(), "Portfolio is empty");
    forTests.logout();
    assertNull(forTests.getPortfolio());
  }
}
