package controller;

import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import model.BetterStockModel;
import view.AdvancedStockViewImpl;
import view.StockView;

import static org.junit.Assert.assertEquals;

/**
 * Tests BetterController class.
 */
public class BetterControllerTest extends AbstractControllerTest {
  BetterController noAccountController;
  BetterController accountController;
  BetterStockModel stockModelNoAccount;
  BetterStockModel stockModelAccount;
  List<String> modelLogNoAccount;
  List<String> modelLogAccount;
  Appendable stringBuilder;
  AdvancedStockViewImpl advancedStockView;
  Appendable appendableForAdvancedView;
  Readable inputForBetterControllerAccount;
  Readable inputForBetterControllerNoAccount;

  @Before
  public void setUp() {
    appendableForAdvancedView = new StringBuilder();
    advancedStockView = new AdvancedStockViewImpl(appendableForAdvancedView);
    modelLogNoAccount = new ArrayList<>();
    stockModelNoAccount = new MockBetterStockModel(modelLogNoAccount, false, false);
    modelLogAccount = new ArrayList<>();
    stockModelAccount = new MockBetterStockModel(modelLogAccount, true, false);
    inputForBetterControllerNoAccount = new StringReader("");
    noAccountController =
            new BetterController(stockModelNoAccount, inputForBetterControllerNoAccount,
                    advancedStockView);
    inputForBetterControllerAccount = new StringReader("");
    accountController = new BetterController(stockModelAccount, inputForBetterControllerAccount,
            advancedStockView);
  }

  @Test
  public void control() {
    inputForBetterControllerNoAccount = new StringReader("create-portfolio Wyatt buy AAPL 30 " +
            "log-out " + "quit");
    noAccountController =
            new BetterController(stockModelNoAccount, inputForBetterControllerNoAccount,
                    advancedStockView);
    noAccountController.control();
    assertEquals(welcomeString + this.getNoPortfolioMenuString() +
            "\n" +
            "\n" +
            "Please enter your next command: created portfolio Wyatt\n" +
            "\n" +
            "Please enter your next command: Bought 30 of AAPL, worth $3.0000\n" +
            "Please enter your next command: successfully logged out\n" +
            "Please enter your next command: \n" +
            "Thank you for using the OODStocks Program", appendableForAdvancedView.toString());
  }

  @Test
  public void composition() {
    Scanner date = new Scanner(new StringReader("2023-01-01"));
    accountController.selectedPortfolio("composition", date);
    assertEquals("composition called with date: 2023-01-01", modelLogAccount.get(1));
    assertEquals("Composition of portfolio current \n" +
            "2023-01-01\n", appendableForAdvancedView.toString());
  }

  @Test
  public void distribution() {
    Scanner date = new Scanner(new StringReader("2023-01-01"));
    accountController.selectedPortfolio("distribution", date);
    assertEquals("distribution called with date: 2023-01-01", modelLogAccount.get(1));
    assertEquals("Distribution of portfolio current \n" +
            "2023-01-01\n", appendableForAdvancedView.toString());
  }

  @Test
  public void saveCurrentPortfolio() {
    Scanner empty = new Scanner(new StringReader(""));
    accountController.selectedPortfolio("save", empty);
    assertEquals("saveCurrentPortfolio called", modelLogAccount.get(0));
    assertEquals("Successfully saved current portfolio. \n",
            appendableForAdvancedView.toString());
  }

  @Test
  public void buyBalanced() {
    Scanner date = new Scanner(new StringReader("1000 2023-01-01 AAPL 0.5 GOOGL 0.5"));
    accountController.selectedPortfolio("buy-balanced", date);
    assertEquals(
            "buyBalanced called with stocksNWeights: GOOGL 0.5 AAPL 0.5  dateAt: " +
                    "2023-01-01 amount: 1000.0",
            modelLogAccount.get(0));
    assertEquals(
            "Successfully bought the stocks, with shown amounts, and requested relative " +
                    "weights: \n" +
                    "     GOOGL: 0.5, 0.5%\n" +
                    "     AAPL: 0.5, 0.5%\n", appendableForAdvancedView.toString());
  }

  @Test
  public void reBalanced() {
    Scanner date = new Scanner(new StringReader("2023-01-01 AAPL 0.5 GOOGL 0.5"));
    accountController.selectedPortfolio("re-balance", date);
    assertEquals("reBalance called with stocksNWeights: GOOGL 0.5 AAPL 0.5  dateAt: " +
                    "2023-01-01",
            modelLogAccount.get(0));
    assertEquals("Successfully rebalanced requested stocks. \n",
            appendableForAdvancedView.toString());
  }

  @Test
  public void plotPortfolio() {
    Scanner date = new Scanner(new StringReader("2023-01-01 2023-01-02"));
    accountController.selectedPortfolio("plot-portfolio", date);
    assertEquals("plotPortfolio called with date1: 2023-01-01 date2: 2023-01-02",
            modelLogAccount.get(1));
    assertEquals("Performance of portfolio current from 2023-01-01 to 2023-01-02: \n" +
            " \n" +
            "2023-01-01 2023-01-02\n", appendableForAdvancedView.toString());
  }

