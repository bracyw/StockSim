package customreading;

import java.io.IOException;
import java.io.InputStream;


/**
 * The {@code DelayedInputStream} class represents a custom input stream that can be appended to
 * throughout the lifetime of the program.
 * This class is designed to be used with the {@code GUIController}
 * and {@code GUIStockView} classes, allowing the {@code GUIStockVIew}
 * to append to this input stream throughout the lifetime of the program, and the
 * {@code GUIController}
 * to read from this input stream
 * to process commands.
 */
public class DelayedInputStream extends InputStream {
  private final StringBuilder buffer;
  private int position;

  /**
   * Constructs a new {@code DelayedInputStream} object with an empty buffer and a position of 0
   * (reading of the {@code buffer} starts from that position).
   */
  public DelayedInputStream() {
    this.buffer = new StringBuilder();
    this.position = 0;
  }

  @Override
  public int read() throws IOException {
    if (position >= buffer.length()) {
      return -1; // No more data
    }
    return buffer.charAt(position++);
  }

  /**
   * Appends the given string to the buffer (e.i. {@code StringBuilder}) that is read from.
   *
   * @param input the input data to add to the buffer.
   */
  public void appendInput(String input) {
    synchronized (buffer) {
      buffer.append(input);
    }
  }
}
