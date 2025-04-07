package dbms_minip;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class CategoryFrame extends JFrame {
    private int userId;
    private CategoriesDAO categoriesDAO = new CategoriesDAO();
    private JTable categoriesTable;

    public CategoryFrame(int userId) {
        this.userId = userId;
        setTitle("Manage Categories");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main panel with dark brown background
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(44, 26, 18)); // Dark brown
        getContentPane().add(mainPanel, BorderLayout.CENTER);

        // Title panel with minimize button
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(44, 26, 18));
        
        // Title label
        JLabel titleLabel = new JLabel("Manage Categories");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Minimize button
        JButton minimizeButton = new JButton("-");
        customizeButton(minimizeButton);
        minimizeButton.setFont(new Font("Arial", Font.BOLD, 18));
        minimizeButton.setForeground(Color.YELLOW);
        minimizeButton.setPreferredSize(new Dimension(50, 50));
        minimizeButton.addActionListener(e -> setState(JFrame.ICONIFIED));
        titlePanel.add(minimizeButton, BorderLayout.EAST);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Display categories in a table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(44, 26, 18));
        displayCategories(tablePanel);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Button panel with consistent styling
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        buttonPanel.setBackground(new Color(44, 26, 18)); // Dark brown

        JButton addButton = new JButton("Add Category");
        JButton updateButton = new JButton("Update Category");
        JButton deleteButton = new JButton("Delete Category");
        JButton backButton = new JButton("Back to Dashboard");

        customizeButton(addButton);
        customizeButton(updateButton);
        customizeButton(deleteButton);
        customizeButton(backButton);

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        addButton.addActionListener(e -> addCategory());
        updateButton.addActionListener(e -> updateSelectedCategory());
        deleteButton.addActionListener(e -> deleteSelectedCategory());
        backButton.addActionListener(e -> {
            DashboardFrame dashboardFrame = new DashboardFrame(userId);
            dashboardFrame.setVisible(true);
            dispose();
        });
    }

    private void displayCategories(JPanel tablePanel) {
        try {
            List<Category> categories = categoriesDAO.getAllCategoriesByUserId(userId);
            String[] columnNames = {"Category ID", "Category Name"};
            Object[][] data = new Object[categories.size()][2];

            for (int i = 0; i < categories.size(); i++) {
                data[i][0] = categories.get(i).getCategoryId();
                data[i][1] = categories.get(i).getCategoryName();
            }

            categoriesTable = new JTable(data, columnNames);
            categoriesTable.setBackground(new Color(88, 56, 39));
            categoriesTable.setForeground(Color.WHITE);
            categoriesTable.setFont(new Font("Arial", Font.BOLD, 16));
            categoriesTable.setRowHeight(30);
            categoriesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
            categoriesTable.getTableHeader().setBackground(new Color(150, 75, 0));
            categoriesTable.getTableHeader().setForeground(Color.WHITE);

            JScrollPane scrollPane = new JScrollPane(categoriesTable);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            tablePanel.add(scrollPane, BorderLayout.CENTER);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching categories: " + ex.getMessage());
        }
    }

    private void addCategory() {
        String categoryName = JOptionPane.showInputDialog(this, "Enter Category Name:");
        if (categoryName != null && !categoryName.isEmpty()) {
            try {
                categoriesDAO.addCategory(userId, categoryName);
                JOptionPane.showMessageDialog(this, "Category added successfully.");
                displayCategories((JPanel) getContentPane().getComponent(0)); // Refresh category list
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error adding category: " + ex.getMessage());
            }
        }
    }

    private void updateSelectedCategory() {
        int selectedRow = categoriesTable.getSelectedRow();
        if (selectedRow >= 0) {
            int categoryId = (int) categoriesTable.getValueAt(selectedRow, 0);
            String newCategoryName = JOptionPane.showInputDialog(this, "Enter new Category Name:");
            if (newCategoryName != null && !newCategoryName.isEmpty()) {
                try {
                    categoriesDAO.updateCategoryName(categoryId, newCategoryName);
                    JOptionPane.showMessageDialog(this, "Category updated successfully.");
                    displayCategories((JPanel) getContentPane().getComponent(0)); // Refresh category list
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error updating category: " + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a category to update.");
        }
    }

    private void deleteSelectedCategory() {
        int selectedRow = categoriesTable.getSelectedRow();
        if (selectedRow >= 0) {
            int categoryId = (int) categoriesTable.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this category?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    categoriesDAO.deleteCategory(categoryId);
                    JOptionPane.showMessageDialog(this, "Category deleted successfully.");
                    displayCategories((JPanel) getContentPane().getComponent(0)); // Refresh category list
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting category: " + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a category to delete.");
        }
    }

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
