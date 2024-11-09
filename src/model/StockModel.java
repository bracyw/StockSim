package model;

import java.time.LocalDate;
import java.util.Set;

/**
 * The {@code StockModel} interface provides a way for the controller to interact with the model.
 * Provides methods that allow the controller to get information about stocks, such as the average.
 * price of a stock over a given number of days, the number of times a stock has.
 * crossed over a given number of days, and the gain or loss of a stock over a given period of time.
 * The {@code StockModel} interface also provides methods that allow the controller to interact with
 * a user's portfolio. The controller can create a new portfolio, set the current portfolio, buy and
 * sell stocks in the portfolio, and get the total value of the current portfolio on a given date.
 */
public interface StockModel {

  /**
   * Returns average price of stock date of to, for x previous days.
   *
   * @param ticker stock in question.
   * @param to     date it starts at.
   * @param xDays  amount of days.
   * @return average as a double.
   */
  public double getXDaysAverage(String ticker, LocalDate to, int xDays);

  /**
   * Returns the days that are x day cross-overs between the two dates.
   *
   * @param stock stock in question
   * @param to    starting date
   * @param from  end date
   * @param days  the avg days for a cross-over to get checked against
   * @return a hashset of every date, that is a crossover.
   */
  public Set<LocalDate> getXDayCrossover(String stock, LocalDate to, LocalDate from, int days);

  /**
   * Return the days that are 30 day crossover.
   *
   * @param stock stock in question
   * @param to    starting date
   * @param from  end date
   * @return a hashset of every date.
   */
  public Set<LocalDate> getDefDayCrossover(String stock, LocalDate to, LocalDate from);

  /**
   * Amount of money a stock has gained or lossed, since the two dates.
   *
   * @param stock stock in question
   * @param to    to date.
   * @param from  from date.
   * @return double of the profit/loss of the stock.
   */
  public double gainLoss(String stock, LocalDate to, LocalDate from);

  /**
   * Returns price of the stock at a given date.
   *
   * @param stock    stock in question.
   * @param evaluate price of the stock.
   * @return price of the stock as a double.
   */
  public double getPrice(String stock, LocalDate evaluate);


  //no portfolio selected

  /**
   * creates a new portfolio with the give name.
   *
   * @param portfolio new portfolio.
   * @return the {@code toString()} of the portfolio created.
   */
  public String createPortfolio(String portfolio);

  /**
   * Sets the users portfolio to a portfolio that has already been created, but may have been
   * logged out of.
   *
   * @param portKey key for the portfolio.
   * @return the portfolio name.
   */
  public String setPortfolio(String portKey) throws IllegalArgumentException;

  //portfolio selected

  /**
   * logs user out of selected portfolio.
   */
  public void logout();

  /**
   * Returns the portfolio.toString().
   *
   * @return portfolio representation.
   */
  public String getPortfolio();

  /**
   * buys x amount of stock returns price of stock at most current date * amount.
   *
   * @param ticker stock in question.
   * @param amt    amount of stock.
   * @return double amount of stock.
   */
  public double buy(String ticker, double amt);

  /**
   * sells x amount of stock returns price of stock at most current date * amount.
   *
   * @param ticker stock in question.
   * @param amt    amount of stock.
   * @return double amount of stock.
   */
  public double sell(String ticker, double amt);

  /**
   * total value of an entire portfolio at a given date.
   *
   * @param evaluate date in question.
   * @return the value of the portfolio.
   */
  public double totalValue(LocalDate evaluate);


  /**
   * Creates a new stock with the given ticker. (see {@code SimpleStock} for more info on stock
   * creation, and it's relation to {@code OODStocks})
   *
   * @param ticker creating the stock.
   */
  public void createStock(String ticker);

  /**
   * Downloads the stock information for the given ticker. This method is used
   * to download the stock information from the internet and store it in a file,
   * which can then be read by the {@code SimpleStock} class.
   *
   * @param ticker the ticker symbol of the stock to download
   * @return a string confirming that the stock has been downloaded
   */
  public String downloadStock(String ticker);
}
