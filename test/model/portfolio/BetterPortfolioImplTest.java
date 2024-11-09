package model.portfolio;

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

import model.OODStocks;
import model.stock.SimpleStock;
import model.stock.Stock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

/**
 * Test for BetterPortfolio.
 */
public class BetterPortfolioImplTest {
  Stock mSFTStock;
  Stock vStock;
  Stock aAPLStock;
  Stock cOSTStock;

  Stock gOOGStock;
  LocalDate mockCurrentDate;
  Map<String, Stock> stocks;
  List<Stock> stockList;
  BetterPortfolio port1;
  BetterPortfolio port2;
  BetterPortfolio port3;
  BetterPortfolio portfolioBalance;
  String testPortfolioData = System.getProperty("user.dir") + "/TestData/TestPortfolios/";
  String preMadeTestData =
          testPortfolioData + "/PreMadePortfolios/";
  File steveFile = new File(preMadeTestData + "Steve.txt");
  File aLotOfPig = new File(preMadeTestData + "aLotOfPig.txt");

  @Before
  public void startUp() {
    // set the portfolio directory path through a mock constructor
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
    cOSTStock = new SimpleStock("COST");

    stocks = OODStocks.getStocks();
    stockList = new ArrayList<>(stocks.values());
    port1 = new MockBetterPortfolio("Steve"); // for loading
    port2 = new MockBetterPortfolio("Steve1");
    portfolioBalance = new MockBetterPortfolio("Steve2");
  }

  @Test
  public void testPortfolioSaveWithSellOnDate() {
    BetterPortfolio steveJobOutHere = new MockBetterPortfolio("steveJobOutHere");
    steveJobOutHere.buyDate(aAPLStock, 15.0, LocalDate.parse("2020-01-01"));
    steveJobOutHere.buyDate(aAPLStock, 5.0, LocalDate.parse("2020-01-03"));
    steveJobOutHere.sellDate(aAPLStock, 15.0, LocalDate.parse("2020-01-03"));
    steveJobOutHere.save();
    File steveJobsFile = new File(testPortfolioData + "steveJobOutHere.txt");
    try {
      Scanner read = new Scanner(steveJobsFile);
      assertEquals(read.nextLine(), "steveJobOutHere");
      assertEquals(read.nextLine(), "2020-01-01");
      assertEquals(read.nextLine(), "buy AAPL 15.0");
      assertEquals(read.nextLine(), "2020-01-03");
      assertEquals(read.nextLine(), "sell AAPL 10.0");
    } catch (Exception e) {
      throw new IllegalArgumentException("Save formating is not as expected");
    }
    steveJobsFile.delete();
  }

