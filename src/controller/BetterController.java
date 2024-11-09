package controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import model.BetterStockModel;
import view.AdvancedStockView;

/**
 * This {@code BetterController} class uses a {@code BetterStockModel},
 * allowing for the tracking of portfolios over-time. Along with various enhancements from the
 * first iteration of the controller, {@code SimpleController} including,
 * re-balancing a variable amount of stocks, plotting the value of stocks and portfolio's, and
 * of various support for behaviour of the {@code BetterPortfolio} interface.
 * This class extends the core commands of the {@code SimpleController}, while the execution of
 * those commands are now based on a {@code BetterStockModel}, which has the functionality
 * of the {@code StockModel}, but implements that functionality differently
 * (see {@code BetterStockModel} for more).
 */
public class BetterController extends SimpleController {
  private final AdvancedStockView stockView;
  private final BetterStockModel model;

  /**
   * Constructs a new {@code BetterController} object with the given model, readable, and view.
   * If the model, readable, or stockView is null, an IllegalArgumentException is thrown.
   *
   * @param model     the {@code BetterStockModel} object to interact with stock data and user
   *                  information
   * @param readable  the Readable object for inputs.
   * @param stockView the {@code AdvancedStockView} object to display the results of the model's
   *                  actions.
   * @throws IllegalArgumentException if the model, readable, or stockView is null
   */
  public BetterController(BetterStockModel model, Readable readable, AdvancedStockView stockView)
          throws IllegalArgumentException {
    super(model, readable, stockView);
    this.model = model;
    this.stockView = stockView;
  }


  @Override
  protected void selectedPortfolio(String userInstruction, Scanner scanner) {
    switch (userInstruction) {
      case "composition": // for current portfolio, <date>
        this.handleComposition(scanner);
        break;
      case "distribution": // for current portfolio, <date>
        this.handleDistribution(scanner);
        break;
      case "save": // saves the current portfolio
        this.handleSave();
        break;
      case "buy-balanced": // <money> <ticker name> <weight> <ticker name> <weight> ... creates a
        this.handleBuyBalanced(scanner);
        break;
      case "re-balance": // <ticker name> <weight> <ticker name> <weight>
        this.handleReBalance(scanner);
        break;
      case "plot-portfolio": //<from date> <to date>
        this.handlePlotPortfolioValue(scanner);
        break;
      case "buy-date": // <name> <amount> <date>
        this.handleBuyOrSellDate(scanner, true);
        break;
      case "sell-date": // <name> <amount> <date>
        this.handleBuyOrSellDate(scanner, false);
        break;
      default:
        super.selectedPortfolio(userInstruction, scanner);
    }
  }

  // SELECTED PORTFOLIO HELPERS
  @Override
  protected void handleLogOut() {
    model.saveCurrentPortfolio();
    model.logout();
    stockView.logOut();
  }

  // Handles delegating to the model and view when the user wants to see the composition of their
  // portfolio
  private void handleComposition(Scanner scanner) {
    try {
      LocalDate date = LocalDate.parse(scanner.next());
      stockView.composition(model.getCurrentPortfolioName(), model.composition(date));
    } catch (Exception e) {
      stockView.displayError(e.getMessage());
    }
  }

  // Handles delegating to the model and view when the user wants to see the distribution of their
  // portfolio
  private void handleDistribution(Scanner scanner) {
    try {
      LocalDate date = LocalDate.parse(scanner.next());
      stockView.distribution(model.getCurrentPortfolioName(), model.distribution(date));
    } catch (Exception e) {
      stockView.displayError(e.getMessage());
    }
  }

  // Handles delegating to the model and view when the user wants to save their portfolio
  protected void handleSave() {
    try {
      model.saveCurrentPortfolio();
      stockView.saveCurrentPortfolio();
    } catch (Exception e) {
      stockView.displayError(e.getMessage());
    }
  }

  // Handles delegating to the model and view when the user wants to reBalance their portfolio
  private void handleBuyBalanced(Scanner sc) {
    try {
      double amountOfCashToSpend = sc.nextDouble();
      LocalDate date = LocalDate.parse(sc.next());
      HashMap<String, Double> stocksNWeights = validateAndGetStocksNWeights(sc);
      Map<String, Double> amountBought =
              model.buyBalanced(stocksNWeights, date, amountOfCashToSpend);
      stockView.buyBalanced(amountBought, stocksNWeights);
    } catch (Exception e) {
      stockView.displayError(e.getMessage());
    }
  }

