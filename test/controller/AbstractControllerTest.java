package controller;

import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.StockModel;
import view.StockView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Abstracts test for controller.
 */
public abstract class AbstractControllerTest {
  List<String> mockModelLog;
  StockModel mockStockModel;
  StockView mockStockView;
  Appendable appendableForView;
  Readable inputForController;
  Controller controller;
  String welcomeString = "Welcome to the OODStocks Program\n";
  String noPortfolioMenuString = this.getNoPortfolioMenuString();
  String selectedPortfolioMenuString = this.getSelectedPortfolioMenuString();

  protected abstract String getNoPortfolioMenuString();

  protected abstract String getSelectedPortfolioMenuString();

  @Before
  public void setup() {
    mockModelLog = new ArrayList<>();
    mockStockModel = getMockStockModel(mockModelLog, false, false);
    appendableForView = new StringBuilder();
    mockStockView = getStockView(appendableForView);
    inputForController = null;
    controller = null;
  }

  protected abstract StockView getStockView(Appendable appendableForView);

  protected abstract StockModel getMockStockModel(List<String> mockModelLog, boolean b, boolean b1);


  private void setUpAndValidateProcessCommand(String input, boolean portfolioSelected,
                                              boolean hasExistingPortfolios,
                                              String expectedViewOutput,
                                              String expectedModelCmd2) {
    inputForController = new StringReader(input);
    mockStockModel = getMockStockModel(mockModelLog, portfolioSelected, hasExistingPortfolios);
    controller = new SimpleController(mockStockModel, inputForController, mockStockView);
    Scanner mockScannerAfterCommand = new Scanner(inputForController);
    String command = mockScannerAfterCommand.next();
    // ensure stock view is set up correctly
    assertEquals("", appendableForView.toString());
    controller.processCommand(command, mockScannerAfterCommand);
    assertEquals(
            expectedViewOutput,
            appendableForView.toString());
    // the controllers first method call should always be getPortfolio
    assertEquals("getPortfolio called", mockModelLog.get(0));
    // check the second command called
    if (expectedModelCmd2 != null) {
      // checking the second command is correct, the first one will always be getPortfolio
      assertEquals(expectedModelCmd2, mockModelLog.get(1));
    }
  }

  @Test // test that the controller does not modify any of the inputs on create
  public void testValidateInputsAfterCreate() {
    inputForController = new StringReader("");
    controller = new SimpleController(mockStockModel, inputForController, mockStockView);
    // ensure that input didn't change
    assertFalse(new Scanner(inputForController).hasNext());
    // ensure the model didn't receive any input
    assertEquals(0, mockModelLog.size());
    // ensure that the mockStockView didn't write anything
    assertEquals("", appendableForView.toString());
  }


  // TEST PROCESSCOMMAND for NO PORTFOLIO
  @Test // test that no portfolio works when asking for the menu
  public void processCommandMenuNoPortfolio() {
    String expectedViewOutput = noPortfolioMenuString + "\n";
    this.setUpAndValidateProcessCommand("menu", false, false,
            expectedViewOutput, null);
    assertEquals(expectedViewOutput, noPortfolioMenuString + "\n");
  }

  // test that process command calls the correct method in the model and view for set-portfolio
  // when the user doesn't have any other portfolios previously created
  @Test
  public void processCommandNoPortfolioSetPortfolioNoOtherPorts() {
    this.setUpAndValidateProcessCommand("set-portfolio portfolio-name", false,
            false,
            "Failed with error message: Portfolio " +
                    "key does not exist no portfolio found with that name",
            "setPortfolio called with, portKey: portfolio-name");
    String testing = "For Tests";
    assertEquals("For Tests", testing);
  }

  // test that process command calls the correct method in the model and view for set-portfolio
  // when the user has previously made portfolios
  @Test
  public void processCommandNoPortfolioSetPortfolioWhenExists() {
    String expectedViewOutput = "Current portfolio set to: portfolio-name\n";
    String expectedModelCmd2 = "setPortfolio called with, portKey: portfolio-name";
    this.setUpAndValidateProcessCommand("set-portfolio portfolio-name",
            false, true,
            expectedViewOutput, expectedModelCmd2);
    String testing = "For Tests";
    assertEquals("For Tests", testing);
  }


