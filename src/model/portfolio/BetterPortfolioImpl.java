package model.portfolio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import model.FormattingUtils;
import model.OODStocks;
import model.stock.Stock;

/**
 * The {@code BetterPortfolioImpl} class represents a portfolio of stocks that is tracked over time,
 * this is achieved by storing the commands that are executed on the portfolio at a given date.
 * Commands are stored in this form to allow for the portfolio to be calculated properly at any
 * given date, regardless of the order of the commands it receives. When the commands are saved to
 * a file, they are simplified for each date to ensure that the commands are stored in a way that
 * can be human-readable and easily understood.
 */
public class BetterPortfolioImpl implements BetterPortfolio {
  // IMPORTANT: changing this path assignment could create major issues, ensure thorough testing
  // of new implementations that re-assign this static method.
  protected static String PORTFOLIOS_STORAGE_PATH =
          System.getProperty("user.dir") + "/PortfolioInfo/";
  private List<DateCommandStorage> allActions;
  // THIS NAME WILL BE UNIQUE ACROSS OUR STORAGE, if data exist in the portfolio path that does not
  // follow this constraint the program will not work as expected.
  private String name;

  /**
   * Creates a new portfolio with the information stored in the given file.
   * See {@link #save()} for the format of the file, needed for this to work.
   *
   * @param fileName the name of the file to read the portfolio information from.
   * @throws IllegalArgumentException if the file does not exist, or the format is
   *                                  incorrect
   */
  public BetterPortfolioImpl(File fileName) throws IllegalArgumentException {
    try {
      Scanner lines = new Scanner(fileName);
      allActions = new ArrayList<>();
      name = lines.nextLine();
      while (lines.hasNextLine()) {
        LocalDate date = LocalDate.parse(lines.nextLine());
        String command = lines.nextLine();
        allActions.add(new DateCommandStorage(command, date));
      }
    } catch (Exception e) {
      throw new IllegalArgumentException("Sadly no file exists with the name " + fileName.getName()
              + " in the directory: " + fileName.getParentFile());
    }
  }

  /**
   * Used for creating a brand new empty portfolio.
   *
   * @param name the name of the portfolio.
   */
  public BetterPortfolioImpl(String name) {
    this.name = name;
    this.allActions = new ArrayList<>();
  }

  // Convenience constructor for creating a new instance with a pre-made list of commands.
  private BetterPortfolioImpl(String name, List<DateCommandStorage> array) {
    this.name = name;
    this.allActions = array;
  }

  /**
   * Saves the portfolio to a file with the name of the portfolio. The file is saved in the
   * PortfolioInfo directory, and is formatted as follows: the first line is the name of the
   * portfolio, and the following lines are the date of the command, followed by the command itself.
   * The command should be formatted as follows for each date:
   * < command >, ...args of that command, < next command >...
   */
  @Override
  public void save() {
    File stockFile = new File(PORTFOLIOS_STORAGE_PATH + this.getName() + ".txt");
    try {
      PrintWriter writer = new PrintWriter(stockFile);
      String information = this.getName();
      for (int i = 0; i < allActions.size(); i++) {
        information += "\n" + allActions.get(i).getDate().toString();
        information += "\n" + allActions.get(i).simplify();
      }

      writer.print(information);
      writer.close();
    } catch (FileNotFoundException e) {
      throw new IllegalStateException("FATAL ERROR THIS SHOULD NEVER THROW: check " +
              "BetterPortfolioImpl save function");
    }
  }

  // Returns the SimplePortfolio representation of this portfolio at a given date.
  private Portfolio getPortfolio(LocalDate date) {
    Portfolio stockInfo = new SimplePortfolio();
    // apply all the commands on the given day to this
    for (int i = 0; i < allActions.size() && ((allActions.get(i).getDate()).isBefore(date) ||
            allActions.get(i).getDate().isEqual(date)); i++) {
      LocalDate dateOfCommands = allActions.get(i).getDate();
      String rawCommands = allActions.get(i).getCommandLine();
      stockInfo = this.applyCommands(stockInfo, dateOfCommands, rawCommands);
    }
    return stockInfo;
  }

