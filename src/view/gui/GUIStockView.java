package view.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.text.NumberFormatter;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JFormattedTextField;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.Component;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JSeparator;
import javax.swing.JScrollPane;
import javax.swing.Box;


import customreading.DelayedInputStream;
import controller.CommandUtils;
import view.AdvancedStockView;


/**
 * The {@code GUIStockView} class represents a GUI view for the stock program, made using Java
 * Swing.
 * This {@code GUIStockView} class takes in a {@code DelayedInputStream} object, which is used to
 * communicate with the {@code GUIController} class, through appending commands that are formatted
 * and added to the input stream, using the {@code CommandUtils} class.
 * This view is designed to be used with the {@code GUIController} class, and provides the user
 * with the ability to interact with the stock data through a GUI interface.
 */
public class GUIStockView implements AdvancedStockView, ActionListener {
  //Input and the frame.
  private JFrame frame;
  private DelayedInputStream inputStream;

  //Command for waiting.
  private boolean commandReady;

  // used for tracking and persisting current display info
  LocalDate dateTotalCompDist = null;
  private String val_comp_dist = "";
  private JTextArea infoTextAreaValCompDest;
  private JTextArea selectedCompDistValDate;

  // All different parts of Different panels to display information
  private JPanel portfolioInfo;
  private JTextArea actPortfolio;
  private JTextArea allPortfolioNames = new JTextArea();
  private JPanel infoPanel;

  // Parts so that buying and selling panel can work
  private JFormattedTextField stockAmountInputField;
  private LocalDate buySellDate;
  private JTextArea buySellDateText;
  private JTextField tickerTextBox;

  /**
   * Constructs a new {@code GUIStockView} object with the given {@code DelayedInputStream} object.
   *
   * @param inputStream the {@code DelayedInputStream} object to append commands inputs too.
   */
  public GUIStockView(DelayedInputStream inputStream) {
    this.inputStream = inputStream;
    this.frame = new JFrame();
    commandReady = false;
  }