  // test that process command calls the correct methods for create portfolio
  @Test
  public void processCommandNoPortfolioCreatePortfolio() {
    String expectedViewOutput = "created portfolio portfolio-name\n";
    String expectedModelCmd2 = "createPortfolio called with, portfolio: portfolio-name";
    this.setUpAndValidateProcessCommand("create-portfolio portfolio-name", false,
            false
            , expectedViewOutput, expectedModelCmd2);
    String testing = "For Tests";
    assertEquals("For Tests", testing);
  }

  // Test selected portfolio works for the menu command
  @Test
  public void testProcessCommandSelectedPortfolioMenu() {
    String expectedViewOutput = selectedPortfolioMenuString + "\n";
    this.setUpAndValidateProcessCommand("menu", true, true,
            expectedViewOutput, null);
    String testing = "For Tests";
    assertEquals("For Tests", testing);
  }

  // test that logout works as expected for model and view calls
  @Test
  public void testProcessCommandSelectedPortfolioLogout() {
    String expectedViewOutput = "successfully logged out";
    String expectedModelCmd2 = "logout called";
    this.setUpAndValidateProcessCommand("log-out", true, true,
            expectedViewOutput, expectedModelCmd2);
    String testing = "For Tests";
    assertEquals("For Tests", testing);
  }

  // test that view-portfolio works as expected for model and views calls
  @Test
  public void testProcessCommandWithPortfolioViewPort() {
    String expectedViewOutput = "portfolioExist\n";
    String expectedModelCmd2 = "viewPortfolio called";
    this.setUpAndValidateProcessCommand("view-portfolio", true,
            true, expectedViewOutput, null);
    String testing = "For Tests";
    assertEquals("For Tests", testing);
  }

  // test that buy works as expected for model and view calls
  @Test
  public void testProcessCommandWithPortfolioBuy() {
    String expectedViewOutput = "Bought 3 of TICKER, worth $3.0000";
    String expectedModelCmd2 = "buy called with, ticker:TICKER, amount: 3.0";
    this.setUpAndValidateProcessCommand("buy ticker 3",
            true, true,
            expectedViewOutput, expectedModelCmd2);
    String testing = "For Tests";
    assertEquals("For Tests", testing);
  }

  // test that sell works as expected for model and view calls
  @Test
  public void testProcessCommandWithPortfolioSell() {
    String expectedViewOutput = "Sold 4 of TICKER, worth $4.00000";
    String expectedModelCmd2 = "sell called with, ticker:TICKER, amount: 4.0";
    this.setUpAndValidateProcessCommand("sell ticker 4",
            true, true,
            expectedViewOutput, expectedModelCmd2);
    String testing = "For Tests";
    assertEquals("For Tests", testing);
  }

  // test that total-value works as expected for model and view calls
  @Test
  public void testProcessCommandWithPortfolioTotalValue() {
    String expectedViewOutput = "total value of portfolio at 2020-01-01 is $5.0000";
    String expectedModelCmd2 = "totalValue called with, evaluate date: 2020-01-01";
    this.setUpAndValidateProcessCommand("total-value 2020-01-01",
            true, true,
            expectedViewOutput, expectedModelCmd2);
    String testing = "For Tests";
    assertEquals("For Tests", testing);
  }


  // test that download works as expected for model and view calls, when no portfolio is selected
  // and when a portfolio is selected
  @Test
  public void testProcessCommandDownload() {
    String expectedViewOutput = "downloadStock\n";
    String expectedModelCmd2 = "downloadStock called with, ticker:TICKER";
    this.setUpAndValidateProcessCommand("download TICKER",
            false, false,
            expectedViewOutput, expectedModelCmd2);
    // reset shared values
    this.setup();
    this.setUpAndValidateProcessCommand("download TICKER",
            true, true,
            expectedViewOutput, expectedModelCmd2);
    String testing = "For Tests";
    assertEquals("For Tests", testing);
  }

  // test that avg works as expected for model and view calls, when no portfolio is selected
  // and when a portfolio is selected
  @Test
  public void testProcessCommandAvg() {
    String expectedViewOutput = "TICKER has an average of $0.0000 for requested days above.";
    String expectedModelCmd2 =
            "getXDaysAverage called with, ticker:TICKER, to date: 2020-01-01, xDays:5";
    this.setUpAndValidateProcessCommand("avg TICKER 2020-01-01 5",
            false, false,
            expectedViewOutput, expectedModelCmd2);
    // reset shared values
    this.setup();
    this.setUpAndValidateProcessCommand("avg TICKER 2020-01-01 5",
            true, true,
            expectedViewOutput, expectedModelCmd2);
    String testing = "For Tests";
    assertEquals("For Tests", testing);
  }

