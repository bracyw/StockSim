package model.portfolio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.FormattingUtils;
import model.OODStocks;
import model.stock.Stock;


/**
 * The {@code SimplePortfolio} object is used for allowing storage of specific stocks and quantities
 * of those stocks. It allows for the ability to buy and sell stocks, to get the total value of
 * the all stocks currently owned on a given date (assumes you have
 * owned the stocks the entire time),
 * and to get the human-readable representation of stock owned.
 */
public class SimplePortfolio implements Portfolio {
  private final Map<String, Double> investments;

  /**
   * Constructs a new {@code SimplePortfolio} object with no stocks. The investments field is
   * initialized to an empty Map. The Map is used because it allows for the ability to
   * store the stock name and the quantity of that stock, and ensures that there are no duplicate
   * stocks.
   */
  public SimplePortfolio() {
    investments = new LinkedHashMap<>();
  }

  SimplePortfolio(Map<String, Double> investments) {
    this.investments = investments;
  }

  @Override
  public double totalValue(LocalDate date) throws IllegalArgumentException {
    double value = 0;
    if (date.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Date cannot be after current date");
    }

    for (Map.Entry<String, Double> entry : investments.entrySet()) {
      try {
        value += OODStocks.getStock(entry.getKey()).getPrice(date) * entry.getValue();
      } catch (Exception e) {
        if (e.getMessage().contains("Market was closed ")) {
          value += Double.parseDouble(e.getMessage().
                  substring(e.getMessage().lastIndexOf(" ") + 1)) * entry.getValue();
        }
      }
    }
    return value;
  }

  //possible to delete
  @Override
  public double getValue(String stockName, LocalDate date) throws IllegalArgumentException {
    double price;
    try {
      price = OODStocks.getStock(stockName).getPrice(date);
    } catch (Exception e) {
      if (e.getMessage().equals("No SimpleStockModel.Stock exists") ||
              e.getMessage().equals("Cannot find data for given date")) {
        throw new IllegalArgumentException("Invalid date or stock");
      }
      price =
              Double.parseDouble(e.getMessage().substring(e.getMessage().lastIndexOf(" ")));
    }
    if (investments.get(stockName) == null) {
      throw new IllegalArgumentException(String.format("Stock is not in " +
              "portfolio, price of stock is %.4f", price));
    }
    return price * investments.get(stockName);
  }

  @Override
  public double buy(String stockName, double amount) throws IllegalArgumentException {
    Stock simple;
    if (amount <= 0) {
      throw new IllegalArgumentException("Amount cannot buy negative stock");
    }
    simple = OODStocks.getStockOrMakeNew(stockName);
    if (investments.containsKey(stockName)) {
      investments.put(stockName, investments.get(stockName) + amount);
    } else {
      investments.put(stockName, amount);
    }
    return simple.getPrice(simple.getRecentDate()) * amount;
  }

  @Override
  public double sell(String stockName, double amount) throws IllegalArgumentException {
    if (amount <= 0) {
      throw new IllegalArgumentException("Amount cannot buy negative stock");
    }
    if (!investments.containsKey(stockName)) {
      throw new IllegalArgumentException("Does not contain that stock");
    }
    if (investments.get(stockName) < amount) {
      throw new IllegalArgumentException("Cannot sell more stock than you own");
    }
    Stock simple = OODStocks.getStock(stockName);
    double newShares = investments.get(stockName) - amount;
    if (newShares == 0) {
      investments.remove(stockName);
    } else {
      investments.put(stockName, investments.get(stockName) - amount);
    }
    return simple.getPrice(simple.getRecentDate()) * amount;
  }

