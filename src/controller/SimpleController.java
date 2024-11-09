package controller;

import java.time.LocalDate;
import java.util.Scanner;

import model.StockModel;
import view.StockView;

/**
 * The {@code SimpleController} class implements the {@code Controller} interface and represents a
 * simple controller of the stock program. This controller works in traditional MVC architecture,
 * where the controller is responsible for processing input and delegating the appropriate actions
 * to the model and view. This controller relies on a {@code StockModel} object to handle the
 * interaction with stock data and user specific information.
 * The {@code StockView} object to handle displaying the results of the model's actions.
 */
public class SimpleController implements Controller {
  private Readable readable;
  private StockView stockView;
  private StockModel model;

  /**
   * Constructs a new {@code SimpleController} object with the given model, readable, and stockView.
   * If the model, readable, or stockView is null, an IllegalArgumentException is thrown.
   *
   * @param model     the StockModel object to interact with stock data and user  information
   * @param readable  the Readable object for inputs
   * @param stockView the StockView object to display the results of the model's actions
   * @throws IllegalArgumentException if the model, readable, or stockView is null
   */
  public SimpleController(StockModel model, Readable readable, StockView stockView)
          throws IllegalArgumentException {
    if ((model == null) || (readable == null) || (stockView == null)) {
      throw new IllegalArgumentException("Model, readable or stockView is null");
    }
    this.model = model;
    this.stockView = stockView;
    this.readable = readable;
  }

  @Override
  public void control() {
    //print the welcome message
    stockView.welcomeMessage();
    stockView.displayMenu(false);
    stockView.inbtwnInputs();
    Scanner sc = new Scanner(readable);

    while (sc.hasNext()) { //continue until the user quits or there is no more input.
      //prompt for the instruction
      String userInstruction = sc.next(); //take the instruction name
      if (userInstruction.equals("quit") || userInstruction.equals("q")) {
        break;
      } else {
        processCommand(userInstruction, sc);
        stockView.inbtwnInputs();
      }

      if (!sc.hasNext()) {
        sc = new Scanner(readable);
      }
    }
    //after the user has quit, print farewell message
    stockView.farewellMessage();
  }

  @Override
  public void processCommand(String userInstruction, Scanner sc) {
    userInstruction = userInstruction.toLowerCase();

    boolean portfolioAssigment = model.getPortfolio() != null;
    if (portfolioAssigment) {
      selectedPortfolio(userInstruction, sc);
    } else {
      noPortfolio(userInstruction, sc);
    }
  }


  /**
   * Used for handling commands that can be used at any time.
   *
   * @param userInstruction the users main instruction /action
   * @param sc              the scanner (containing the rest of the arguments for the given command)
   */
  protected void handleGeneralCommands(String userInstruction, Scanner sc) {
    userInstruction = userInstruction.toLowerCase();
    String ticker;
    LocalDate to;
    LocalDate from;
    int days;
    switch (userInstruction) {
      case "download":
        this.handleDownload(sc);
        break;
      case "avg":
        this.handleAvg(sc);
        break;
      case "cross-default":
        this.handleCrossDefault(sc);
        break;
      case "cross-x":
        this.handleCrossX(sc);
        break;
      case "gain-loss":
        this.handleGainLoss(sc);
        break;
      case "get-price":
        this.handleGetPrice(sc);
        break;
      default:
        stockView.failedOperation(userInstruction, "operation unknown");
    }
  }

  // GENERAL COMMAND HELPERS
  // Handles delegating to the model and view when the user requests to download a stock
  private void handleDownload(Scanner sc) {
    try {
      String ticker = sc.next().toUpperCase();
      stockView.downloadStock(model.downloadStock(ticker));
    } catch (Exception e) {
      stockView.displayError(e.getMessage());
    }
  }

  // Handles delegating to the model and view when the user requests the x-day average of a stock
  private void handleAvg(Scanner sc) {
    try {
      String ticker = sc.next().toUpperCase();
      LocalDate to = LocalDate.parse(sc.next());
      int days = sc.nextInt();
      stockView.xDaysAvg(ticker, model.getXDaysAverage(ticker, to, days));
    } catch (Exception e) {
      stockView.displayError(e.getMessage());
    }
  }

  // Handles delegating to the model and view when the user
  // requests the default crossover of a stock
  private void handleCrossDefault(Scanner sc) {
    try {
      String ticker = sc.next().toUpperCase();
      LocalDate to = LocalDate.parse(sc.next());
      LocalDate from = LocalDate.parse(sc.next());
      stockView.defDaysCrossover(ticker, model.getDefDayCrossover(ticker, to, from));
    } catch (Exception e) {
      stockView.displayError(e.getMessage());
    }
  }

