package view;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

/**
 * The {@code SimpleStockViewTest} class is a JUnit test class that tests
 * the methods in the {@code SimpleStockView} class.
 */
public class SimpleStockViewTest {
  public StockView stockView;
  public Appendable bufferedWriter;

  @Before
  public void startUp() {
    bufferedWriter = new StringBuilder();
    stockView = new SimpleStockView(bufferedWriter);
  }

  @Test
  public void createdPortfolio() {
    stockView.createdPortfolio("test");
    assertEquals("created portfolio test\n", bufferedWriter.toString());
    this.startUp();
    stockView.createdPortfolio("awjeht");
    assertEquals("created portfolio awjeht\n", bufferedWriter.toString());
    this.startUp();
    stockView.createdPortfolio("23526_235");
    assertEquals("created portfolio 23526_235\n", bufferedWriter.toString());
  }

  @Test
  public void downloadStock() {
    stockView.downloadStock("msft");
    assertEquals("msft\n", bufferedWriter.toString());
    this.startUp();
    stockView.downloadStock("PPYL");
    assertEquals("PPYL\n", bufferedWriter.toString());
    this.startUp();
    stockView.downloadStock("downloaded steve");
    assertEquals("downloaded steve\n", bufferedWriter.toString());
  }

  @Test
  public void failedOperation() {
    stockView.failedOperation("download", "big L");
    assertEquals("\n" +
            "Failed operation download with message: big L\n", bufferedWriter.toString());
    this.startUp();
    stockView.failedOperation("price", "big L");
    assertEquals("\n" +
            "Failed operation price with message: big L\n", bufferedWriter.toString());
    this.startUp();
    stockView.failedOperation("beans", "the magical fruit");
    assertEquals("\n" +
            "Failed operation beans with message: the magical fruit\n", bufferedWriter.toString());
  }

  @Test
  public void portfolioSet() {
    stockView.portfolioSet("msft");
    assertEquals("Current portfolio set to: msft\n", bufferedWriter.toString());
    this.startUp();
    stockView.portfolioSet("PPYL");
    assertEquals("Current portfolio set to: PPYL\n", bufferedWriter.toString());
    this.startUp();
    stockView.portfolioSet("downloadedSteve");
    assertEquals("Current portfolio set to: downloadedSteve\n",
            bufferedWriter.toString());
  }

  @Test
  public void displayPorfolio() {
    stockView.displayPortfolio("msft");
    assertEquals("msft\n", bufferedWriter.toString());
    this.startUp();
    stockView.displayPortfolio("PPYL");
    assertEquals("PPYL\n", bufferedWriter.toString());
    this.startUp();
    stockView.displayPortfolio("downloaded steve");
    assertEquals("downloaded steve\n", bufferedWriter.toString());
  }

  @Test
  public void displayBuy() {
    stockView.displayBuy("msft", 10, 10);
    assertEquals("Bought 10 of msft, worth $10.0000", bufferedWriter.toString());
    this.startUp();
    stockView.displayBuy("PPYL", 60, 173.3);
    assertEquals("Bought 60 of PPYL, worth $173.3000", bufferedWriter.toString());
    this.startUp();
    stockView.displayBuy("downloaded steve", 12, 137.32);
    assertEquals("Bought 12 of downloaded steve, worth $137.3200",
            bufferedWriter.toString());
  }

  @Test
  public void displaySell() {
    stockView.displaySell("msft", 10, 10);
    assertEquals("Sold 10 of msft, worth $10.00000", bufferedWriter.toString());
    this.startUp();
    stockView.displaySell("PPYL", 60, 173.3);
    assertEquals("Sold 60 of PPYL, worth $173.30000", bufferedWriter.toString());
    this.startUp();
    stockView.displaySell("downloaded steve", 12, 137.32);
    assertEquals("Sold 12 of downloaded steve, worth $137.32000",
            bufferedWriter.toString());
  }

  @Test
  public void totalValue() {
    stockView.totalValue(LocalDate.parse("2032-11-08"), 10);
    assertEquals("total value of portfolio at 2032-11-08 is $10.0000",
            bufferedWriter.toString());
    this.startUp();
    stockView.totalValue(LocalDate.parse("2011-06-22"), 173.3);
    assertEquals("total value of portfolio at 2011-06-22 is $173.3000",
            bufferedWriter.toString());
    this.startUp();
    stockView.totalValue(LocalDate.parse("1021-02-07"), 137.32);
    assertEquals("total value of portfolio at 1021-02-07 is $137.3200",
            bufferedWriter.toString());
  }

  @Test
  public void logOut() {
    stockView.logOut();
    assertEquals("successfully logged out", bufferedWriter.toString());
  }

  @Test
  public void displayMenu() {
    stockView.displayMenu(true);
    assertEquals("Please select an option from the following menu:\n" +
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
                    "          quit or q\n\n",
            bufferedWriter.toString());
    this.startUp();
    stockView.displayMenu(false);
    assertEquals("Please select an option from the following menu:\n" +
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
                    "          quit or q\n\n",
            bufferedWriter.toString());
  }

  @Test
  public void xDaysAvg() {
    stockView.xDaysAvg("msft", 10);
    assertEquals("msft has an average of $10.0000 for requested days above.",
            bufferedWriter.toString());
    this.startUp();
    stockView.xDaysAvg("PPYL", 173.3);
    assertEquals("PPYL has an average of $173.3000 for requested days above.",
            bufferedWriter.toString());
    this.startUp();
    stockView.xDaysAvg("downloaded steve", 137.32);
    assertEquals("downloaded steve has an average of $137.3200 for requested days above.",
            bufferedWriter.toString());
  }

