package model;

import java.time.LocalDate;

import model.portfolio.SimplePortfolio;

/**
 * The {@code SimpleStockModel} object is made for use by a controller to communicate
 * with the useful parts of the rest of the model (methods the user should have access to).
 * This model keeps can keep track of multiple {@code Portfolio}, and the current {@code Portfolio}
 * in use. It provides methods that are specific to the current portfolios, and other methods that
 * can be accessed by anyone without a portfolios set.
 * Please note: This model does not currently ensure security of these portfolios, beyond requiring
 * knowledge of a portfolios name.
 */
public class SimpleStockModel extends AbstractStockModel {
  /**
   * Constructs a new {@code SimpleStockModel} object with no portfolios, and no current
   * portfolios account.
   */
  public SimpleStockModel() {
    super();
  }

  @Override
  protected void addPortfolio(String portfolioName) {
    this.viewedPortfolios.put(portfolioName, new SimplePortfolio());
  }

  @Override
  public double totalValue(LocalDate date) {
    return currentAct.totalValue(date);
  }
}
