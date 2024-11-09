package controller;

import java.util.List;

import model.StockModel;
import view.SimpleStockView;
import view.StockView;

/**
 * Tests the SimpleController class.
 */
public class ControllerTest extends AbstractControllerTest {
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
    return new SimpleStockView(appendableForView);
  }

  protected StockModel getMockStockModel(List<String> mockModelLog, boolean b, boolean b1) {
    return new MockStockModel(mockModelLog, b, b1);
  }
}