  @Test
  public void defDaysCrossover() {
    HashSet<LocalDate> forTest1 = new HashSet<>();
    HashSet<LocalDate> forTest2 = new HashSet<>();
    HashSet<LocalDate> forTest3 = new HashSet<>();
    forTest1.add(LocalDate.parse("2024-03-23"));
    forTest2.add(LocalDate.parse("2024-03-23"));
    forTest2.add(LocalDate.parse("2012-03-23"));

    forTest3.add(LocalDate.parse("2024-03-23"));
    forTest3.add(LocalDate.parse("2024-03-23"));
    forTest3.add(LocalDate.parse("2024-03-23"));

    stockView.defDaysCrossover("msft", forTest1);
    assertEquals(
            "The amount of days that are crossovers of the stock, msft for the time " +
                    "interval of 30 days is 1 with the dates being as follows:\n" +
                    "2024-03-23.", bufferedWriter.toString());
    this.startUp();
    stockView.defDaysCrossover("PPYL", forTest2);
    assertEquals(
            "The amount of days that are crossovers of the stock, PPYL for the time " +
                    "interval of 30 days is 2 with the dates being as follows:\n" +
                    "2024-03-23, 2012-03-23.",
            bufferedWriter.toString());
    this.startUp();
    stockView.defDaysCrossover("downloaded steve", forTest3);
    assertEquals(
            "The amount of days that are crossovers of the stock, downloaded steve " +
                    "for the time interval of 30 days is 1 with the dates being as follows:\n" +
                    "2024-03-23.",
            bufferedWriter.toString());
  }

  @Test
  public void xDaysCrossover() {
    HashSet<LocalDate> forTest1 = new HashSet<>();
    HashSet<LocalDate> forTest2 = new HashSet<>();
    HashSet<LocalDate> forTest3 = new HashSet<>();
    forTest1.add(LocalDate.parse("2024-03-23"));
    forTest2.add(LocalDate.parse("2024-03-23"));
    forTest2.add(LocalDate.parse("2012-03-23"));

    forTest3.add(LocalDate.parse("2024-03-23"));
    forTest3.add(LocalDate.parse("2024-03-23"));
    forTest3.add(LocalDate.parse("2024-03-23"));

    stockView.xDaysCrossover("msft", forTest1, 30);
    assertEquals("The amount of days that are crossovers of the stock, msft for the " +
            "time interval " +
            "of 30 days is 1 with the dates being as follows:\n" +
            "2024-03-23.", bufferedWriter.toString());
    this.startUp();
    stockView.xDaysCrossover("PPYL", forTest2, 30);
    assertEquals(
            "The amount of days that are crossovers of the stock, PPYL for the time " +
                    "interval of 30 days is 2 with the dates being as follows:\n" +
                    "2024-03-23, 2012-03-23.",
            bufferedWriter.toString());
    this.startUp();
    stockView.xDaysCrossover("downloaded steve", forTest3, 30);
    assertEquals(
            "The amount of days that are crossovers of the stock, downloaded steve " +
                    "for the time interval of 30 days is 1 with the dates being as follows:\n" +
                    "2024-03-23.",
            bufferedWriter.toString());
  }

  @Test
  public void gainLoss() {
    stockView.gainLoss("msft", 150.0);
    assertEquals("msft has gained $150.0000 dollars", bufferedWriter.toString());
    this.startUp();
    stockView.gainLoss("PPYL", 173.3);
    assertEquals("PPYL has gained $173.3000 dollars", bufferedWriter.toString());
    this.startUp();
    stockView.gainLoss("downloaded steve", 137.32);
    assertEquals("downloaded steve has gained $137.3200 dollars",
            bufferedWriter.toString());
  }

  @Test
  public void getPriceStock() {
    stockView.getPriceStock("msft", 150.0);
    assertEquals("msft has a price of 150.0000 dollars", bufferedWriter.toString());
    this.startUp();
    stockView.getPriceStock("PPYL", 173.3);
    assertEquals("PPYL has a price of 173.3000 dollars", bufferedWriter.toString());
    this.startUp();
    stockView.getPriceStock("downloaded steve", 137.32);
    assertEquals("downloaded steve has a price of 137.3200 dollars",
            bufferedWriter.toString());
  }

  @Test
  public void displayError() {
    stockView.displayError("msft");
    assertEquals("Failed with error message: msft", bufferedWriter.toString());
    this.startUp();
    stockView.displayError("PPYL");
    assertEquals("Failed with error message: PPYL", bufferedWriter.toString());
    this.startUp();
    stockView.displayError("downloaded steve");
    assertEquals("Failed with error message: downloaded steve",
            bufferedWriter.toString());
  }

  @Test
  public void welcomeMessage() {
    stockView.welcomeMessage();
    assertEquals("Welcome to the OODStocks Program" + System.lineSeparator(),
            bufferedWriter.toString());

  }

  @Test
  public void inbtwnInputs() {
    stockView.inbtwnInputs();
    assertEquals("\n" +
            "Please enter your next command: ", bufferedWriter.toString());

  }

  @Test
  public void farewellMessage() {
    stockView.farewellMessage();

    assertEquals("\n" +
            "Thank you for using the OODStocks Program", bufferedWriter.toString());
  }
}