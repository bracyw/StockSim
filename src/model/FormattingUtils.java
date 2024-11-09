package model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * This {@code FormattingUtils} class represents the utils for abstracting the formating
 * of different types of tables, graphs, etc. that are commonly used in the model.
 */
public class FormattingUtils {
  /**
   * Computes the proper interval spacing for a {@link #barGraph(Map, double)} between two dates.
   *
   * @param from the date to start computing at.
   * @param to   the date to stop computing at.
   * @return the proper interval spacing for a graph between two dates.
   */
  public int interval(LocalDate from, LocalDate to) {
    if (from.isAfter(to)) {
      throw new IllegalArgumentException("The start date must be before the end date.");
    }
    int interval = 1;
    long days = DAYS.between(from, to);

    if (days <= 10) {
      interval = 1;
    } else if (days <= 58) {
      interval = 2;
    } else if (days <= 175) {
      interval = 7;
    } else if (days <= 870) {
      interval = 30;
    } else if (days <= 5475) {
      interval = 365;
    } else {
      interval = 365 * 3;
    }
    return interval;
  }


  /**
   * Creates a bar graph with the given values and dates, using the max value to maintain a relative
   * scale.
   *
   * @param valuesOverTime the dates mapped to their given values to be displayed
   * @param max            the maximum value of all values over time.
   * @return String the formatted bar graph.
   */
  public String barGraph(Map<LocalDate, Double> valuesOverTime,
                         double max) {


    double scale = Math.max(1, max / 50); // Ensure at most 50 asterisks per line

    // Generate the chart
    StringBuilder chart = new StringBuilder();
    for (Map.Entry<LocalDate, Double> entry : valuesOverTime.entrySet()) {
      LocalDate date = entry.getKey();
      double value = entry.getValue();
      int numAsterisks = (int) Math.ceil(value / scale);
      chart.append(String.format("%s %s\n", date, "*".repeat(numAsterisks)));

    }
    chart.append(String.format("scale * %.4f", scale));
    return chart.toString();
  }

  /**
   * Takes in a list of {@code String}(s) and a header, then returns them as a formatted table.
   *
   * @param table  the list of strings to be formatted as a table.
   * @param header the header of the table.
   * @return the formatted table as a string.
   */
  public String createTable(List<String> table, String header) {
    StringBuilder outputTable = new StringBuilder();
    outputTable.append(header + "\n");
    for (int i = 0; i < table.size(); i++) {
      outputTable.append(table.get(i));
      // ensure that a new line isn't added at the end of the table.
      if (i < table.size() - 1) {
        outputTable.append('\n');
      }
    }

    return outputTable.toString();
  }
}
