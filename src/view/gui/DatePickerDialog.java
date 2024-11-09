package view.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JDialog;

/**
 * The {@code DatePickerDialog} class represents a dialog box that allows the user to select a date
 * using three combo boxes for day, month, and year. The user can then select the date using a
 * "Select" button. The {@link #showDialog(JFrame, String)} method can be used to display the dialog
 * and returns the selected date as a {@link LocalDate} object.
 */
public class DatePickerDialog extends JDialog implements ItemListener, ActionListener {
  private JComboBox<Integer> dayComboBox;
  private JComboBox<String> monthComboBox;
  private JComboBox<Integer> yearComboBox;
  private LocalDate selectedDate;

  /**
   * The date picker dialog constructor creates everything needed.
   * @param parent The frame for the popup.
   * @param title Title name for the popup.
   */
  public DatePickerDialog(JFrame parent, String title) {
    super(parent, title, true);
    setLayout(new GridLayout(2, 4, 10, 10));

    // Day ComboBox
    dayComboBox = new JComboBox<>();
    updateDays(31); // Start with 31 days by default
    add(new JLabel("Day:"));
    add(dayComboBox);

    // Month ComboBox
    monthComboBox = new JComboBox<>();
    ArrayList<String> months = new ArrayList<>();
    months.add("Jan");
    months.add("Feb");
    months.add("Mar");
    months.add("Apr");
    months.add("May");
    months.add("Jun");
    months.add("Jul");
    months.add("Aug");
    months.add("Sep");
    months.add("Oct");
    months.add("Nov");
    months.add("Dec");
    for (int i = 0; i < 12; i++) {
      monthComboBox.addItem((i + 1) + " " + months.get(i));
    }
    add(new JLabel("Month:"));
    add(monthComboBox);

    // Year ComboBox
    yearComboBox = new JComboBox<>();
    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
    for (int i = currentYear; i >= 1990; i--) {
      yearComboBox.addItem(i);
    }
    add(new JLabel("Year:"));
    add(yearComboBox);

    // Add this as an item listener for the boxes that change the day options
    monthComboBox.addItemListener(this);
    yearComboBox.addItemListener(this);

    // Select Button
    JButton selectButton = new JButton("Select");
    selectButton.addActionListener(this);
    add(selectButton);

    pack();
    setLocationRelativeTo(parent);
  }

  private void updateDays(int numDays) {
    dayComboBox.removeAllItems();
    for (int i = 1; i <= numDays; i++) {
      dayComboBox.addItem(i);
    }
  }

  private void updateDayComboBox() {
    int month = Integer.parseInt(((String) (monthComboBox.getSelectedItem())).substring(0, 1));
    int year = (int) yearComboBox.getSelectedItem();
    int daysInMonth = getDaysInMonth(month, year);
    updateDays(daysInMonth);
  }

  private int getDaysInMonth(int month, int year) {
    switch (month) {
      case 2:
        return isLeapYear(year) ? 29 : 28;
      case 4:
      case 6:
      case 9:
      case 11:
        return 30;
      default:
        return 31;
    }
  }

  private boolean isLeapYear(int year) {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
  }

  public LocalDate getSelectedDate() {
    return selectedDate;
  }

  /**
   * creates the datepickerdialog and makes a popup.
   * @param parent larger name of the frame.
   * @param title title of the popup.
   * @return the localDate that has been selected.
   */
  public static LocalDate showDialog(JFrame parent, String title) {
    DatePickerDialog dialog = new DatePickerDialog(parent, title);
    dialog.setVisible(true);
    return dialog.getSelectedDate();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    int day = (int) dayComboBox.getSelectedItem();
    int month = Integer.parseInt(((String) (monthComboBox.getSelectedItem())).substring(0, 1));
    int year = (int) yearComboBox.getSelectedItem();
    selectedDate = LocalDate.of(year, month, day);
    dispose();
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    updateDayComboBox();
  }
}