  // test that cross works as expected for model and view calls, when no portfolio is selected
  // and when a portfolio is selected
  @Test
  public void testProcessCommandCross() {
    String expectedViewOutput =
            "The amount of days that are crossovers of the stock," +
                    " TICKER for the time interval of " +
                    "30 days is 1 with the dates being as follows:\n" +
                    "1111-11-11.";
    String expectedModelCmd2 =
            "getXDayCrossover called with, stock:TICKER, to date: 2020-01-01, from date: " +
                    "2020-01-02";
    this.setUpAndValidateProcessCommand("cross-default TICKER 2020-01-01 2020-01-02",
            false, false,
            expectedViewOutput, expectedModelCmd2);
    // reset shared values
    this.setup();
    expectedViewOutput = "The amount of days that are crossovers of the stock, TICKER for the " +
            "time interval of 15 days is 1 with the dates being as follows:\n" +
            "1111-11-11.";
    expectedModelCmd2 =
            "getXDayCrossover called with, stock:TICKER, to date: 2020-01-01," +
                    " from date: 2020-01-02days checking 15";
    this.setUpAndValidateProcessCommand("cross-x TICKER 2020-01-01 2020-01-02 15",
            true, true,
            expectedViewOutput, expectedModelCmd2);
    String testing = "For Tests";
    assertEquals("For Tests", testing);
  }


  // test that gain-loss works as expected for model and view calls, when no portfolio is selected
  // and when a portfolio is selected
  @Test
  public void testProcessCommandGainLoos() {
    String expectedViewOutput = "TICKER has gained $1.0000 dollars";
    String expectedModelCmd2 =
            "gainLoss called with, stock:TICKER, to date: 2020-01-01, from date: 2020-01-02";
    this.setUpAndValidateProcessCommand("gain-loss TICKER 2020-01-01 2020-01-02",
            false, false,
            expectedViewOutput, expectedModelCmd2);
    // reset shared values
    this.setup();
    this.setUpAndValidateProcessCommand("gain-loss TICKER 2020-01-01 2020-01-02",
            true, true,
            expectedViewOutput, expectedModelCmd2);
    String testing = "For Tests";
    assertEquals("For Tests", testing);
  }

  // test that get-price works as expected for model and view calls, when no portfolio is selected
  // and when a portfolio is selected
  @Test
  public void testProcessCommandGetPrice() {
    String expectedViewOutput = "TICKER has a price of 2.0000 dollars";
    String expectedModelCmd2 = "getPrice called with, stock:TICKER, evaluate date: 2020-01-01";
    this.setUpAndValidateProcessCommand("get-price TICKER 2020-01-01", false,
            false,
            expectedViewOutput, expectedModelCmd2);
    // reset shared values
    this.setup();
    this.setUpAndValidateProcessCommand("get-price TICKER 2020-01-01", true,
            true,
            expectedViewOutput, expectedModelCmd2);
    String testing = "For Tests";
    assertEquals("For Tests", testing);
  }


  // test that process command does not call any extra methods in the model
  // and displays the correct output in the view when the command is invalid
  @Test
  public void testProcessCommandInvalidCommand() {
    String expectedViewOutput = "\n" +
            "Failed operation invalid-command with message: operation unknown\n";
    this.setUpAndValidateProcessCommand("invalid-command", false,
            false,
            expectedViewOutput, null);
    String testing = "For Tests";
    assertEquals("For Tests", testing);
  }

  @Test
  // test that the controller displays the correct first message.
  // With farewell message, when run with quit.
  public void testControlQuit() {
    inputForController = new StringReader("quit");
    controller = new SimpleController(mockStockModel, inputForController, mockStockView);
    controller.control();
    assertEquals(welcomeString + noPortfolioMenuString +
            "\n\n" +
            "Please enter your next command: \n" +
            "Thank you for using the OODStocks Program", appendableForView.toString());
    assertEquals(0, mockModelLog.size());
  }

  @Test // test that the controller works as expected when the user enters the set-portfolio cmd
  public void testControlSetPortfolio() {
    inputForController = new StringReader("set-portfolio portfolio-name");
    mockStockModel = getMockStockModel(mockModelLog, false, true);
    controller = new SimpleController(mockStockModel, inputForController, mockStockView);
    controller.control();
    assertEquals(welcomeString + noPortfolioMenuString +
            "\n\n" +
            "Please enter your next command: Current portfolio set to: portfolio-name\n" +
            "\n" +
            "Please enter your next command: \n" +
            "Thank you for using the OODStocks Program", appendableForView.toString());
    assertEquals(2, mockModelLog.size());
  }

