package model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import model.portfolio.Portfolio;
import model.stock.SimpleStock;

/**
 * The {@code AbstractStockModel} takes all the functionality that is shared
 * between the {@code SimpleStockModel} and {@code BetterStockModelImpl}, as the only
 * difference between the results of each portfolio is the exact functionality behind
 * the Portfolio objects based into the portfolios saved. (See the differences between
 * {@code BetterPortfolioImpl} and {@code SimplePortfolio} for more in-depth understanding.)
 */
public abstract class AbstractStockModel implements StockModel {
  protected Map<String, Portfolio> viewedPortfolios;
  protected Portfolio currentAct; // load it to this

  public AbstractStockModel() {
    this.viewedPortfolios = new HashMap<>();
    this.currentAct = null;
  }

  @Override
  public double sell(String ticker, double amt) {
    validateLoggedIn();
    return applySell(ticker, amt);
  }


  /**
   * Validates whether the user is logged in.
   *
   * @throws IllegalArgumentException if no portfolio is selected.
   */
  protected void validateLoggedIn() throws IllegalArgumentException {
    if (currentAct == null) {
      throw new IllegalArgumentException("No portfolio selected");
    }
  }

  /**
   * Applies the sell function to the current account.
   *
   * @param ticker the symbol of the stock
   * @param amt    the amount of shares to sell
   * @return the result of the sell.
   */
  protected double applySell(String ticker, double amt) {
    return currentAct.sell(ticker, amt);
  }


  @Override
  public String createPortfolio(String port) {
    if (this.viewedPortfolios.containsKey(port)) {
      throw new IllegalArgumentException("Portfolio already exists");
    }
    this.addPortfolio(port);
    this.setCurrentAct(port, false);
    return port;
  }


  protected abstract void addPortfolio(String portfolioName);

  @Override
  public void logout() {
    setCurrentAct("doesn't matter", true);
  }


  @Override
  public double buy(String ticker, double amt) {
    validateLoggedIn();
    return this.applyBuy(ticker, amt);
  }

  /**
   * Applies the buy function to the current account.
   *
   * @param ticker the symbol of the stock.
   * @param amt    the amount of shares to buy.
   * @return the result of the buy.
   */
  protected double applyBuy(String ticker, double amt) {
    return currentAct.buy(ticker, amt);
  }


  @Override
  public String getPortfolio() {
    try {
      validateLoggedIn();
      return getCurrentActInfo();
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  @Override
  public String setPortfolio(String portKey) throws IllegalArgumentException {
    this.setCurrentAct(portKey, false);
    return this.getCurrentActInfo();
  }

  protected String getCurrentActInfo() {
    return currentAct.toString();
  }

  protected void setCurrentAct(String portKey, boolean setNull) throws IllegalArgumentException {
    if (setNull) {
      currentAct = null;
      return;
    }
    if (viewedPortfolios.containsKey(portKey)) {
      currentAct = viewedPortfolios.get(portKey);
    } else {
      throw new IllegalArgumentException(
              "Portfolio key does not exist no portfolios found with that name");
    }
  }

  @Override
  public void createStock(String ticker) {
    try {
      new SimpleStock(ticker);
    } catch (Exception e) {
      throw new IllegalArgumentException(e.getMessage());
    }
  }

  @Override
  public String downloadStock(String stock) {
    OODStocks.create(stock);
    return "created stock - " + stock;
  }

  @Override
  public double getXDaysAverage(String stock, LocalDate to, int xDays) {
    if (OODStocks.getStocks().containsKey(stock)) {
      return OODStocks.getStock(stock).xDayAvg(to, xDays);
    } else {
      createStock(stock);
      return OODStocks.getStock(stock).xDayAvg(to, xDays);
    }
  }

  @Override
  public Set<LocalDate> getDefDayCrossover(String stock, LocalDate to, LocalDate from) {
    if (OODStocks.getStocks().containsKey(stock)) {
      return OODStocks.getStock(stock).defDayCrossover(to, from);
    } else {
      createStock(stock);

      return OODStocks.getStock(stock).defDayCrossover(to, from);
    }
  }

  @Override
  public Set<LocalDate> getXDayCrossover(String stock, LocalDate to, LocalDate from, int days) {
    if (OODStocks.getStocks().containsKey(stock)) {
      return OODStocks.getStock(stock).xDayCrossover(to, from, days);
    } else {
      createStock(stock);

      return OODStocks.getStock(stock).xDayCrossover(to, from, days);
    }
  }

  @Override
  public double gainLoss(String stock, LocalDate to, LocalDate from) {
    if (!OODStocks.getStocks().containsKey(stock)) {
      createStock(stock);
    }
    return OODStocks.getStock(stock).gainLoss(to, from);
  }

  @Override
  public double getPrice(String stock, LocalDate date) {
    if (OODStocks.getStocks().containsKey(stock)) {
      return OODStocks.getStock(stock).getPrice(date);
    }
    createStock(stock);
    return OODStocks.getStock(stock).getPrice(date);
  }
}
