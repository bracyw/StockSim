package model;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import model.stock.Stock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * Test class for betterStockModel.
 */
public class BetterStockModelImplTest extends AbstractedModelTests {
  private final String PORTFOLIOS_STORAGE_PATH =
          System.getProperty("user.dir") + "/PortfolioInfo/";
  BetterStockModelImpl betterStockModel;

  @Before
  public void setUp() throws Exception {
    betterStockModel = new BetterStockModelImpl();
  }

  @Test
  public void setCurrentActValidViewed() {
    // check that the current account is what we said it is
    betterStockModel.createPortfolio("ABC");
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers  Shares", betterStockModel.composition(LocalDate.now()));
    assertEquals("ABC", betterStockModel.getCurrentPortfolioName());
    betterStockModel.logout();
    assertNull(betterStockModel.getPortfolio());
    assertThrows(NullPointerException.class, () -> betterStockModel.getCurrentPortfolioName());
    betterStockModel.setPortfolio("ABC");
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers  Shares", betterStockModel.composition(LocalDate.now()));
  }

  @Test
  public void setCurrentActValidLoaded() {
    betterStockModel.setPortfolio("spiderPig");
    assertEquals("spiderPig", betterStockModel.getCurrentPortfolioName());
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers    Shares\n" +
            "AAPL     106.2298\n" +
            "MSFT      26.1480\n" +
            "COST      15.0000", betterStockModel.composition(LocalDate.now()));
    betterStockModel.logout();
    assertNull(betterStockModel.getPortfolio());
    // ensure we can still set the portfolio again.
    betterStockModel.setPortfolio("spiderPig");
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers    Shares\n" +
            "AAPL     106.2298\n" +
            "MSFT      26.1480\n" +
            "COST      15.0000", betterStockModel.composition(LocalDate.now()));
  }

  @Test(expected = IllegalArgumentException.class)
  public void setCurrentActBadNameFails() {
    betterStockModel.setPortfolio("SDFLKJ");
  }


  @Test
  public void addPortfolio() {
    // this adds portfolio to viewed portfolios
    assertThrows(IllegalArgumentException.class, () -> betterStockModel.setPortfolio("ABC"));
    betterStockModel.addPortfolio("ABC");
    assertNull(betterStockModel.getPortfolio());
    betterStockModel.setPortfolio("ABC");
    assertEquals("Portfolio is empty", betterStockModel.getPortfolio());
  }

  @Test
  public void composition() {
    betterStockModel.createPortfolio("Wyatt");
    LocalDate inputDate = LocalDate.parse("2024-03-25");
    LocalDate randomDate = LocalDate.of(2021, 11, 2);
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers  Shares", betterStockModel.composition(inputDate));
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers  Shares", betterStockModel.composition(randomDate));
    betterStockModel.buyDate("MSFT", 10, LocalDate.parse("2024-03-25"));
    betterStockModel.buyDate("AAPL", 20, LocalDate.parse("2024-03-25"));
    betterStockModel.buyDate("V", 30, LocalDate.parse("2024-03-25"));
    // ensure there is something in the portfolio
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers   Shares\n" +
            "MSFT     10.0000\n" +
            "AAPL     20.0000\n" +
            "V        30.0000", betterStockModel.composition(LocalDate.parse("2024-03-25")));
    // check that before that date there is no composition
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers  Shares", betterStockModel.composition(LocalDate.parse("2024-03-24")));
    // check that selling the date results in the portfolio holding nothing.
    betterStockModel.sellDate("MSFT", 10, LocalDate.parse("2024-03-25"));
    betterStockModel.sellDate("AAPL", 20, LocalDate.parse("2024-03-25"));
    betterStockModel.sellDate("V", 30, LocalDate.parse("2024-03-25"));
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers  Shares", betterStockModel.composition(inputDate));
  }

  @Test
  public void distribution() {
    betterStockModel.createPortfolio("Wyatt"); // Create a new portfolio named "Wyatt"
    LocalDate inputDate = LocalDate.parse("2024-03-25");
    LocalDate randomDate = LocalDate.of(2021, 11, 2);

    // Check initial state: portfolio is empty
    assertEquals("Portfolio is currently holding:\n" +
                    "Tickers  Values",
            betterStockModel.distribution(inputDate));
    assertEquals("Portfolio is currently holding:\n" +
                    "Tickers  Values",
            betterStockModel.distribution(randomDate));

    // Add stocks to the portfolio on the specified date
    betterStockModel.buyDate("GOOG", 10, inputDate);
    betterStockModel.buyDate("COST", 10, inputDate);
    betterStockModel.buyDate("AAPL", 10, inputDate);
    betterStockModel.buyDate("V", 10, inputDate);

    // Calculate the formatted values for each stock
    String googValue = formatTickerAmount("GOOG", 10, inputDate);
    String costValue = formatTickerAmount("COST", 10, inputDate);
    String aaplValue = formatTickerAmount("AAPL", 10, inputDate);
    String vValue = formatTickerAmount("V", 10, inputDate);

    // Expected distribution after buying stocks
    String expectedDistribution = String.format("Portfolio is currently holding:\n" +
            "Tickers     Values\n" +
            "GOOG     %s\n" +
            "COST     %s\n" +
            "AAPL     %s\n" +
            "V        %s", googValue, costValue, aaplValue, vValue);

    // Verify the distribution after adding stocks
    assertEquals(expectedDistribution, betterStockModel.distribution(inputDate));

    // Sell some of the stocks and verify the distribution
    betterStockModel.sellDate("GOOG", 5, inputDate); // Sell half of GOOG shares

    googValue = formatTickerAmount("GOOG", 5, inputDate); // Updated value for GOOG

    // exp distribution after selling some stocks
    String updatedDistribution = String.format("Portfolio is currently holding:\n" +
            "Tickers     Values\n" +
            "GOOG      %s\n" +
            "COST     %s\n" +
            "AAPL     %s\n" +
            "V        %s", googValue, costValue, aaplValue, vValue);

    assertEquals(updatedDistribution, betterStockModel.distribution(inputDate));

    // Sell off all stocks to empty the portfolio
    betterStockModel.sellDate("GOOG", 5, inputDate); // Sell remaining GOOG shares
    betterStockModel.sellDate("COST", 10, inputDate); // Sell all COST shares
    betterStockModel.sellDate("AAPL", 10, inputDate); // Sell all AAPL shares
    betterStockModel.sellDate("V", 10, inputDate); // Sell all V shares

    // Verify the portfolio is empty after selling all stocks
    assertEquals("Portfolio is currently holding:\n" +
                    "Tickers  Values",
            betterStockModel.distribution(inputDate));
  }

  @Test
  public void saveCurrentPortfolio() {
    // ensure this writes to the correct file
    betterStockModel.createPortfolio("Wyatt");
    betterStockModel.buyDate("GOOG", 10, LocalDate.parse("2024-03-25"));
    betterStockModel.saveCurrentPortfolio();
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers   Shares\n" +
            "GOOG     10.0000", betterStockModel.composition(LocalDate.parse("2024-03-25")));
    File stockFile = new File(PORTFOLIOS_STORAGE_PATH + "Wyatt.txt");
    assertTrue(stockFile.exists());
    betterStockModel.logout();
    betterStockModel.setPortfolio("Wyatt");
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers   Shares\n" +
            "GOOG     10.0000", betterStockModel.composition(LocalDate.parse("2024-03-25")));
    BetterStockModel newStockModel = new BetterStockModelImpl();
    newStockModel.setPortfolio("Wyatt");
    betterStockModel.buyDate("AAPL", 20, LocalDate.parse("2024-04-25"));
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers   Shares\n" +
            "GOOG     10.0000\n" +
            "AAPL     20.0000", betterStockModel.composition(LocalDate.parse("2024-04-25")));
    betterStockModel.saveCurrentPortfolio();
    try {
      Scanner read = new Scanner(stockFile);
      assertEquals(read.nextLine(), "Wyatt");
      assertEquals(read.nextLine(), "2024-03-25");
      assertEquals(read.nextLine(), "buy GOOG 10.0");
      assertEquals(read.nextLine(), "2024-04-25");
      assertEquals(read.nextLine(), "buy AAPL 20.0");
    } catch (Exception e) {
      throw new IllegalArgumentException("Save formating is not as expected");
    }
    // delete the file used for testing.
    stockFile.delete();
  }

  @Test
  public void balanceAllEven() {
    betterStockModel.createPortfolio("Portfolio1");
    LocalDate inputDate = LocalDate.parse("2024-03-25");
    LocalDate mockCurrentDate = LocalDate.of(2024, 6, 1);

    // Add initial stock to the portfolio
    betterStockModel.buyDate("MSFT", 10, inputDate);

    // Check the total value on different dates
    assertEquals(0, betterStockModel.totalValue(LocalDate.parse("2023-03-13")), 0.001);
    assertEquals(4216.5, betterStockModel.totalValue(LocalDate.parse("2024-03-26")), 0.001);
    assertEquals(4228.6, betterStockModel.totalValue(inputDate), 0.001);

    // Set up the desired balance
    Map<String, Double> balance1 = new LinkedHashMap<>();
    balance1.put("COST", 25.0);
    balance1.put("V", 25.0);
    balance1.put("AAPL", 25.0);
    balance1.put("GOOG", 25.0);

    // Balance the portfolio
    betterStockModel.buyBalanced(balance1, mockCurrentDate, 15000);

    // Check that the shares look reasonable and all stocks are present
    assertEquals("Portfolio is currently holding:\n" +
                    "Tickers   Shares\n" +
                    "MSFT     10.0000\n" +
                    "COST      4.6303\n" +
                    "V        13.7635\n" +
                    "AAPL     19.5059\n" +
                    "GOOG     21.5567",
            betterStockModel.composition(mockCurrentDate));

    // Check that the values of stocks are even
    assertEquals("Portfolio is currently holding:\n" +
                    "Tickers     Values\n" +
                    "MSFT     4151.3000\n" +
                    "COST     3750.0000\n" +
                    "V        3750.0000\n" +
                    "AAPL     3750.0000\n" +
                    "GOOG     3750.0000",
            betterStockModel.distribution(mockCurrentDate));
  }

  @Test
  public void testBalanceRandom() {
    betterStockModel.createPortfolio("Steve2");
    LocalDate mockCurrentDate = LocalDate.of(2024, 6, 1);

    // Create a balance map with one stock having invalid percentage
    Map<String, Double> balance1 = new LinkedHashMap<>();
    balance1.put("GOOG", 26.0);
    assertThrows(IllegalArgumentException.class, () ->
            betterStockModel.buyBalanced(balance1, mockCurrentDate, 15000));

    balance1.put("GOOG", -1.0); // Invalid negative percentage
    balance1.put("AAPL", 51.0);
    assertThrows(IllegalArgumentException.class, () ->
            betterStockModel.buyBalanced(balance1, mockCurrentDate, 15000));

    // Create a valid balance map but with incorrect sum
    balance1.put("V", 25.0);
    balance1.put("COST", 25.0);
    balance1.put("AAPL", 25.0);
    balance1.put("GOOG", 24.0);
    assertThrows(IllegalArgumentException.class, () ->
            betterStockModel.buyBalanced(balance1, mockCurrentDate, 15000));

    // Create a correct balance map
    Map<String, Double> balance2 = new LinkedHashMap<>();
    balance2.put("V", 55.0);
    balance2.put("COST", 45.0);

    // Balance the portfolio
    betterStockModel.buyBalanced(balance2, mockCurrentDate, 15000);

    // Check that the shares are correctly allocated
    assertEquals("Portfolio is currently holding:\n" +
                    "Tickers   Shares\n" +
                    "V        30.2797\n" +
                    "COST      8.3345",
            betterStockModel.composition(mockCurrentDate));

    // Check that the values reflect the intended distribution
    assertEquals("Portfolio is currently holding:\n" +
                    "Tickers     Values\n" +
                    "V        8250.0000\n" +
                    "COST     6750.0000",
            betterStockModel.distribution(mockCurrentDate));
  }


  @Test
  public void buyBalancedSmallAmount() {
    // ensure that this outputs the correct distribution
    betterStockModel.createPortfolio("Steve2");
    LocalDate mockCurrentDate = LocalDate.of(2024, 6, 1);
    Map<String, Double> balance1 = new LinkedHashMap<>();
    balance1.put("GOOG", 25.0);
    balance1.put("AAPL", 75.0);
    betterStockModel.buyBalanced(balance1, mockCurrentDate, 1);
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers  Shares\n" +
            "GOOG     0.0014\n" +
            "AAPL     0.0039", betterStockModel.composition(mockCurrentDate));
  }

  @Test(expected = IllegalArgumentException.class)
  public void buyBalancedInvalidUnder100() {
    betterStockModel.createPortfolio("Steve2");
    LocalDate mockCurrentDate = LocalDate.of(2024, 6, 1);
    Map<String, Double> balance1 = new LinkedHashMap<>();
    balance1.put("GOOG", 25.0);
    balance1.put("AAPL", 70.0);
    betterStockModel.buyBalanced(balance1, mockCurrentDate, 10000);
  }

  @Test(expected = IllegalArgumentException.class)
  public void buyBalancedInvalidOver100() {
    betterStockModel.createPortfolio("Steve2");
    LocalDate mockCurrentDate = LocalDate.of(2024, 6, 1);
    Map<String, Double> balance1 = new LinkedHashMap<>();
    balance1.put("GOOG", 25.0);
    balance1.put("AAPL", 76.0);
    betterStockModel.buyBalanced(balance1, mockCurrentDate, 10000);
  }

  @Test
  public void getCurrentActInfo() {
    betterStockModel.createPortfolio("Steve2");
    assertEquals("Portfolio is empty", betterStockModel.getCurrentActInfo());
    betterStockModel.buy("AAPL", 2);
    assertEquals("Portfolio is currently holding:\n" +
                    "Ticker  Shares  Dollar Value\n" +
                    "AAPL    2.0000      " + formatTickerAmount("AAPL", 2),
            betterStockModel.getCurrentActInfo());
  }

  @Test
  public void plotPortfolio() {
    // Relies on Steve file already having values.
    betterStockModel.createPortfolio("Steve");

    // Buy 10 MSFT shares on 2000-03-25
    betterStockModel.buyDate("MSFT", 10, LocalDate.parse("2000-03-25"));

    // Check plot for a given date range
    String expectedPlot1 = "2000-03-26 **************************************************\n" +
            "2000-04-25 ********************************\n" +
            "2000-05-25 ****************************\n" +
            "2000-06-24 ***********************************\n" +
            "2000-07-24 ********************************\n" +
            "2000-08-23 ********************************\n" +
            "2000-09-22 *****************************\n" +
            "2000-10-22 ******************************\n" +
            "2000-11-21 *******************************\n" +
            "2000-12-21 ********************\n" +
            "2001-01-20 ****************************\n" +
            "2001-02-19 **************************\n" +
            "2001-03-21 ***********************\n" +
            "2001-04-20 *******************************\n" +
            "scale * 22.3380";
    assertEquals(expectedPlot1, betterStockModel.plotPortfolio(LocalDate.parse("2000-03-26"),
            LocalDate.parse("2001-04-20")));

    // Additional stock purchases
    betterStockModel.buyDate("MSFT", 1, LocalDate.parse("2000-03-25"));
    betterStockModel.buyDate("MSFT", 100, LocalDate.parse("2000-12-25"));

    String expectedPlot2 = "2000-03-26 *********\n" +
            "2000-04-25 *****\n" +
            "2000-05-25 *****\n" +
            "2000-06-24 ******\n" +
            "2000-07-24 ******\n" +
            "2000-08-23 ******\n" +
            "2000-09-22 *****\n" +
            "2000-10-22 *****\n" +
            "2000-11-21 *****\n" +
            "2000-12-21 ****\n" +
            "2001-01-20 *********************************************\n" +
            "2001-02-19 ******************************************\n" +
            "2001-03-21 *************************************\n" +
            "2001-04-20 **************************************************\n" +
            "scale * 153.1800";
    assertEquals(expectedPlot2, betterStockModel.plotPortfolio(LocalDate.parse("2000-03-26"),
            LocalDate.parse("2001-04-20")));

    // Create a new portfolio for additional tests
    betterStockModel.createPortfolio("Steve1");

    // Buy MSFT and AAPL shares on 2000-01-01
    betterStockModel.buyDate("MSFT", 10, LocalDate.parse("2000-01-01"));
    betterStockModel.buyDate("AAPL", 5, LocalDate.parse("2000-01-01"));

    // Test plot for less than 5 days
    String expectedPlot3 = "2011-01-01 *************************************************\n" +
            "2011-01-02 *************************************************\n" +
            "2011-01-03 **************************************************\n" +
            "2011-01-04 ***************************************************\n" +
            "scale * 38.7465";
    assertEquals(expectedPlot3, betterStockModel.plotPortfolio(LocalDate.parse("2011-01-01"),
            LocalDate.parse("2011-01-04")));

    // Test plot for 7 days
    String expectedPlot4 = "2011-01-01 *************************************************\n" +
            "2011-01-02 *************************************************\n" +
            "2011-01-03 **************************************************\n" +
            "2011-01-04 **************************************************\n" +
            "2011-01-05 **************************************************\n" +
            "2011-01-06 **************************************************\n" +
            "2011-01-07 **************************************************\n" +
            "2011-01-08 **************************************************\n" +
            "2011-01-09 **************************************************\n" +
            "scale * 39.3320";
    assertEquals(expectedPlot4, betterStockModel.plotPortfolio(LocalDate.parse("2011-01-01"),
            LocalDate.parse("2011-01-09")));

    // Test plot for 28 days
    String expectedPlot5 = "2011-01-01 ***********************************************\n" +
            "2011-01-03 ************************************************\n" +
            "2011-01-05 *************************************************\n" +
            "2011-01-07 *************************************************\n" +
            "2011-01-09 *************************************************\n" +
            "2011-01-11 **************************************************\n" +
            "2011-01-13 **************************************************\n" +
            "2011-01-15 **************************************************\n" +
            "2011-01-17 **************************************************\n" +
            "2011-01-19 *************************************************\n" +
            "2011-01-21 ************************************************\n" +
            "2011-01-23 ************************************************\n" +
            "2011-01-25 **************************************************\n" +
            "2011-01-27 **************************************************\n" +
            "2011-01-29 *************************************************\n" +
            "2011-01-31 *************************************************\n" +
            "2011-02-02 **************************************************\n" +
            "2011-02-04 **************************************************\n" +
            "scale * 40.5080";
    assertEquals(expectedPlot5, betterStockModel.plotPortfolio(LocalDate.parse("2011-01-01"),
            LocalDate.parse("2011-02-04")));

    // Test plot for 60 days
    String expectedPlot6 = "2011-01-01 **********************************************\n" +
            "2011-01-08 ************************************************\n" +
            "2011-01-15 **************************************************\n" +
            "2011-01-22 ***********************************************\n" +
            "2011-01-29 ************************************************\n" +
            "2011-02-05 *************************************************\n" +
            "2011-02-12 **************************************************\n" +
            "2011-02-19 **************************************************\n" +
            "2011-02-26 *************************************************\n" +
            "2011-03-04 **************************************************\n" +
            "scale * 41.1905";
    assertEquals(expectedPlot6, betterStockModel.plotPortfolio(LocalDate.parse("2011-01-01"),
            LocalDate.parse("2011-03-04")));

    // Test plot for 135 days
    String expectedPlot7 = "2011-01-01 **********************************************\n" +
            "2011-01-08 ************************************************\n" +
            "2011-01-15 **************************************************\n" +
            "2011-01-22 ***********************************************\n" +
            "2011-01-29 ************************************************\n" +
            "2011-02-05 *************************************************\n" +
            "2011-02-12 **************************************************\n" +
            "2011-02-19 **************************************************\n" +
            "2011-02-26 *************************************************\n" +
            "2011-03-05 **************************************************\n" +
            "2011-03-12 *************************************************\n" +
            "2011-03-19 ***********************************************\n" +
            "2011-03-26 *************************************************\n" +
            "2011-04-02 *************************************************\n" +
            "2011-04-09 ************************************************\n" +
            "2011-04-16 **********************************************\n" +
            "2011-04-23 *************************************************\n" +
            "2011-04-28 *************************************************\n" +
            "scale * 41.1905";
    assertEquals(expectedPlot7, betterStockModel.plotPortfolio(LocalDate.parse("2011-01-01"),
            LocalDate.parse("2011-04-28")));

    // Test plot for 550 days
    String expectedPlot8 = "2011-01-01 *****************************\n" +
            "2011-01-31 ******************************\n" +
            "2011-03-02 ******************************\n" +
            "2011-04-01 ******************************\n" +
            "2011-05-01 ******************************\n" +
            "2011-05-31 ******************************\n" +
            "2011-06-30 *****************************\n" +
            "2011-07-30 **********************************\n" +
            "2011-08-29 *********************************\n" +
            "2011-09-28 **********************************\n" +
            "2011-10-28 ***********************************\n" +
            "2011-11-27 *******************************\n" +
            "2011-12-27 ***********************************\n" +
            "2012-01-26 **************************************\n" +
            "2012-02-25 ********************************************\n" +
            "2012-03-26 **************************************************\n" +
            "2012-04-25 **************************************************\n" +
            "2012-05-25 **********************************************\n" +
            "2012-06-24 ************************************************\n" +
            "2012-07-04 **************************************************\n" +
            "scale * 67.4400";
    assertEquals(expectedPlot8, betterStockModel.plotPortfolio(LocalDate.parse("2011-01-01"),
            LocalDate.parse("2012-07-04")));

    // Test plot for 600 days
    String expectedPlot9 = "2011-01-01 ***************************\n" +
            "2011-01-31 ****************************\n" +
            "2011-03-02 ****************************\n" +
            "2011-04-01 ****************************\n" +
            "2011-05-01 ****************************\n" +
            "2011-05-31 ****************************\n" +
            "2011-06-30 ***************************\n" +
            "2011-07-30 *******************************\n" +
            "2011-08-29 *******************************\n" +
            "2011-09-28 *******************************\n" +
            "2011-10-28 ********************************\n" +
            "2011-11-27 *****************************\n" +
            "2011-12-27 ********************************\n" +
            "2012-01-26 ***********************************\n" +
            "2012-02-25 *****************************************\n" +
            "2012-03-26 ***********************************************\n" +
            "2012-04-25 ***********************************************\n" +
            "2012-05-25 *******************************************\n" +
            "2012-06-24 *********************************************\n" +
            "2012-07-24 **********************************************\n" +
            "2012-08-23 **************************************************\n" +
            "scale * 72.3140";
    assertEquals(expectedPlot9, betterStockModel.plotPortfolio(LocalDate.parse("2011-01-01"),
            LocalDate.parse("2012-08-23")));

    // Test plot for 800 days
    String expectedPlot10 = "2011-01-01 *************************\n" +
            "2011-01-31 **************************\n" +
            "2011-03-02 ***************************\n" +
            "2011-04-01 **************************\n" +
            "2011-05-01 ***************************\n" +
            "2011-05-31 ***************************\n" +
            "2011-06-30 **************************\n" +
            "2011-07-30 ******************************\n" +
            "2011-08-29 *****************************\n" +
            "2011-09-28 ******************************\n" +
            "2011-10-28 *******************************\n" +
            "2011-11-27 ****************************\n" +
            "2011-12-27 *******************************\n" +
            "2012-01-26 **********************************\n" +
            "2012-02-25 ***************************************\n" +
            "2012-03-26 *********************************************\n" +
            "2012-04-25 *********************************************\n" +
            "2012-05-25 *****************************************\n" +
            "2012-06-24 *******************************************\n" +
            "2012-07-24 ********************************************\n" +
            "2012-08-23 ************************************************\n" +
            "2012-09-22 ***************************************************\n" +
            "2012-10-22 **********************************************\n" +
            "2012-11-21 *****************************************\n" +
            "2012-12-21 **************************************\n" +
            "2013-01-20 *************************************\n" +
            "2013-02-19 **********************************\n" +
            "2013-03-11 *********************************\n" +
            "scale * 76.2475";
    assertEquals(expectedPlot10, betterStockModel.plotPortfolio(LocalDate.parse("2011-01-01"),
            LocalDate.parse("2013-03-11")));

    // Test plot for 2000 days
    String expectedPlot11 = "2011-01-01 ******************************\n" +
            "2012-01-01 ************************************\n" +
            "2012-12-31 ***********************************************\n" +
            "2013-12-31 **************************************************\n" +
            "2014-12-31 ****************\n" +
            "2015-12-31 ******************\n" +
            "2016-06-23 ****************\n" +
            "scale * 63.5840";
    assertEquals(expectedPlot11, betterStockModel.plotPortfolio(LocalDate.parse("2011-01-01"),
            LocalDate.parse("2016-06-23")));

    // Test plot for 5000 days
    String expectedPlot12 = "2005-01-01 **********\n" +
            "2006-01-01 **********\n" +
            "2007-01-01 ************\n" +
            "2008-01-01 **********************\n" +
            "2008-12-31 **********\n" +
            "2009-12-31 **********************\n" +
            "2010-12-31 *******************************\n" +
            "2011-12-31 *************************************\n" +
            "2012-12-30 *********************************************\n" +
            "2013-12-30 **************************************************\n" +
            "2014-12-30 *****************\n" +
            "2015-12-30 ******************\n" +
            "2016-12-29 ********************\n" +
            "2017-12-29 ****************************\n" +
            "2018-09-10 ***********************************\n" +
            "scale * 62.9100";
    assertEquals(expectedPlot12, betterStockModel.plotPortfolio(LocalDate.parse("2005-01-01"),
            LocalDate.parse("2018-09-10")));

    // Test plot for 7300 days
    String expectedPlot13 = "2003-01-01 **********\n" +
            "2005-12-31 ***********\n" +
            "2008-12-30 ***********\n" +
            "2011-12-30 **************************************\n" +
            "2014-12-29 ******************\n" +
            "2017-12-28 *****************************\n" +
            "2020-12-27 ************************************************\n" +
            "2022-12-27 **************************************************\n" +
            "scale * 60.3950";
    assertEquals(expectedPlot13, betterStockModel.plotPortfolio(LocalDate.parse("2003-01-01"),
            LocalDate.parse("2022-12-27")));
  }


  @Test
  public void testPlotStockShortTerm() {
    betterStockModel.createPortfolio("Random");
    LocalDate jan5 = LocalDate.of(2019, 1, 5);
    LocalDate jan6 = LocalDate.of(2019, 1, 6);
    assertEquals("2019-01-05 **************************************************\n" +
            "2019-01-06 **************************************************\n" +
            "scale * 2.9652", betterStockModel.plotStockValue("AAPL", jan5, jan6));
    // test only one day
    assertEquals(
            "2019-01-05 **************************************************\n" +
                    "scale * 2.9652", betterStockModel.plotStockValue("AAPL", jan5, jan5));
    assertEquals("2019-01-05 **************************************************\n" +
            "2019-01-06 **************************************************\n" +
            "scale * 21.4142", betterStockModel.plotStockValue("GOOG", jan5, jan6));
    assertEquals("2019-01-05 **************************************************\n" +
            "2019-01-06 **************************************************\n" +
            "scale * 2.6730", betterStockModel.plotStockValue("V", jan5, jan6));
  }

  @Test
  public void testPlotLongTerm() {
    LocalDate jan2004 = LocalDate.of(2019, 1, 1);
    LocalDate jan2024 = LocalDate.of(2024, 1, 1);
    assertEquals("2019-01-01 ***************************\n" +
            "2020-01-01 **************************************************\n" +
            "2020-12-31 ***********************\n" +
            "2021-12-31 *******************************\n" +
            "2022-12-31 ***********************\n" +
            "2023-12-31 *********************************\n" +
            "2024-01-01 *********************************\n" +
            "scale * 5.8730", betterStockModel.plotStockValue("AAPL", jan2004, jan2024));
    assertEquals("2019-01-01 ******************\n" +
            "2020-01-01 ************************\n" +
            "2020-12-31 *******************************\n" +
            "2021-12-31 **************************************************\n" +
            "2022-12-31 **\n" +
            "2023-12-31 ***\n" +
            "2024-01-01 ***\n" +
            "scale * 57.8718", betterStockModel.plotStockValue("GOOG", jan2004, jan2024));
    assertEquals("2019-01-01 **************************\n" +
            "2020-01-01 *************************************\n" +
            "2020-12-31 *******************************************\n" +
            "2021-12-31 ******************************************\n" +
            "2022-12-31 ****************************************\n" +
            "2023-12-31 **************************************************\n" +
            "2024-01-01 **************************************************\n" +
            "scale * 5.2070", betterStockModel.plotStockValue("V", jan2004, jan2024));
  }

  @Test
  public void testPlotFails() {
    LocalDate jan1 = LocalDate.of(2024, 1, 1);
    LocalDate jan2 = LocalDate.of(2024, 1, 2);
    Stock msft = OODStocks.getStockOrMakeNew("MSFT");
    assertThrows(IllegalArgumentException.class, () ->
            betterStockModel.plotStockValue("MSFT", jan2, jan1));
    assertThrows(IllegalArgumentException.class, () ->
            betterStockModel.plotStockValue("MSFT", jan1,
            jan1.minusDays(100)));
    // test fails for out of bounds IPODate
    assertThrows(IllegalArgumentException.class, () ->
            betterStockModel.plotStockValue("AAPL",
                    msft.getIPODate().minusDays(1),
                    msft.getIPODate()));
    // test fails for out of bounds recentdate
    assertThrows(IllegalArgumentException.class, () ->
            msft.plot(msft.getRecentDate(),
                    msft.getRecentDate().plusDays(2)));
  }

  @Test
  public void reBalance() {
    betterStockModel.createPortfolio("Big Baller Benz");
    LocalDate date1 = LocalDate.of(2023, 11, 3);
    LocalDate date2 = LocalDate.parse("2024-01-25");
    betterStockModel.buyDate("MSFT", 10, date1);
    betterStockModel.buyDate("AAPL", 120, date1);
    betterStockModel.buyDate("V", 120, date1);

    Map<String, Double> balencer = new HashMap<>();
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers      Values\n" +
            "MSFT      4048.7000\n" +
            "AAPL     23300.4000\n" +
            "V        32713.2000", betterStockModel.distribution(date2));
    balencer.put("MSFT", 50.0);
    balencer.put("V", 50.0);
    assertEquals("Successfully balanced portfolio",
            betterStockModel.reBalance(balencer, date2));
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers      Values\n" +
            "AAPL     23300.4000\n" +
            "MSFT     18380.9500\n" +
            "V        18380.9500", betterStockModel.distribution(date2));
    betterStockModel.reBalance(balencer, LocalDate.parse("2024-04-25"));
    assertEquals("Portfolio is currently holding:\n" +
                    "Tickers      Values\n" +
                    "AAPL     23300.4000\n" +
                    "MSFT     18380.9500\n" +
                    "V        18380.9500",
            betterStockModel.distribution(date2));
  }

  @Test
  public void getCurrentPortfolioName() {
    // easy
    betterStockModel.createPortfolio("Big Baller Benz");
    assertEquals("Big Baller Benz", betterStockModel.getCurrentPortfolioName());
    betterStockModel.logout();
    betterStockModel.setPortfolio("Big Baller Benz");
    assertEquals("Big Baller Benz", betterStockModel.getCurrentPortfolioName());
    betterStockModel.logout();
    betterStockModel.setPortfolio("Steve");
    assertEquals("Steve", betterStockModel.getCurrentPortfolioName());
  }

  @Override
  protected BetterStockModel instantiateModel() {
    return new BetterStockModelImpl();
  }

  @Test
  public void totalValue() {
    BetterStockModel newStockModel = new BetterStockModelImpl();
    LocalDate date1 = LocalDate.parse("2024-03-16");
    LocalDate date2 = LocalDate.parse("2024-06-01");
    assertThrows(IllegalArgumentException.class, () -> {
      newStockModel.totalValue(date1);
    });
    newStockModel.createPortfolio("f");
    assertThrows(IllegalArgumentException.class, () -> {
      newStockModel.totalValue(LocalDate.parse("2024-08-16"));
    });

    newStockModel.buyDate("MSFT", 10, LocalDate.parse("2002-01-16"));
    newStockModel.buyDate("AAPL", 50, date1);
    newStockModel.buyDate("AAPL", 50, date2);
    assertEquals(newStockModel.totalValue(date2), 23376.3, .01);
    assertEquals(newStockModel.totalValue(LocalDate.parse("2021-03-17")), 2370.4, .01);
    assertEquals(newStockModel.totalValue(date1), 12795.2, .01);
    assertEquals(newStockModel.totalValue(LocalDate.parse("2000-03-17")), 0, .01);
    assertEquals(newStockModel.totalValue(LocalDate.parse("1998-03-17")), 0, .01);
  }

  @Test
  public void buy() {
    assertThrows(IllegalArgumentException.class, () -> {
      betterStockModel.buy("AAPL", 50);
    });
    betterStockModel.createPortfolio("f");

    assertThrows(IllegalArgumentException.class, () -> {
      betterStockModel.buy("AAPLa", 50);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      betterStockModel.buy("AAPL", -50);
    });
    betterStockModel.buy("AAPL", 50);
    // Ensure the buy command executes on the most recent available date for the given stock.
    assertEquals("Portfolio is currently holding:\n" +
                    "Tickers   Shares\n" +
                    "AAPL     50.0000",
            betterStockModel
                    .composition(OODStocks.getStockOrMakeNew("AAPl")
                            .getRecentDate()));
    // Ensure the buy command is still there in the future
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers   Shares\n" +
            "AAPL     50.0000", betterStockModel
            .composition(LocalDate.of(2027, 1, 1)));
    betterStockModel.buy("AAPL", 50);
    betterStockModel.buy("COST", 50);
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers    Shares\n" +
            "COST      50.0000\n" +
            "AAPL     100.0000", betterStockModel.composition(LocalDate.now()));
  }

  @Test
  public void sell() {
    // Expect IllegalArgumentException when selling without a portfolio
    assertThrows(IllegalArgumentException.class, () -> {
      betterStockModel.sell("AAPL", 50);
    });
    betterStockModel.createPortfolio("f");

    // Expect IllegalArgumentException for invalid ticker or negative amount
    assertThrows(IllegalArgumentException.class, () -> {
      betterStockModel.sell("AAPLa", 50);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      betterStockModel.sell("AAPL", -50);
    });

    // setup
    betterStockModel.buy("AAPL", 50);
    betterStockModel.buy("COST", 50);

    // Check portfolio composition after initial buys
    assertEquals("Portfolio is currently holding:\n" +
                    "Tickers   Shares\n" +
                    "COST     50.0000\n" +
                    "AAPL     50.0000",
            betterStockModel.composition(OODStocks.getStockOrMakeNew("AAPL").getRecentDate()));

    // Sell part of AAPL stock
    betterStockModel.sell("AAPL", 20);
    assertEquals("Portfolio is currently holding:\n" +
                    "Tickers   Shares\n" +
                    "COST     50.0000\n" +
                    "AAPL     30.0000",
            betterStockModel.composition(OODStocks.getStockOrMakeNew("AAPL").getRecentDate()));

    // Sell the off AAPL stock
    betterStockModel.sell("AAPL", 30);
    assertEquals("Portfolio is currently holding:\n" +
                    "Tickers   Shares\n" +
                    "COST     50.0000",
            betterStockModel.composition(OODStocks.getStockOrMakeNew("COST").getRecentDate()));

    // Sell off the COST stock
    betterStockModel.sell("COST", 50);
    assertEquals("Portfolio is currently holding:\n" +
                    "Tickers  Shares",
            betterStockModel.composition(OODStocks.getStockOrMakeNew("COST").getRecentDate()));
  }

  @Test
  public void buyDate() {
    // Ensure that an exception is thrown for an invalid portfolio
    assertThrows(IllegalArgumentException.class, () -> {
      betterStockModel.buyDate("AAPL", 50, LocalDate.now());
    });
    betterStockModel.createPortfolio("f");
    assertThrows(IllegalArgumentException.class, () -> {
      betterStockModel.buyDate("AAPL", 50, null);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      betterStockModel.buyDate("AAPLa", 50, LocalDate.parse("0000-01-01"));
    });
    // Ensure that an exception is thrown for invalid ticker or negative amount
    assertThrows(IllegalArgumentException.class, () -> {
      betterStockModel.buyDate("AAPLa", 50, LocalDate.now());
    });
    assertThrows(IllegalArgumentException.class, () -> {
      betterStockModel.buyDate("AAPL", -50, LocalDate.of(2023, 1, 1));
    });

    // Test buying with a valid ticker and positive amount on a specific date
    LocalDate buyDate = LocalDate.of(2023, 6, 1);
    double valueBought = betterStockModel.buyDate("AAPL", 50, buyDate);

    // Ensure the buy command executes on the specified date
    assertEquals("Portfolio is currently holding:\n" +
                    "Tickers   Shares\n" +
                    "AAPL     50.0000",
            betterStockModel.composition(buyDate));

    // Ensure the portfolio remains the same when checking in the future
    assertEquals("Portfolio is currently holding:\n" +
                    "Tickers   Shares\n" +
                    "AAPL     50.0000",
            betterStockModel.composition(LocalDate.of(2027, 1, 1)));

    // Test buying additional shares on a different date
    LocalDate additionalBuyDate = LocalDate.of(2023, 7, 1);
    betterStockModel.buyDate("AAPL", 50, additionalBuyDate);
    betterStockModel.buyDate("COST", 50, additionalBuyDate);

    // Check portfolio composition on the second buy date
    assertEquals("Portfolio is currently holding:\n" +
                    "Tickers    Shares\n" +
                    "AAPL     100.0000\n" +
                    "COST      50.0000",
            betterStockModel.composition(additionalBuyDate));
  }

  @Test
  public void sellDate() {
    // Ensure that an exception is thrown for an invalid portfolio
    assertThrows(IllegalArgumentException.class, () -> {
      betterStockModel.sellDate("AAPL", 50, LocalDate.now());
    });
    assertThrows(IllegalArgumentException.class, () -> {
      betterStockModel.sellDate("AAPL", 50, LocalDate.parse("0000-01-01"));
    });
    assertThrows(IllegalArgumentException.class, () -> {
      betterStockModel.sellDate("AAPL", 50, null);
    });
    betterStockModel.createPortfolio("f");

    // Ensure that an exception is thrown for invalid ticker or negative amount
    assertThrows(IllegalArgumentException.class, () -> {
      betterStockModel.sellDate("AAPLa", 50, LocalDate.now());
    });
    assertThrows(IllegalArgumentException.class, () -> {
      betterStockModel.sellDate("AAPL", -50, LocalDate.now());
    });

    // Setup initial buys to have stocks to sell
    betterStockModel.buyDate("AAPL", 50, LocalDate.of(2023, 6, 1));
    betterStockModel.buyDate("COST", 50, LocalDate.of(2023, 6, 1));

    // Check portfolio composition after initial buys
    assertEquals("Portfolio is currently holding:\n" +
                    "Tickers   Shares\n" +
                    "AAPL     50.0000\n" +
                    "COST     50.0000",
            betterStockModel.composition(LocalDate.of(2023, 6, 1)));

    // Sell part of AAPL stock on a specific date
    LocalDate sellDate = LocalDate.of(2023, 7, 1);
    double valueSold = betterStockModel.sellDate("AAPL", 20, sellDate);
    assertEquals("Portfolio is currently holding:\n" +
                    "Tickers   Shares\n" +
                    "AAPL     30.0000\n" +
                    "COST     50.0000",
            betterStockModel.composition(sellDate));

    // Sell all remaining AAPL stock on a different date
    LocalDate finalSellDate = LocalDate.of(2023, 8, 1);
    betterStockModel.sellDate("AAPL", 30, finalSellDate);
    assertEquals("Portfolio is currently holding:\n" +
                    "Tickers   Shares\n" +
                    "COST     50.0000",
            betterStockModel.composition(finalSellDate));

    // Sell off all COST stock
    betterStockModel.sellDate("COST", 50, LocalDate.of(2023, 9, 1));
    assertEquals("Portfolio is currently holding:\n" +
                    "Tickers  Shares",
            betterStockModel.composition(LocalDate.of(2023, 9, 1)));
    assertThrows(IllegalArgumentException.class, () ->
            betterStockModel.sellDate("AAPL", 20, LocalDate.of(2023, 9, 1)));
  }

  @Test
  public void listAllPortfolios() {
    List<String> test = new ArrayList<>();
    test.add("spiderPig");
    test.add("Steve");
    assertEquals(test, betterStockModel.listAllPortfolios());
    betterStockModel.createPortfolio("f");
    test.add(0, "f");
    assertEquals(test, betterStockModel.listAllPortfolios());
  }


  @Test
  public void testPortfolio() {
    assertEquals("Portfolio Info for spiderPig:\n" +
            "Ticker    Shares  Dollar Value\n" +
            "AAPL    106.2298    22758.6806\n" +
            "MSFT     26.1480    11087.0111\n" +
            "COST     15.0000    12514.5000",betterStockModel.viewPortfolio("spiderPig"));
  }
}