  // Handles delegating to the model and view when the user requests the x-day crossover of a stock
  private void handleCrossX(Scanner sc) {
    try {
      String ticker = sc.next().toUpperCase();
      LocalDate to = LocalDate.parse(sc.next());
      LocalDate from = LocalDate.parse(sc.next());
      int days = sc.nextInt();
      stockView.xDaysCrossover(ticker, model.getXDayCrossover(ticker, to, from, days), days);
    } catch (Exception e) {
      stockView.displayError(e.getMessage());
    }
  }

  // Handles delegating to the model and view when the user requests the gain/loss of a stock
  private void handleGainLoss(Scanner sc) {
    try {
      String ticker = sc.next().toUpperCase();
      LocalDate to = LocalDate.parse(sc.next());
      LocalDate from = LocalDate.parse(sc.next());
      stockView.gainLoss(ticker, model.gainLoss(ticker, to, from));
    } catch (Exception e) {
      stockView.displayError(e.getMessage());
    }
  }

  // Handles delegating to the model and view when the user requests the price of a stock
  private void handleGetPrice(Scanner sc) {
    try {
      String ticker = sc.next().toUpperCase();
      LocalDate to = LocalDate.parse(sc.next());
      stockView.getPriceStock(ticker, model.getPrice(ticker, to));
    } catch (Exception e) {
      stockView.displayError(e.getMessage());
    }
  }

  /**
   * Handles commands that can only be used when a portfolio is selected.
   *
   * @param userInstruction the users main instruction / command.
   * @param sc              the scanner (containing the rest of the arguments for the given command)
   */
  protected void selectedPortfolio(String userInstruction, Scanner sc) {
    String ticker;
    int amt;
    switch (userInstruction) {
      case "log-out":
        this.handleLogOut();
        break;
      case "view-portfolio":
        this.handleViewPortfolio();
        break;
      case "buy":
        this.handleBuyOrSell(sc, true);
        break;
      case "sell":
        this.handleBuyOrSell(sc, false);
        break;
      case "total-value":
        this.handleTotalValue(sc);
        break;
      case "menu":
        stockView.displayMenu(true);
        break;
      default:
        handleGeneralCommands(userInstruction, sc);
    }
  }

  // SELECTED PORTFOLIO HELPERS
  // Handles delegating to the model and view when a user logs out
  protected void handleLogOut() {
    model.logout();
    stockView.logOut();
  }

  // Handles delegating to the model and view when the user views their portfolio
  private void handleViewPortfolio() {
    stockView.displayPortfolio(model.getPortfolio());
  }

  // Handles delegating to the model and view when the user buys or sells a stock
  // (based on the flag)
  private void handleBuyOrSell(Scanner sc, boolean buy) {
    try {
      String ticker = sc.next().toUpperCase();
      int amt = sc.nextInt();
      double modelOuput;
      if (buy) {
        modelOuput = model.buy(ticker, amt);
        stockView.displayBuy(ticker, amt, modelOuput);
      } else {
        modelOuput = model.sell(ticker, amt);
        stockView.displaySell(ticker, amt, modelOuput);
      }
    } catch (Exception e) {
      stockView.displayError(e.getMessage());
    }
  }

  // Handle delegating to the model and view when the user requests the total-value of their
  // portfolio
  private void handleTotalValue(Scanner sc) {
    try {
      LocalDate date = LocalDate.parse(sc.next());
      stockView.totalValue(date, model.totalValue(date));
    } catch (Exception e) {
      stockView.displayError(e.getMessage());
    }
  }

  /**
   * Handles commands that can only be used when a portfolio is not selected.
   *
   * @param userInstruction the users main instruction / command.
   * @param sc              the scanner (containing the rest of the arguments for the given command)
   */
  protected void noPortfolio(String userInstruction, Scanner sc) {
    switch (userInstruction) {
      case "set-portfolio":
        this.handleSetPortfolio(sc);
        break;
      case "create-portfolio":
        this.handleCreatePortfolio(sc);
        break;
      case "menu":
        stockView.displayMenu(false);
        break;
      default:
        handleGeneralCommands(userInstruction, sc);
    }
  }

  // NO PORTFOLIO HELPERS
  // Handles delegating to the model and view when the user creates a portfolio
  private void handleCreatePortfolio(Scanner sc) {
    try {
      String portfolioName = sc.next();
      stockView.createdPortfolio(model.createPortfolio(portfolioName));
    } catch (IllegalArgumentException e) {
      stockView.displayError(e.getMessage());
    }
  }

  // Handles delegating to the model and view when the user sets a portfolio
  private void handleSetPortfolio(Scanner sc) {
    try {
      String portfolio = sc.next();
      model.setPortfolio(portfolio);
      stockView.portfolioSet(portfolio);
    } catch (Exception e) {
      stockView.displayError(e.getMessage());
    }
  }
}