  // Applies the given commands to the portfolio, given the date they were executed on
  private Portfolio applyCommands(Portfolio portfolio, LocalDate date, String rawCommands) {
    String[] arrayedCommands = rawCommands.split(",");
    for (String command : arrayedCommands) {
      String[] commandParts = command.split(" ");
      switch (commandParts[0]) {
        case "buy":
          portfolio.buy(commandParts[1], Double.parseDouble(commandParts[2]));
          break;
        case "sell":
          portfolio.sell(commandParts[1], Double.parseDouble(commandParts[2]));
          break;

        case "rebalance":
          Map<String, Double> parts = new LinkedHashMap<>();
          for (int j = 1; j < commandParts.length; j += 2) {
            parts.put(commandParts[j], Double.parseDouble(commandParts[j + 1]));
          }
          portfolio = reBalanceHelper(parts, portfolio, date);
          break;

        default:
          break;
      }
    }
    return portfolio;
  }

  // Used for buying and selling (where the index of a certain date is, in relation
  // Finds the correct index for a given date in the
  private int getClosestIndex(LocalDate dateToFind) {
    int index = 0;
    LocalDate previous = null;
    LocalDate current = null;
    for (int i = 0; i < allActions.size(); i++) {
      current = allActions.get(i).getDate();
      if (dateToFind.isEqual(current)) {
        throw new IllegalArgumentException(index + "");
      }
      if (previous == null) {
        if (current.isAfter(dateToFind)) {
          return index;
        }
      } else {
        if (current.isAfter(dateToFind)) {
          return index;
        }
      }
      previous = current;
      index++;

    }
    return index;
  }

  // Takes in a portfolio and balances it
  private Portfolio reBalanceHelper(Map<String, Double> toBalance, Portfolio portfolio,
                                    LocalDate time) throws IllegalArgumentException {
    double amountOfMoney = 0;
    Map<String, Double> copyPortfolio = portfolio.getAmount(time);
    Map<Stock, Double> conversion = new LinkedHashMap<>();

    for (Map.Entry<String, Double> entry : toBalance.entrySet()) {

      conversion.put(OODStocks.getStock(entry.getKey()), entry.getValue());

      if (copyPortfolio.containsKey(entry.getKey())) {
        amountOfMoney += portfolio.getValue(entry.getKey(), time);
        copyPortfolio.remove(entry.getKey());
      }
    }
    if (amountOfMoney == 0) {
      throw new IllegalArgumentException("User does not have any money in these stocks");
    }
    BetterPortfolio cloneCopy = this.clone();
    Map<Stock, Double> balance = cloneCopy.balance(conversion, time, amountOfMoney);
    for (Map.Entry<Stock, Double> entry : balance.entrySet()) {
      copyPortfolio.put(entry.getKey().getTicker(), entry.getValue());

    }
    return new SimplePortfolio(copyPortfolio);
  }

  // Checks if any of the dates are null, throws if it is.
  private void validateDates(LocalDate... dates) throws IllegalArgumentException {
    for (LocalDate date : dates) {
      if (date == null) {
        throw new IllegalArgumentException("Date cannot be null");
      }
    }
  }


  @Override
  public String getName() {
    return name;
  }

  @Override
  public Map<Stock, Double> balance(Map<Stock, Double> stocksNPercentages, LocalDate dateAt,
                                    double money) throws IllegalArgumentException {
    this.validateDates(dateAt);
    this.validatePercentages(stocksNPercentages);
    Map<Stock, Double> correctAmounts = new LinkedHashMap<>();
    for (Map.Entry<Stock, Double> entry : stocksNPercentages.entrySet()) {
      double value = 0;
      value = entry.getKey().fetchClosestStockPrice(dateAt);
      double amountToBuy = (money * entry.getValue() * .01) / value;
      correctAmounts.put(entry.getKey(), amountToBuy);
    }
    // Technically should also add all Stock to buy message.
    for (Map.Entry<Stock, Double> entry : correctAmounts.entrySet()) {
      this.buyDate(entry.getKey(), entry.getValue(), dateAt);
    }

    return correctAmounts;
  }