  @Test
  public void buyDate() {
    Scanner date = new Scanner(new StringReader("AAPL 100 2023-01-01"));
    accountController.selectedPortfolio("buy-date", date);
    assertEquals("buyDate called with ticker: AAPL shares: 100.0 date: 2023-01-01",
            modelLogAccount.get(0));
    assertEquals("Bought 100 of AAPL, worth $1.0000", appendableForAdvancedView.toString());
  }

  @Test
  public void sellDate() {
    Scanner date = new Scanner(new StringReader("AAPL 100 2023-01-01"));
    accountController.selectedPortfolio("sell-date", date);
    assertEquals("sellDate called with ticker: AAPL shares: 100.0 date: 2023-01-01",
            modelLogAccount.get(0));
    assertEquals("Sold 100 of AAPL, worth $2.00000", appendableForAdvancedView.toString());
  }

  @Test
  public void plotStockValue() {
    Scanner date = new Scanner(new StringReader("AAPL 2023-01-01 2023-01-02"));
    accountController.selectedPortfolio("plot-stock", date);
    assertEquals("plotStockValue called with ticker: AAPL date1: 2023-01-01 date2: " +
                    "2023-01-02",
            modelLogAccount.get(0));
    assertEquals("Performance of stock AAPL from 2023-01-01 to 2023-01-02: \n" +
            " \n" +
            "AAPL 2023-01-01 2023-01-02\n", appendableForAdvancedView.toString());
  }

  @Override
  protected String getNoPortfolioMenuString() {
    return "Please select an option from the following menu:\n" +
            "     To download information on a stock:\n" +
            "          download <stock ticker>\n" +
            "     Please give all dates in:\n" +
            "          YYYY-MM-DD format\n" +
            "     To create a portfolio:\n" +
            "          create-portfolio <portfolio name>\n" +
            "     To set your current portfolio:\n" +
            "          set-portfolio <portfolio name>\n" +
            "     To plot the value of a stock from one date to another:\n" +
            "          plot-stock <ticker> <date1> <date2>\n" +
            "     To view the contents of a specific portfolio:\n" +
            "          view-specific-portfolio <name of portfolio>\n" +
            "     To see all the portfolios available to select:\n" +
            "          list-all-portfolios\n" +
            "     To see xDaysAverage:\n" +
            "          avg <ticker> <date> <amount of days>\n" +
            "     To see x day crossover for a stock:\n" +
            "          cross-x <ticker> <from date> <to date> <amount of days>\n" +
            "     To see the 30 day crossover for a stock:\n" +
            "          cross-default <ticker> <from date> <to date>\n" +
            "     To see the gain or loss for a given ticker:\n" +
            "          gain-loss <ticker> <from date> <to date>\n" +
            "     To see the price of a stock on certain date:\n" +
            "          get-price <ticker> <date>\n" +
            "     To see the menu again:\n" +
            "          menu\n" +
            "     To quit the program:\n" +
            "          quit or q\n";
  }

  @Override
  protected String getSelectedPortfolioMenuString() {
    return "Please select an option from the following menu:\n" +
            "     To download information on a stock:\n" +
            "          download <stock ticker>\n" +
            "     Please give all dates in:\n" +
            "          YYYY-MM-DD format\n" +
            "     To buy a certain amount of stock (must be a WHOLE NUMBER):\n" +
            "          buy <stock ticker> <amount of stock>\n" +
            "     To sell a certain amount of stock you own (must be a WHOLE NUMBER):\n" +
            "          sell <stock ticker> <amount of stock>\n" +
            "     To see the contents of your portfolio:\n" +
            "          view-portfolio\n" +
            "     To see the total value of your portfolio:\n" +
            "          total-value <date>\n" +
            "     To log out:\n" +
            "          log-out\n" +
            "     To see the composition of your current portfolio on a given date:\n" +
            "          composition <date>\n" +
            "     To see the distribution of your current portfolio on a given date:\n" +
            "          distribution <date>\n" +
            "     To save your current portfolion (this WILL OVERWRITE LAST SAVE FOR THIS PORTFOLIO"
            +
            " NAME):\n" +
            "          save\n" +
            "     To buy multiple stocks with a certain amount of with percentage " +
            "spend on each:\n" +
            "          buy-balanced <amount of money> <date> <ticker name1> " +
            "<weight1> <ticker name2> "  +
            "<weight2>...\n" +
            "     To re-balance stocks already in your portfolio to the given weights:\n" +
            "          re-balance <date> <ticker name1> <weight1> <ticker name2> <weight2>...\n" +
            "     To plot the value of your current portfolio from date1 to date2:\n" +
            "          plot-portfolio <date1> <date2>\n" +
            "     To buy a stock on a given date:\n" +
            "          buy-date <ticker> <amount> <date>\n" +
            "     To sell a stock on a given date:\n" +
            "          sell-date <ticker> <amount> <date>\n" +
            "     To plot the value of a stock from one date to another:\n" +
            "          plot-stock <ticker> <date1> <date2>\n" +
            "     To view the contents of a specific portfolio:\n" +
            "          view-specific-portfolio <name of portfolio>\n" +
            "     To see all the portfolios available to select:\n" +
            "          list-all-portfolios\n" +
            "     To see xDaysAverage:\n" +
            "          avg <ticker> <date> <amount of days>\n" +
            "     To see x day crossover for a stock:\n" +
            "          cross-x <ticker> <from date> <to date> <amount of days>\n" +
            "     To see the 30 day crossover for a stock:\n" +
            "          cross-default <ticker> <from date> <to date>\n" +
            "     To see the gain or loss for a given ticker:\n" +
            "          gain-loss <ticker> <from date> <to date>\n" +
            "     To see the price of a stock on certain date:\n" +
            "          get-price <ticker> <date>\n" +
            "     To see the menu again:\n" +
            "          menu\n" +
            "     To quit the program:\n" +
            "          quit or q\n";
  }

