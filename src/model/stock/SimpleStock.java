package model.stock;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import model.FormattingUtils;
import model.OODStocks;

/**
 * This {@code SimpleStock} object is a simple implementation.
 * Of the {@code SimpleStockModel.Stock} interface.
 * It is used to store the stock information of a given stock.
 * This implementation relies on the  OODStocks class to manage the stocks that have been created.
 * The OODStocks class used is a singleton class that manages the stocks that have been created.
 * It is used to ensure that multiple instances of the same stock are not created.
 * It also handles API calls to get stock information and stores the information in a file.
 * Stock creation is reliant on the downloaded info in the StockInfo folder, WHICH MUST
 * BE IN THIS DIRECTORY.
 */
public class SimpleStock implements Stock {
  public final StringBuilder stockInfo;
  private final String symbol;
  private final LocalDate recentDate; // last day of data
  private final LocalDate IPODate; // earliest day of data


  /**
   * Constructs a {@code SimpleStock} object, if the stock has not already been created.
   * By using {@code OODStocks} to manage already made stocks, we are able to ensure
   * that multiple of the same stock are not made. When this constructor is called
   * and a {@code SimpleStock} is successfully returned it, is automatically added
   * to the {@code stocks} static field in {@code OODStocks}.
   *
   * @param tckr the ticker symbol of the stock, used to identify the stock
   * @throws StockAlreadyExistsException if the stock has already been created, and is stored in
   *                                     the {@code OODStocks} singleton class.
   */
  public SimpleStock(String tckr) throws StockAlreadyExistsException {
    symbol = tckr;
    try {
      OODStocks.getStock(tckr);
      throw new StockAlreadyExistsException("stock already exists");
    } catch (IllegalArgumentException e) { // no stock exists
      try {
        // creating path
        Path filePath = Paths.get(OODStocks.DIRECTORYPATH + tckr + ".txt");
        // reading file from given path
        this.stockInfo = new StringBuilder(Files.readString(filePath));
        // printing the content
        recentDate = LocalDate.parse(stockInfo.substring(38, 48));
        IPODate = LocalDate.parse(stockInfo.substring(stockInfo.lastIndexOf("-") - 7,
                stockInfo.lastIndexOf("-") + 3));
        OODStocks.putStock(this);
      } catch (IOException f) {
        throw new RuntimeException("Stock does not in the database");
      }
    }
  }

  @Override
  public double gainLoss(LocalDate fromDate, LocalDate toDate) throws IllegalArgumentException {
    if (fromDate.isAfter(toDate)) {
      throw new IllegalArgumentException("start date is after end date");
    }

    if (fromDate.isBefore(IPODate)) {
      throw new IllegalArgumentException("Date to early");
    }
    if (toDate.isAfter(recentDate)) {
      throw new IllegalArgumentException("Date is in the future");
    }
    while (stockInfo.indexOf(fromDate.toString()) == -1) {
      fromDate = fromDate.plusDays(-1);
      if (fromDate.isBefore(IPODate)) {
        throw new IllegalArgumentException("Cannot find data for given date");
      }
    }
    while (stockInfo.indexOf(toDate.toString()) == -1) {
      toDate = toDate.plusDays(-1);
    }

    return getPrice(toDate) - getPrice(fromDate);
  }

  @Override
  public double xDayAvg(LocalDate startDate, int days) throws DateTimeException {
    if (startDate.isAfter(recentDate) || startDate.isBefore(IPODate)) {
      throw new IllegalArgumentException("Cannot find data for given date");
    }
    double cost = 0;
    int amtDays = 0;
    int index = stockInfo.indexOf(startDate.toString());
    while (index == -1) {
      startDate = startDate.plusDays(-1);
      index = stockInfo.indexOf(startDate.toString());
      if (startDate.isBefore(IPODate)) {
        throw new IllegalArgumentException("Cannot find data for given date");
      }


    }

    String firstPrice = stockInfo.substring(stockInfo.indexOf(startDate.toString()));
    for (int i = 0; i < days; ++i) {
      if (!firstPrice.contains("\n")) {
        throw new IllegalArgumentException(
                String.format("running average is %.4f and over the amount of days %d",
                        cost / amtDays, amtDays));
      }
      String temp = firstPrice.substring(0, firstPrice.indexOf("\n"));
      cost += Double.parseDouble(temp.split(",")[4]);
      amtDays++;
      firstPrice = firstPrice.substring(firstPrice.indexOf("\n") + 1);
    }


    return cost / amtDays;
  }