  // Ensures that the percentages in the Map given add up to 100
  private void validatePercentages(Map<Stock, Double> stockPercentages) {
    // validate percentage
    double totalPercentage = 0;
    for (Double value : stockPercentages.values()) {
      if (value < 0) {
        throw new IllegalArgumentException("Cannot have a negative percentage");
      }
      totalPercentage += value;
    }
    if (totalPercentage != 100) {
      throw new IllegalArgumentException("Balance percentage split is not equal to 100.");
    }
  }


  @Override
  public double buyDate(Stock stock, double amount, LocalDate dateAt)
          throws IllegalArgumentException {
    int index;
    if (amount <= 0) {
      throw new IllegalArgumentException("Amount cannot buy negative stock");
    }
    try {
      index = getClosestIndex(dateAt);
      allActions.add(index, new DateCommandStorage("", dateAt));
      allActions.get(index).buy(stock.getTicker(), amount);
    } catch (IllegalArgumentException e) {
      index = Integer.parseInt(e.getMessage());
      allActions.get(index).buy(stock.getTicker(), amount);
    }
    return stock.fetchClosestStockPrice(dateAt) * amount;
  }

  @Override
  public void reBalance(Map<Stock, Double> stocks, LocalDate dateAt)
          throws IllegalArgumentException {
    this.validateDates(dateAt);
    int index;
    try {
      index = getClosestIndex(dateAt);
      allActions.add(index, new DateCommandStorage("", dateAt));
      allActions.get(index).reBalance(stocks);
    } catch (IllegalArgumentException e) {
      index = Integer.parseInt(e.getMessage());
      allActions.get(index).reBalance(stocks);
    }

    try {
      this.getPortfolio(dateAt);
    } catch (IllegalArgumentException e) {
      allActions.get(index).removeLastCommand();
      throw new IllegalArgumentException(e.getMessage());
    }

  }

  @Override
  public double sellDate(Stock stock, double amount, LocalDate dateAt)
          throws IllegalArgumentException {
    this.validateDates(dateAt);
    int index;
    if (amount <= 0) {
      throw new IllegalArgumentException("Amount cannot buy negative stock");
    }
    try {
      index = getClosestIndex(dateAt);
      allActions.add(index, new DateCommandStorage("", dateAt));
      allActions.get(index).sell(stock.getTicker(), amount);
    } catch (IllegalArgumentException e) {
      index = Integer.parseInt(e.getMessage());
      allActions.get(index).sell(stock.getTicker(), amount);
    }
    try {
      this.getPortfolio(dateAt);
    } catch (IllegalArgumentException e) {
      allActions.get(index).removeLastCommand();
      throw new IllegalArgumentException("this sell command causes complication " +
              "with either a sell command or balance command");
    }
    return stock.fetchClosestStockPrice(dateAt) * amount;
  }

  @Override
  public String composition(LocalDate dateAt) throws IllegalArgumentException {
    this.validateDates(dateAt);
    Portfolio portfolio = this.getPortfolio(dateAt);
    SimplePortfolio simplePortfolio = new SimplePortfolio(this.getAmount(dateAt));
    List<String> tickers = simplePortfolio.getAllTickers();
    tickers.add(0, "Tickers"); // add the header for col
    List<String> shares = simplePortfolio.getAllShares();
    shares.add(0, "Shares"); // add the header for the col

    List<String> table = simplePortfolio.addCols(tickers, shares);
    String title = "Portfolio is currently holding:";

    return new FormattingUtils().createTable(table, title);
  }

  @Override
  public String plot(LocalDate from, LocalDate to) throws IllegalArgumentException {
    this.validateDates(from, to);
    FormattingUtils plotting = new FormattingUtils();
    int interval = plotting.interval(from, to);
    // Validate date range
    Map<LocalDate, Double> valuesOverTime = new LinkedHashMap<>();
    LocalDate active = from;
    double max = Integer.MIN_VALUE;
    while (active.isBefore(to)) {
      try {
        double value = this.totalValue(active);
        valuesOverTime.put(active, value);
        active = active.plusDays(interval);
        if (value > max) {
          max = value;
        }
      } catch (Exception e) {
        //does nothing
      }
    }

    try {
      double value = this.totalValue(to);
      valuesOverTime.put(to, value);
      if (value > max) {
        max = value;
      }
    } catch (Exception e) {
      //does nothing
    }

    return plotting.barGraph(valuesOverTime, max);
  }