  /**
   * Returns a human-readable representation of the stocks in the portfolio. The representation
   * includes the stock ticker, the number of shares of that stock, and the dollar value of that
   * stock, all formatted in a table.
   *
   * @return a human-readable representation of the stocks in the portfolio (a table of stocks owned
   *       and their values).
   */
  @Override
  public String toString() {
    if (investments.isEmpty()) {
      return "Portfolio is empty";
    }
    String col1Header = "Ticker";
    String col2Header = "Shares";
    String col3Header = "Dollar Value";

    // Add all the tickers in order
    List<String> col1 = this.getAllTickers();
    col1.add(0, col1Header);
    // Add all the shares in order
    List<String> col2 = this.getAllShares();
    col2.add(0, col2Header);
    // Add the dollar amount in order
    List<String> col3 = this.getAllValues(null);
    col3.add(0, col3Header);

    List<String> table = this.addCols(col1, col2);
    table = this.addCols(table, col3);

    String title = "Portfolio is currently holding:";

    return new FormattingUtils().createTable(table, title);
  }

  /**
   * Returns all the ticker values, of stocks currently owned, as strings in a list.
   *
   * @return the ticker values of stocks owned in a list
   */
  protected List<String> getAllTickers() {
    List<String> tickerValues = new ArrayList<>();
    // Add all the tickers in order
    for (Map.Entry<String, Double> entry : investments.entrySet()) {
      tickerValues.add(entry.getKey());
    }
    return tickerValues;
  }

  /**
   * Return the amount of shares owned of each stock, in the ORDER of which they were bought.
   *
   * @return a list of the amount of shares owned for each stock.
   */
  protected List<String> getAllShares() {
    List<String> amounts = new ArrayList<>();
    for (Map.Entry<String, Double> entry : investments.entrySet()) {
      amounts.add(String.format("%.4f", entry.getValue()));
    }
    return amounts;
  }

  /**
   * Returns the values of all stocks owned on the specified date. If the given date is null
   * returns the most recent price of the given stock.
   *
   * @return the total value of each stock owned on a specific date, individually listed as strings
   *       in a list.
   */
  protected List<String> getAllValues(LocalDate date) {
    List<String> values = new ArrayList<>();
    for (Map.Entry<String, Double> entry : investments.entrySet()) {
      Stock currentStock = OODStocks.getStock(entry.getKey());
      double price;
      if (date == null) {
        price = currentStock.getPrice();
      } else {
        price = currentStock.fetchClosestStockPrice(date);
      }

      values.add(String.format("%.4f", price * entry.getValue()));
    }
    return values;
  }


  /**
   * Adds two lists of {@code String} each representing a column, and add them together
   * into an evenly spaced list representing a table with two rows (evenly spaced
   * meaning that each {@code String} in the list will be the length of the largest
   * String in its column, by using the string format function).
   *
   * @param col1 the first column, represented by a list of strings
   * @param col2 the second column, represented by a list of strings.
   * @return a list of strings representing a formated table with two columns.
   */
  protected List<String> addCols(List<String> col1, List<String> col2) {
    if (col1.size() != col2.size()) {
      throw new IllegalArgumentException("Rows do not match");
    }

    // Find the maximum length of strings in each column
    int maxCol1Length = 0;
    int maxCol2Length = 0;
    for (int i = 0; i < col1.size(); i++) {
      maxCol1Length = Math.max(maxCol1Length, col1.get(i).length());
      maxCol2Length = Math.max(maxCol2Length, col2.get(i).length());
    }

    // set one space of value for each
    maxCol1Length++;
    maxCol2Length++;

    List<String> concatList = new ArrayList<>();

    // Calculate the width needed for each column to ensure even separation
    String format = "%-" + maxCol1Length + "s" + "%" + maxCol2Length + "s";

    for (int i = 0; i < col1.size(); i++) {
      // Format each column based on the calculated width
      concatList.add(String.format(format, col1.get(i), col2.get(i)));
    }

    return concatList;
  }


  @Override
  public Portfolio clone() {
    return new SimplePortfolio(new LinkedHashMap<>(investments));
  }

  @Override
  public HashMap<String, Double> getAmount(LocalDate time) {
    return new LinkedHashMap<>(investments);
  }
}