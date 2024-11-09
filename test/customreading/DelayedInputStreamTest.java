package customreading;

import java.io.InputStreamReader;
import java.util.Scanner;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * Class used for testing DelayedInputStreamTest.
 */
public class DelayedInputStreamTest {
  DelayedInputStream delayedInputStream = new DelayedInputStream();


  //Testing the delayedInputStream with appending inputs and reading inputs.
  @Test
  public void testAppendInput() {
    delayedInputStream.appendInput("hat 123");
    Scanner scanner = new Scanner(new InputStreamReader(delayedInputStream));
    assertEquals(scanner.next(), "hat");
    assertEquals(scanner.next(), "123");
  }
}