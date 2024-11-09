import java.io.InputStreamReader;
import java.io.StringReader;

import customreading.DelayedInputStream;
import controller.BetterController;
import controller.Controller;
import controller.GUIController;
import model.BetterStockModel;
import model.BetterStockModelImpl;
import view.AdvancedStockView;
import view.AdvancedStockViewImpl;
import view.gui.GUIStockView;


/**
 * The main program class that is used for running this stock program, that allows a user
 * to interact with stocks and a portfolio.
 */
public class StockProgram {
  /**
   * Main method of the stock program, this starts the stock program.
   *
   * @param args any command line arguments.
   */
  public static void main(String[] args) {
    gui();
  }


  private static void gui() {
    BetterStockModel model = new BetterStockModelImpl();
    DelayedInputStream inputStream = new DelayedInputStream();
    // testing if the regular input stream reader will work
    InputStreamReader readable = new InputStreamReader(inputStream);
    AdvancedStockView view = new GUIStockView(inputStream);
    Controller controller = new GUIController(model, readable, view);
    controller.control();
  }


  private static void user() {

    BetterStockModel model = new BetterStockModelImpl();
    Readable rd = new InputStreamReader(System.in);
    AdvancedStockView stockView = new AdvancedStockViewImpl(System.out);
    Controller controller = new BetterController(model, rd, stockView);
    controller.control();
  }

  private static void debug() {
    BetterStockModel model = new BetterStockModelImpl();
    Readable rd = new StringReader("create-portfolio yourmom sell-date AAPL 10 2024-01-01");
    AdvancedStockView stockView = new AdvancedStockViewImpl(System.out);
    Controller controller = new BetterController(model, rd, stockView);
    controller.control();
  }
}

