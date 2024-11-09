package model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import model.stock.SimpleStock;
import model.stock.Stock;

/**
 * The {@code OODStocks} object is used for storing all the stocks that have been made during
 * a stock programs run time. It allows for the assurance, that multiple of the same stock are
 * not created (see the constructor {@code SimpleStock(String tckr)}).
 * This class also has the directory path to the where the stock info is stored
 * (which is public because stock info can be found anywhere, but BE MINDFUL for future
 * implementation that this directory path is public).
 * This class also offers the ability to {@code create(String)} add new stock info file, and
 * manipulate the stocks field that keeps track of all stocks made.
 */
public class OODStocks {
  public final static String DIRECTORYPATH = System.getProperty("user.dir") + "/StockInfo/";
  private final static Map<String, Stock> stocks = new HashMap<>();

  private OODStocks() {
  }

  /**
   * Adds a stock, to the stocks being kept track of.
   *
   * @param stock the stock to be added to the stocks.
   */
  static public void putStock(Stock stock) {
    stocks.put(stock.getTicker(), stock);
  }

  /**
   * Creates a new stock file with the given ticker.
   * If the stock file does not exist, then the method will fetch the stock data from the
   * API and write it to the file. If the stock data is not found, then the method will return
   * without creating a stock info.
   *
   * @param ticker the ticker of the stock to be created.
   */
  public static void create(String ticker) {
    File stockFile = new File(DIRECTORYPATH + ticker + ".txt");

    String stockData = getStockData(ticker);
    if (stockData == null) {
      return;
    }
    String badAPICallSpecificString = "rate limit";
    if (stockData.contains(badAPICallSpecificString)) {
      throw new RuntimeException("Rate limit for API reached.");
    }

    if (stockFile.exists() || stockExists(ticker)) {
      stockFile.delete();
      OODStocks.removeStock(ticker);
      stockFile = new File(DIRECTORYPATH + ticker + ".txt");
    }

    writeToFile(stockFile, stockData);
  }

  // checks if a given stock ticker exist in our local db.
  private static boolean stockExists(String ticker) {
    return stocks.containsKey(ticker);
  }

  // Actually get the stock date from the api, for the given ticker.
  private static String getStockData(String ticker) {
    StringBuilder output = new StringBuilder();
    URL url = createAPIUrl(ticker);

    try (InputStream in = url.openStream()) {
      int b;
      while ((b = in.read()) != -1) {
        output.append((char) b);
      }
    } catch (IOException e) {
      System.err.println("No price data found for " + ticker);
      return null;
    }

    if (output.toString().contains("Invalid API")) {
      throw new RuntimeException("INVALID API OR STOCK CALL");
    }

    return output.toString();
  }

  // Writes the given data to the file.
  private static boolean writeToFile(File file, String data) {
    try (FileWriter writer = new FileWriter(file)) {
      writer.write(data);
      return true;
    } catch (IOException e) {
      System.err.println("An error occurred while writing to the file.");
      e.printStackTrace();
      return false;
    }
  }

  // Currently creates the url for Alpha Vantage API.
  private static URL createAPIUrl(String ticker) throws RuntimeException {
    // given API Key: W0M1JOKC82EZEQA8
    String apiKey = "FDI392BFlS3";
    URL url;

    try {
      url = new URL("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY"
              + "&symbol=" + ticker + "&outputsize=full&apikey=" + apiKey + "&datatype=csv");
    } catch (MalformedURLException e) {
      throw new RuntimeException("The Alphavantage API has either changed or no longer works");
    }

    return url;
  }


  /**
   * Returns the stock object with the given ticker. If the stock does not exist, then a
   * {@code IllegalArgumentException} is thrown.
   *
   * @param ticker the ticker of the stock to be returned.
   * @return the stock object with the given ticker.
   * @throws IllegalArgumentException if the stock does not exist.
   */
  public static Stock getStock(String ticker) throws IllegalArgumentException {
    Stock ret = stocks.get(ticker);
    if (ret == null) {
      throw new IllegalArgumentException("No Stock exists");
    }
    return ret;
  }


  /**
   * Returns a copy of the stocks being kept track of.
   *
   * @return a copy of the stocks being kept track of.
   */
  static public Map<String, Stock> getStocks() {
    return new HashMap<String, Stock>(stocks);
  }

  /**
   * Removes the stock with the given ticker from the stocks being kept track of.
   *
   * @param ticker the ticker of the stock to be removed.
   * @return the stock that was removed.
   * @throws IllegalArgumentException if the stock does not exist.
   */
  public static Stock removeStock(String ticker) throws IllegalArgumentException {
    return stocks.remove(ticker);
  }

  /**
   * Returns a {@code Stock} with the given ticker name, either creating a new stock if there
   * hasn't been one made, or returning the stock with the ticker, that is already in the database.
   *
   * @param ticker the ticker of the stock.
   * @return the stock with the given ticker.
   * @throws IllegalArgumentException if the stock does not exist locally.
   */
  public static Stock getStockOrMakeNew(String ticker) throws IllegalArgumentException {
    Stock actualStock;
    try {
      actualStock = OODStocks.getStock(ticker);
    } catch (Exception e) {
      try {
        actualStock = new SimpleStock(ticker);
      } catch (Exception e1) {
        throw new IllegalArgumentException("Stock does not exist in database");
      }
    }
    return actualStock;
  }
}


