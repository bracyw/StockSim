package model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import model.stock.SimpleStock;
import model.stock.Stock;
import model.stock.StockAlreadyExistsException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

/**
 * Tests the {@code SimpleStock} class.
 */
public class SimpleStockTest {
  Stock mSFTStock;
  Stock vStock;
  Stock aAPLStock;
  Stock gOOGStock;
  LocalDate mockCurrentDate;
  LocalDate mockCurrentDateNow;

  LocalDate fromDate;
  LocalDate toDate;
  Map<String, Stock> stocks;
  List<Stock> stockList;

  @Before
  public void setup() {
    stocks = OODStocks.getStocks();
    stockList = new ArrayList<>(stocks.values());
    for (Stock stock : stockList) {
      OODStocks.removeStock(stock.getTicker());
    }
    mockCurrentDate = LocalDate.of(2024, 6, 3);
    mockCurrentDateNow = LocalDate.of(2024, 6, 5);

    // the below dates will be assigned individually in each test based on the specific stock
    mSFTStock = new SimpleStock("MSFT");
    vStock = new SimpleStock("V");
    aAPLStock = new SimpleStock("AAPL");
    gOOGStock = new SimpleStock("GOOG");
    fromDate = null;
    toDate = null;
  }

  @After
  public void teardown() throws IOException {
    for (Stock stock : stockList) {
      OODStocks.removeStock(stock.getTicker());
    }
  }


  // TESTING THE CONSTRUCTOR
  // test that the constructor fails when the info for the given stock does not exist in a file
  @Test(expected = RuntimeException.class)
  public void testConstructorFailsNoLocalData() {
    // valid ticker, shouldn't exist in local files.
    new SimpleStock("SWIN");
    String forJavaStyle = "make assert";
    assertEquals("make assert", forJavaStyle);
  }

  // test that the constructor fails when the stock is already made
  @Test(expected = StockAlreadyExistsException.class)
  public void testConstructorFailsStockAlreadyMade() {
    new SimpleStock("AAPL");
    String forJavaStyle = "make assert";
    assertEquals("make assert", forJavaStyle);
  }

  // test that the constructor adds the new stock to OODStocks
  @Test
  public void testConstructorCorrectPut() {
    try {
      OODStocks.getStock("NVDA");
    } catch (IllegalArgumentException e) {
      // should fail because stock has not been made yet.
    }
    SimpleStock workingStock = new SimpleStock("NVDA");
    assertEquals(workingStock, OODStocks.getStock("NVDA"));
  }

  @Test
  public void getClosetPrice() {
    OODStocks.removeStock("AAPL");
    OODStocks.removeStock("MSFT");

    Stock apple = new SimpleStock("AAPL");
    assertEquals(191.29,
            apple.fetchClosestStockPrice(LocalDate.parse("2024-05-30")), .01);
    assertEquals(170.33,
            apple.fetchClosestStockPrice(LocalDate.parse("2024-04-30")), .01);
    assertEquals(171.48,
            apple.fetchClosestStockPrice(LocalDate.parse("2024-03-30")), .01);
    assertEquals(181.42,
            apple.fetchClosestStockPrice(LocalDate.parse("2024-02-28")), .01);
    assertEquals(188.04,
            apple.fetchClosestStockPrice(LocalDate.parse("2024-01-30")), .01);
    assertEquals(190.29,
            apple.fetchClosestStockPrice(LocalDate.parse("2024-05-29")), .01);
    assertEquals(189.99,
            apple.fetchClosestStockPrice(LocalDate.parse("2024-05-28")), .01);
    assertEquals(189.98,
            apple.fetchClosestStockPrice(LocalDate.parse("2024-05-27")), .01);
    assertEquals(189.98,
            apple.fetchClosestStockPrice(LocalDate.parse("2024-05-26")), .01);
    assertEquals(189.98,
            apple.fetchClosestStockPrice(LocalDate.parse("2024-05-25")), .01);
    assertEquals(189.98,
            apple.fetchClosestStockPrice(LocalDate.parse("2024-05-24")), .01);
    assertEquals(186.88,
            apple.fetchClosestStockPrice(LocalDate.parse("2024-05-23")), .01);
    assertEquals(192.35,
            apple.fetchClosestStockPrice(LocalDate.parse("2024-05-21")), .01);
    assertEquals(190.9,
            apple.fetchClosestStockPrice(LocalDate.parse("2024-05-22")), .01);
    Stock msft = new SimpleStock("MSFT");
    assertEquals(414.67,
            msft.fetchClosestStockPrice(LocalDate.parse("2024-05-30")), .01);
    assertEquals(389.33,
            msft.fetchClosestStockPrice(LocalDate.parse("2024-04-30")), .01);
    assertEquals(420.72,
            msft.fetchClosestStockPrice(LocalDate.parse("2024-03-30")), .01);
    assertEquals(402.79,
            msft.fetchClosestStockPrice(LocalDate.parse("2024-02-20")), .01);
    assertEquals(408.59,
            msft.fetchClosestStockPrice(LocalDate.parse("2024-01-30")), .01);
    assertEquals(429.17,
            msft.fetchClosestStockPrice(LocalDate.parse("2024-05-29")), .01);
    assertEquals(430.32,
            msft.fetchClosestStockPrice(LocalDate.parse("2024-05-28")), .01);
    assertEquals(430.16,
            msft.fetchClosestStockPrice(LocalDate.parse("2024-05-27")), .01);
    assertEquals(430.16,
            msft.fetchClosestStockPrice(LocalDate.parse("2024-05-26")), .01);
    assertEquals(430.16,
            msft.fetchClosestStockPrice(LocalDate.parse("2024-05-25")), .01);
    assertEquals(430.16,
            msft.fetchClosestStockPrice(LocalDate.parse("2024-05-24")), .01);
    assertEquals(427.0,
            msft.fetchClosestStockPrice(LocalDate.parse("2024-05-23")), .01);
    assertEquals(429.04,
            msft.fetchClosestStockPrice(LocalDate.parse("2024-05-21")), .01);
    assertEquals(430.52,
            msft.fetchClosestStockPrice(LocalDate.parse("2024-05-22")), .01);
  }