  @Test
  // test that the controller works as expected when the user enters the create-portfolio command
  public void testControlCreatePortfolio() {
    inputForController = new StringReader("create-portfolio portfolio-name");
    mockStockModel = getMockStockModel(mockModelLog, false, false);
    controller = new SimpleController(mockStockModel, inputForController, mockStockView);
    controller.control();
    assertEquals(welcomeString + noPortfolioMenuString +
            "\n\n" +
            "Please enter your next command: created portfolio portfolio-name\n" +
            "\n" +
            "Please enter your next command: \n" +
            "Thank you for using the OODStocks Program", appendableForView.toString());
    assertEquals(2, mockModelLog.size());
  }

  // test that the controller works as expected when the user enters the menu command
  // and has no portfolio selected
  @Test
  public void testControlMenuNoPortfolio() {
    inputForController = new StringReader("menu");
    controller = new SimpleController(mockStockModel, inputForController, mockStockView);
    controller.control();
    assertEquals(welcomeString + noPortfolioMenuString +
            "\n\n" +
            "Please enter your next command: " +
            noPortfolioMenuString + "\n\n" +
            "Please enter your next command: \n" +
            "Thank you for using the OODStocks Program", appendableForView.toString());
    assertEquals(1, mockModelLog.size());
  }

  // test that the controller works as expected when the user enters the menu command
  // and has a portfolio selected
  @Test
  public void testControlMenuPortfolio() {
    inputForController = new StringReader("menu");
    mockStockModel = getMockStockModel(mockModelLog, true, true);
    controller = new SimpleController(mockStockModel, inputForController, mockStockView);
    controller.control();
    assertEquals(welcomeString + noPortfolioMenuString +
            "\n\n" +
            "Please enter your next command: " +
            selectedPortfolioMenuString + "\n\n" +
            "Please enter your next command: \n" +
            "Thank you for using the OODStocks Program", appendableForView.toString());
    assertEquals(1, mockModelLog.size());
  }

  // Test that the controller works as expected when the user enters the log-out command
  @Test
  public void testControlLogOut() {
    inputForController = new StringReader("log-out");
    mockStockModel = getMockStockModel(mockModelLog, true, true);
    controller = new SimpleController(mockStockModel, inputForController, mockStockView);
    controller.control();
    assertEquals(welcomeString + noPortfolioMenuString +
            "\n\n" +
            "Please enter your next command: " +
            "successfully logged out" +
            "\n" +
            "Please enter your next command: \n" +
            "Thank you for using the OODStocks Program", appendableForView.toString());
    assertEquals(2, mockModelLog.size());
  }

  // Test that the controller works as expected when the user enters the view-portfolio command
  @Test
  public void testControlViewPortfolio() {
    inputForController = new StringReader("view-portfolio");
    mockStockModel = getMockStockModel(mockModelLog, true, true);
    controller = new SimpleController(mockStockModel, inputForController, mockStockView);
    controller.control();
    assertEquals(welcomeString + noPortfolioMenuString +
            "\n\n" +
            "Please enter your next command: " +
            "portfolioExist\n" +
            "\n" +
            "Please enter your next command: \n" +
            "Thank you for using the OODStocks Program", appendableForView.toString());
    assertEquals(2, mockModelLog.size());
  }

  // Test that the controller works as expected when the user enters the buy command
  @Test
  public void testControlBuy() {
    inputForController = new StringReader("buy TICKER 3");
    mockStockModel = getMockStockModel(mockModelLog, true, true);
    controller = new SimpleController(mockStockModel, inputForController, mockStockView);
    controller.control();
    assertEquals(welcomeString + noPortfolioMenuString +
            "\n\n" +
            "Please enter your next command: " +
            "Bought 3 of TICKER, worth $3.0000" +
            "\n" +
            "Please enter your next command: \n" +
            "Thank you for using the OODStocks Program", appendableForView.toString());
    assertEquals(2, mockModelLog.size());
  }

  // Test that the controller works as expected when the user enters the sell command
  @Test
  public void testControlSell() {
    inputForController = new StringReader("sell TICKER 4");
    mockStockModel = getMockStockModel(mockModelLog, true, true);
    controller = new SimpleController(mockStockModel, inputForController, mockStockView);
    controller.control();
    assertEquals(welcomeString + noPortfolioMenuString +
            "\n\n" +
            "Please enter your next command: " +
            "Sold 4 of TICKER, worth $4.00000" +
            "\n" +
            "Please enter your next command: \n" +
            "Thank you for using the OODStocks Program", appendableForView.toString());
    assertEquals(2, mockModelLog.size());
  }

