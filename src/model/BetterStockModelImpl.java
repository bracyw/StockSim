package model;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.portfolio.BetterPortfolio;
import model.portfolio.BetterPortfolioImpl;
import model.stock.Stock;


/**
 * implementation of the better stock model impl.
 */
public class BetterStockModelImpl extends AbstractStockModel implements BetterStockModel {
  private Map<String, BetterPortfolio> viewedBetterPortfolios;
  private BetterPortfolio currentBetterAct; // load it to this


  /**
   * base constructors.
   */
  public BetterStockModelImpl() {
    super();
    viewedBetterPortfolios = new HashMap<>();
    currentBetterAct = null;
  }

  @Override // this current account saves
  protected void setCurrentAct(String portKey, boolean setNull) throws IllegalArgumentException {
    if (setNull) {
      currentBetterAct = null;
      super.setCurrentAct(portKey, setNull);
      return;
    }
    try {
      super.setCurrentAct(portKey, false);
    } catch (IllegalArgumentException e) {
      new BetterPortfolioImpl(portKey);
      File fileName = new File(System.getProperty("user.dir")
              + "/PortfolioInfo/" + portKey + ".txt");
      if (fileName.exists()) {
        this.viewedPortfolios.put(portKey, new BetterPortfolioImpl(fileName));
        this.viewedBetterPortfolios.put(portKey, new BetterPortfolioImpl(fileName));
        super.setCurrentAct(portKey, false);
      } else {
        throw new IllegalArgumentException("Portfolio key does not exist no " +
                "portfolios found with that name");
      }

    }
    if (viewedBetterPortfolios.containsKey(portKey)) {
      currentBetterAct = viewedBetterPortfolios.get(portKey);
    } else {
      throw new IllegalArgumentException("Accounts in AbstractModel and BetterStockModelImpl " +
              "do not match: FATAL ERROR");
    }
  }

  @Override
  protected void addPortfolio(String portfolioName) {
    this.viewedPortfolios.put(portfolioName, new BetterPortfolioImpl(portfolioName));
    this.viewedBetterPortfolios.put(portfolioName, new BetterPortfolioImpl(portfolioName));
  }

  @Override
  protected double applyBuy(String ticker, double amt) {
    super.applyBuy(ticker, amt);
    return currentBetterAct.buy(ticker, amt);
  }

  @Override
  protected double applySell(String ticker, double amt) {
    super.applyBuy(ticker, amt);
    return currentBetterAct.sell(ticker, amt);
  }

  @Override
  public String composition(LocalDate date) {
    return currentBetterAct.composition(date);
  }

  @Override
  public String distribution(LocalDate date) {
    return currentBetterAct.distribution(date);
  }

  @Override
  public void saveCurrentPortfolio() throws IllegalArgumentException {
    if (currentBetterAct == null) {
      throw new IllegalArgumentException("Current account is not set!");
    }
    currentBetterAct.save();
  }

  @Override
  public Map<String, Double> buyBalanced(Map<String, Double> stocksNWeights,
                                         LocalDate dateAt, double amount)
          throws IllegalArgumentException {
    // convert String to stocks
    Map<Stock, Double> stocksNWeightsConvert = this.convertToStocks(stocksNWeights);
    // Call balance with those values
    Map<Stock, Double> stockNAmounts =
            currentBetterAct.balance(stocksNWeightsConvert, dateAt, amount);

    return this.convertToTickers(stockNAmounts);
  }

  // Convert a Map with keys as Stocks, to a Map with keys as tickers
  private <T> Map<String, T> convertToTickers(Map<Stock, T> stocksNWeightsConvert) {
    Map<String, T> withTickers = new HashMap<>();
    for (Stock stock : stocksNWeightsConvert.keySet()) {
      withTickers.put(stock.getTicker(), stocksNWeightsConvert.get(stock));
    }
    return withTickers;
  }


  // Converts a Map with keys as stock ticker, to a Map with keys as stocks
  private <T> Map<Stock, T> convertToStocks(Map<String, T> stocksNWeights) {
    Map<Stock, T> withStocks = new LinkedHashMap<>();
    for (String portKey : stocksNWeights.keySet()) {
      Stock actualStock = OODStocks.getStockOrMakeNew(portKey);
      withStocks.put(actualStock, stocksNWeights.get(portKey));
    }
    return withStocks;
  }


  @Override
  protected String getCurrentActInfo() {
    return currentBetterAct.toString();
  }


  @Override
  public String plotPortfolio(LocalDate date1, LocalDate date2) {
    return currentBetterAct.plot(date1, date2);
  }


  @Override
  public String plotStockValue(String ticker, LocalDate date1, LocalDate date2) {
    return OODStocks.getStockOrMakeNew(ticker).plot(date1, date2);
  }

  @Override
  public double buyDate(String ticker, double shares, LocalDate date) {
    validateLoggedIn();
    return currentBetterAct.buyDate(OODStocks.getStockOrMakeNew(ticker), shares, date);
  }

  @Override
  protected void validateLoggedIn() throws IllegalArgumentException {
    super.validateLoggedIn();
    if (currentBetterAct == null) {
      throw new IllegalArgumentException("No portfolio selected");
    }
  }

  @Override
  public double sellDate(String ticker, double shares, LocalDate date) {
    validateLoggedIn();
    return currentBetterAct.sellDate(OODStocks.getStockOrMakeNew(ticker), shares, date);
  }

  @Override
  public String reBalance(Map<String, Double> stocksNWeights, LocalDate dateAt)
          throws IllegalArgumentException {
    Map<Stock, Double> stocksNWeightsConvert = this.convertToStocks(stocksNWeights);
    currentBetterAct.reBalance(stocksNWeightsConvert, dateAt);
    return "Successfully balanced portfolio";
  }

  @Override
  public String getCurrentPortfolioName() {
    return currentBetterAct.getName();
  }

  //works!
  @Override
  public List<String> listAllPortfolios() {
    List<String> names = new ArrayList<>();
    for (Map.Entry<String, BetterPortfolio> entry : viewedBetterPortfolios.entrySet()) {
      names.add(entry.getKey());
    }
    File directory = new File(System.getProperty("user.dir") + "/PortfolioInfo");

    File[] files = directory.listFiles();
    for (File file : files) {
      String fileName = file.getName().substring(0, file.getName().indexOf("."));

      if (!fileName.equals("") && !names.contains(fileName)) {
        names.add(fileName);
      }
    }

    return names;
  }

  @Override
  public String viewPortfolio(String name) {
    String ogCurrentAct = null;
    if (currentBetterAct != null) {
      ogCurrentAct = currentBetterAct.getName();
    }
    this.setPortfolio(name);
    String tableForPort = this.getPortfolio();
    tableForPort = tableForPort.substring(tableForPort.indexOf("\n") + 1);
    this.logout();
    if (ogCurrentAct != null) {
      this.setPortfolio(ogCurrentAct);
    }
    String header = "Portfolio Info for " + name + ":\n";
    return header + tableForPort;
  }

  @Override
  public double totalValue(LocalDate evaluate) {
    validateLoggedIn();
    return currentBetterAct.totalValue(evaluate);
  }
}