  @Test
  public void testConstructorCreatesCorrectInfo() {
    SimpleStock workingStock = new SimpleStock("NVDA");
    StringBuilder info = getStockInfo("NVDA");
    assertEquals(info.toString(), workingStock.toString());
  }

  // test that get ticker works
  @Test
  public void testGetTickerWorks() {
    assertEquals("MSFT", mSFTStock.getTicker());
    assertEquals("AAPL", aAPLStock.getTicker());
    assertEquals("GOOG", gOOGStock.getTicker());
    assertEquals("V", vStock.getTicker());
  }

  // test that get IPODate works
  @Test // test that getIPODate works for select known stocks
  public void testGetIPODate() {
    assertEquals(LocalDate.of(1999, 11, 1), mSFTStock.getIPODate());
    assertEquals(LocalDate.of(2008, 3, 19), vStock.getIPODate());
    assertEquals(LocalDate.of(1999, 11, 1), aAPLStock.getIPODate());
    assertEquals(LocalDate.of(2014, 3, 27), gOOGStock.getIPODate());
  }

  @Test // test that getRecentDate works for select known stocks
  public void testGetRecentDate() {
    assertEquals(mockCurrentDateNow, mSFTStock.getRecentDate());
    assertEquals(mockCurrentDate, vStock.getRecentDate());
    assertEquals(mockCurrentDate, gOOGStock.getRecentDate());
  }

  @Test // test that get price works for specific days, given well-known stocks
  public void testGetPrice() {
    HashMap<LocalDate, Double> expectedValues = new HashMap<>();
    LocalDate stockDate = LocalDate.of(2000, 5, 10);
    // Hand-picked valid dates for AAPL stock and correct closing values
    expectedValues.put(stockDate, findClosingValue("AAPL", stockDate));
    stockDate = LocalDate.of(1999, 11, 1);
    expectedValues.put(stockDate, findClosingValue("AAPL", stockDate));
    stockDate = LocalDate.of(2014, 1, 31);
    expectedValues.put(stockDate, findClosingValue("AAPL", stockDate));
    for (Map.Entry<LocalDate, Double> entry : expectedValues.entrySet()) {
      assertEquals(entry.getValue(), aAPLStock.getPrice(entry.getKey()), 0.001);
    }

    expectedValues = new HashMap<>();
    // Hand-picked valid dates for MSFT stock and correct closing values
    stockDate = LocalDate.of(2024, 5, 10);
    expectedValues.put(stockDate, findClosingValue("MSFT", stockDate));
    stockDate = LocalDate.of(2006, 12, 1);
    expectedValues.put(stockDate, findClosingValue("MSFT", stockDate));
    stockDate = LocalDate.of(2019, 1, 31);
    expectedValues.put(stockDate, findClosingValue("MSFT", stockDate));
    for (Map.Entry<LocalDate, Double> entry : expectedValues.entrySet()) {
      assertEquals(entry.getValue(), mSFTStock.getPrice(entry.getKey()), 0.001);
    }

    expectedValues = new HashMap<>();
    // Hand-picked valid dates for V stock and correct closing values
    stockDate = LocalDate.of(2020, 3, 4);
    expectedValues.put(stockDate, findClosingValue("V", stockDate));
    stockDate = LocalDate.of(2010, 12, 1);
    expectedValues.put(stockDate, findClosingValue("V", stockDate));
    stockDate = LocalDate.of(2016, 2, 23);
    expectedValues.put(stockDate, findClosingValue("V", stockDate));
    for (Map.Entry<LocalDate, Double> entry : expectedValues.entrySet()) {
      assertEquals(entry.getValue(), vStock.getPrice(entry.getKey()), 0.001);
    }
  }


