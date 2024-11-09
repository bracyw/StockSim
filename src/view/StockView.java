package view;

import java.time.LocalDate;
import java.util.Set;


/**
 * The {@code StockView} interface is made to be implemented by a class that will display the
 * results of the model's actions. This interface is used in the traditional MVC architecture,
 * where the view is simply responsible for displaying what the controller tells it to display.
 */
public interface StockView {
  /**
   * Displays a message confirming that a portfolio has been created.
   * Can use the portfolio name in the message.
   *
   * @param port the name of the portfolio that was created.
   */
  void createdPortfolio(String port);

  /**
   * Displays a message when a operation has failed.
   * Can use the operation name and relevant information in the message.
   *
   * @param operationName the name of the operation that failed.
   * @param relavantInfo  relevant information about the failure.
   */
  void failedOperation(String operationName, String relavantInfo);

  /**
   * Displays a message confirming that a portfolio has been set.
   * Can use the portfolio name in the message.
   *
   * @param portfolioName the name of the portfolio that was set.
   */
  void portfolioSet(String portfolioName);

  /**
   * Displays the contents of a portfolio.
   *
   * @param portfolio the contents of the portfolio.
   */
  void displayPortfolio(String portfolio);

  /**
   * Displays a message confirming that a stock has been bought.
   * Can use the ticker, amount, and total value in the message.
   *
   * @param ticker     the ticker of the stock that was bought.
   * @param amt        the amount of the stock that was bought.
   * @param totalOwned the total value of that stock now owned (including previously owned stock).
   */
  void displayBuy(String ticker, int amt, double totalOwned);

  /**
   * Displays a message confirming that a stock has been sold.
   * Can use the ticker, amount, and total value in the message.
   *
   * @param ticker     the ticker of the stock that was sold.
   * @param amt        the amount of the stock that was sold.
   * @param totalOwned the total value of that stock now owned (including previously owned stock).
   */
  void displaySell(String ticker, int amt, double totalOwned);

  /**
   * Displays the total value of the portfolio on a given date.
   *
   * @param date  the date to evaluate the portfolio on.
   * @param value the total value of the portfolio on that date.
   */
  void totalValue(LocalDate date, double value);

  /**
   * Displays a message confirming that the user has logged out.
   */
  void logOut();

  /**
   * Displays the main menu of the application.
   *
   * @param portfolioSelected whether a portfolio is currently selected.
   */
  void displayMenu(boolean portfolioSelected);

  /**
   * Displays a message welcoming the user to the application.
   */
  void welcomeMessage();

  /**
   * Displays a message that is show between inputs.
   * This usually will prompt the user for the next input.
   */
  void inbtwnInputs();

  /**
   * Displays a message that is shown when the user is quit the program.
   */
  void farewellMessage();


  /**
   * Displays a message showing the x day average for a givne stock that
   * has been requested.
   *
   * @param ticker the stock ticker.
   * @param price  the x day average price.
   */
  void xDaysAvg(String ticker, double price);

  /**
   * Displays a message showing the 30 day crossover for a given stock that
   * has been requested.
   *
   * @param ticker the stock ticker.
   * @param dates  the dates that crossover has occurred on.
   */
  void defDaysCrossover(String ticker, Set<LocalDate> dates);

  /**
   * Displays a message showing the 30 day crossover for a given stock that
   * has been requested.
   *
   * @param ticker the stock ticker.
   * @param dates  the dates that crossover has occurred on.
   * @param days   the days to calculate the xDayAvg against the closing price for each day.
   */
  void xDaysCrossover(String ticker, Set<LocalDate> dates, int days);

  /**
   * Displays a message showing the gain or loss for a given stock that
   * has been requested.
   *
   * @param ticker the stock ticker.
   * @param price  the gain or loss for the stock.
   */
  void gainLoss(String ticker, Double price);

  /**
   * Displays a message showing the price of a given stock that
   * has been requested.
   *
   * @param ticker the stock ticker.
   * @param price  the price of the stock.
   */
  void getPriceStock(String ticker, double price);

  /**
   * Displays a message showing an error that has occurred.
   *
   * @param error the error message.
   */
  void displayError(String error);

  /**
   * Displays a message showing that a stock has been downloaded.
   *
   * @param message the message showing that the stock has been downloaded.
   */
  void downloadStock(String message);
}