  // Test that the controller works as expected when the user enters the total-value command
  @Test
  public void testControlTotalValue() {
    inputForController = new StringReader("total-value 2020-01-01");
    mockStockModel = getMockStockModel(mockModelLog, true, true);
    controller = new SimpleController(mockStockModel, inputForController, mockStockView);
    controller.control();
    assertEquals(welcomeString + noPortfolioMenuString +
            "\n\n" +
            "Please enter your next command: " +
            "total value of portfolio at 2020-01-01 is $5.0000" +
            "\n" +
            "Please enter your next command: \n" +
            "Thank you for using the OODStocks Program", appendableForView.toString());
    assertEquals(2, mockModelLog.size());
  }

  // Test that the controller works as expected when the user enters the download command
  @Test
  public void testControlDownload() {
    inputForController = new StringReader("download TICKER");
    mockStockModel = getMockStockModel(mockModelLog, false, false);
    controller = new SimpleController(mockStockModel, inputForController, mockStockView);
    controller.control();
    assertEquals(welcomeString + noPortfolioMenuString +
            "\n\n" +
            "Please enter your next command: " +
            "downloadStock\n" +
            "\n" +
            "Please enter your next command: \n" +
            "Thank you for using the OODStocks Program", appendableForView.toString());
    assertEquals(2, mockModelLog.size());
  }

  // Test that the controller works as expected when the user enters the avg command
  @Test
  public void testControlAvg() {
    inputForController = new StringReader("avg TICKER 2020-01-01 5");
    mockStockModel = getMockStockModel(mockModelLog, false, false);
    controller = new SimpleController(mockStockModel, inputForController, mockStockView);
    controller.control();
    assertEquals(welcomeString + noPortfolioMenuString +
            "\n\n" +
            "Please enter your next command: " +
            "TICKER has an average of $0.0000 for requested days above." +
            "\n" +
            "Please enter your next command: \n" +
            "Thank you for using the OODStocks Program", appendableForView.toString());
    assertEquals(2, mockModelLog.size());
  }

  // Test that the controller works as expected when the user enters the cross command
  @Test
  public void testControlCross() {
    inputForController = new StringReader("cross-default TICKER 2020-01-01 2020-01-02");
    mockStockModel = getMockStockModel(mockModelLog, false, false);
    controller = new SimpleController(mockStockModel, inputForController, mockStockView);
    controller.control();
    assertEquals(welcomeString + noPortfolioMenuString +
            "\n\n" +
            "Please enter your next command: The amount of days that are crossovers " +
            "of the stock, TICKER for the time interval of 30 days is 1 with the " +
            "dates being as follows:\n" +
            "1111-11-11.\n" +
            "Please enter your next command: \n" +
            "Thank you for using the OODStocks Program", appendableForView.toString());
    assertEquals(2, mockModelLog.size());
  }

  // Test that the controller works as expected when the user enters the gain-loss command
  @Test
  public void testControlGainLoss() {
    inputForController = new StringReader("gain-loss TICKER 2020-01-01 2020-01-02");
    mockStockModel = getMockStockModel(mockModelLog, false, false);
    controller = new SimpleController(mockStockModel, inputForController, mockStockView);
    controller.control();
    assertEquals(welcomeString + noPortfolioMenuString +
            "\n\n" +
            "Please enter your next command: " +
            "TICKER has gained $1.0000 dollars" +
            "\n" +
            "Please enter your next command: \n" +
            "Thank you for using the OODStocks Program", appendableForView.toString());
    assertEquals(2, mockModelLog.size());
  }


  // Test that the controller works as expected when the user enters the get-price command
  @Test
  public void testControlGetPrice() {
    inputForController = new StringReader("get-price TICKER 2020-01-01");
    mockStockModel = getMockStockModel(mockModelLog, false, false);
    controller = new SimpleController(mockStockModel, inputForController, mockStockView);
    controller.control();
    assertEquals(welcomeString + noPortfolioMenuString +
            "\n\n" +
            "Please enter your next command: " +
            "TICKER has a price of 2.0000 dollars" +
            "\n" +
            "Please enter your next command: \n" +
            "Thank you for using the OODStocks Program", appendableForView.toString());
    assertEquals(2, mockModelLog.size());
  }
}