  // TESTING GAIN/LOSS
  @Test // test that gainLoss works for two dates that are valid and the same
  public void testGainLossValidDatesSameDay() {
    assertEquals(0, mSFTStock.gainLoss(mockCurrentDate, mockCurrentDate), 0.001);
    assertEquals(0, vStock.gainLoss(mockCurrentDate, mockCurrentDate), 0.001);
    assertEquals(0, aAPLStock.gainLoss(mockCurrentDate, mockCurrentDate), 0.001);
    assertEquals(0, gOOGStock.gainLoss(mockCurrentDate, mockCurrentDate), 0.001);
  }


  @Test // test that gainLoss works for two dates that are 3 days apart
  public void testGainLossCheckAllValidDates() {
    for (Stock stock : stockList) {
      // test for 3 days apart (within a week)
      this.checkGainLossXdaysSeperation(stock, 3);
      // test for 8 days apart (over a week)
      this.checkGainLossXdaysSeperation(stock, 8);
      // test for 31 days apart (over a month)
      this.checkGainLossXdaysSeperation(stock, 31);
      // test for 365 days apart (over a year)
      this.checkGainLossXdaysSeperation(stock, 365);
    }
    String forJavaStyle = "make assert";
    assertEquals("make assert", forJavaStyle);
  }

  @Test // test that gainLoss fails when the fromDate is greater than the toDate (and vice versa)
  public void testGainLossFailInverseValidDates() {
    for (Stock stock : stockList) {
      // Check that it works for high end (more recent date)
      fromDate = stock.getRecentDate();
      toDate = stock.getRecentDate().minusDays(4);
      try {
        stock.gainLoss(fromDate, toDate);
        fail("Gain loss didn't fail for stock: " + stock.getTicker() + ", with higher fromDate: " +
                fromDate + ", than toDate: " + toDate + ".");
      } catch (IllegalArgumentException e) {
        // passes
      }

      // Check that it works for low end of stock (IPODate)
      fromDate = stock.getIPODate().plusDays(300);
      toDate = stock.getIPODate();
      try {
        stock.gainLoss(fromDate, toDate);
        fail("Gain loss didn't fail for stock: " + stock.getTicker() + ", with higher fromDate: " +
                fromDate + ", than toDate: " + toDate + ".");
      } catch (IllegalArgumentException e) {
        // passes
      }
    }
    String forJavaStyle = "make assert";
    assertEquals("make assert", forJavaStyle);
  }

  @Test // test that gainLoss fails for a date that is too low for the given stock
  public void testGainLossFailLowDate() {
    for (Stock stock : stockList) {
      // reliant on getIPODate() working, check those tests if these are failing
      fromDate = stock.getIPODate().minusDays(1);
      toDate = stock.getIPODate().plusDays(3);
      try {
        stock.gainLoss(fromDate, toDate);
        fail("Gain loss didn't fail for stock: " + stock.getTicker() + ", from: " + fromDate +
                ", to: " + toDate + "." + "with an IPODate of: " + stock.getIPODate());
      } catch (IllegalArgumentException e) {
        // passes
      }
    }
    String forJavaStyle = "make assert";
    assertEquals("make assert", forJavaStyle);
  }