  @Override
  public String distribution(LocalDate date) throws IllegalArgumentException {
    this.validateDates(date);
    Portfolio portfolio = this.getPortfolio(date);
    SimplePortfolio simplePortfolio = new SimplePortfolio(this.getAmount(date));
    List<String> tickers = simplePortfolio.getAllTickers();
    tickers.add(0, "Tickers"); // add the header for col
    List<String> values = simplePortfolio.getAllValues(date);
    values.add(0, "Values"); // add the header for the col

    // create combined table
    List<String> table = simplePortfolio.addCols(tickers, values);
    String title = "Portfolio is currently holding:";


    return new FormattingUtils().createTable(table, title);
  }


  /**
   * Returns the totalValue of this portfolio on the given date.
   * Assumes that all stocks and quantities exist on the given date.
   *
   * @param date the date to find the value of all the stocks
   * @return the value of all the stocks in this portfolio on the given date.
   * @throws IllegalArgumentException if the given date does not exist for any of the stocks.
   */
  @Override
  public double totalValue(LocalDate date) throws IllegalArgumentException {
    this.validateDates(date);
    return this.getPortfolio(date).totalValue(date);
  }

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
  @Override
  public double getValue(String stockName, LocalDate date) throws IllegalArgumentException {
    this.validateDates(date);
    return this.getPortfolio(date).getValue(stockName, date);
  }

  /**
   * Buys the given amount of the stock, on the most recent date available for the given stock info.
   *
   * @param stockName the name of the stock to buy.
   * @param amount    the amount of the stock to buy.
   * @return the total value of the stock bought, on the most recent date available for its value.
   * @throws IllegalArgumentException if the given stock does not exist.
   */
  @Override
  public double buy(String stockName, double amount) throws IllegalArgumentException {
    Stock toBuy = OODStocks.getStockOrMakeNew(stockName);
    return this.buyDate(toBuy, amount, toBuy.getRecentDate());
  }

  /**
   * Sells the given amount of the given stock, on the most recent date available for the given
   * stock info.
   *
   * @param stockName the name of the stock to sell.
   * @param amount    the amount of the stock to sell.
   * @return the total value of the stock sold.
   * @throws IllegalArgumentException if the given stock does not exist,
   *                                  or if the given amount is greater than
   *                                  the quantity of the stock
   *                                  in this portfolio.
   */
  @Override
  public double sell(String stockName, double amount) throws IllegalArgumentException {
    Stock toSell = OODStocks.getStockOrMakeNew(stockName);
    return this.sellDate(toSell, amount, toSell.getRecentDate());
  }

  @Override
  public BetterPortfolio clone() {
    List<DateCommandStorage> toRet = new ArrayList<>();
    for (int i = 0; i < allActions.size(); i++) {
      DateCommandStorage store = new DateCommandStorage(allActions.get(i).getCommandLine(),
              allActions.get(i).getDate());
      toRet.add(store);

    }
    return new BetterPortfolioImpl(this.getName(), toRet);
  }

  @Override
  public Map<String, Double> getAmount(LocalDate dat) throws IllegalArgumentException {
    this.validateDates(dat);
    return this.getPortfolio(dat).getAmount(dat);
  }

  @Override
  public String toString() {
    return this.getPortfolio(LocalDate.now()).toString();
  }


  /**
   * Creates very specific strings for representation of the commands of a portfolio on a given
   * date, order of the commands is based on the real time order in which they are called.
   * Formated as follows: < command >, ...args of that command, < next command >...
   * All in reference to the date that can continuously receive additional commands, which
   * are ordered first to last called (which would be real-time chronological order).
   */
  private class DateCommandStorage {
    private final LocalDate date;
    private String commandLine;

    /**
     * Constructs a {@code DateCommandStorage} object with the given command and date.
     *
     * @param command the commands for that day
     * @param date    the date
     * @throws IllegalArgumentException if the date is null.
     */
    public DateCommandStorage(String command, LocalDate date) throws IllegalArgumentException {
      this.commandLine = command;
      validateDates(date);
      this.date = date;
    }