  // Test constructor
  @Test // test constructor with valid file, (also in-inadvertently testing load)
  public void testConstructorWithValidFile() {
    LocalDate firstBuyDate = LocalDate.of(2024, 1, 1);
    LocalDate lastBuyDate = LocalDate.of(2024, 6, 3);
    BetterPortfolio stevePortfolio = new MockBetterPortfolio(steveFile);
    // check to make sure there is nothing before the first buy
    assertEquals("Portfolio is currently holding:\n" +
                    "Tickers  Shares",
            stevePortfolio.composition(firstBuyDate.minusDays(1)));
    // check ot make sure there is the correct amount for the first day commands
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers   Shares\n" +
            "MSFT     10.0000", stevePortfolio.composition(firstBuyDate));
    // check that there is the correct amount for the most recent commands.
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers   Shares\n" +
            "MSFT     30.0000", stevePortfolio.composition(lastBuyDate));

    BetterPortfolio spiderPigPortfolio = new MockBetterPortfolio(aLotOfPig);
    firstBuyDate = LocalDate.of(2001, 1, 1);
    // check to make sure there is nothing before the first buy
    assertEquals("Portfolio is currently holding:\n" +
                    "Tickers  Shares",
            spiderPigPortfolio.composition(firstBuyDate.minusDays(1)));
    // ensure that a more complex series of commands works correctly
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers    Shares\n" +
            "AAPL     106.2298\n" +
            "MSFT      26.1480\n" +
            "COST      15.0000", spiderPigPortfolio.composition(firstBuyDate));
  }

  @Test // test the constructor with an invalid file
  public void testConstructorWithInvalidFile() {
    File nonExistentFile = new File(preMadeTestData + "noDiddy.txt");
    try {
      BetterPortfolio brokenPortfolio = new MockBetterPortfolio(nonExistentFile);
      fail("Should have thrown an exception");
    } catch (IllegalArgumentException e) {
      assertEquals("Sadly no file exists with the name " + nonExistentFile.getName() +
              " in the directory: " + nonExistentFile.getParentFile(), e.getMessage());
    }
  }

  @Test
  public void testConstructorWithName() {
    BetterPortfolio brokenPortfolio = new MockBetterPortfolio("DJ Khaled");
    assertEquals("DJ Khaled", brokenPortfolio.getName());
    assertEquals(0, brokenPortfolio.totalValue(LocalDate.now()), 0.001);
  }

  @Test
  public void balanceAllEven() {
    port1.buyDate(OODStocks.getStock("MSFT"), 10, LocalDate.parse("2024-03-25"));
    assertEquals(port1.totalValue(LocalDate.parse("2023-03-13")), 0, .001);
    assertEquals(port1.totalValue(LocalDate.parse("2024-03-26")), 4216.5, .001);
    assertEquals(port1.totalValue(LocalDate.parse("2024-03-25")), 4228.6, .001);
    Map<Stock, Double> balance1 = new LinkedHashMap<>();
    balance1.put(cOSTStock, 25.0);
    balance1.put(vStock, 25.0);
    balance1.put(aAPLStock, 25.0);
    balance1.put(gOOGStock, 25.0);
    portfolioBalance.balance(balance1, mockCurrentDate, 15000);
    // Check that the shares look reasonable and all stock are there
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers   Shares\n" +
            "COST      4.5990\n" +
            "V        13.8694\n" +
            "AAPL     19.3269\n" +
            "GOOG     21.4998", portfolioBalance.composition(mockCurrentDate));
    // Check that values of stock are even
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers     Values\n" +
            "COST     3750.0000\n" +
            "V        3750.0000\n" +
            "AAPL     3750.0000\n" +
            "GOOG     3750.0000", portfolioBalance.distribution(mockCurrentDate));
  }

  @Test
  public void testBalanceRandom() {
    Map<Stock, Double> balance1 = new LinkedHashMap<>();
    portfolioBalance = new MockBetterPortfolio("Steve2");

    balance1.put(gOOGStock, 26.0);
    assertThrows(IllegalArgumentException.class, () ->
            portfolioBalance.balance(balance1, mockCurrentDate, 15000));
    balance1.put(gOOGStock, -1.0);
    balance1.put(aAPLStock, 51.0);

    assertThrows(IllegalArgumentException.class, () ->
            portfolioBalance.balance(balance1, mockCurrentDate, 15000));
    balance1.put(vStock, 25.0);
    balance1.put(cOSTStock, 25.0);
    balance1.put(aAPLStock, 25.0);
    balance1.put(gOOGStock, 24.0);
    assertThrows(IllegalArgumentException.class, () ->
            portfolioBalance.balance(balance1, mockCurrentDate, 15000));
    Map<Stock, Double> balance2 = new LinkedHashMap<>();
    balance2.put(vStock, 55.0);
    balance2.put(cOSTStock, 45.0);

    portfolioBalance.balance(balance2, mockCurrentDate, 15000);
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers   Shares\n" +
            "V        30.5126\n" +
            "COST      8.2782", portfolioBalance.composition(mockCurrentDate));
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers     Values\n" +
            "V        8250.0000\n" +
            "COST     6750.0000", portfolioBalance.distribution(mockCurrentDate));
  }

  @Test
  public void buyDate() {
    port1.buyDate(OODStocks.getStock("MSFT"), 10, LocalDate.parse("2024-03-25"));
    assertEquals(10, port1.getAmount(mockCurrentDate).get("MSFT"), .001);
    port1.buyDate(OODStocks.getStock("MSFT"), 10, LocalDate.parse("2024-03-25"));
    assertEquals(20, port1.getAmount(mockCurrentDate).get("MSFT"), .001);
    port1.buyDate(OODStocks.getStock("MSFT"), 10, LocalDate.parse("2024-04-25"));
    assertEquals(30, port1.getAmount(mockCurrentDate).get("MSFT"), .001);
    assertEquals(20, port1.getAmount(LocalDate.parse("2024-04-01")).get("MSFT"),
            .001);
  }

  @Test
  public void reBalanceBuyEarlier() {
    BetterPortfolio portfolioTest = new BetterPortfolioImpl("bob");
    portfolioTest.buyDate(mSFTStock, 15, LocalDate.parse("2020-01-01"));
    portfolioTest.buyDate(cOSTStock, 15, LocalDate.parse("2020-01-01"));
    Map<Stock, Double> stocks = new LinkedHashMap<>();
    stocks.put(cOSTStock, 50.0);
    stocks.put(mSFTStock, 50.0);
    //Expected first value
    portfolioTest.reBalance(stocks, LocalDate.parse("2021-01-01"));

    assertEquals("Portfolio is currently holding:\n" +
            "Tickers   Shares\n" +
            "COST     11.9274\n" +
            "MSFT     20.2050", portfolioTest.composition(LocalDate.parse("2021-01-01")));
    portfolioTest.buyDate(mSFTStock, 10, LocalDate.parse("2020-01-01"));
    //expected second value with updated amounts
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers   Shares\n" +
            "COST     14.8790\n" +
            "MSFT     25.2050", portfolioTest.composition(LocalDate.parse("2021-01-01")));


  }

  @Test
  public void reBalance() {
    port1.buyDate(mSFTStock, 10, LocalDate.parse("2024-03-25"));
    port1.buyDate(aAPLStock, 120, LocalDate.parse("2024-03-25"));
    port1.buyDate(vStock, 120, LocalDate.parse("2024-03-25"));


    //r

    Map<Stock, Double> balencer = new HashMap<>();
    balencer.put(mSFTStock, 50.0);
    balencer.put(vStock, 50.0);
    double stockPrice = ((mSFTStock.getPrice((LocalDate.parse("2024-04-25"))) * 10) +
            (vStock.getPrice(LocalDate.parse("2024-04-25")) * 120)) / 2;
    // should be after split

    port1.reBalance(balencer, LocalDate.parse("2024-04-25"));
    Map<String, Double> maps = port1.getAmount(mockCurrentDate);
    for (Map.Entry<String, Double> entry : maps.entrySet()) {
      if (entry.getKey().equals("MSFT")) {
        assertEquals(entry.getValue(), (stockPrice / 399.04), .1);
      }
      if (entry.getKey().equals("V")) {
        assertEquals(entry.getValue(), (stockPrice / 275.16), .1);
      }
      if (entry.getKey().equals("AAPL")) {
        assertEquals(entry.getValue(), 120, .1);
      }
    }
    port1.reBalance(balencer, LocalDate.parse("2024-04-25"));
    for (Map.Entry<String, Double> entry : maps.entrySet()) {
      if (entry.getKey().equals("MSFT")) {
        assertEquals(entry.getValue(), (stockPrice / 399.04), .1);
      }
      if (entry.getKey().equals("V")) {
        assertEquals(entry.getValue(), (stockPrice / 275.16), .1);
      }
      if (entry.getKey().equals("AAPL")) {
        assertEquals(entry.getValue(), 120, .1);
      }
    }
    port1.reBalance(balencer, LocalDate.parse("2024-04-25"));
    for (Map.Entry<String, Double> entry : maps.entrySet()) {
      if (entry.getKey().equals("MSFT")) {
        assertEquals(entry.getValue(), (stockPrice / 399.04), .1);
      }
      if (entry.getKey().equals("V")) {
        assertEquals(entry.getValue(), (stockPrice / 275.16), .1);
      }
      if (entry.getKey().equals("AAPL")) {
        assertEquals(entry.getValue(), 120, .1);
      }
    }

  }

  @Test
  public void sellDate() {
    port2.buyDate(OODStocks.getStock("MSFT"), 10, LocalDate.parse("2024-03-25"));
    assertEquals(10, port2.getAmount(mockCurrentDate).get("MSFT"), .001);


    assertEquals(port2.sellDate(OODStocks.getStock("MSFT"),
            10, LocalDate.parse("2024-03-25")), 4228.6, .01);
    assertEquals(null, port2.getAmount(mockCurrentDate).get("MSFT"));
    port2.buyDate(OODStocks.getStock("MSFT"), 10, LocalDate.parse("2024-03-25"));

    assertThrows(IllegalArgumentException.class, () -> {
      port2.sellDate(OODStocks.getStock("MSFT"),
              15, LocalDate.parse("2024-03-25"));
    });

    assertEquals(10, port2.getAmount(mockCurrentDate).get("MSFT"), .001);

  }

  @Test
  public void load() {
    this.save();
    File filename = new File(System.getProperty("user.dir") + "/PortfolioInfo/" +
            port1.getName() + ".txt");
    port3 = new MockBetterPortfolio(filename);
    assertEquals(port3.getName(), "Steve");
    Map<String, Double> amount = new LinkedHashMap<>();
    amount.put("MSFT", 10.0);
    assertEquals(port3.getAmount(LocalDate.parse("2024-01-01")), amount);
    amount.put("MSFT", 30.0);
    assertEquals(port3.getAmount(mockCurrentDate), amount);
  }

  // Used for testing the save function (coupled with testing the load function... so we just call
  // it before we started the load function).
  private void save() {
    port1.buyDate(mSFTStock, 10, mockCurrentDate);
    port1.buyDate(mSFTStock, 10, mockCurrentDate);
    port1.buyDate(mSFTStock, 10, LocalDate.parse("2024-01-01"));
    port1.save();
    File filename = new File(testPortfolioData +
            port1.getName() + ".txt");
    try {
      Scanner read = new Scanner(filename);
      assertEquals(read.nextLine(), "Steve");
      assertEquals(read.nextLine(), "2024-01-01");
      assertEquals(read.nextLine(), "buy MSFT 10.0");
      assertEquals(read.nextLine(), "2024-06-03");
      assertEquals(read.nextLine(), "buy MSFT 20.0");
    } catch (Exception e) {
      throw new IllegalArgumentException("Save formating is not as expected");
    }
  }

  @Test
  public void saveWithSell() {
    BetterPortfolio bobRoss = new MockBetterPortfolio("Bob Ross");
    LocalDate date = LocalDate.parse("2001-01-01");
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers  Shares", bobRoss.composition(date));
    bobRoss.buyDate(aAPLStock, 10, date);
    bobRoss.buyDate(aAPLStock, 20, date);
    bobRoss.sellDate(aAPLStock, 10, date);
    bobRoss.buyDate(aAPLStock, 40, date);
    bobRoss.sellDate(aAPLStock, 10, date);
    bobRoss.save();
    File filename = new File(testPortfolioData +
            bobRoss.getName() + ".txt");
    try {
      Scanner read = new Scanner(filename);
      assertEquals(read.nextLine(), "Bob Ross");
      assertEquals(read.nextLine(), date.toString());
      assertEquals(read.nextLine(), "buy AAPL 50.0");
    } catch (Exception e) {
      throw new IllegalArgumentException("Bob Ross portfolio did not save properly");
    }
    Map<Stock, Double> rebalancing = new LinkedHashMap<>();
    rebalancing.put(aAPLStock, 50.0);
    rebalancing.put(mSFTStock, 50.0);

    bobRoss.buyDate(mSFTStock, 30, date);
    bobRoss.reBalance(rebalancing, date);
    bobRoss.sellDate(mSFTStock, 5, date);
    bobRoss.buyDate(mSFTStock, 5, date);
    bobRoss.buyDate(aAPLStock, 15, date);
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers   Shares\n" +
            "AAPL     83.7298\n" +
            "MSFT     23.5754", bobRoss.composition(date));
    bobRoss.save();
    // check composition after change
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers   Shares\n" +
            "AAPL     83.7298\n" +
            "MSFT     23.5754", bobRoss.composition(date));
    try {
      Scanner read = new Scanner(filename);
      assertEquals(read.nextLine(), "Bob Ross");
      assertEquals(read.nextLine(), date.toString());
      assertEquals(read.nextLine(),
              "buy AAPL 50.0,buy MSFT 30.0,rebalance AAPL 50.0 MSFT 50.0,buy AAPL 15.0");
    } catch (Exception e) {
      throw new IllegalArgumentException("something went very wrong");
    }
    bobRoss.reBalance(rebalancing, date);
    bobRoss.buyDate(aAPLStock, 15, date);
    bobRoss.buyDate(aAPLStock, 15, date);
    bobRoss.buyDate(cOSTStock, 15, date);
    bobRoss.save();

    try {
      Scanner read = new Scanner(filename);
      assertEquals(read.nextLine(), "Bob Ross");
      assertEquals(read.nextLine(), date.toString());
      assertEquals(read.nextLine(),
              "buy AAPL 50.0,buy MSFT 30.0,rebalance AAPL 50.0 MSFT 50.0,buy AAPL 15.0" +
                      ",rebalance AAPL 50.0 MSFT 50.0,buy AAPL 30.0,buy COST 15.0");
    } catch (Exception e) {
      throw new IllegalArgumentException("There should still be a file for the scanner to read");
    }
  }

  @Test
  public void plot() {
    port1.buyDate(OODStocks.getStock("MSFT"), 10, LocalDate.parse("2000-03-25"));
    assertEquals("2000-03-26 **************************************************\n" +
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
                    "scale * 22.3380",
            port1.plot(LocalDate.parse("2000-03-26"), LocalDate.parse("2001-04-20")));


    port1.buyDate(OODStocks.getStock("MSFT"), 1, LocalDate.parse("2000-03-25"));
    port1.buyDate(OODStocks.getStock("MSFT"), 100, LocalDate.parse("2000-12-25"));
    assertEquals("2000-03-26 *********\n" +
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
                    "scale * 153.1800",
            (port1.plot(LocalDate.parse("2000-03-26"), LocalDate.parse("2001-04-20"))));

    port2.buyDate(mSFTStock, 10, LocalDate.parse("2000-01-01"));
    port2.buyDate(aAPLStock, 5, LocalDate.parse("2000-01-01"));
    //test less than 5 days
    assertEquals("2011-01-01 *************************************************\n" +
            "2011-01-02 *************************************************\n" +
            "2011-01-03 **************************************************\n" +
            "2011-01-04 ***************************************************\n" +
            "scale * 38.7465", port2.plot(LocalDate.parse("2011-01-01"),
            LocalDate.parse("2011-01-04")));

    // test 7 days
    assertEquals("2011-01-01 *************************************************\n" +
            "2011-01-02 *************************************************\n" +
            "2011-01-03 **************************************************\n" +
            "2011-01-04 **************************************************\n" +
            "2011-01-05 **************************************************\n" +
            "2011-01-06 **************************************************\n" +
            "2011-01-07 **************************************************\n" +
            "2011-01-08 **************************************************\n" +
            "2011-01-09 **************************************************\n" +
            "scale * 39.3320", port2.plot(LocalDate.parse("2011-01-01"),
            LocalDate.parse("2011-01-09")));
    //test 28 days
    assertEquals("2011-01-01 ***********************************************\n" +
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
            "scale * 40.5080", port2.plot(LocalDate.parse("2011-01-01"),
            LocalDate.parse("2011-02-04")));
    //test 60 days
    assertEquals("2011-01-01 **********************************************\n" +
            "2011-01-08 ************************************************\n" +
            "2011-01-15 **************************************************\n" +
            "2011-01-22 ***********************************************\n" +
            "2011-01-29 ************************************************\n" +
            "2011-02-05 *************************************************\n" +
            "2011-02-12 **************************************************\n" +
            "2011-02-19 **************************************************\n" +
            "2011-02-26 *************************************************\n" +
            "2011-03-04 **************************************************\n" +
            "scale * 41.1905", port2.plot(LocalDate.parse("2011-01-01"),
            LocalDate.parse("2011-03-04")));
    //test 135 days
    assertEquals("2011-01-01 **********************************************\n" +
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
            "scale * 41.1905", port2.plot(LocalDate.parse("2011-01-01"),
            LocalDate.parse("2011-04-28")));
    //tests 550
    assertEquals("2011-01-01 *****************************\n" +
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
            "scale * 67.4400", port2.plot(LocalDate.parse("2011-01-01"),
            LocalDate.parse("2012-07-04")));
    // tests 600
    assertEquals("2011-01-01 ***************************\n" +
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
            "scale * 72.3140", port2.plot(LocalDate.parse("2011-01-01"),
            LocalDate.parse("2012-08-23")));
    // test 800 days
    assertEquals("2011-01-01 *************************\n" +
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
            "scale * 76.2475", port2.plot(LocalDate.parse("2011-01-01"),
            LocalDate.parse("2013-03-11")));
    // tests 2000 days
    assertEquals("2011-01-01 ******************************\n" +
            "2012-01-01 ************************************\n" +
            "2012-12-31 ***********************************************\n" +
            "2013-12-31 **************************************************\n" +
            "2014-12-31 ****************\n" +
            "2015-12-31 ******************\n" +
            "2016-06-23 ****************\n" +
            "scale * 63.5840", port2.plot(LocalDate.parse("2011-01-01"),
            LocalDate.parse("2016-06-23")));
    // tests 5000 days
    assertEquals("2005-01-01 **********\n" +
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
            "scale * 62.9100", port2.plot(LocalDate.parse("2005-01-01"),
            LocalDate.parse("2018-09-10")));
    // tests 7300 days
    assertEquals("2003-01-01 **********\n" +
            "2005-12-31 ***********\n" +
            "2008-12-30 ***********\n" +
            "2011-12-30 **************************************\n" +
            "2014-12-29 ******************\n" +
            "2017-12-28 *****************************\n" +
            "2020-12-27 ************************************************\n" +
            "2022-12-27 **************************************************\n" +
            "scale * 60.3950", port2.plot(LocalDate.parse("2003-01-01"),
            LocalDate.parse("2022-12-27")));
  }

  @Test
  public void testTotalValue() {
    port1.buyDate(aAPLStock, 10, LocalDate.parse("2005-01-01"));
    assertEquals(0, port1.totalValue(LocalDate.parse("2004-01-01")), .01);
    assertEquals(644, port1.totalValue(LocalDate.parse("2005-01-01")), .01);
    assertEquals(2107.32, port1.totalValue(LocalDate.parse("2010-01-01")), .01);
    assertEquals(2936.5, port1.totalValue(LocalDate.parse("2020-01-01")), .01);
  }

  @Test
  public void totalValueRandom() {
    port1.buyDate(aAPLStock, 10, LocalDate.parse("2002-01-01"));
    port1.buyDate(aAPLStock, 10, LocalDate.parse("2012-01-01"));
    LocalDate time = LocalDate.parse("2002-01-01");
    assertEquals(0, port1.totalValue(time.plusDays(-1)), .01);
    for (int i = 0; i < 100; i += 6) {
      double price = 0;
      try {
        price += aAPLStock.getPrice(time) * 10;

      } catch (Exception e) {
        price += Double.parseDouble(e.getMessage().
                substring(e.getMessage().lastIndexOf(" ") + 1)) * 10;
      }
      assertEquals(price, port1.totalValue(time), .01);
      time = time.plusDays(6);
    }
    time = LocalDate.parse("2012-01-01");
    for (int i = 0; i < 100; i += 6) {
      double price = 0;
      try {
        price += aAPLStock.getPrice(time) * 20;

      } catch (Exception e) {
        price += Double.parseDouble(e.getMessage().
                substring(e.getMessage().lastIndexOf(" ") + 1)) * 20;
      }
      assertEquals(price, port1.totalValue(time), .01);
      time = time.plusDays(6);
    }
  }

  @Test
  public void getValue() {
    port1.buyDate(aAPLStock, 10, LocalDate.parse("2002-01-01"));
    port1.buyDate(aAPLStock, 10, LocalDate.parse("2012-01-01"));
    assertEquals(219, port1.getValue("AAPL", LocalDate.parse("2002-01-01")),
            .01);
    assertEquals(8100, port1.getValue("AAPL", LocalDate.parse("2012-01-01")),
            .01);
    assertEquals(3551.399, port1.getValue("AAPL", LocalDate.parse("2022-01-01")),
            .01);

  }

  @Test
  public void getAmount() {
    Map<String, Double> amounts = new HashMap<>();
    Map<String, Double> amountsFromGetAmount = new HashMap<>();

    port1.buyDate(aAPLStock, 15, LocalDate.parse("2000-01-01"));
    port1.buyDate(aAPLStock, 15, LocalDate.parse("2010-01-01"));
    port1.buyDate(aAPLStock, 15, LocalDate.parse("2020-01-01"));
    port1.buyDate(vStock, 15, LocalDate.parse("2020-01-01"));
    amounts.put("AAPL", 15.0);
    assertEquals(amounts, port1.getAmount(LocalDate.parse("2001-01-01")));
    amounts.put("AAPL", 30.0);
    assertEquals(amounts, port1.getAmount(LocalDate.parse("2011-01-01")));
    amounts.put("AAPL", 45.0);
    amounts.put("V", 15.0);
    assertEquals(amounts, port1.getAmount(LocalDate.parse("2021-01-01")));
  }

  @Test
  public void testComposition() {
    LocalDate inputDate = LocalDate.parse("2024-03-25");
    LocalDate randomDate = LocalDate.of(2021, 11, 2);
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers  Shares", port1.composition(inputDate));
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers  Shares", port1.composition(randomDate));
    port1.buyDate(mSFTStock, 10, LocalDate.parse("2024-03-25"));
    port1.buyDate(aAPLStock, 20, LocalDate.parse("2024-03-25"));
    port1.buyDate(vStock, 30, LocalDate.parse("2024-03-25"));
    // ensure there is something in the portfolio
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers   Shares\n" +
            "MSFT     10.0000\n" +
            "AAPL     20.0000\n" +
            "V        30.0000", port1.composition(LocalDate.parse("2024-03-25")));
    // check that before that date there is no composition
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers  Shares", port1.composition(LocalDate.parse("2024-03-24")));
    // check that selling the date results in the portfolio holding nothing.
    port1.sellDate(mSFTStock, 10, LocalDate.parse("2024-03-25"));
    port1.sellDate(aAPLStock, 20, LocalDate.parse("2024-03-25"));
    port1.sellDate(vStock, 30, LocalDate.parse("2024-03-25"));
    assertEquals("Portfolio is currently holding:\n" +
            "Tickers  Shares", port1.composition(inputDate));
  }

  @Test
  public void testDistribution() {
    // Setting up the dates
    LocalDate inputDate = LocalDate.parse("2024-03-25");
    LocalDate mockCurrentDate = LocalDate.now();

    // Assuming initial state: portfolio is empty
    assertEquals("Portfolio is currently holding:\n" +
                    "Tickers  Values",
            port1.distribution(inputDate));
    assertEquals("Portfolio is currently holding:\n" +
                    "Tickers  Values",
            port1.distribution(mockCurrentDate));

    // Adding stocks to the portfolio on the input date
    port1.buyDate(gOOGStock, 10, inputDate);
    port1.buyDate(cOSTStock, 10, inputDate);
    port1.buyDate(aAPLStock, 10, inputDate);
    port1.buyDate(vStock, 10, inputDate);

    // Example values for shares, replace with actual values if necessary
    String googValue = formatTickerAmount(gOOGStock, 10, inputDate);
    String costValue = formatTickerAmount(cOSTStock, 10, inputDate);
    String aaplValue = formatTickerAmount(aAPLStock, 10, inputDate);
    String vValue = formatTickerAmount(vStock, 10, inputDate);

    // Expected distribution format
    String expectedDistribution = String.format("Portfolio is currently holding:\n" +
            "Tickers     Values\n" +
            "GOOG     %s\n" +
            "COST     %s\n" +
            "AAPL     %s\n" +
            "V        %s", googValue, costValue, aaplValue, vValue);

    // Verifying distribution after buying stocks
    assertEquals(expectedDistribution, port1.distribution(inputDate));

    // Selling some stocks and verifying the distribution again
    port1.sellDate(gOOGStock, 5, inputDate);  // selling half of the GOOG shares

    googValue = formatTickerAmount(gOOGStock, 5, inputDate);  // updated value after selling

    // Updated expected distribution after selling some stocks
    String updatedDistribution = String.format("Portfolio is currently holding:\n" +
            "Tickers     Values\n" +
            "GOOG      %s\n" +
            "COST     %s\n" +
            "AAPL     %s\n" +
            "V        %s", googValue, costValue, aaplValue, vValue);

    assertEquals(updatedDistribution, port1.distribution(inputDate));

    // Selling off stocks
    port1.sellDate(gOOGStock, 5, inputDate);
    port1.sellDate(cOSTStock, 10, inputDate);
    port1.sellDate(aAPLStock, 10, inputDate);
    port1.sellDate(vStock, 10, inputDate);

    assertEquals("Portfolio is currently holding:\n" +
                    "Tickers  Values",
            port1.distribution(inputDate));
  }

  @Test
  public void testBalanceWithNullDate() {
    Map<Stock, Double> stocks = Map.of(OODStocks.getStockOrMakeNew("AAPL"), 100.0);
    double money = 10000.0;
    assertThrows(IllegalArgumentException.class, () -> {
      port1.balance(stocks, null, money);
    });
  }

  @Test
  public void testReBalanceWithNullDate() {
    Map<Stock, Double> stocks = Map.of(OODStocks.getStockOrMakeNew("GOOGL"), 100.0);
    assertThrows(IllegalArgumentException.class, () -> {
      port1.reBalance(stocks, null);
    });
  }

  @Test
  public void testBuyDateWithNullDate() {
    Stock stock = OODStocks.getStockOrMakeNew("MSFT");
    double amount = 10.0;
    assertThrows(IllegalArgumentException.class, () -> {
      port1.buyDate(stock, amount, null);
    });
  }

  @Test
  public void testSellDateWithNullDate() {
    Stock stock = OODStocks.getStockOrMakeNew("AAPL");
    double amount = 5.0;
    assertThrows(IllegalArgumentException.class, () -> {
      port1.sellDate(stock, amount, null);
    });
  }

  @Test
  public void testCompositionWithNullDate() {
    assertThrows(IllegalArgumentException.class, () -> {
      port1.composition(null);
    });
  }

  @Test
  public void testTotalValueWithNullDate() {
    assertThrows(IllegalArgumentException.class, () -> {
      port1.totalValue(null);
    });
  }

  @Test
  public void testPlotWithNullFromDate() {
    LocalDate toDate = LocalDate.of(2023, 12, 31);
    assertThrows(IllegalArgumentException.class, () -> {
      port1.plot(null, toDate);
    });
  }

  @Test
  public void testPlotWithNullToDate() {
    LocalDate fromDate = LocalDate.of(2023, 1, 1);
    assertThrows(IllegalArgumentException.class, () -> {
      port1.plot(fromDate, null);
    });
  }

  @Test
  public void testDistributionWithNullDate() {
    assertThrows(IllegalArgumentException.class, () -> {
      port1.distribution(null);
    });
  }

  // Helper method to get the formatted ticker amount
  private String formatTickerAmount(Stock stock, double shares, LocalDate date) {
    return String.format("%.4f", stock.getPrice(date) * shares);
  }


  /**
   * This class is used to create instances of {@code BetterPortfolioImpl} with modified
   * storages paths.
   */
  protected class MockBetterPortfolio extends BetterPortfolioImpl {
    /**
     * Creates a {@code BetterPortfolioImpl} with a modified storage path.
     *
     * @param file the file to load.
     */
    public MockBetterPortfolio(File file) {
      super(file);
      PORTFOLIOS_STORAGE_PATH = testPortfolioData;
    }

    /**
     * Creates a {@code BetterPortfolioImpl} with a modified storage path.
     *
     * @param portName the portfolios name.
     */
    public MockBetterPortfolio(String portName) {
      super(portName);
      PORTFOLIOS_STORAGE_PATH = testPortfolioData;
    }
  }
}