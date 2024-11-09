package model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import model.stock.Stock;
import model.stock.StockAlreadyExistsException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the OODStocks class and methods.
 */
public class OODStocksTest {
  Stock aPPLModelStock;
  Stock mSFTModelStock;
  Stock vModelStock;
  Stock gOOGModelStock;
  Map<String, Stock> stocks;

  @Before
  public void setUp() throws Exception {
    aPPLModelStock = new ModelStock("AAPL");
    mSFTModelStock = new ModelStock("MSFT");
    vModelStock = new ModelStock("V");
    gOOGModelStock = new ModelStock("GOOG");
    stocks = new HashMap<>();
    assertEquals(stocks, OODStocks.getStocks());
  }

  @After
  public void tearDown() {
    for (Stock stock : OODStocks.getStocks().values()) {
      OODStocks.removeStock(stock.getTicker());
    }
  }

  @Test
  public void putStock() {
    OODStocks.putStock(aPPLModelStock);
    stocks.put("AAPL", aPPLModelStock);
    assertEquals(stocks, OODStocks.getStocks());
    assertEquals(aPPLModelStock, OODStocks.getStock("AAPL"));

    OODStocks.putStock(mSFTModelStock);
    stocks.put("MSFT", mSFTModelStock);
    assertEquals(stocks, OODStocks.getStocks());
    assertEquals(mSFTModelStock, OODStocks.getStock("MSFT"));

    OODStocks.putStock(vModelStock);
    stocks.put("V", vModelStock);
    assertEquals(stocks, OODStocks.getStocks());
    assertEquals(vModelStock, OODStocks.getStock("V"));

    OODStocks.putStock(gOOGModelStock);
    stocks.put("GOOG", gOOGModelStock);
    assertEquals(stocks, OODStocks.getStocks());
    assertEquals(gOOGModelStock, OODStocks.getStock("GOOG"));
  }

  @Test
  public void create() {
    try {
      OODStocks.create("QQQ");
      File creation = new File(System.getProperty("user.dir") + "/StockInfo/QQQ.txt");
      assertTrue(creation.exists());
      creation.delete();

    } catch (Exception e) {
      assertEquals(e.getMessage(), "Rate limit for API reached.");
    }
  }

  @Test
  public void getStock() {
    OODStocks.putStock(aPPLModelStock);
    stocks.put("AAPL", aPPLModelStock);
    assertEquals(aPPLModelStock, OODStocks.getStock("AAPL"));

    OODStocks.putStock(mSFTModelStock);
    stocks.put("MSFT", mSFTModelStock);
    assertEquals(mSFTModelStock, OODStocks.getStock("MSFT"));

    OODStocks.putStock(vModelStock);
    stocks.put("V", vModelStock);
    assertEquals(vModelStock, OODStocks.getStock("V"));

    OODStocks.putStock(gOOGModelStock);
    stocks.put("GOOG", gOOGModelStock);
    assertEquals(gOOGModelStock, OODStocks.getStock("GOOG"));
  }

  @Test
  public void getStocksEmpty() {
    assertEquals(stocks, OODStocks.getStocks());
  }

  @Test
  public void removeStock() {
    OODStocks.putStock(aPPLModelStock);
    stocks.put("AAPL", aPPLModelStock);
    assertEquals(aPPLModelStock, OODStocks.getStock("AAPL"));

    OODStocks.putStock(mSFTModelStock);
    stocks.put("MSFT", mSFTModelStock);
    assertEquals(mSFTModelStock, OODStocks.getStock("MSFT"));

    OODStocks.putStock(vModelStock);
    stocks.put("V", vModelStock);
    assertEquals(vModelStock, OODStocks.getStock("V"));

    OODStocks.putStock(gOOGModelStock);
    stocks.put("GOOG", gOOGModelStock);
    assertEquals(gOOGModelStock, OODStocks.getStock("GOOG"));

    assertEquals(aPPLModelStock, OODStocks.removeStock("AAPL"));
    stocks.remove("AAPL");
    assertEquals(stocks, OODStocks.getStocks());

    assertEquals(mSFTModelStock, OODStocks.removeStock("MSFT"));
    stocks.remove("MSFT");
    assertEquals(stocks, OODStocks.getStocks());

    assertEquals(vModelStock, OODStocks.removeStock("V"));
    stocks.remove("V");
    assertEquals(stocks, OODStocks.getStocks());

    assertEquals(gOOGModelStock, OODStocks.removeStock("GOOG"));
    stocks.remove("GOOG");
    assertEquals(stocks, OODStocks.getStocks());
  }


  class ModelStock implements Stock {
    public final StringBuilder stockInfo;
    private final String symbol;
    private final LocalDate recentDate; // last day of data
    private final LocalDate IPODate; // earliest day of data


    /**
     * Constructor that mocks SimpleStock without any coupling with {@code OODStocks}.
     *
     * @param tckr the ticker symbol of the stock, used to identify the stock
     * @throws StockAlreadyExistsException if the stock has already been created, and is stored in
     *                                     the {@code OODStocks} singleton class.
     */
    public ModelStock(String tckr) throws StockAlreadyExistsException {
      this.symbol = tckr;
      try {
        // creating path
        Path filePath = Paths.get(OODStocks.DIRECTORYPATH + tckr + ".txt");
        // reading file from given path
        this.stockInfo = new StringBuilder(Files.readString(filePath));
        // printing the content
        recentDate = LocalDate.parse(stockInfo.substring(38, 48));
        IPODate = LocalDate.parse(stockInfo.substring(stockInfo.lastIndexOf("-") - 7,
                stockInfo.lastIndexOf("-") + 3));
      } catch (IOException f) {
        throw new RuntimeException("vStock is not in the database");
      }
    }

    @Override
    public double gainLoss(LocalDate fromDate, LocalDate toDate) throws IllegalArgumentException {
      return 0;
    }

    @Override
    public double xDayAvg(LocalDate startDate, int days) throws IllegalArgumentException {
      return 0;
    }

    @Override
    public HashSet<LocalDate> defDayCrossover(LocalDate from, LocalDate to)
            throws IllegalArgumentException {
      return null;
    }

    @Override
    public HashSet<LocalDate> xDayCrossover(LocalDate from, LocalDate to, int days)
            throws IllegalArgumentException {
      return null;
    }

    @Override
    public String getTicker() {
      return this.symbol;
    }

    @Override
    public double getPrice() {
      return 0;
    }

    @Override
    public double getPrice(LocalDate date) throws DateTimeException {
      return 0;
    }

    @Override
    public LocalDate getRecentDate() {
      return null;
    }

    @Override
    public LocalDate getIPODate() {
      return null;
    }

    @Override
    public double fetchClosestStockPrice(LocalDate dateAt) throws IllegalArgumentException {
      return 0;
    }

    @Override
    public String plot(LocalDate date1, LocalDate date2) {
      return "";
    }
  }
}