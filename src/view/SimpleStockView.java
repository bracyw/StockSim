package view;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * The {@code SimpleStockView} class is a simple implementation of the {@code StockView} interface,
 * which displays data by writing it to the appendable given on create.
 * (used in mainly for writing to console).
 * This class is used to display the data given to it in the text format (usually in the console).
 */
public class SimpleStockView implements StockView {
  Appendable appendable;

  public SimpleStockView(Appendable appendable) {
    this.appendable = appendable;
  }

  @Override
  public void createdPortfolio(String portfolio) {
    writeMessage(String.format("created portfolio %s\n", portfolio));
  }

  @Override
  public void downloadStock(String message) {
    writeMessage(message + "\n");
  }

  @Override
  public void failedOperation(String operationName, String relavantInfo) {
    writeMessage(System.lineSeparator() + "Failed operation " + operationName + " with message: " +
            relavantInfo +
            System.lineSeparator());
  }

  @Override
  public void portfolioSet(String portfolioName) {
    writeMessage("Current portfolio set to: " + portfolioName + System.lineSeparator());
  }

  @Override
  public void displayPortfolio(String portfolio) {
    writeMessage(portfolio + System.lineSeparator());
  }

  @Override
  public void displayBuy(String ticker, int amt, double cashValue) {
    writeMessage(
            String.format("Bought %d of %s, worth $%.4f", amt, ticker, cashValue));
  }

  @Override
  public void displaySell(String ticker, int amt, double cashValue) {
    writeMessage(String.format("Sold %d of %s, worth $%.5f", amt, ticker, cashValue));
  }

  @Override
  public void totalValue(LocalDate date, double value) {
    writeMessage(String.format("total value of portfolio" +
            " at %s is $%.4f", date.toString(), value));
  }

  @Override
  public void logOut() {
    writeMessage("successfully logged out");
  }

  @Override
  public void displayMenu(boolean portfolioSelected) {
    // when adding new descriptions ensure that you add a
    // commands description AND THEN the cmdSyntax IN THAT ORDER
    List<String> cmdDescription = new ArrayList<>();
    List<String> cmdSyntax = new ArrayList<>();
    String indent = "     ";
    String formatDes = indent + "%s:\n";
    String formatSyntax = indent + indent + "%s\n";
    StringBuilder msgToWrite = new StringBuilder();
    msgToWrite.append("Please select an option from the following menu:\n");
    this.addImportantFirstInfo(cmdDescription, cmdSyntax);
    if (portfolioSelected) {
      this.addPortfolioCommands(cmdDescription, cmdSyntax);
    } else {
      this.addNonPortfolioCommands(cmdDescription, cmdSyntax);
    }
    this.addAdditionalCommands(cmdDescription, cmdSyntax);
    // skip the broad header with i = 1
    for (int i = 0; i < cmdDescription.size(); i++) {
      msgToWrite.append(String.format(formatDes, cmdDescription.get(i)));
      msgToWrite.append(String.format(formatSyntax, cmdSyntax.get(i)));
    }
    writeMessage(msgToWrite.toString() + System.lineSeparator());
  }

  protected void addImportantFirstInfo(List<String> cmdDescription, List<String> cmdSyntax) {
    cmdDescription.add("To download information on a stock");
    cmdSyntax.add("download <stock ticker>");
    cmdDescription.add("Please give all dates in");
    cmdSyntax.add("YYYY-MM-DD format");
  }

  protected void addPortfolioCommands(List<String> cmdDescription, List<String> cmdSyntax) {
    cmdDescription.add("To buy a certain amount of stock (must be a WHOLE NUMBER)");
    cmdSyntax.add("buy <stock ticker> <amount of stock>");
    cmdDescription.add("To sell a certain amount of stock you own (must be a WHOLE NUMBER)");
    cmdSyntax.add("sell <stock ticker> <amount of stock>");
    cmdDescription.add("To see the contents of your portfolio");
    cmdSyntax.add("view-portfolio");
    cmdDescription.add("To see the total value of your portfolio");
    cmdSyntax.add("total-value <date>");
    cmdDescription.add("To log out");
    cmdSyntax.add("log-out");
  }