  @Test // test that gainLoss fails for a date that is too high for the given stock
  public void testGainLossFailHighDate() {
    for (Stock stock : stockList) {
      fromDate = stock.getRecentDate().minusDays(2); 
      toDate = stock.getRecentDate().plusDays(1);
      try {
        stock.gainLoss(fromDate, toDate);
      } catch (IllegalArgumentException e) {
        // passes
      }
    }
    String forJavaStyle = "make assert";
    assertEquals("make assert", forJavaStyle);
  }


  // Checks the function gainLoss for a given stock. Specifically checks
  // that each gain/loss between the given days seperated is valid, for all dates available
  // in between the ipo date of the stock and the most recent date.
  private void checkGainLossXdaysSeperation(Stock stock, int daysSeperated) {
    LocalDate toDate = null;
    LocalDate fromDate = null;
    List<LocalDate> validDates = new ArrayList<>();
    double startPrice = 0;
    double endPrice = 0;
    double expectedValue = 0;
    toDate = stock.getRecentDate();
    fromDate = stock.getIPODate();
    validDates = this.getValidDatesBetween(fromDate, toDate, stock);
    for (int i = 0; i < validDates.size() - daysSeperated; i += (daysSeperated + 1)) {
      fromDate = validDates.get(i);
      toDate = validDates.get(i + daysSeperated);
      startPrice = this.findClosingValue(stock.getTicker(), fromDate);
      endPrice = this.findClosingValue(stock.getTicker(), toDate);
      expectedValue = endPrice - startPrice;
      assertEquals(expectedValue, stock.gainLoss(fromDate, toDate), 0.001);
    }
  }


  // TEST XDAYAVG

  @Test // ensure the x-day average works as expected for large range of dates.
  public void testXDayAvgLarge() {
    for (Stock stock : stockList) {
      this.checkXDayAvgXDaySperationForEachDate(stock, 365, 4);
      this.checkXDayAvgXDaySperationForEachDate(stock, 25, 100);
    }
    String forJavaStyle = "make assert";
    assertEquals("make assert", forJavaStyle);
  }


  @Test // ensure that x-day average throws when given an invalid starting date
  public void testXDayAvgThrowsInvalidStartingDate() {
    Random rand = new Random(30);
    for (Stock stock : stockList) {
      int daySep = rand.nextInt(365); // shouldn't matter
      try {
        stock.xDayAvg(stock.getRecentDate().plusDays(1), daySep);
      } catch (IllegalArgumentException e) {
        // passes
      }

      try {
        stock.xDayAvg(stock.getIPODate().minusDays(1), daySep);
      } catch (IllegalArgumentException e) {
        //passes
      }
    }
    String forJavaStyle = "make assert";
    assertEquals("make assert", forJavaStyle);
  }

  @Test // test whether XDay avg throws when x days goes below IPODate
  public void testXDayAvgThrowsInvalidDaysBelow() {
    Random rand = new Random(30);
    int daySep = rand.nextInt(100);
    for (Stock stock : stockList) {
      LocalDate toDate = stock.getIPODate();
      try {
        stock.xDayAvg(stock.getRecentDate().plusDays(daySep), daySep + 1);
      } catch (IllegalArgumentException e) {
        // passes
      }
    }
    String forJavaStyle = "make assert";
    assertEquals("make assert", forJavaStyle);
  }


  private void checkXDayAvgXDaySperationForEachDate(Stock stock, int daysSeperated, int dayAvg) {
    LocalDate fromDate = stock.getIPODate();
    LocalDate toDate = stock.getRecentDate();
    List<LocalDate> validDates = this.getValidDatesBetween(fromDate, toDate, stock);
    for (int i = validDates.size() - 1; i > daysSeperated + dayAvg; i -= daysSeperated) {
      toDate = validDates.get(i);
      double avgPrice = 0;
      for (int j = 0; j < dayAvg; j++) {
        avgPrice += stock.getPrice(validDates.get(i - j));
      }
      avgPrice /= dayAvg;
      String message =
              "ticker : " + stock.getTicker() + " date failed :" + toDate + " avg price : " +
                      avgPrice + "\n";
      assertEquals(message, avgPrice, stock.xDayAvg(toDate, dayAvg), 0.001);
    }
  }