  // Handles delegating to the model and view when the user wants to re-balance their portfolio
  private void handleReBalance(Scanner sc) {
    try {
      LocalDate date = LocalDate.parse(sc.next());
      HashMap<String, Double> stocksNWeights = validateAndGetStocksNWeights(sc);
      model.reBalance(stocksNWeights, date);
      stockView.reBalance();
    } catch (Exception e) {
      stockView.displayError(e.getMessage());
    }
  }

  //
  private HashMap<String, Double> validateAndGetStocksNWeights(Scanner sc) {
    HashMap<String, Double> stocksNWeights = new HashMap<>();
    String line = sc.nextLine();
    sc = new Scanner(line += " stop");
    while (!sc.hasNext("stop")) {
      String currentTicker = sc.next();
      if (sc.hasNextDouble()) {
        double weight = sc.nextDouble();
        stocksNWeights.put(currentTicker, weight);
      } else {
        throw new IllegalArgumentException("Each ticker must have a corresponding weight.");
      }
    }
    return stocksNWeights;
  }

  // Handles delegating to the model and view when the user wants to plot the value of their
  // portfolio
  private void handlePlotPortfolioValue(Scanner sc) {
    try {
      String currentPortfolioName = model.getCurrentPortfolioName();
      LocalDate from = LocalDate.parse(sc.next());
      LocalDate to = LocalDate.parse(sc.next());
      String plottedInfo = model.plotPortfolio(from, to);
      stockView.plotPortfolio(currentPortfolioName, plottedInfo, from, to);
    } catch (Exception e) {
      stockView.displayError(e.getMessage());
    }
  }

  // Handles delegating to the model and view when the user wants to buy or sell a stock
  // on a specific date
  private void handleBuyOrSellDate(Scanner sc, boolean buy) {
    try {
      String ticker = sc.next();
      int amt = sc.nextInt();
      LocalDate date = LocalDate.parse(sc.next());
      double modelOutput;
      if (buy) {
        modelOutput = model.buyDate(ticker, amt, date);
        stockView.displayBuy(ticker, amt, modelOutput);
      } else {
        modelOutput = model.sellDate(ticker, amt, date);
        stockView.displaySell(ticker, amt, modelOutput);
      }
    } catch (Exception e) {
      stockView.displayError(e.getMessage());
    }
  }


  @Override
  protected void handleGeneralCommands(String userInstruction, Scanner sc) {
    userInstruction = userInstruction.toLowerCase();
    String ticker;
    LocalDate date;
    String modelOutput;
    switch (userInstruction) {
      case "plot-stock":
        this.handlePlotStockValue(sc);
        break;
      case "view-specific-portfolio":
        this.handleViewSpecificPortfolio(sc);
        break;
      case "list-all-portfolios":
        this.handleListAllPortfolios(sc);
        break;
      default:
        super.handleGeneralCommands(userInstruction, sc);
    }
  }

  private void handleListAllPortfolios(Scanner sc) {
    try {
      List<String> names = model.listAllPortfolios();
      stockView.displayNames(names);
    } catch (Exception e) {
      stockView.displayError(e.getMessage());
    }
  }

  // GENERAL COMMANDS HELPERS
  // Handles delegating to the model and view when the user wants to plot the value of a stock
  private void handlePlotStockValue(Scanner sc) {
    try {
      String ticker = sc.next();
      LocalDate date1 = LocalDate.parse(sc.next());
      LocalDate date2 = LocalDate.parse(sc.next());
      String plottedInfo = model.plotStockValue(ticker, date1, date2);
      stockView.plotStockValue(ticker, plottedInfo, date1, date2);
    } catch (Exception e) {
      stockView.displayError(e.getMessage());
    }
  }

  // Handles delegating to the model and view when the user wants to view a portfolio with a
  // specific name.
  private void handleViewSpecificPortfolio(Scanner sc) {
    try {
      String portfolioName = sc.next();
      stockView.displayPortfolio(model.viewPortfolio(portfolioName));
    } catch (Exception e) {
      stockView.displayError(e.getMessage());
    }
  }

}
