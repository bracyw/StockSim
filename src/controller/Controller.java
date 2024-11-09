package controller;

import java.util.Scanner;

/**
 * The {@code Controller} interface represents a controller that can run until quiting and process
 * commands given a scanner.
 * This controller works in traditional MVC architecture, where the controller is responsible for
 * processing input and delegating the appropriate actions to the model and view.
 */
public interface Controller {
  /**
   * "Starts the controller." This method allows the controller to begin "controlling" the program.
   * Will keep running as long as the controller has input to read. It is responsible for processing
   * the input, calling the appropriate methods to handle the input, and outputting the results.
   */
  void control();

  /**
   * Given a command and a scanner, method processes the command and reads the corresponding inputs
   * from the scanner. It is responsible for calling the appropriate methods to handle the command.
   * It is also responsible for outputting the results of the command.
   *
   * @param command the command to process.
   * @param scanner the scanner to read input from (which has specific paramters for the command).
   */
  void processCommand(String command, Scanner scanner);
}
