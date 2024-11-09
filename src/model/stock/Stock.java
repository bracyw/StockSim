package model.stock;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Set;


/**
 * Holds the functionality that all stocks should have. A stock is a representation of a company's
 * stock on the stock market. This interface is used to allow for the comparison of different stocks
 * and to allow for the calculation of different metrics for each stock.
 */
public interface Stock {

  /**
   * Allows for examining the gain or loss between two dates for this stock.
   * The calculation will use the closing value of the fromDate as the value that
   * will be compared to the toDate closing price.
   * Note: This calculation does not take into account any days in-between and including
   * each date in which data does not exist for this stock.
   *
   * @param fromDate the lower bound date to calculate the gain/loss, this value will be used as the
   *                 starting point for the calculation.
   * @param toDate   the upper bound date to calculate the gain/loss, this value will be used as the
   *                 ending point for the calculation.
   * @return the difference between the startDate (closing price) and the endDate(closing price)
   * @throws IllegalArgumentException if either of the dates don't exist for this stock.
   */
  double gainLoss(LocalDate fromDate, LocalDate toDate) throws IllegalArgumentException;

  /**
   * Calculates the x-day moving average.
   * The x-day moving average is the average of the last x days (starting from the given date).
   * Note: this corresponds to the last x days when the stock prices are available. If the given
   * date is valid and the last x days go beyond validate dates for the given stock, will just
   * calculate the average of the available days.
   *
   * @param startDate the start date to begin calculating from.
   * @param days      the past x days from the given starting date.
   * @return the moving average of the past x days from the given starting date.
   * @throws IllegalArgumentException if the given date does not exist for this stock or, if there
   *                                  are not the last amount of days
   *                                  available for this stock. The error message should contain
   *                                  the average calculated up until the last available day,
   *                                  and how many days it was able to calculate.
   */
  double xDayAvg(LocalDate startDate, int days) throws IllegalArgumentException;

  /**
   * Returns the dates that the x-day moving average is lower than the closing price, in between the
   * given start and end dates. (see {@code xDayAvg()} for more information on the moving average)
   * Note for implementation: date ranges can be easily checked by using the compareTo method
   * of the {@code LocalDate} class.
   *
   * @param from the end date to stop checking for crossovers.
   *             (must be less than or equal to the start date)
   * @param to   the start date to begin checking for crossovers.
   *             (must be greater than or equal to the end date)
   * @param days the days to calculate the xDayAvg to check against
   * @return unique dates where the x-day moving average crosses the closing price for this stock.
   * @throws IllegalArgumentException if the given from and to date are not within the
   *                                  start and end date of this stock.
   */
  Set<LocalDate> xDayCrossover(LocalDate from, LocalDate to, int days)
          throws IllegalArgumentException;

  /**
   * Returns the dates where the 30-day moving average is lower than the closing price,
   * Between the given dates. (see {@code xDayAvg()} for more information on the moving average)
   * Note for implementation: date ranges can be easily checked by using the compareTo method
   * of the {@code LocalDate} class.
   *
   * @param from the end date to stop checking for crossovers.
   *             (must be less than or equal to the start date)
   * @param to   the start date to begin checking for crossovers.
   *             (must be greater than or equal to the end date)
   * @return unique dates in which the x-day moving average crosses the closing price .
   * @throws IllegalArgumentException if the given from and to date are not within the
   *                                  start and end date of this stock.
   */
  Set<LocalDate> defDayCrossover(LocalDate from, LocalDate to) throws IllegalArgumentException;

  /**
   * Returns the ticker symbol of this stock.
   * The ticker symbol is the abbreviation of the company name.
   * (e.g. Apple = AAPL, Microsoft = MSFT)
   *
   * @return the ticker symbol of this stock.
   */
  String getTicker();

  /**
   * Returns the closing price of this stock on the most recent date available.
   *
   * @return the closing price of this stock on the most recent date available.
   */
  double getPrice();

  /**
   * Returns the closing price of this stock on the given date.
   *
   * @param date the date you want to get the closing price for.
   * @return the closing price of this stock on the given date.
   * @throws DateTimeException if the given date does not exist for this stock.
   */
  double getPrice(LocalDate date) throws DateTimeException;

  /**
   * Returns the most recent date that this stock is available.
   * (i.e. the most recent day in this stocks info, if the most
   * recent day seems off. Probably check to make sure that you have
   * updated everything)
   *
   * @return the most recent date this stock is available.
   */
  LocalDate getRecentDate();

  /**
   * Returns the date that this stock was first available.
   *
   * @return the date that this stock was first available.
   */
  LocalDate getIPODate();

  /**
   * Allows access to a copy of the raw stock information.
   *
   * @return the raw stock information.
   */
  @Override
  String toString();

  /**
   * Returns the price of this stock, regardless of whether the market is open. If the market
   * is closed will return the closing price for the recent open date.
   *
   * @param dateAt the date to get the closest
   */
  double fetchClosestStockPrice(LocalDate dateAt) throws IllegalArgumentException;


  /**
   * The plot of the stock from the given first date to the given second last date.
   *
   * @param date1 the date to start the plot from.
   * @param date2 the date to end the plot at.
   * @return the graph of the stock from the first given date to the last given date.
   */
  String plot(LocalDate date1, LocalDate date2);
}
