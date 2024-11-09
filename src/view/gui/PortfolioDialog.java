package view.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.BorderLayout;


/**
 * text field prompt.:
 * submit button name: "Create"
 */
public class PortfolioDialog extends JDialog implements FocusListener, ActionListener {
  private JTextField nameField;
  private JLabel displayLabel;

  /**
   * constructor for a portfolio dialog.
   * @param parent main feild.
   * @param title of frame.
   * @param textFieldPrompt text for the prompt.
   * @param submitButtonText action for submit text.
   */
  public PortfolioDialog(JFrame parent, String title, String textFieldPrompt,
                         String submitButtonText) {
    super(parent, title, true);
    // Set the layout for the dialog
    setLayout(new FlowLayout());
    this.setSize(new Dimension(400, 100));
    // Create a panel for the input and button
    JPanel inputPanel = new JPanel();
    inputPanel.setLayout(new FlowLayout());

    // Create a label, text field, and button
    JLabel nameLabel = new JLabel(textFieldPrompt);

    nameField = new JTextField(10);
    nameField.addFocusListener(this);
    JButton submitButton = new JButton(submitButtonText);
    submitButton.addActionListener(this);

    // Add components to the input panel
    inputPanel.add(nameLabel);
    inputPanel.add(nameField);
    inputPanel.add(submitButton);

    // Add input and display panels to the main panel
    add(inputPanel, BorderLayout.NORTH);
  }

  // Method to display the name
  private void confirmValid() {
    String name = nameField.getText();
    if (name.isEmpty()) {
      throw new IllegalStateException("Name cannot be empty");
    }
    if (name.contains(" ")) {
      throw new IllegalStateException("Name cannot contain spaces");

    }
  }


  /**
   * gets the portfolio input name.
   * @return the name of the portfolio.
   * @throws IllegalStateException if not portfolio is selected.
   */
  public String getPortfolioInputName() throws IllegalStateException {
    if (nameField.getText().isEmpty()) {
      throw new IllegalStateException("Name is empty.");
    }
    return nameField.getText();
  }

  /**
   * to create a pop up for the the user to see.
   * @param parent main frame.
   * @param title title of the pop up.
   * @param textFieldPrompt text for pop up.
   * @param submitButtonText action for the return
   * @return the string of the option.
   * @throws IllegalStateException nothing selected.
   */
  public static String showDialog(JFrame parent, String title, String textFieldPrompt,
                                  String submitButtonText) throws IllegalStateException {
    PortfolioDialog dialog = new PortfolioDialog(parent, title, textFieldPrompt, submitButtonText);
    dialog.setVisible(true);
    return dialog.getPortfolioInputName();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    try {
      confirmValid();
      dispose();
    } catch (IllegalStateException ex) {
      String message = ex.getMessage();
      if (message.contains("spaces")) {
        JOptionPane.showMessageDialog(new JFrame(),
                "Name of portfolio cannot have spaces.",
                "Portfolio Name With Spaces",
                JOptionPane.WARNING_MESSAGE);
      }
      if (message.contains("empty")) {
        JOptionPane.showMessageDialog(new JFrame(),
                "Name of portfolio cannot be empty.",
                "Empty Portfolio Name",
                JOptionPane.WARNING_MESSAGE);
      }
    }
  }

  @Override
  public void focusGained(FocusEvent e) {
    if (e.getSource() == nameField) {
      nameField.setText("");
    }
  }

  @Override
  public void focusLost(FocusEvent e) {
    // does nothing
    
  }
}
