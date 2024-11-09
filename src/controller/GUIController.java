package controller;

import model.BetterStockModel;
import view.AdvancedStockView;

/**
 * The {@code GUIController} class represents a controller that interacts with the model and view
 * to provide the user with the ability to interact with the stock data. This controller is designed
 * to be used with a GUI interface, that appends to the given {@code DelayedInputStream} object,
 * throughout the lifetime of the program.
 * This controller is designed to be used with the {@code AdvancedStockView}
 * interface, which provides the methodology to display
 * the results of the model's actions, and add custom
 * functionality in between user interactions.
 * TLDR: this controller is identical to the {@code BetterController}
 * class, but uses a custom input stream
 * that should get appended to throughout the lifetime of the program.
 */
public class GUIController extends BetterController {

  /**
   * Constructs a new {@code BetterController} object with the given model, readable, and view.
   * If the model, readable, or stockView is null, an IllegalArgumentException is thrown.
   *
   * @param model     the {@code BetterStockModel} object to interact with stock data and user
   *                  information
   * @param stockView the {@code AdvancedStockView} object to display the results of the model's
   *                  actions.
   * @throws IllegalArgumentException if the model, readable, or stockView is null
   */
  public GUIController(BetterStockModel model, Readable readable, AdvancedStockView stockView)
          throws IllegalArgumentException {
    super(model, readable, stockView);
  }

}
