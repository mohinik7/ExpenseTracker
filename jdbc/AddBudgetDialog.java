package dbms_minip;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AddBudgetDialog extends JDialog {
    private JComboBox<String> categoryComboBox;
    private JTextField budgetAmountField;
    private JTextField startDateField;
    private JTextField endDateField;
    private BudgetsDAO budgetsDAO;
    private int userId;
    private BudgetFrame parentFrame;

    public AddBudgetDialog(int userId, BudgetsDAO budgetsDAO, BudgetFrame parentFrame) {
        this.userId = userId;
        this.budgetsDAO = budgetsDAO;
        this.parentFrame = parentFrame;

        setTitle("Add Budget");
        setSize(300, 300);
        setLayout(new GridLayout(5, 2, 10, 10));
        setLocationRelativeTo(parentFrame);

        // Category selection
        add(new JLabel("Category:"));
        categoryComboBox = new JComboBox<>();
        loadCategories();
        add(categoryComboBox);

        // Budget amount input
        add(new JLabel("Budget Amount:"));
        budgetAmountField = new JTextField();
        add(budgetAmountField);

        // Start date input
        add(new JLabel("Start Date (YYYY-MM-DD):"));
        startDateField = new JTextField(LocalDate.now().toString());
        add(startDateField);

        // End date input
        add(new JLabel("End Date (YYYY-MM-DD):"));
        endDateField = new JTextField(LocalDate.now().plusMonths(1).toString());
        add(endDateField);

        // Buttons
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        add(saveButton);
        add(cancelButton);

        // Action listeners
        saveButton.addActionListener(e -> saveBudget());
        cancelButton.addActionListener(e -> dispose());

        setModal(true);
        setVisible(true);
    }

    private void loadCategories() {
        try {
            List<String> categories = budgetsDAO.getAllCategoryNames(userId);
            for (String category : categories) {
                categoryComboBox.addItem(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveBudget() {
        try {
            String categoryName = (String) categoryComboBox.getSelectedItem();
            int categoryId = budgetsDAO.getCategoryIdByName(categoryName);
            double budgetAmount = Double.parseDouble(budgetAmountField.getText());
            Date startDate = Date.valueOf(startDateField.getText());
            Date endDate = Date.valueOf(endDateField.getText());

            budgetsDAO.addBudget(userId, categoryId, budgetAmount, startDate, endDate);
            parentFrame.loadBudgets();
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Please enter valid data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

