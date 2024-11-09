package view;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the advanced stock view class.
 */
public class AdvancedStockViewImplTest {
  public AdvancedStockView stockView;
  public Appendable bufferedWriter;

  @Before
  public void startUp() {
    bufferedWriter = new StringBuilder();
    stockView = new AdvancedStockViewImpl(bufferedWriter);
  }

  @Test
  public void composition() {
    stockView.composition("aabc", "10");
    assertEquals("Composition of portfolio aabc \n" +
            "10\n", bufferedWriter.toString());
    stockView.composition("aabc", "10");
  }

  @Test
  public void distribution() {
    stockView.distribution("aabc", "10");
    assertEquals("Distribution of portfolio aabc \n" +
            "10\n", bufferedWriter.toString());
  }

  @Test
  public void saveCurrentPortfolio() {
    stockView.saveCurrentPortfolio();
    assertEquals("Successfully saved current portfolio. \n", bufferedWriter.toString());
  }


  @Test
  public void buyBalanced() {
    Map<String, Double> stocks = Map.of("a", 20.0);
    Map<String, Double> stocks2 = Map.of("a", 10.0);
    stockView.buyBalanced(stocks, stocks2);
    assertEquals(
            "Successfully bought the stocks, with shown amounts, and requested" +
                    " relative weights: \n" +
                    "     a: 20.0, 10.0%\n", bufferedWriter.toString());
  }

  @Test
  public void reBalance() {
    stockView.reBalance();
    assertEquals("Successfully rebalanced requested stocks. \n", bufferedWriter.toString());

  }

  @Test
  public void plotStockValue() {
    stockView.plotStockValue("happy", "sad", LocalDate.parse("2024-01-01"),
            LocalDate.parse("2024-01-02"));
    assertEquals("Performance of stock happy from 2024-01-01 to 2024-01-02: \n" +
            " \n" +
            "sad\n", bufferedWriter.toString());

  }

  @Test
  public void plotPortfolio() {
    stockView.plotPortfolio("happy", "sad", LocalDate.parse("2024-01-01"),
            LocalDate.parse("2024-01-02"));
    assertEquals("Performance of portfolio happy from 2024-01-01 to 2024-01-02: \n" +
            " \n" +
            "sad\n", bufferedWriter.toString());
  }

  @Test
  public void displayBuy() {
    stockView.displayBuy("happy", 5, 15);
    assertEquals("Bought 5 of happy, worth $15.0000", bufferedWriter.toString());
  }


  @Test
  public void displaySell() {
    stockView.displaySell("happy", 5, 15);
    assertEquals("Sold 5 of happy, worth $15.00000", bufferedWriter.toString());
  }
}