  /**
   * Displays the whole application interface based on whether the user is in a portfolio.
   *
   * @param portfolioSelected whether a portfolio is currently selected.
   */
  @Override
  public void displayMenu(boolean portfolioSelected) {
    // Create the JFrame and configure layout
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(600, 600);
    frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));

    // Create button and add ActionListener (this class implements ActionListener)
    JPanel addBuySell = this.addBuySellPanelToFrame();
    JPanel valCompDist = addValCompDistFrame(frame);
    portfolioInfo = createPortfolioPanel(null);
    JPanel buyOrVal = new JPanel();
    buyOrVal.setLayout(new BoxLayout(buyOrVal, BoxLayout.Y_AXIS));
    buyOrVal.add(addBuySell);
    buyOrVal.add(valCompDist);

    JPanel portfolioInfoName = new JPanel();
    portfolioInfoName.setLayout(new BoxLayout(portfolioInfoName, BoxLayout.Y_AXIS));
    portfolioInfoName.add(portfolioInfo);

    frame.add(portfolioInfo);
    frame.add(buyOrVal);
    // Display the frame
    frame.setVisible(true);
  }

  // this updates the title of the portfolio info title
  private void updateInfoPanelBorder(String title) {
    infoPanel.setBorder(BorderFactory.createTitledBorder(title));
  }

  // Creates a panel for basic portfolio control based on the active name of a portfolio,
  // pass in as null if in-active
  private JPanel createPortfolioPanel(String active) {
    // Create a panel to hold all the portfolio information in.
    JPanel portfolioPanel = new JPanel();
    // make the portfolio account name area, ensuring it is view only and will wrap if needed
    actPortfolio = new JTextArea("No Current Portfolio");
    actPortfolio.setEditable(false);
    actPortfolio.setLineWrap(true);
    actPortfolio.setWrapStyleWord(true);
    actPortfolio.setAlignmentX(Component.LEFT_ALIGNMENT);
    // Ensure that the layout is on the y-axis
    portfolioPanel.setLayout(new BoxLayout(portfolioPanel, BoxLayout.Y_AXIS));
    portfolioPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    // Create all that can be used in relation to a portfolio account (at least directly)
    JButton createPortfolioButton = new JButton("Create Portfolio");
    JButton setPortfolioButton = new JButton("Set Portfolio");
    JButton logout = new JButton("Logout");
    JButton save = new JButton("Save");
    // add the stock view as a listener to each button
    createPortfolioButton.addActionListener(this);
    setPortfolioButton.addActionListener(this);
    logout.addActionListener(this);
    save.addActionListener(this);
    // add the buttons that are relevant when signed in to a portfolio.
    if (active != null) {
      portfolioPanel.add(logout);
      portfolioPanel.add(save);
    } else {
      // Add the buttons that can be used when not signed in.
      portfolioPanel.add(createPortfolioButton);
      portfolioPanel.add(setPortfolioButton);
      // call list all portfolios to update the names to be displayed
      String listAllCmd = CommandUtils.preFormatListAllPortfoliosCommand();
      commandReady = CommandUtils.addCommand(listAllCmd, inputStream);
      portfolioPanel.add(allPortfolioNames);

    }
    allPortfolioNames.setEditable(false);
    portfolioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    allPortfolioNames.setAlignmentX(Component.LEFT_ALIGNMENT);

    // add the account info to the portfolio panel (this adds it above the buttons
    portfolioPanel.add(actPortfolio, 0);
    return portfolioPanel;
  }

  // Will return the JPanel that contains the info about buying and selling stocks.
  private JPanel addBuySellPanelToFrame() {
    JPanel buySellPanel = new JPanel();
    // create the text box for the ticker input
    tickerTextBox = new JTextField(8);
    JPanel tickerInput = this.createInputTextBox(tickerTextBox, "Enter Stock Ticker:",
            "set", "tickerInputBuySell");
    // Create the integer input box for amount
    JPanel amountBox = this.createIntegerInputBox("Enter Shares:");
    // Create a panel that contains the button for the date and a text field showing the last
    // selected date
    buySellDateText = new JTextArea(1, 1);
    JPanel datePanel =
            this.createSelectableDate(buySellDateText, "Please select a Date",
                    "Date", "date-for-buy/sell");
    buySellPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
    buySellPanel.setLayout(new BoxLayout(buySellPanel, BoxLayout.Y_AXIS));
    buySellPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    JPanel buySellButtons = new JPanel();
    JButton buyButton = new JButton("Buy");
    buyButton.setActionCommand("buy-date");
    buyButton.addActionListener(this);

    JButton sellButton = new JButton("Sell");
    sellButton.setActionCommand("sell-date");
    sellButton.addActionListener(this);

    buySellButtons.add(buyButton);
    buySellButtons.add(sellButton);

    buySellPanel.add(tickerInput);
    buySellPanel.add(amountBox);
    buySellPanel.add(datePanel);
    buySellPanel.add(buySellButtons);

    return (buySellPanel);
  }

  // Returns JPanel that creates a box for only integers.
  private JPanel createIntegerInputBox(String prompt) {
    JPanel integerInputPanel = new JPanel();
    integerInputPanel.setLayout(new FlowLayout());

    // Create a label
    JLabel nameLabel = new JLabel(prompt);

    NumberFormat integerFormat = NumberFormat.getIntegerInstance();
    integerFormat.setGroupingUsed(false);

    // Create a NumberFormatter with the integer format
    NumberFormatter numberFormatter = new NumberFormatter(integerFormat);
    numberFormatter.setValueClass(
            Integer.class);
    numberFormatter.setMinimum(0);
    numberFormatter.setMaximum(Integer.MAX_VALUE);
    numberFormatter.setAllowsInvalid(false);

    stockAmountInputField = new JFormattedTextField(numberFormatter);
    stockAmountInputField.setPreferredSize(new Dimension(110, 30));


    JButton resetButton = new JButton("Reset");
    resetButton.setActionCommand("reset-shares");
    resetButton.addActionListener(this);

    integerInputPanel.add(nameLabel);
    integerInputPanel.add(stockAmountInputField);
    integerInputPanel.add(resetButton);
    return integerInputPanel;
  }

  // Creates a text input box with the prompts given to it and a button.
  private JPanel createInputTextBox(JTextField textField, String prompt, String buttonName,
                                    String buttonActionName) {
    // Create a panel for the input and button
    JPanel inputPanel = new JPanel();
    inputPanel.setLayout(new FlowLayout());

    // Create a label
    JLabel nameLabel = new JLabel(prompt);

    // Add components to the input panel
    inputPanel.add(nameLabel);
    inputPanel.add(textField);

    return inputPanel;
  }

  
  // Creates the value composition and distribution frame, and adds it to the given frame.
  private JPanel addValCompDistFrame(JFrame frame) {
    // Set up the panel for displaying and request info on the total value, composition and
    // distribution of the given portfolio.
    JPanel valueCompDistPanel = new JPanel();
    valueCompDistPanel.setLayout(
            new BoxLayout(valueCompDistPanel, BoxLayout.Y_AXIS));
    valueCompDistPanel.setBorder(BorderFactory.createLineBorder(Color.black));

    // make the panel that holds the main commands
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
    // make the buttons that will be used for the panel
    JButton totValButton = new JButton("Total Value");
    JButton compositionButton = new JButton("Composition");
    JButton distributionButton = new JButton("Distribution");

    // Add this stockView as a listener for them.
    totValButton.addActionListener(this);
    compositionButton.addActionListener(this);
    distributionButton.addActionListener(this);

    // add them to the button panel
    buttonPanel.add(totValButton);
    buttonPanel.add(compositionButton);
    buttonPanel.add(distributionButton);


    // Create a panel that contains the button for the date and a text field showing the last
    // selected date
    selectedCompDistValDate = new JTextArea(1, 1);
    JPanel datePanel =
            this.createSelectableDate(selectedCompDistValDate,
                    "Please select a date", "Date",
                    "dateForPortfolioInfo");
    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

    // Create a separator between the date panel and the information panel
    JSeparator separator = new JSeparator();
    separator.setPreferredSize(new Dimension(300, 6));
    separator.setBackground(Color.blue);
    separator.setMaximumSize(separator.getPreferredSize());

    // create the info panel to display outputs from the commands above
    infoPanel = new JPanel();
    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
    infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));

    // set up the default display area
    infoTextAreaValCompDest = new JTextArea(10, 30);
    JScrollPane scrollPane = new JScrollPane(infoTextAreaValCompDest);
    infoTextAreaValCompDest.setEditable(false);
    infoTextAreaValCompDest.setLineWrap(true);
    infoTextAreaValCompDest.setWrapStyleWord(true);
    infoTextAreaValCompDest.setAlignmentX(Component.CENTER_ALIGNMENT);
    infoTextAreaValCompDest.setText("Please select Total Value, " +
            "Composition, or Distribution with a date");
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    infoPanel.add(scrollPane);

    // add the panel of command buttons, the date selector for the commands and the info panel
    // together.
    valueCompDistPanel.add(buttonPanel);
    valueCompDistPanel.add(datePanel);
    valueCompDistPanel.add(separator);
    valueCompDistPanel.add(
            Box.createRigidArea(new Dimension(0, 10)));
    valueCompDistPanel.add(infoPanel);

    // add it all to the current frame
    return valueCompDistPanel;
  }

  // Create date selectable panel to select a date.
  private JPanel createSelectableDate(JTextArea area, String defaultTextField, String buttonName,
                                      String actionName) {
    JPanel datePanel = new JPanel();
    area.setEditable(false);
    area.setLineWrap(true);
    area.setWrapStyleWord(true);
    area.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
    area.setText(defaultTextField);
    // set up the button that set's the date for the actions val, com, dist
    JButton dateSelectButton = new JButton(buttonName);
    dateSelectButton.setActionCommand(actionName);
    dateSelectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    // add this stockView as a listener
    dateSelectButton.addActionListener(this);

    // add the button and the text field to the date panel
    datePanel.add(dateSelectButton);
    datePanel.add(area);
    return datePanel;
  }


  /**
   * updates infoTextArea to show composition of portfolio.
   *
   * @param portfolioName the name of the portfolio.
   * @param stockAmounts  a formatted string representing the amounts of stock in the portfolio.
   */
  @Override
  public void composition(String portfolioName, String stockAmounts) {
    infoTextAreaValCompDest.setText(val_comp_dist + " at date " + dateTotalCompDist.toString() + " "
            + stockAmounts);
  }

  /**
   * updates infoTextArea to show distribution of portfolio.
   *
   * @param portfolioName the name of the portfolio.
   * @param stockValues   a formatted string representing the values of each stock in the portfolio.
   */
  @Override
  public void distribution(String portfolioName, String stockValues) {
    infoTextAreaValCompDest.setText(val_comp_dist + " at date " + dateTotalCompDist.toString() + " "
            + stockValues);
  }

  /**
   * saves the portfolio and shows a message that it is saved.
   */
  @Override
  public void saveCurrentPortfolio() {
    JOptionPane.showMessageDialog(frame, "Successfully saved");
  }


  /**
   * displays a list of every single name of given portfolios.
   *
   * @param names a list of portfolio names to display.
   */
  @Override
  public void displayNames(List<String> names) {
    String displayName = "All Existing Portfolios:\n";
    for (String name : names) {
      displayName += name + "\n";
    }
    allPortfolioNames.setText(displayName);
  }

  /**
   * shows a pop-up of a created portfolio.
   *
   * @param portfolioName the name of the portfolio that was created.
   */
  @Override
  public void createdPortfolio(String portfolioName) {
    frame.remove(portfolioInfo);
    portfolioInfo = createPortfolioPanel(portfolioName);
    frame.add(portfolioInfo, 0);
    actPortfolio.setText("Current Portfolio: " + portfolioName);
    JOptionPane.showMessageDialog(frame, "Successfully logged in to: " + portfolioName);
  }

  /**
   * pop up for a failed operation.
   * THIS SHOULD NEVER HAPPEN IN A WORKING DEPLOYED PROGRAM. If this does get called
   * we show a fatal error
   * because it means the view button inputs are messed up.
   *
   * @param operationName the name of the operation that failed.
   * @param relevantInfo  relevant information about the failure.
   */
  @Override
  public void failedOperation(String operationName, String relevantInfo) {
    JOptionPane.showMessageDialog(frame,
            "Program needs to be reset, one more actions were incorrectly performed",
            "FATAL ERROR",
            JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Sets to the current portfolio given.
   *
   * @param portfolioName the name of the portfolio that was set.
   */
  @Override
  public void portfolioSet(String portfolioName) {
    frame.remove(portfolioInfo);
    portfolioInfo = createPortfolioPanel(portfolioName);
    frame.add(portfolioInfo, 0);
    actPortfolio.setText("Current Portfolio: " + portfolioName);

    JOptionPane.showConfirmDialog(frame, "Successfully logged in to: " + portfolioName);
  }


  /**
   * Displays pop up for buying stock, used when a stock is auto bought at a date/
   * PLEASE NOTE FOR REVIEW: this functionality is not currently supported through the options
   * in {@link #displayMenu(boolean)}. We only have the option to buy a stock at a given date.
   *
   * @param ticker     the ticker of the stock that was bought.
   * @param amt        the amount of the stock that was bought.
   * @param totalOwned the total value of that stock now owned (including previously owned stock).
   */
  @Override
  public void displayBuy(String ticker, int amt, double totalOwned) {
    JOptionPane.showMessageDialog(frame,
            "Ticker: " + ticker + "\nAmount: " + amt + "\nValue: " + totalOwned);
  }
  /**
   * displays a panel for buying stock.
   *
   * @param ticker the ticker of the stock that was bought.
   * @param amount the amount of the stock that was bought.
   * @param value  the total value of that stock now owned (including previously owned stock).
   * @param date   the date when the stock was bought.
   */
  @Override
  public void displayBuy(String ticker, int amount, double value, LocalDate date) {
    JOptionPane.showMessageDialog(frame, "Ticker: " + ticker + "\nAmount: "
            + amount + "\nValue: " + value + "\nDate: " + date);
  }

  /**
   * displays pop up for selling stock.
   * PLEASE NOTE FOR REVIEW: this functionality is not currently supported through the options
   * in {@link #displayMenu(boolean)}. We only have the option to buy a stock at a given date.
   *
   * @param ticker     the ticker of the stock that was sold.
   * @param amt        the amount of the stock that was sold.
   * @param totalOwned the total value of that stock now owned (including previously owned stock).
   */
  @Override
  public void displaySell(String ticker, int amt, double totalOwned) {
    JOptionPane.showMessageDialog(frame,
            "Ticker: " + ticker + "\nAmount: " + amt + "\nValue: " + totalOwned);
  }
  /**
   * displays a panel for selling stock.
   *
   * @param ticker the ticker of the stock that was sold.
   * @param amount the amount of the stock that was sold.
   * @param value  the total value of that stock now owned (including previously owned stock).
   * @param date   the date when the stock was sold.
   */
  @Override
  public void displaySell(String ticker, int amount, double value, LocalDate date) {
    JOptionPane.showMessageDialog(frame, "Ticker: " + ticker +
            "\nAmount: " + amount + "\nValue: " + value + "\nDate: " + date);
  }

  /**
   * displays total value and updates info to show value.
   *
   * @param date  the date to evaluate the portfolio on.
   * @param value the total value of the portfolio on that date.
   */
  @Override
  public void totalValue(LocalDate date, double value) {
    String forText = val_comp_dist + " at date: " + date.toString() + (String.format(
            "\nIs $%.4f", value));
    infoTextAreaValCompDest.setText(forText);
  }

  /**
   * allows for the user to logout of a portfolio.
   */
  @Override
  public void logOut() {
    frame.remove(portfolioInfo);
    portfolioInfo = createPortfolioPanel(null);
    portfolioInfo.remove(actPortfolio);
    actPortfolio.setText("No Current Portfolio");
    portfolioInfo.add(actPortfolio, 0);
    frame.remove(portfolioInfo);
    frame.add(portfolioInfo, 0);

    JOptionPane.showMessageDialog(frame, "Logged out and saved portfolio");

  }

  // Waits until the button clicked flag is true, then resets the flag to false.
  private void waitUntilInputReady() {
    // Wait until buttonClicked is true
    while (!commandReady) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
        this.failedOperation("Wait for Input", e.getMessage());
      }
    }
    commandReady = false;
  }


  /**
   * Allows for the view to wait until the user inputs needed data.
   */
  @Override
  public void inbtwnInputs() {
    // Wait until button is clicked
    //  This allows for the view to wait until the user inputs data that is appended to the
    //  delayedInputStream.
    waitUntilInputReady();
  }


  /**
   * to show when encountering an error.
   *
   * @param error the error message.
   */
  @Override
  public void displayError(String error) {
    if (error == null) {
      error = "FATAL ERROR Command FAILED.";
    }
    JOptionPane.showMessageDialog(new JFrame(), error,
            "Last Action Failed With Error Message",
            JOptionPane.WARNING_MESSAGE);
  }


  // For handling total value, composition and distribution button presses in relation to
  // selected date.
  private void showLogicDate() {
    String commandInput = "";
    if (hasPortfolioDisplay()) {
      this.portfolioRequiredDialog(val_comp_dist);
      return;
    }
    switch (val_comp_dist) {
      case "Total Value":
        commandInput =
                CommandUtils.preFormatTotalValueCommand(dateTotalCompDist);
        commandReady = CommandUtils.addCommand(commandInput, inputStream);
        break;
      case "Composition":
        commandInput =
                CommandUtils.preFormatCompositionCommand(dateTotalCompDist);
        commandReady = CommandUtils.addCommand(commandInput, inputStream);
        break;
      case "Distribution":
        commandInput =
                CommandUtils.preFormatDistributionCommand(dateTotalCompDist);
        commandReady = CommandUtils.addCommand(commandInput, inputStream);
        break;
      default:
        infoTextAreaValCompDest.setText("Displaying No Information At Date " +
                dateTotalCompDist.toString() +
                "\nPlease select Information.");
        break;
    }
  }


  /**
   * action performed the user clicked on a button and the logic that happens next.
   *
   * @param aE the event to be processed.
   */
  @Override
  public void actionPerformed(ActionEvent aE) {
    String actionCommand = aE.getActionCommand();
    actionCommand = actionCommand.toLowerCase();
    String commandInput;
    String ticker = "AAPL";
    String autoStartForDisplayInfo = "A Portfolio and DATE must be selected ";
    switch (actionCommand) {
      case "logout":
        commandInput = CommandUtils.preFormatLogOutCommand();
        commandReady = CommandUtils.addCommand(commandInput, inputStream);
        break;
      case "save":
        commandInput = CommandUtils.preFormatSavePortfolioCommand();
        commandReady = CommandUtils.addCommand(commandInput, inputStream);
        break;
      case "create portfolio":
        try {
          String newPortfolioName = PortfolioDialog.showDialog(frame, "Create a Portfolio",
                  "Name your portfolio:", "Create");
          commandInput = CommandUtils.preFormatCreatePortfolioCommand(newPortfolioName);
          commandReady = CommandUtils.addCommand(commandInput, inputStream);
        } catch (IllegalStateException e) {
          if (e.getMessage().equals("Name is empty.")) {
            // do nothing
          } else {
            this.displayError(e.getMessage());
          }
        }
        break;
      case "set portfolio":
        try {
          String portfolioToSetTo = PortfolioDialog.showDialog(frame, "Set Current Portfolio",
                  "Existing Portfolio to Set:", "Set");
          commandInput = CommandUtils.preFormatSetPortfolioCommand(portfolioToSetTo);
          commandReady = CommandUtils.addCommand(commandInput, inputStream);
        } catch (IllegalStateException e) {
          if (e.getMessage().equals("Name is empty.")) {
            // do nothing
          } else {
            this.displayError(e.getMessage());
          }
        }
        break;
      case "total value":
        val_comp_dist = "Total Value";
        updateInfoPanelBorder(val_comp_dist + " Information");
        if (dateTotalCompDist == null) {
          infoTextAreaValCompDest.setText(
                  autoStartForDisplayInfo + "to display Total Value Information.");
        } else {
          showLogicDate();
        }
        break;
      case "composition":
        val_comp_dist = "Composition";
        updateInfoPanelBorder(val_comp_dist + " Information");
        if (dateTotalCompDist == null) {
          infoTextAreaValCompDest.setText(
                  autoStartForDisplayInfo + "to display Composition Information.");
        } else {
          showLogicDate();
        }
        break;
      case "distribution":
        val_comp_dist = "Distribution";
        updateInfoPanelBorder(val_comp_dist + " Information");
        if (dateTotalCompDist == null) {
          infoTextAreaValCompDest.setText(
                  autoStartForDisplayInfo + "to display Distribution Information.");
        } else {
          showLogicDate();
        }
        break;
      case "dateforportfolioinfo":
        LocalDate maybeCorrect = DatePickerDialog.showDialog(frame, "Select Date");
        if (maybeCorrect != null) {
          dateTotalCompDist = maybeCorrect;
        }
        if (dateTotalCompDist != null) {
          selectedCompDistValDate.setText("Selected date is " + dateTotalCompDist.toString());
          showLogicDate();
        } else {
          infoTextAreaValCompDest.setText("Displaying " + val_comp_dist + " Information\n" +
                  "Please enter a valid date.");
        }
        break;
      case "date-for-buy/sell":
        LocalDate maybeCorrectBuy = DatePickerDialog.showDialog(frame, "Select Date");
        if (maybeCorrectBuy != null) {
          buySellDate = maybeCorrectBuy;
          buySellDateText.setText("Selected date is " + buySellDate.toString());
        }
        break;
      case "buy-date":
        this.handleBuyOrSellButtonPress(true);
        break;
      case "sell-date":
        this.handleBuyOrSellButtonPress(false);
        break;
      case "reset-shares":
        stockAmountInputField.setValue(0);
        break;
      default:
        break;
    }
  }

  private void handleBuyOrSellButtonPress(boolean buy) {
    String buyOrSell = buy ? "Buy" : "Sell";
    if (hasPortfolioDisplay()) {
      portfolioRequiredDialog(buyOrSell);
      return;
    }

    if (buySellInfoExists()) {
      String commandInput = CommandUtils.preFormatSellOrBuyDateCommand(buy,
              tickerTextBox.getText(), getShares(), buySellDate);
      commandReady = CommandUtils.addCommand(commandInput, inputStream);
    } else {
      JOptionPane.showMessageDialog(frame, "You must enter all info before " +
              buyOrSell + "ing" + " a stock");
    }
  }

  private boolean buySellInfoExists() {
    return buySellDate != null && !tickerTextBox.getText().isEmpty()
            && getShares() > 0;
  }

  // Allows for safely getting the amount of shares input from the user (if the box is empty
  // or for some reason contains anything that is not an integer then we just return 0)
  private int getShares() {
    try {
      return Integer.parseInt(stockAmountInputField.getText());
    } catch (NumberFormatException nE) {
      return 0;
    }
  }


  // Shows a dialog message if the portfolio sign-in is displayed with a portfolio name.
  // returns true if the portfolio i
  private boolean hasPortfolioDisplay() {
    return (actPortfolio.getText().equals("No Current Portfolio"));
  }

  // Shows a dialog message if the portfolio sign is displayed with a portfolio name.
  private void portfolioRequiredDialog(String action) {
    JOptionPane.showMessageDialog(frame,
            action + " not currently available. Must be Signed in to Portfolio",
            "Please Sign In",
            JOptionPane.WARNING_MESSAGE);
  }

  @Override
  public void welcomeMessage() {
    // NOTE FOR GRADING: this can be used when starting the program to display a welcome pop up
    // before the program is created but for ease of use we have it commented out.
    // If you would like to see how it works you can just uncomment and run program.

    // JOptionPane.showMessageDialog(frame, "Welcome to the Stock View Application!!!!\n" +
    // "Press OK to get started.");
  }

  // ALL BELOW METHODS ARE CURRENTLY UNSUPPORTED:
  // they will never be called because THERE (SHOULDN'T) is no way for the user to input
  // commands that will request this information.

  /**
   * Currently unsupported method.
   */
  @Override
  public void buyBalanced(Map<String, Double> stockAmounts, Map<String, Double> stockWeights) {
    JOptionPane.showMessageDialog(frame,
            "Unimplemented method, if seeing please text" +
                    "340-513-7415", "FATAL ERROR", JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Currently unsupported method.
   */
  @Override
  public void reBalance() {
    JOptionPane.showMessageDialog(frame,
            "Unimplemented method, if seeing please text" +
                    "340-513-7415", "FATAL ERROR", JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Currently unsupported method.
   */
  @Override
  public void plotStockValue(String ticker, String modelOutput, LocalDate date1, LocalDate date2) {
    JOptionPane.showMessageDialog(frame,
            "Unimplemented method, if seeing please text" +
                    "340-513-7415", "FATAL ERROR", JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Currently unsupported method.
   */
  @Override
  public void plotPortfolio(String portfolioName, String plottedInfo, LocalDate date1,
                            LocalDate date2) {
    JOptionPane.showMessageDialog(frame,
            "Unimplemented method, if seeing please text" +
                    "340-513-7415", "FATAL ERROR", JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Currently unsupported method.
   */
  @Override
  public void downloadStock(String message) {
    JOptionPane.showMessageDialog(frame,
            "Unimplemented method, if seeing please text" +
                    "340-513-7415", "FATAL ERROR", JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Currently unsupported method.
   */
  @Override
  public void farewellMessage() {
    JOptionPane.showMessageDialog(frame,
            "Unimplemented method, if seeing please text" +
                    "340-513-7415", "FATAL ERROR", JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Currently unsupported method.
   */
  @Override
  public void xDaysAvg(String ticker, double price) {
    JOptionPane.showMessageDialog(frame,
            "Unimplemented method, if seeing please text" +
                    "340-513-7415", "FATAL ERROR", JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Currently unsupported method.
   */
  @Override
  public void defDaysCrossover(String ticker, Set<LocalDate> dates) {
    JOptionPane.showMessageDialog(frame,
            "Unimplemented method, if seeing please text" +
                    "340-513-7415", "FATAL ERROR", JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Currently unsupported method.
   */
  @Override
  public void xDaysCrossover(String ticker, Set<LocalDate> dates, int days) {
    JOptionPane.showMessageDialog(frame,
            "Unimplemented method, if seeing please text" +
                    "340-513-7415", "FATAL ERROR", JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Currently unsupported method.
   */
  @Override
  public void gainLoss(String ticker, Double price) {
    JOptionPane.showMessageDialog(frame,
            "Unimplemented method, if seeing please text" +
                    "340-513-7415", "FATAL ERROR", JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Currently unsupported method.
   */
  @Override
  public void getPriceStock(String ticker, double price) {
    JOptionPane.showMessageDialog(frame,
            "Unimplemented method, if seeing please text" +
                    "340-513-7415", "FATAL ERROR", JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Currently unsupported method.
   *
   * @param portfolio the portfolio to display.
   */
  @Override
  public void displayPortfolio(String portfolio) {
    JOptionPane.showMessageDialog(frame,
            "Unimplemented method, if seeing please text" +
                    "340-513-7415", "FATAL ERROR", JOptionPane.ERROR_MESSAGE);
  }
}