  @Override
  protected StockView getStockView(Appendable appendableForView) {
    return new AdvancedStockViewImpl(appendableForView);
  }

  @Override
  protected BetterStockModel getMockStockModel(List<String> mockModelLog, boolean b, boolean b1) {
    return new MockBetterStockModel(mockModelLog, b, b1);
  }

  static class MockBetterStockModel extends MockStockModel
          implements BetterStockModel {

    /**
     * Constructs a {@code MockStockModel} object with a log to keep track of methods called.
     * Also takes in a in boolean to signal whether there is a portfolio (useful for testing
     * process command).
     *
     * @param log                the log, which will be mutated.
     * @param portfolioExist     a boolean flag to signal whether this model has a portfolio.
     * @param hasOtherPortfolios does it have other ports?
     */
    public MockBetterStockModel(List<String> log, boolean portfolioExist,
                                boolean hasOtherPortfolios) {
      super(log, portfolioExist, hasOtherPortfolios);
    }

    @Override
    public String composition(LocalDate date) {
      log.add("composition called with date: " + date.toString());
      return date.toString();
    }

    @Override
    public String distribution(LocalDate date) {
      log.add("distribution called with date: " + date.toString());
      return date.toString();
    }

    @Override
    public void saveCurrentPortfolio() {
      hasOtherPortfolios = true;
      log.add("saveCurrentPortfolio called");
    }

    @Override
    public Map<String, Double> buyBalanced(Map<String, Double> stocksNWeights, LocalDate dateAt,
                                           double amount) throws IllegalArgumentException {
      String stockNWieghString = "";
      for (Map.Entry<String, Double> entry : stocksNWeights.entrySet()) {
        stockNWieghString += entry.getKey() + " " + entry.getValue() + " ";
      }
      log.add("buyBalanced called with stocksNWeights: " + stockNWieghString +
              " dateAt: " + dateAt.toString() + " amount: " + amount);
      return stocksNWeights;
    }

    @Override
    public String plotPortfolio(LocalDate date1, LocalDate date2) {
      log.add("plotPortfolio called with date1: " + date1.toString() + " date2: " +
              date2.toString());
      return date1.toString() + " " + date2.toString();
    }

    @Override
    public String plotStockValue(String ticker, LocalDate date1, LocalDate date2) {
      log.add("plotStockValue called with ticker: " + ticker + " date1: " + date1.toString() +
              " date2: " + date2.toString());
      return ticker + " " + date1.toString() + " " + date2.toString();
    }

    @Override
    public double buyDate(String ticker, double shares, LocalDate date) {
      log.add("buyDate called with ticker: " + ticker + " shares: " + shares + " date: " +
              date.toString());
      return 1;
    }

    @Override
    public double sellDate(String ticker, double shares, LocalDate date) {
      log.add("sellDate called with ticker: " + ticker + " shares: " + shares + " date: " +
              date.toString());
      return 2;
    }

    @Override
    public String reBalance(Map<String, Double> stocksNWeights, LocalDate dateAt)
            throws IllegalArgumentException {
      String stockNWieghString = "";
      for (Map.Entry<String, Double> entry : stocksNWeights.entrySet()) {
        stockNWieghString += entry.getKey() + " " + entry.getValue() + " ";
      }
      log.add("reBalance called with stocksNWeights: " + stockNWieghString + " dateAt: " +
              dateAt.toString());
      return stockNWieghString;
    }

    @Override
    public String getCurrentPortfolioName() {
      log.add("getCurrentPortfolioName called");
      return "current";
    }

    @Override
    public List<String> listAllPortfolios() {
      return List.of();
    }

    @Override
    public String viewPortfolio(String name) {
      return "";
    }
  }
}