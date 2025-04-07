package dbms_minip;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class BudgetFrame extends JFrame {
    private int userId;
    private BudgetsDAO budgetsDAO;
    private JTable budgetTable;
    private DefaultTableModel tableModel;

    public BudgetFrame(int userId) {
        this.userId = userId;
        budgetsDAO = new BudgetsDAO();

        setTitle("Manage Budgets");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set frame to full-screen mode
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Main panel with dark brown background
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(44, 26, 18)); // Dark brown background
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding
        getContentPane().add(mainPanel, BorderLayout.CENTER);

        // Title label
        JLabel titleLabel = new JLabel("Manage Budgets");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(44, 26, 18));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Table to display budgets with a customized aesthetic
        tableModel = new DefaultTableModel(new String[]{"Category", "Budget Amount", "Start Date", "End Date"}, 0);
        budgetTable = new JTable(tableModel);
        budgetTable.setBackground(new Color(88, 56, 39));
        budgetTable.setForeground(Color.WHITE);
        budgetTable.setFont(new Font("Arial", Font.PLAIN, 14));
        budgetTable.setRowHeight(25);

        // Set table header font to bold
        JTableHeader header = budgetTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16)); // Make header bold
        header.setBackground(new Color(44, 26, 18)); // Dark brown background for header
        header.setForeground(Color.WHITE); // White text for header

        // Make the content bold and more visible
        budgetTable.setFont(new Font("Arial", Font.BOLD, 14)); // Bold content text
        loadBudgets();

        // Scroll pane for the table
        JScrollPane scrollPane = new JScrollPane(budgetTable);
        scrollPane.setPreferredSize(new Dimension(550, 300)); // Limit table size to keep it compact
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel with consistent styling
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        buttonPanel.setBackground(new Color(44, 26, 18)); // Dark brown background

        JButton addButton = new JButton("Add Budget");
        JButton updateButton = new JButton("Update Budget");
        JButton deleteButton = new JButton("Delete Budget");
        JButton backButton = new JButton("Back");

        customizeButton(addButton);
        customizeButton(updateButton);
        customizeButton(deleteButton);
        customizeButton(backButton);

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Action listeners for buttons
        addButton.addActionListener(e -> {
            new AddBudgetDialog(userId, budgetsDAO, this);
        });

        updateButton.addActionListener(e -> {
            int selectedRow = budgetTable.getSelectedRow();
            if (selectedRow != -1) {
                String categoryName = tableModel.getValueAt(selectedRow, 0).toString();
                double currentAmount = Double.parseDouble(tableModel.getValueAt(selectedRow, 1).toString());
                new UpdateBudgetDialog(userId, categoryName, currentAmount, budgetsDAO, this);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a budget to update.");
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = budgetTable.getSelectedRow();
            if (selectedRow != -1) {
                String categoryName = tableModel.getValueAt(selectedRow, 0).toString();
                int categoryId = 0;
                try {
                    categoryId = budgetsDAO.getCategoryIdByName(categoryName);
                    budgetsDAO.deleteBudget(userId, categoryId);
                    loadBudgets();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting budget: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a budget to delete.");
            }
        });

        backButton.addActionListener(e -> {
            new DashboardFrame(userId).setVisible(true);
            dispose();
        });
    }

    // Method to load budgets from the database and display them in the table
    public void loadBudgets() {
        try {
            List<Budget> budgets = budgetsDAO.getAllBudgets(userId);
            tableModel.setRowCount(0); // Clear existing rows
            for (Budget budget : budgets) {
                String categoryName = budgetsDAO.getCategoryNameById(budget.getCategoryId());
                tableModel.addRow(new Object[]{categoryName, budget.getBudgetAmount(), budget.getStartDate(), budget.getEndDate()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading budgets: " + e.getMessage());
        }
    }

    // Customizes button appearance for consistent look-and-feel
    private void customizeButton(JButton button) {
        button.setBackground(new Color(88, 56, 39)); // Dark brown
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Set colors for when the button is pressed or hovered over
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(150, 75, 0)); // Lighter brown on hover
                button.setForeground(Color.YELLOW); // Change text color to yellow on hover
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(88, 56, 39)); // Original background color
                button.setForeground(Color.WHITE); // Original text color
            }
            
            public void mousePressed(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(44, 26, 18)); // Darker shade when pressed
                button.setForeground(Color.CYAN); // Different color on press
            }
            
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(150, 75, 0)); // Reset to hover color
                button.setForeground(Color.YELLOW); // Reset to hover text color
            }
        });
    }
}