    private String simplify() {
      String[] commandList = commandLine.split(",");
      Map<String, Double> stockInfo = new LinkedHashMap<>();
      String simpleCommandLine = "";

      for (int i = 0; i < commandList.length; i++) {
        String[] individual = commandList[i].split(" ");
        if (individual[0].indexOf("rebalance") == 0) {
          simpleCommandLine = simplifyToBuyOrSell(stockInfo, simpleCommandLine);
          stockInfo = new LinkedHashMap<>();
          simpleCommandLine += "," + commandList[i];
        } else if (individual[0].equals("buy")) {
          stockInfo = this.convertBuyCmd(stockInfo, individual);
        } else {
          stockInfo = this.convertSellCmd(stockInfo, individual);
        }
      }

      simpleCommandLine = simplifyToBuyOrSell(stockInfo, simpleCommandLine);

      if (simpleCommandLine.indexOf(",") == 0) {
        simpleCommandLine = simpleCommandLine.substring(1);
      }
      commandLine = simpleCommandLine;
      return simpleCommandLine;
    }

    private Map<String, Double> convertBuyCmd(Map<String, Double> stockInfo, String[] individual) {
      if (stockInfo.containsKey(individual[1])) {
        stockInfo.put(individual[1], stockInfo.get(individual[1]) +
                Double.parseDouble(individual[2]));
      } else {
        stockInfo.put(individual[1], Double.parseDouble(individual[2]));
      }
      return stockInfo;
    }

    private Map<String, Double> convertSellCmd(Map<String, Double> stockInfo, String[] individual) {
      if (stockInfo.containsKey(individual[1])) {
        stockInfo.put(individual[1], stockInfo.get(individual[1]) -
                Double.parseDouble(individual[2]));
      } else {
        stockInfo.put(individual[1], -Double.parseDouble(individual[2]));
      }
      return stockInfo;
    }


    // Takes in a Map of tickers and their shares values in the portfolio, and
    private String simplifyToBuyOrSell(Map<String, Double> stockInfo, String simpleCommandLine) {
      for (Map.Entry<String, Double> entry : stockInfo.entrySet()) {
        // ensure the amount is not zero as you can't actually buy that amount of a stock.
        if (entry.getValue() > 0) {
          simpleCommandLine += ",buy " + entry.getKey() + " " + entry.getValue();
        }
        if (entry.getValue() < 0) {
          simpleCommandLine += ",sell " + entry.getKey() + " " + entry.getValue() * -1;
        }
      }
      return simpleCommandLine;
    }


    /**
     * Returns the command line for this {@code DateCommandStorage}.
     *
     * @return the command line for this date.
     */
    public String getCommandLine() {
      return commandLine;
    }

    /**
     * Returns the date for this command line.
     *
     * @return the date for this command line
     */
    public LocalDate getDate() {
      return date;
    }

    // This should ONLY BE USED FOR CHECKING if a sell is valid

    /**
     * Removes the last command. this was made with the express PURPOSE of removing an invalid sell
     * command.
     */
    public void removeLastCommand() {
      if (commandLine.contains(",")) {
        commandLine = commandLine.substring(0, commandLine.lastIndexOf(","));
      }
      else {
        commandLine = "";
      }
    }

    /**
     * Documents a buy with the given stock and amount.
     */
    public void buy(String stock, double amount) {
      commandLine += ",buy " + stock + " " + amount;
      removeFirst();
    }

    /**
     * Documents a sell with the given stock and amount.
     */
    public void sell(String stock, double amount) {
      commandLine += ",sell " + stock + " " + amount;
      removeFirst();
    }

    /**
     * Documents a re-balance with the given stocks and percentage weight for each stock.
     */
    public void reBalance(Map<Stock, Double> stocks) {
      String command = ",rebalance";
      validatePercentages(stocks);
      for (Map.Entry<Stock, Double> entry : stocks.entrySet()) {
        // add the stock and percentage that it has been waited
        command += " " + entry.getKey().getTicker() + " " + entry.getValue();
      }
      commandLine += command;
      removeFirst();
    }


    // Removes first comma, to make for easier parsing.
    private void removeFirst() {
      if (commandLine.indexOf(",") == 0) {
        commandLine = commandLine.substring(1);
      }
    }
  }

}