  @Override
  public HashSet<LocalDate> defDayCrossover(LocalDate fromDate, LocalDate toDate)
          throws IllegalArgumentException {
    return this.xDayCrossover(fromDate, toDate, 30);

  }

  @Override
  public HashSet<LocalDate> xDayCrossover(LocalDate fromDate, LocalDate toDate, int days) {
    HashSet<LocalDate> toRet = new HashSet<>();
    if (fromDate.isAfter(toDate)) {
      throw new IllegalArgumentException("start date is after end date");
    }

    if (fromDate.isBefore(IPODate)) {
      throw new IllegalArgumentException("Date to early");
    }
    if (toDate.isAfter(recentDate)) {
      throw new IllegalArgumentException("Date is in the future");
    }
    while (stockInfo.indexOf(fromDate.toString()) == -1) {
      fromDate = fromDate.plusDays(-1);
      if (fromDate.isBefore(IPODate)) {
        throw new IllegalArgumentException("Cannot find data for given date");
      }
    }
    while (stockInfo.indexOf(toDate.toString()) == -1) {
      toDate = toDate.plusDays(-1);
    }
    while (fromDate.plusDays(-1).isBefore(toDate)) {
      if (stockInfo.indexOf(toDate.toString()) != -1) {
        if (getPrice(toDate) > xDayAvg(toDate, days)) {
          toRet.add(toDate);
        }
      }
      toDate = toDate.plusDays(-1);
    }
    return toRet;
  }

  @Override
  public String getTicker() {
    return this.symbol;
  }

  @Override
  public double getPrice() {
    return getPrice(recentDate);
  }

  @Override
  public double getPrice(LocalDate date) throws DateTimeException {
    if (date.isAfter(recentDate) || date.isBefore(IPODate)) {
      throw new IllegalArgumentException("Cannot find data for given date");
    }
    if (stockInfo.indexOf(date.toString()) == -1) {
      while (stockInfo.indexOf(date.toString()) == -1) {
        date = date.plusDays(-1);
      }
      String dateStr = stockInfo.substring(stockInfo.indexOf(date.toString()));
      dateStr = dateStr.substring(0, dateStr.indexOf("\n"));
      dateStr = dateStr.split(",")[4];

      throw new IllegalArgumentException(
              String.format("Market was closed at requested date, the last closing price was %.4f",
                      Double.parseDouble(dateStr)));
    }
    if (stockInfo.indexOf(date.toString()) == -1) {
      throw new IllegalArgumentException("invalid date");
    }
    String dateStr = stockInfo.substring(stockInfo.indexOf(date.toString()));
    dateStr = dateStr.substring(0, dateStr.indexOf("\n"));
    dateStr = dateStr.split(",")[4];

    return Double.parseDouble(dateStr);
  }

  @Override
  public LocalDate getRecentDate() {
    return LocalDate.parse(this.recentDate.toString());
  }

  @Override
  public LocalDate getIPODate() {
    return LocalDate.parse(this.IPODate.toString());
  }

  /**
   * Allows access to a copy of the raw stock info which is stored as a String
   * (see the {@code SimpleStock} description for more info).
   *
   * @return the raw stock information as {@code String}.
   */
  @Override
  public String toString() {
    return stockInfo.toString();
  }

  @Override
  public double fetchClosestStockPrice(LocalDate dateAt) {
    try {
      return this.getPrice(dateAt);
    } catch (IllegalArgumentException e) {
      if (!e.getMessage().contains("Market was closed at requested date")) {
        throw new IllegalArgumentException("Date was out of bounds for this stock data.");
      }
      return Double.parseDouble(e.getMessage().substring(e.getMessage().lastIndexOf(" ")));
    }
  }

  @Override
  public String plot(LocalDate from, LocalDate to) {
    FormattingUtils plotting = new FormattingUtils();
    int interval = plotting.interval(from, to);
    // Validate date range
    Map<LocalDate, Double> valuesOverTime = new LinkedHashMap<>();
    LocalDate active = from;
    double max = Integer.MIN_VALUE;
    double value;
    while (active.isBefore(to)) {
      value = this.fetchClosestStockPrice(active);
      valuesOverTime.put(active, value);
      active = active.plusDays(interval);
      if (value > max) {
        max = value;
      }
    }
    value = fetchClosestStockPrice(to);
    if (value > max) {
      max = value;
    }
    valuesOverTime.put(to, value);

    return plotting.barGraph(valuesOverTime, max);
  }
}