  protected void addNonPortfolioCommands(List<String> cmdDescription, List<String> cmdSyntax) {
    cmdDescription.add("To create a portfolio");
    cmdSyntax.add("create-portfolio <portfolio name>");
    cmdDescription.add("To set your current portfolio");
    cmdSyntax.add("set-portfolio <portfolio name>");
  }

  protected void addAdditionalCommands(List<String> cmdDescription, List<String> cmdSyntax) {
    cmdDescription.add("To see xDaysAverage");
    cmdSyntax.add("avg <ticker> <date> <amount of days>");
    cmdDescription.add("To see x day crossover for a stock");
    cmdSyntax.add("cross-x <ticker> <from date> <to date> <amount of days>");
    cmdDescription.add("To see the 30 day crossover for a stock");
    cmdSyntax.add("cross-default <ticker> <from date> <to date>");
    cmdDescription.add("To see the gain or loss for a given ticker");
    cmdSyntax.add("gain-loss <ticker> <from date> <to date>");
    cmdDescription.add("To see the price of a stock on certain date");
    cmdSyntax.add("get-price <ticker> <date>");
    cmdDescription.add("To see the menu again");
    cmdSyntax.add("menu");
    cmdDescription.add("To quit the program");
    cmdSyntax.add("quit or q");
  }


  @Override
  public void xDaysAvg(String ticker, double price) {
    writeMessage(
            String.format("%s has an average of $%.4f for requested days above.", ticker, price));
  }

  @Override
  public void defDaysCrossover(String ticker, Set<LocalDate> dates) {
    String msg = this.generateCrossoverMessage(ticker, dates, 30);
    writeMessage(msg);
  }

  @Override
  public void xDaysCrossover(String ticker, Set<LocalDate> dates, int days) {
    String msg = generateCrossoverMessage(ticker, dates, days);
    writeMessage(msg);
  }

  // Helper method to generate the crossover message
  private String generateCrossoverMessage(String ticker, Set<LocalDate> dates, int days) {
    StringBuilder msg = new StringBuilder();
    msg.append("The amount of days that are crossovers of the stock, " + ticker +
            " for the time interval of " + days + " days is " + dates.size() +
            " with the dates being as follows:\n");

    int numDays = 0;
    for (LocalDate date : dates) {
      numDays++;
      msg.append(date.toString());
      if (numDays == dates.size()) {
        msg.append(".");
      } else {
        msg.append(", ");
      }
      if (numDays % 6 == 0) {
        msg.append("\n");
      }
    }
    return msg.toString();
  }

  @Override
  public void gainLoss(String ticker, Double price) {
    if (price >= 0) {
      writeMessage(String.format("%s has gained $%.4f dollars", ticker, price));
    } else {
      writeMessage(String.format("%s has lost $%.4f dollars", ticker, price * -1));
    }
  }

  @Override
  public void getPriceStock(String ticker, double price) {
    writeMessage(String.format("%s has a price of %.4f dollars", ticker, price));


  }

  @Override
  public void displayError(String error) {
    if (error == null) {
      error = "One or more inputs after command did not follow menu. Please " +
              "ensure you follow all instructions.";
    }
    writeMessage("Failed with error message: " + error);
  }


  @Override
  public void welcomeMessage() {
    writeMessage("Welcome to the OODStocks Program" + System.lineSeparator());
  }

  @Override
  public void inbtwnInputs() {
    writeMessage("\nPlease enter your next command: ");
  }

  @Override
  public void farewellMessage() {
    writeMessage("\nThank you for using the OODStocks Program");
  }

  protected void writeMessage(String message) throws IllegalStateException {
    try {
      appendable.append(message);
    } catch (IOException e) {
      throw new IllegalStateException(e.getMessage());
    }
  }
}
