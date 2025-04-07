package dbms_minip;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class UpdateBudgetDialog extends JDialog {
    private JTextField budgetAmountField;
    private BudgetsDAO budgetsDAO;
    private int userId;
    private String categoryName;
    private BudgetFrame parentFrame;

    public UpdateBudgetDialog(int userId, String categoryName, double currentAmount, BudgetsDAO budgetsDAO, BudgetFrame parentFrame) {
        this.userId = userId;
        this.categoryName = categoryName;
        this.budgetsDAO = budgetsDAO;
        this.parentFrame = parentFrame;

        setTitle("Update Budget");
        setSize(300, 150);
        setLayout(new GridLayout(3, 2, 10, 10));
        setLocationRelativeTo(parentFrame);

        // Current category and amount display
        add(new JLabel("Category:"));
        add(new JLabel(categoryName));

        add(new JLabel("New Budget Amount:"));
        budgetAmountField = new JTextField(String.valueOf(currentAmount));
        add(budgetAmountField);

        // Buttons
        JButton updateButton = new JButton("Update");
        JButton cancelButton = new JButton("Cancel");
        add(updateButton);
        add(cancelButton);

        // Action listeners
        updateButton.addActionListener(e -> updateBudget());
        cancelButton.addActionListener(e -> dispose());

        setModal(true);
        setVisible(true);
    }

    private void updateBudget() {
        try {
            int categoryId = budgetsDAO.getCategoryIdByName(categoryName);
            double newBudgetAmount = Double.parseDouble(budgetAmountField.getText());

            budgetsDAO.updateBudgetAmount(userId, categoryId, newBudgetAmount);
            parentFrame.loadBudgets();
            dispose();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid budget amount.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
