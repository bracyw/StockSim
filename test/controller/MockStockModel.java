package controller;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.StockModel;

/**
 * This {@code MockStockModel} is used for testing the controller, and keeps track of what
 * inputs it receives to each function. Still maintains concept of keeping track of a portfolio
 * by having a boolean flag to simulate it.
 *
 * <p>All methods that output doubles will always return a unique value (in relation to the
 * other methods):
 * <ul>
 *   <li>{@code getXDaysAverage} - returns 0</li>
 *   <li>{@code gainLoss} - returns 1</li>
 *   <li>{@code getPrice} - returns 2</li>
 *   <li>{@code buy} - returns 3</li>
 *   <li>{@code sell} - returns 4</li>
 *   <li>{@code totalValue} - returns 5</li>
 * </ul>
 *
 * <p>All methods that output strings will always return their name as a string
 * or null if appropriate (based on portfolio flag):
 * <ul>
 *   <li>{@code createPortfolio} - returns "createPortfolio"</li>
 *   <li>{@code setPortfolio} - returns "setPortfolio"</li>
 *   <li>{@code getPortfolio} - returns "getPortfolio" || null</li>
 *   <li>{@code downloadStock} - returns "downloadStock"</li>
 * </ul>
 */
public class MockStockModel implements StockModel {
  protected List<String> log;
  protected boolean portfolioExist;
  protected boolean hasOtherPortfolios;

  /**
   * Constructs a {@code MockStockModel} object with a log to keep track of methods called.
   * Also takes in a in boolean to signal whether there is a portfolio (useful for testing
   * process command).
   *
   * @param log            the log, which will be mutated.
   * @param portfolioExist a boolean flag to signal whether this model has a portfolio.
   */
  public MockStockModel(List<String> log, boolean portfolioExist,
                        boolean hasOtherPortfolios) {
    this.log = log;
    this.portfolioExist = portfolioExist;
    this.hasOtherPortfolios = hasOtherPortfolios;
  }


  @Override
  public double getXDaysAverage(String ticker, LocalDate to, int xDays) {
    log.add("getXDaysAverage called with, ticker:" + ticker + ", to date: " + to.toString() +
            ", xDays:" + xDays);
    return 0;
  }

  @Override
  public Set<LocalDate> getDefDayCrossover(String stock, LocalDate to, LocalDate from) {
    log.add("getXDayCrossover called with, stock:" + stock + ", to date: " + to.toString() +
            ", from date: " + from.toString());
    Set<LocalDate> toReturn = new HashSet<LocalDate>();
    toReturn.add(LocalDate.of(1111, 11, 11));
    return toReturn;
  }

  @Override
  public Set<LocalDate> getXDayCrossover(String stock, LocalDate to, LocalDate from, int days) {
    log.add("getXDayCrossover called with, stock:" + stock + ", to date: " + to.toString() +
            ", from date: " + from.toString() + "days checking " + days);
    Set<LocalDate> toReturn = new HashSet<LocalDate>();
    toReturn.add(LocalDate.of(1111, 11, 11));
    return toReturn;
  }

  @Override
  public double gainLoss(String stock, LocalDate to, LocalDate from) {
    log.add("gainLoss called with, stock:" + stock + ", to date: " + to.toString() +
            ", from date: " + from.toString());
    return 1;
  }

  @Override
  public double getPrice(String stock, LocalDate evaluate) {
    log.add("getPrice called with, stock:" + stock + ", evaluate date: " + evaluate.toString());
    return 2;
  }

  @Override
  public String createPortfolio(String portfolio) {
    log.add("createPortfolio called with, portfolio: " + portfolio);
    portfolioExist = true;
    return portfolio;
  }

  @Override
  public String setPortfolio(String portKey) {
    log.add("setPortfolio called with, portKey: " + portKey);
    if (hasOtherPortfolios) {
      return portKey;
    } else {
      throw new IllegalArgumentException(
              "Portfolio key does not exist no portfolio found with that name");
    }
  }

  @Override
  public void logout() {
    log.add("logout called");
    portfolioExist = false;
  }

  @Override
  public String getPortfolio() {
    log.add("getPortfolio called");
    return portfolioExist ? "portfolioExist" : null;
  }

  @Override
  public double buy(String ticker, double amt) {
    log.add("buy called with, ticker:" + ticker + ", amount: " + amt);
    return 3;
  }

  @Override
  public double sell(String ticker, double amt) {
    log.add("sell called with, ticker:" + ticker + ", amount: " + amt);
    return 4;
  }

  @Override
  public double totalValue(LocalDate evaluate) {
    log.add("totalValue called with, evaluate date: " + evaluate.toString());
    return 5;
  }

  @Override
  public void createStock(String ticker) {
    log.add("createStock called with, ticker:" + ticker);
  }

  @Override
  public String downloadStock(String ticker) {
    log.add("downloadStock called with, ticker:" + ticker);
    return "downloadStock";
  }
}