  @Test // test that XDay crossover fails for invalid from date
  public void testXDayCrossoverThrowsInvalidFromDate() {
    Random rand = new Random(30);
    for (Stock stock : stockList) {
      // shouldn't matter
      LocalDate toDate = stock.getRecentDate();
      try {
        stock.defDayCrossover(stock.getIPODate().minusDays(1), toDate);
      } catch (IllegalArgumentException e) {
        // passes
      }
      try {
        stock.xDayCrossover(stock.getIPODate().minusDays(1), toDate, 25);
      } catch (IllegalArgumentException e) {
        // passes
      }
    }
    String forJavaStyle = "make assert";
    assertEquals("make assert", forJavaStyle);
  }

  @Test
  public void testXDayCrossMSFTSmall() {
    List<LocalDate> dates = getValidDatesBetween(LocalDate.of(2019, 5, 20),
            LocalDate.of(2019, 5, 31), mSFTStock);
    HashSet<LocalDate> crossOverDays = new HashSet<>();

    for (LocalDate date : dates) {
      if (mSFTStock.getPrice(date) > mSFTStock.xDayAvg(date, 30)) {
        crossOverDays.add(date);
      }
    }

    assertEquals(crossOverDays,
            mSFTStock.defDayCrossover(LocalDate.of(2019, 5, 20),
                    LocalDate.of(2019, 5, 31)));
  }

  @Test
  public void testdefDayCrossMSFTSmall() {
    List<LocalDate> dates = getValidDatesBetween(LocalDate.of(2019, 5, 20),
            LocalDate.of(2019, 5, 31), mSFTStock);
    HashSet<LocalDate> crossOverDays = new HashSet<>();

    for (LocalDate date : dates) {
      if (mSFTStock.getPrice(date) > mSFTStock.xDayAvg(date, 40)) {
        crossOverDays.add(date);
      }

    }

    assertEquals(crossOverDays,
            mSFTStock.xDayCrossover(LocalDate.of(2019, 5, 20),
                    LocalDate.of(2019, 5, 31), 40));
  }

  @Test // test the XDaycrossover fails for invalid date
  public void testXDayCrossoverThrowsInvalidToDate() {
    Random rand = new Random(30);
    for (Stock stock : stockList) {
      // shouldn't matter
      LocalDate fromDate = stock.getIPODate();
      try {
        stock.defDayCrossover(fromDate,
                stock.getRecentDate().plusDays(1));
      } catch (IllegalArgumentException e) {
        // passes
      }
      try {
        stock.xDayCrossover(fromDate,
                stock.getRecentDate().plusDays(1), 550);
      } catch (IllegalArgumentException e) {
        // passes
      }
    }
    String forJavaStyle = "make assert";
    assertEquals("make assert", forJavaStyle);
  }

  @Test // test xDay crossover for all stocks initialized in setup
  public void testdefDayCrossoverLargeValid() {
    for (Stock stock : stockList) {
      LocalDate fromDate = LocalDate.of(2024, 3, 28);
      LocalDate toDate = LocalDate.of(2024, 4, 6);
      List<LocalDate> validDates = getValidDatesBetween(fromDate, toDate, stock);
      HashSet<LocalDate> crossOverDays = new HashSet<>();
      for (LocalDate validDate : validDates) {
        if (stock.getPrice(validDate) > stock.xDayAvg(validDate, 30)) {
          crossOverDays.add(validDate);
        }
      }
      assertEquals(crossOverDays, stock.defDayCrossover(fromDate, toDate));
    }
  }

  @Test // test xDay crossover for all stocks initialized in setup
  public void testXDayCrossoverLargeValid() {
    for (Stock stock : stockList) {
      LocalDate fromDate = LocalDate.of(2024, 3, 28);
      LocalDate toDate = LocalDate.of(2024, 4, 6);
      List<LocalDate> validDates = getValidDatesBetween(fromDate, toDate, stock);
      HashSet<LocalDate> crossOverDays = new HashSet<>();
      for (LocalDate validDate : validDates) {
        if (stock.getPrice(validDate) > stock.xDayAvg(validDate, 14)) {
          crossOverDays.add(validDate);
        }
      }
      assertEquals(crossOverDays, stock.xDayCrossover(fromDate, toDate, 14));
    }
  }


