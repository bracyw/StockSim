package model.portfolio;

import java.time.LocalDate;
import java.util.Map;

import model.stock.Stock;


/**
 * An interface for the betterPortfolio.
 * This interface implements new methods that a better portfolio requires.
 */
public interface BetterPortfolio extends Portfolio {

  /**
   * Gets this portfolio's name.
   *
   * @return the name of this portfolio.
   */
  String getName();

  /**
   * Buys all the given stocks with their weights, as a percentage of the total value of money
   * to be spent, on the given date.
   *
   * @param stocks the stocks and their weights, as a percentage of the total value of money
   * @param dateAt the date to buy the stocks on.
   * @param money  the total value of money to be spent.
   * @return the stocks that were bought, mapped to the shares of each stock.
   * @throws IllegalArgumentException if the date is null.
   */
  Map<Stock, Double> balance(Map<Stock, Double> stocks, LocalDate dateAt, double money)
          throws IllegalArgumentException;

  /**
   * Rebalances the portfolio to the given stocks and their weights, as a percentage of the total
   * value of the stocks given (in this portfolio), on the given date.
   *
   * @param stocks the stocks and their weights, as a percentage of the total value of the portfolio
   * @param dateAt the date to rebalance the portfolio on.
   * @throws IllegalArgumentException if the date is null.
   */
  void reBalance(Map<Stock, Double> stocks, LocalDate dateAt) throws IllegalArgumentException;

  /**
   * Purchase a specific number of shares of a specific stock on a specified date, and add them
   * to the portfolio.
   *
   * @param stock the stock to buy.
   * @param amount the amount of shares to buy.
   * @param dateAt the date to buy the stock on.
   * @return the cost of the purchase.
   * @throws IllegalArgumentException if the date is null.
   */
  double buyDate(Stock stock, double amount, LocalDate dateAt) throws IllegalArgumentException;

  /**
   * Sell a specific number of shares of a specific stock on a specified date from the portfolio.
   *
   * @param stock the stock to sell.
   * @param amount the amount of shares to sell.
   * @param dateAt the date to sell the stock on.
   * @return the proceeds of the sale.
   * @throws IllegalArgumentException if the date is null.
   */
  double sellDate(Stock stock, double amount, LocalDate dateAt) throws IllegalArgumentException;

  /**
   * Returns the composition, which includes (a) the list of stocks and (b) the number of shares
   * of each stock.
   *
   * @param dateAt the day to find the composition of the portfolio on.
   * @return the composition of the portfolio.
   * @throws IllegalArgumentException if the date is null.
   */
  String composition(LocalDate dateAt) throws IllegalArgumentException;

  /**
   * Returns the total value of the portfolio on the given date. The value of the portfolio is the
   * sum of the value of each stock in the portfolio on the given date.
   *
   * @param date the date to find the value of the portfolio on.
   * @return the total value of the portfolio on the given date.
   * @throws IllegalArgumentException if the date is null.
   */
  @Override
  double totalValue(LocalDate date) throws IllegalArgumentException;

  /**
   * Saves the portfolio to a file.
   */
  void save();

  /**
   * Returns the string representation of the portfolio. The string representation of the portfolio
   * includes (a) the name of the portfolio, (b) the stocks in the portfolio, and (c) the number of
   * shares of each stock in the portfolio on the most recent date.
   *
   * @return the string representation of the portfolio.
   */
  @Override
  String toString();

  /**
   * Returns the plot of the portfolio from the given date to the given date. The plot of the
   * portfolio is returned as a string,
   * where the string is a bar graph of the value of the portfolio
   * over time. The bar graph should be generated by the {@code FormattingUtils} class.
   *
   * @param from the date to start the plot from.
   * @param to   the date to end the plot at.
   * @return the graph of the portfolio from the first given date to the last given date.
   * @throws IllegalArgumentException if either date is null.
   */
  String plot(LocalDate from, LocalDate to) throws IllegalArgumentException;

  /**
   * Returns the distribution of value, which includes (a) the stocks and (b) the value of each
   * individual stock in the portfolio.
   *
   * @param date the date to find the distribution at.
   * @return the distribution of the stocks and their values.
   * @throws IllegalArgumentException if the date is null.
   */
  String distribution(LocalDate date) throws IllegalArgumentException;
}
