package model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Represents the better stock model implementation.
 */
public interface BetterStockModel extends StockModel {
  /**
   * Returns the composition of the CURRENT PORTFOLIO, which includes (a) the list of stocks and
   * (b) the number of shares of each stock.
   *
   * @param date the day to find the composition of the portfolio on.
   * @return the composition of the portfolio as an HMS, with two rows, one for stock ticker,
   *        the other for shares.
   */
  String composition(LocalDate date);

  /**
   * Returns the current portfolios distribution of value, which includes (a) the stocks
   * (b) the value of each individual stock in the portfolio.
   *
   * @param date the date to find the distribution at.
   * @return the HMS of the stocks and their values, in a table.
   */
  String distribution(LocalDate date);

  /**
   * Saves the current portfolio as a file.
   */
  void saveCurrentPortfolio();

  /**
   * Used for buying multiple stocks with a certain amount of money. The portion of money
   * being spent on each, is based on the corresponding weights for each stalk (adding up to 100%).
   *
   * @param amount         of shares to buy
   * @param stocksNWeights the stocks and percentage of the money to spend on each
   *                       NOTE: (weights MUST add up to 100%)
   * @return the shares of each stock bought.
   * @throws IllegalArgumentException if any of the stocks don't exist, or the weights given
   *                                  do not add up to 100%
   */
  Map<String, Double> buyBalanced(Map<String, Double> stocksNWeights, LocalDate dateAt,
                                  double amount)
          throws IllegalArgumentException;


  /**
   * Plots the current portfolio value from the first given date, to the second date.
   *
   * @param date1 the date to start plotting at.
   * @param date2 the date to stop plotting at.
   * @return the HMS row representing the values of the portfolio over time as a bar graph.
   */
  String plotPortfolio(LocalDate date1, LocalDate date2);

  /**
   * Plots the stock value from the first given date, to the second date.
   *
   * @param ticker the ticker of the stock to plot.
   * @param date1  the date to start plotting at.
   * @param date2  the date to stop plotting at.
   * @return the HMS row representing the value of the stock overtime as a bar graph.
   */
  String plotStockValue(String ticker, LocalDate date1, LocalDate date2);

  /**
   * Buys a given stock on a specific date, in the current portfolio.
   *
   * @param ticker the ticker of the stock.
   * @param shares the amount of shares to buy.
   * @param date   the date to buy the stock on.
   * @return the monetary value of the shares bought.
   */
  double buyDate(String ticker, double shares, LocalDate date);

  /**
   * Sells a given stock on a specific date, in the current portfolio.
   *
   * @param ticker the ticker of the stock.
   * @param shares the amount of shares to sell.
   * @param date   the date to sell the shares on.
   * @return the monetary value of the shares sold.
   */
  double sellDate(String ticker, double shares, LocalDate date);

  /**
   * Used for re-balancing stocks already in your portfolio with money that you already have
   * invested in them.
   *
   * @param stocksNWeights the stocks and new monetary weights that should be assigned relative
   *                       to the total value of all those stocks in the portfolio.
   * @return the new amounts of each
   * @throws IllegalArgumentException if any of the stocks don't exist *in the portfolio*,
   *                                  or the weights given  do not add up to 100%
   */
  String reBalance(Map<String, Double> stocksNWeights, LocalDate dateAt)
          throws IllegalArgumentException;


  /**
   * Returns the name of the current portfolio.
   *
   * @return the current portfolio name.
   */
  String getCurrentPortfolioName();


  /**
   * returns a list of every single portfolio name.
   * The name needed to set-portfolio (name).
   *
   * @return list of portfolio.
   */
  List<String> listAllPortfolios();

  /**
   * Gives the option to view a portfolio with the given name, (see {@code StockModel} and
   * it's method {@code getPortfolio()}, as the syntax returned for that function is identical
   * to this one.)
   *
   * @param name the name of the portfolio to view.
   */
  String viewPortfolio(String name);
}