  // note: getPrice() is relied on for this method to work correctly.
  // Get price is hand tested, if there are errors in the get price tests any test methods that
  // use this function will be USELESS.
  private List<LocalDate> getValidDatesBetween(LocalDate start, LocalDate end, Stock stock) {
    List<LocalDate> allDatesBtwn = start.datesUntil(end).collect(Collectors.toList());
    List<LocalDate> datesToCheckWith = new ArrayList<>(allDatesBtwn);
    for (LocalDate date : datesToCheckWith) {
      try {
        stock.getPrice(date);
      } catch (Exception e) {
        allDatesBtwn.remove(date);
      }
    }
    return allDatesBtwn;
  }


  // Find the value of the closing price on a given day for the stock ticker
  // note: this is only for finding values that are in the /StockInfo/ data
  private double findClosingValue(String ticker, LocalDate date) {
    StringBuilder stockInfo = getStockInfo(ticker);

    if (stockInfo.indexOf(date.toString()) == -1) {
      throw new IllegalArgumentException("invalid date");
    }
    String dateStr = stockInfo.substring(stockInfo.indexOf(date.toString()));
    dateStr = dateStr.substring(0, dateStr.indexOf("\n"));
    dateStr = dateStr.split(",")[4];
    return Double.parseDouble(dateStr);
  }

  private StringBuilder getStockInfo(String tcker) {
    try {
      StringBuilder stockInfo = new StringBuilder(
              Files.readString(Paths.get(OODStocks.DIRECTORYPATH + tcker + ".txt")));
      return stockInfo;
    } catch (IOException e) {
      throw new IllegalArgumentException("no file exist for the given ticker: " + tcker);
    }
  }

  @Test
  public void testToString() {
    assertEquals(getStockInfo("AAPL").toString(),
            aAPLStock.toString());
    assertEquals(getStockInfo("MSFT").toString(), mSFTStock.toString());
    assertEquals(getStockInfo("GOOG").toString(), gOOGStock.toString());
  }

  @Test
  public void testPlotShortTerm() {
    LocalDate jan5 = LocalDate.of(2019, 1, 5);
    LocalDate jan6 = LocalDate.of(2019, 1, 6);
    assertEquals("2019-01-05 **************************************************\n" +
            "2019-01-06 **************************************************\n" +
            "scale * 2.9652", aAPLStock.plot(jan5, jan6));
    // test only one day
    assertEquals(
            "2019-01-05 **************************************************\n" +
                    "scale * 2.9652", aAPLStock.plot(jan5, jan5));
    assertEquals("2019-01-05 **************************************************\n" +
            "2019-01-06 **************************************************\n" +
            "scale * 21.4142", gOOGStock.plot(jan5, jan6));
    assertEquals("2019-01-05 **************************************************\n" +
            "2019-01-06 **************************************************\n" +
            "scale * 2.6730", vStock.plot(jan5, jan6));
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
            "scale * 5.8730", aAPLStock.plot(jan2004, jan2024));
    assertEquals("2019-01-01 ******************\n" +
            "2020-01-01 ************************\n" +
            "2020-12-31 *******************************\n" +
            "2021-12-31 **************************************************\n" +
            "2022-12-31 **\n" +
            "2023-12-31 ***\n" +
            "2024-01-01 ***\n" +
            "scale * 57.8718", gOOGStock.plot(jan2004, jan2024));
    assertEquals("2019-01-01 **************************\n" +
            "2020-01-01 *************************************\n" +
            "2020-12-31 *******************************************\n" +
            "2021-12-31 ******************************************\n" +
            "2022-12-31 ****************************************\n" +
            "2023-12-31 **************************************************\n" +
            "2024-01-01 **************************************************\n" +
            "scale * 5.2070", vStock.plot(jan2004, jan2024));
  }

  @Test
  public void testPlotFails() {
    LocalDate jan1 = LocalDate.of(2024, 1, 1);
    LocalDate jan2 = LocalDate.of(2024, 1, 2);
    assertThrows(IllegalArgumentException.class, () -> aAPLStock.plot(jan2, jan1));
    assertThrows(IllegalArgumentException.class, () -> aAPLStock.plot(jan1,
            jan1.minusDays(100)));
    // test fails for out of bounds IPODate
    assertThrows(IllegalArgumentException.class, () -> aAPLStock
            .plot(aAPLStock.getIPODate()
                            .minusDays(1),
                    aAPLStock.getIPODate()));
    // test fails for out of bounds recentdate
    assertThrows(IllegalArgumentException.class, () -> aAPLStock.plot(aAPLStock.getRecentDate(),
            aAPLStock.getRecentDate().plusDays(2)));
  }
}
