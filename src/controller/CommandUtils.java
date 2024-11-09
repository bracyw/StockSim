package controller;

import java.time.LocalDate;
import customreading.DelayedInputStream;


/**
 * Adds commands to a delayed input stream, that then communicates with the controller.
 * this allows the controller to ensure that it has full commands.
 */
public class CommandUtils {
  private static int called = 0;

  /**
   * Adds the given command to a delayed input stream, that can be used to add commands periodically
   * to a {@code SimpleController} and any classes that extend it (which DON'T override the {@code
   * control} method.
   *
   * @param command            the command to input that follows the {@code processCommand} format.
   * @param delayedInputStream the delayed input stream to add the command to.
   */
  public static boolean addCommand(String command, DelayedInputStream delayedInputStream) {
    delayedInputStream.appendInput(command);
    return true;
  }

  // DIRECTLY SUPPORTED COMMANDS BELOW... if you would like add more look to BetterControllerImpl
  // and SimpleController for supported commands, and follow the logical format for adding a new
  // supported command.

  /**
   * Returns a preformatted command for buy or sell.
   * to ensure correct order of arguments are made without.
   * a user typing. (used to be able to integrate any form of input with the readable based inputs)
   *
   * @param buy    true if this is a buy, false if formating a sell command.
   * @param ticker the stocks ticker value to buy or sell.
   * @param amount the amount of shares to buy or sell of the stock.
   * @param date   the date to buy or sell the stock on.
   * @return the formatted buy or sell on date command (based on the {@code SimpleController} and
   *            its implementations supported command formats).
   * @throws IllegalArgumentException if the date given is null.
   */
  public static String preFormatSellOrBuyDateCommand(boolean buy, String ticker, int amount,
                                                     LocalDate date)
          throws IllegalArgumentException {
    if (date == null) {
      throw new IllegalArgumentException("date cannot be null");
    }
    String command = "sell-date";
    if (buy) {
      command = "buy-date";
    }

    command += " " + ticker + " " + amount + " " + date;
    return command;
  }

  //  The ability to create a new portfolio

  /**
   * Returns a preformatted command for creating a new portfolio.
   * to ensure correct order of arguments are made without.
   * a user typing. (used to be able to integrate any form of input with the readable based inputs)
   *
   * @param portfolioName the name of the portfolio to create.
   * @return the formatted create portfolio command (based on the {@code SimpleController} and
   */
  public static String preFormatCreatePortfolioCommand(String portfolioName) {
    String command = "create-portfolio";
    command += " " + portfolioName;
    return command;
  }

  /**
   * Returns a preformatted command for saving a portfolio.
   * to a file to ensure correct order of arguments are made without.
   * a user typing. (used to be able to integrate any form of input with the readable based inputs)
   *
   * @return the formatted save portfolio command.
   */
  public static String preFormatSavePortfolioCommand() {
    return "save";
  }


  /**
   * Returns a preformatted command for setting the current portfolio.
   * to a selected portfolio to ensure correct order of arguments are made without
   * a user typing. (used to be able to integrate any form of input with the readable based inputs)
   *
   * @param name the name of the portfolio to set as the current portfolio.
   * @return the formatted set portfolio command (based on the {@code SimpleController}
   *          and its implementations supported command formats).
   * @throws IllegalArgumentException if the name given is null.
   */
  public static String preFormatSetPortfolioCommand(String name) throws IllegalArgumentException {
    if (name == null) {
      throw new IllegalArgumentException("name cannot be null");
    }
    String command = "set-portfolio";
    command += " " + name;
    return command;
  }


  /**
   * Returns a preformatted command for querying the total value of a selected portfolio at a
   * certain date to ensure correct order of arguments are made without a user typing.
   * (used to be able to integrate any form of input with the readable based inputs)
   *
   * @param date the date to query the portfolio value on.
   * @return the formatted query portfolio value command (based on the {@code SimpleController} and
   *           its implementations supported command formats).
   * @throws IllegalArgumentException if the date given is null.
   */
  public static String preFormatTotalValueCommand(LocalDate date) throws IllegalArgumentException {
    if (date == null) {
      throw new IllegalArgumentException("date cannot be null");
    }
    String command = "total-value";
    command += " " + date;
    return command;
  }

  /**
   * Returns a preformatted command for querying the composition of a selected portfolio at a
   * certain date to ensure correct order of arguments are made without a user typing.
   * (used to be able to integrate any form of input with the readable based inputs)
   *
   * @param date the date to query the portfolio composition on.
   * @return the formatted query portfolio composition command
   *        (based on the {@code SimpleController} and its implementations supported command
   *        formats).
   * @throws IllegalArgumentException if the date given is null.
   */
  public static String preFormatCompositionCommand(LocalDate date) throws IllegalArgumentException {
    if (date == null) {
      throw new IllegalArgumentException("date cannot be null");
    }
    String command = "composition";
    command += " " + date;
    return command;
  }


  /**
   * Returns a preformatted command for querying the composition of a selected portfolio at a
   * certain date to ensure correct order of arguments are made without a user typing.
   * (used to be able to integrate any form of input with the readable based inputs)
   *
   * @param date the date to query the portfolio composition on.
   * @return the formatted query portfolio composition command
   */
  public static String preFormatDistributionCommand(LocalDate date) {
    String command = "distribution";
    command += " " + date;
    return command;
  }

  /**
   * Returns a preformatted command for logging out of the current portfolio to ensure correct.
   * order of arguments are made without.
   * a user typing. (used to be able to integrate any form of input with the readable based inputs)
   *
   * @return the formatted log-out command (based on the {@code SimpleController}
   *          and its implementations supported command formats).
   */
  public static String preFormatLogOutCommand() throws IllegalArgumentException {
    String command = "log-out";
    return command;
  }

  /**
   * Returns a preformatted command for listing all the portfolios to ensure correct order.
   * of arguments are made without.
   * a user typing. (used to be able to integrate any form of input with the readable based inputs)
   *
   * @return the formatted list portfolios command (based on the {@code SimpleController}.
   *          and its implementations supported command formats).
   */
  public static String preFormatListAllPortfoliosCommand() {
    return "list-all-portfolios";
  }
}
