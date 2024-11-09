package model.stock;

/**
 * Represents an exception that is thrown when a stock already exists in the system,
 * and the user tries to create a new stock with the same ticker.
 * Currently, the system is {@code OODStocks}.
 */
public class StockAlreadyExistsException extends RuntimeException {
  /**
   * Constructs a new {@code StockAlreadyExistsException} with the given message.
   *
   * @param message the message to be displayed when the exception is thrown.
   */
  public StockAlreadyExistsException(String message) {
    super(message);
  }
}
