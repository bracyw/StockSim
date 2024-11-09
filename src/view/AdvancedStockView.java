package view;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


/**
 * The {@code AdvancedStockView} interface extends the {@code StockView} interface to support
 * additional methods for advanced functionalities in a stock management application.
 * This interface provides methods for displaying various details and performing operations
 * on stocks and portfolios, enhancing the capabilities provided by the {@code StockView} interface.
 */
public interface AdvancedStockView extends StockView {

  /**
   * Displays the composition of a specified portfolio, showing the amounts of each stock in it.
   *
   * @param portfolioName the name of the portfolio.
   * @param stockAmounts a formatted string representing the amounts of each stock in the portfolio.
   */
  void composition(String portfolioName, String stockAmounts);

  /**
   * Displays the distribution of a specified portfolio, showing the values of each stock in it.
   *
   * @param portfolioName the name of the portfolio.
   * @param stockValues a formatted string representing the values of each stock in the portfolio.
   */
  void distribution(String portfolioName, String stockValues);

  /**
   * Displays a message confirming that the current state of the portfolio has been saved.
   */
  void saveCurrentPortfolio();

  /**
   * Displays a message confirming the purchase of a balanced set of stocks.
   *
   * @param stockAmounts a map containing stock tickers as keys and the amounts purchased as values.
   * @param stockWeights a map containing stock tickers as keys and their corresponding  as values.
   */
  void buyBalanced(Map<String, Double> stockAmounts, Map<String, Double> stockWeights);

  /**
   * Displays a message confirming that the portfolio has been rebalanced.
   */
  void reBalance();

  /**
   * Displays a plot showing the value of a specified stock over a given date range.
   *
   * @param ticker the ticker of the stock.
   * @param modelOutput a formatted string representing the model output used for plotting.
   * @param date1 the start date for the plot.
   * @param date2 the end date for the plot.
   */
  void plotStockValue(String ticker, String modelOutput, LocalDate date1, LocalDate date2);

  /**
   * Displays a plot showing the information of a specified portfolio over a given date range.
   *
   * @param portfolioName the name of the portfolio.
   * @param plottedInfo a formatted string representing the information to be plotted.
   * @param date1 the start date for the plot.
   * @param date2 the end date for the plot.
   */
  void plotPortfolio(String portfolioName, String plottedInfo, LocalDate date1, LocalDate date2);

  /**
   * Displays a message confirming that a stock has been bought, including the date of purchase.
   *
   * @param ticker the ticker of the stock that was bought.
   * @param amount the amount of the stock that was bought.
   * @param value the total value of that stock now owned (including previously owned stock).
   * @param date the date when the stock was bought.
   */
  void displayBuy(String ticker, int amount, double value, LocalDate date);

  /**
   * Displays a message confirming that a stock has been sold, including the date of sale.
   *
   * @param ticker the ticker of the stock that was sold.
   * @param amount the amount of the stock that was sold.
   * @param value the total value of that stock now owned (including previously owned stock).
   * @param date the date when the stock was sold.
   */
  void displaySell(String ticker, int amount, double value, LocalDate date);

  /**
   * Displays a list of portfolio names.
   * This could be used to display available portfolios, stocks, or other named entities.
   *
   * @param names a list of portfolio names to display.
   */
  void displayNames(List<String> names);
}
