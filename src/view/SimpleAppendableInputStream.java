package view;

import java.io.IOException;
import java.io.InputStream;

/**
 * A simple InputStream implementation that allows data to be appended dynamically.
 * Useful in scenarios where data is generated or received in parts. The stream supports appending
 * data and notifies readers when new data is available,
 */
public class SimpleAppendableInputStream extends InputStream {
  private final StringBuilder buffer;
  private int position;
  private boolean closed;

  /**
   * constructor to create the buffer the position in the stream, and the fact that the stream is.
   * not closed.
   */
  public SimpleAppendableInputStream() {
    buffer = new StringBuilder();
    position = 0;
    closed = false;
  }

  @Override
  public synchronized int read() throws IOException {
    while (position >= buffer.length() && !closed) {
      try {
        wait(); // Wait for more data to be appended
      } catch (InterruptedException e) {
        throw new IOException("Read interrupted", e);
      }
    }

    if (position < buffer.length()) {
      return buffer.charAt(position++);
    }
    return -1; // End of stream and no more data
  }

  public synchronized void append(String data) {
    buffer.append(data);
    notifyAll(); // Notify readers that new data is available
  }

  public synchronized void closeStream() {
    closed = true;
    notifyAll(); // Notify readers to stop waiting
  }
}

