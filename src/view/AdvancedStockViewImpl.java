package view;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * The {@code AdvancedStockViewImpl} class extends the functionality used in
 * {@code SimpleStockView}, adding on additonal features to display the functionality of
 * the {@code BetterStockModel} interface. In addition to the display features of
 * {@code SimpleStockView} this class has the ability to display
 * information to the user for the following extra features for displaying messages
 * about the following:
 * Composition of a portfolio on a given date
 * Distribution of a portfolio on a given date
 * Correctly saved current portfolio
 * Successfully bought stocks with given amounts and weights
 * Successfully rebalanced requested stocks
 * Plotted value of a stock from one date to another
 * Plotted value of a portfolio from one date to another
 * Successfully saved current portfolio
 * Successfully bought stocks with given amounts and weights
 */
public class AdvancedStockViewImpl extends SimpleStockView implements AdvancedStockView {
  public AdvancedStockViewImpl(Appendable appendable) {
    super(appendable);
  }

  @Override
  public void composition(String portfolioName, String stockAmounts) {
    String header = "Composition of portfolio " + portfolioName;
    this.writeTableWithHeader(header, stockAmounts);
  }

  @Override
  public void distribution(String portfolioName, String stockValues) {
    String header = "Distribution of portfolio " + portfolioName;
    this.writeTableWithHeader(header, stockValues);
  }

  private void writeTableWithHeader(String header, String table) {
    writeMessage(header + " \n" + table + "\n");
  }

  @Override
  public void saveCurrentPortfolio() {
    writeMessage("Successfully saved current portfolio. \n");
  }

  @Override
  public void buyBalanced(Map<String, Double> stocksNAmounts, Map<String, Double> weights) {
    String balancedMsg =
            "Successfully bought the stocks," +
                    " with shown amounts, and requested relative weights: \n";
    String indent = "     ";
    for (String symbol : stocksNAmounts.keySet()) {
      balancedMsg = balancedMsg + indent + symbol + ": " + stocksNAmounts.get(symbol) + ", "
              + weights.get(symbol) + "%" + "\n";
    }
    writeMessage(balancedMsg);
  }

  @Override
  public void reBalance() {
    writeMessage("Successfully rebalanced requested stocks. \n");
  }

  @Override
  public void plotStockValue(String ticker, String plottedInfo, LocalDate date1, LocalDate date2) {
    String header = this.plotValueHeader(ticker, true, date1, date2);
    this.writeTableWithHeader(header, plottedInfo);
  }

  // Returns the correct header using given name and flag to determine if it is creating a stock
  // header or a portfolio header.
  private String plotValueHeader(String name, boolean stock, LocalDate date1, LocalDate date2) {
    String portOrStock = stock ? "stock" : "portfolio";
    return "Performance of " + portOrStock + " " + name +
            " from " + date1.toString() + " to " + date2.toString() + ": \n";
  }

  @Override
  public void plotPortfolio(String portfolioName, String plottedInfo, LocalDate date1,
                            LocalDate date2) {
    String header = this.plotValueHeader(portfolioName, false, date1, date2);
    this.writeTableWithHeader(header, plottedInfo);
  }


  public void displayBuy(String ticker, int amt, double cashValue, LocalDate date) {
    super.displayBuy(ticker, amt, cashValue);
    this.onDateAddOn(date);
  }

  @Override
  public void displaySell(String ticker, int amount, double value, LocalDate date) {
    super.displaySell(ticker, amount, value);
    this.onDateAddOn(date);
  }

  // Adds on date at the end of whatever has been written to the appendable object.
  private void onDateAddOn(LocalDate date) {
    writeMessage(String.format(", on date %s", date));
  }

  @Override
  protected void addPortfolioCommands(List<String> cmdDescription, List<String> cmdSyntax) {
    super.addPortfolioCommands(cmdDescription, cmdSyntax);
    cmdDescription.add("To see the composition of your current portfolio on a given date");
    cmdSyntax.add("composition <date>");
    cmdDescription.add("To see the distribution of your current portfolio on a given date");
    cmdSyntax.add("distribution <date>");
    cmdDescription.add("To save your current portfolion " +
            "(this WILL OVERWRITE LAST SAVE FOR THIS PORTFOLIO NAME)");
    cmdSyntax.add("save");
    cmdDescription.add("To buy multiple stocks with a certain amount of " +
            "with percentage spend on each");
    cmdSyntax.add("buy-balanced <amount of money> <date> <ticker name1> <weight1> <ticker name2> " +
            "<weight2>...");
    cmdDescription.add("To re-balance stocks already in your portfolio to the given weights");
    cmdSyntax.add("re-balance <date> <ticker name1> <weight1> <ticker name2> <weight2>...");
    cmdDescription.add("To plot the value of your current portfolio from date1 to date2");
    cmdSyntax.add("plot-portfolio <date1> <date2>");
    cmdDescription.add("To buy a stock on a given date");
    cmdSyntax.add("buy-date <ticker> <amount> <date>");
    cmdDescription.add("To sell a stock on a given date");
    cmdSyntax.add("sell-date <ticker> <amount> <date>");
  }

  @Override
  protected void addNonPortfolioCommands(List<String> cmdDescription, List<String> cmdSyntax) {
    super.addNonPortfolioCommands(cmdDescription, cmdSyntax);

  }

  @Override
  protected void addAdditionalCommands(List<String> cmdDescription, List<String> cmdSyntax) {
    cmdDescription.add("To plot the value of a stock from one date to another");
    cmdSyntax.add("plot-stock <ticker> <date1> <date2>");
    cmdDescription.add("To view the contents of a specific portfolio");
    cmdSyntax.add("view-specific-portfolio <name of portfolio>");
    cmdDescription.add("To see all the portfolios available to select");
    cmdSyntax.add("list-all-portfolios");
    // this super must come last because it has the quit command at the end which is important to
    // include.
    super.addAdditionalCommands(cmdDescription, cmdSyntax);
  }

  @Override
  public void displayNames(List<String> names) {
    StringBuilder add = new StringBuilder();
    int indent = 5;
    for (int i = 0; i < names.size(); i++) {
      add.append(" ".repeat(indent) + names.get(i) + "\n");
    }

    writeMessage("All portfolio names:\n" + add + "\n");
  }

}
