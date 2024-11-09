package model.portfolio;

import java.time.LocalDate;
import java.util.Map;

/**
 * Represents a portfolio of stocks.
 * A portfolio is a collection of stocks and the quantity of each stock.
 * The portfolio can be used to buy and sell stocks.
 * To get the total value of the portfolio on a given date.
 * The portfolio can also be used to get the value of a specific stock.
 * This includes across different dates in this portfolio on a given date.
 */
public interface Portfolio {
  /**
   * Returns the totalValue of this portfolio on the given date.
   * Assumes that all stocks and quantities exist on the given date.
   *
   * @param date the date to find the value of all the stocks
   * @return the value of all the stocks in this portfolio on the given date.
   * @throws IllegalArgumentException if the given date does not exist for any of the stocks.
   */
  double totalValue(LocalDate date) throws IllegalArgumentException;

  /**
   * Gets the value of the given stock in the portfolio, on the given date.
   * The value is based on the closing price of the given stock.
   *
   * @param stockName the name of the stock.
   * @param date      the date that you want the value of
   * @return the value of the given stock on the given date.
   * @throws IllegalArgumentException if the given stock does not exist in this portfolio
   *                                  or if the given date does not exist for the given stock.
   */
  //possible to delete
  double getValue(String stockName, LocalDate date) throws IllegalArgumentException;

  /**
   * Buys the given amount of the given stock.
   *
   * @param stockName the name of the stock to buy.
   * @param amount    the amount of the stock to buy.
   * @return the total value of the stock bought, for the last available price of the stock.
   * @throws IllegalArgumentException if the given stock does not exist.
   */
  double buy(String stockName, double amount) throws IllegalArgumentException;

  /**
   * Sells the given amount of the given stock.
   *
   * @param stockName the name of the stock to sell.
   * @param amount    the amount of the stock to sell.
   * @return the total value of the stock sold.
   * @throws IllegalArgumentException if the given stock does not exist,
   *                                  or if the given amount is greater than
   *                                  the quantity of the stock
   *                                  in this portfolio.
   */
  double sell(String stockName, double amount) throws IllegalArgumentException;

  @Override
  String toString();


  /**
   * Returns a clone of the given portfolio.
   *
   * @return the clone of the given portfolio.
   */
  Portfolio clone();

  /**
   * returns the amounts of each Stock, in the form of a ticker mapped to an amount of shares.
   *
   * @param time the time period you want the amount at.
   * @return the mapped tickers to amounts of stock at any given point in time.
   */
  Map<String, Double> getAmount(LocalDate time);
}
