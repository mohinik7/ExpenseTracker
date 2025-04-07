package dbms_minip;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class RecordFrame extends JFrame {
    private JTable recordTable;
    private DefaultTableModel tableModel;
    private RecordsDAO recordsDAO;
    private int userId;

    public RecordFrame(int userId) {
        this.userId = userId;
        this.recordsDAO = new RecordsDAO();

        setTitle("Records");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Set frame to full-screen mode
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Main panel with dark brown background
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(44, 26, 18)); // Dark brown background
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        getContentPane().add(mainPanel, BorderLayout.CENTER);

        // Title label
        JLabel titleLabel = new JLabel("Manage Records");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(44, 26, 18));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Table to display records with customized aesthetics
        tableModel = new DefaultTableModel(new String[]{"Record ID", "Date", "Time", "Category", "Account", "Amount", "Notes"}, 0) {
            // Make column headers bold
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        recordTable = new JTable(tableModel);
        recordTable.setBackground(new Color(88, 56, 39)); // Table background color
        recordTable.setForeground(Color.WHITE); // Table text color
        recordTable.setFont(new Font("Arial", Font.PLAIN, 14)); // Table font
        recordTable.setRowHeight(25);

        // Set column headers to be bold and centered
        JTableHeader header = recordTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setForeground(Color.YELLOW); // Header text color
        header.setBackground(new Color(88, 56, 39)); // Header background color
        recordTable.setTableHeader(header);

        loadRecords();

        JScrollPane scrollPane = new JScrollPane(recordTable);
        scrollPane.setPreferredSize(new Dimension(700, 400)); // Scroll pane size
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel with consistent styling
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        buttonPanel.setBackground(new Color(44, 26, 18)); // Dark brown background

        JButton addButton = new JButton("Add Record");
        JButton updateButton = new JButton("Update Record");
        JButton deleteButton = new JButton("Delete Record");
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
        addButton.addActionListener(e -> openAddRecordDialog());
        updateButton.addActionListener(e -> openUpdateRecordDialog());
        deleteButton.addActionListener(e -> deleteRecord());
        backButton.addActionListener(e -> {
            new DashboardFrame(userId).setVisible(true);
            dispose();
        });
    }

    private void loadRecords() {
        try {
            ArrayList<Record> records = recordsDAO.getRecordsForUser(userId);
            tableModel.setRowCount(0); // Clear existing rows
            for (Record record : records) {
                tableModel.addRow(new Object[]{
                        record.getRecordId(),
                        record.getDate(),
                        record.getTime(),
                        record.getCategoryName(),
                        record.getAccountName(),
                        record.getAmount(),
                        record.getNotes()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading records: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openAddRecordDialog() {
        JDialog dialog = new JDialog(this, "Add Record", true);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JComboBox<String> categoryDropdown = new JComboBox<>();
        JComboBox<String> accountDropdown = new JComboBox<>();
        JTextField amountField = new JTextField();
        JTextField notesField = new JTextField();

        populateDropdowns(categoryDropdown, accountDropdown);

        dialog.add(new JLabel("Category:"));
        dialog.add(categoryDropdown);
        dialog.add(new JLabel("Account:"));
        dialog.add(accountDropdown);
        dialog.add(new JLabel("Amount:"));
        dialog.add(amountField);
        dialog.add(new JLabel("Notes:"));
        dialog.add(notesField);

        JButton saveButton = new JButton("Save");
        customizeButton(saveButton);
        saveButton.addActionListener(e -> {
            try {
                int categoryId = recordsDAO.getCategoryIdByName((String) categoryDropdown.getSelectedItem(), userId);
                int accountId = recordsDAO.getAccountIdByName((String) accountDropdown.getSelectedItem(), userId);
                double amount = Double.parseDouble(amountField.getText());
                String notes = notesField.getText();

                recordsDAO.addRecord(categoryId, accountId, amount, notes);
                loadRecords();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding record: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(saveButton);
        dialog.setVisible(true);
    }

    private void openUpdateRecordDialog() {
        int selectedRow = recordTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a record to update", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int recordId = (int) tableModel.getValueAt(selectedRow, 0);

        JDialog dialog = new JDialog(this, "Update Record", true);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JComboBox<String> categoryDropdown = new JComboBox<>();
        JComboBox<String> accountDropdown = new JComboBox<>();
        JTextField amountField = new JTextField(tableModel.getValueAt(selectedRow, 5).toString());
        JTextField notesField = new JTextField(tableModel.getValueAt(selectedRow, 6).toString());

        populateDropdowns(categoryDropdown, accountDropdown);

        dialog.add(new JLabel("Category:"));
        dialog.add(categoryDropdown);
        dialog.add(new JLabel("Account:"));
        dialog.add(accountDropdown);
        dialog.add(new JLabel("Amount:"));
        dialog.add(amountField);
        dialog.add(new JLabel("Notes:"));
        dialog.add(notesField);

        JButton updateButton = new JButton("Update");
        customizeButton(updateButton);
        updateButton.addActionListener(e -> {
            try {
                int categoryId = recordsDAO.getCategoryIdByName((String) categoryDropdown.getSelectedItem(), userId);
                int accountId = recordsDAO.getAccountIdByName((String) accountDropdown.getSelectedItem(), userId);
                double amount = Double.parseDouble(amountField.getText());
                String notes = notesField.getText();

                recordsDAO.updateRecord(recordId, categoryId, accountId, amount, notes);
                loadRecords();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error updating record: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(updateButton);
        dialog.setVisible(true);
    }

    private void deleteRecord() {
        int selectedRow = recordTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a record to delete", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int recordId = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            recordsDAO.deleteRecord(recordId);
            loadRecords();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting record: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateDropdowns(JComboBox<String> categoryDropdown, JComboBox<String> accountDropdown) {
        try {
            ArrayList<String> categories = recordsDAO.getUserCategories(userId);
            ArrayList<String> accounts = recordsDAO.getUserAccounts(userId);

            categoryDropdown.removeAllItems();
            accountDropdown.removeAllItems();

            for (String category : categories) categoryDropdown.addItem(category);
            for (String account : accounts) accountDropdown.addItem(account);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error populating dropdowns: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void customizeButton(JButton button) {
        button.setBackground(new Color(44, 26, 18)); // Button background color
        button.setForeground(Color.WHITE); // Button text color
        button.setFont(new Font("Arial", Font.BOLD, 14)); // Button font style
        button.setBorder(BorderFactory.createLineBorder(Color.YELLOW)); // Button border color
    }
}
