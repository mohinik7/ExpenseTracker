package dbms_minip;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;
import java.awt.Dimension;
import java.awt.Toolkit;

public class AccountFrame extends JFrame {
	private int userId;
	private AccountsDAO accountsDAO = new AccountsDAO();
	private JTable accountsTable;

	public AccountFrame(int userId) {
		this.userId = userId;
		setTitle("Manage Accounts");
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set frame to full-screen mode
        setExtendedState(JFrame.MAXIMIZED_BOTH);


		// Main panel with dark brown background
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBackground(new Color(44, 26, 18));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		getContentPane().add(mainPanel, BorderLayout.CENTER);

		// Title panel with minimize button
		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.setBackground(new Color(44, 26, 18));

		JLabel titleLabel = new JLabel("Manage Accounts");
		titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titlePanel.add(titleLabel, BorderLayout.CENTER);

		// Add a minimize button
		JButton minimizeButton = new JButton("-");
		minimizeButton.setFont(new Font("Arial", Font.BOLD, 18));
		minimizeButton.setForeground(Color.WHITE);
		minimizeButton.setBackground(new Color(44, 26, 18));
		minimizeButton.setFocusPainted(false);
		minimizeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		minimizeButton.addActionListener(e -> setState(JFrame.ICONIFIED));

		JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		controlPanel.setBackground(new Color(44, 26, 18));
		controlPanel.add(minimizeButton);
		titlePanel.add(controlPanel, BorderLayout.EAST);

		mainPanel.add(titlePanel, BorderLayout.NORTH);

		// Display accounts in a table with padding
		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.setBackground(new Color(44, 26, 18));
		tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		displayAccounts(tablePanel);

		mainPanel.add(tablePanel, BorderLayout.CENTER);

		// Button panel with consistent styling
		JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
		buttonPanel.setBackground(new Color(44, 26, 18));

		JButton addButton = new JButton("Add Account");
		JButton updateButton = new JButton("Update Account");
		JButton deleteButton = new JButton("Delete Account");
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
		addButton.addActionListener(e -> addAccount());
		updateButton.addActionListener(e -> updateSelectedAccount());
		deleteButton.addActionListener(e -> deleteSelectedAccount());
		backButton.addActionListener(e -> {
			DashboardFrame dashboardFrame = new DashboardFrame(userId);
			dashboardFrame.setVisible(true);
			dispose();
		});
	}

	private void displayAccounts(JPanel tablePanel) {
		try {
			List<Account> accounts = accountsDAO.getAllAccountsByUserId(userId);
			String[] columnNames = { "Account ID", "Account Name", "Initial Amount" };
			Object[][] data = new Object[accounts.size()][3];

			for (int i = 0; i < accounts.size(); i++) {
				data[i][0] = accounts.get(i).getAccountId();
				data[i][1] = accounts.get(i).getAccountName();
				data[i][2] = accounts.get(i).getInitialAmount();
			}

			accountsTable = new JTable(data, columnNames);
			accountsTable.setBackground(new Color(88, 56, 39));
			accountsTable.setForeground(Color.WHITE);
			accountsTable.setFont(new Font("Arial", Font.BOLD, 14)); // Make text bold
			accountsTable.setRowHeight(25);

			// Make header bold and visible
			accountsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
			accountsTable.getTableHeader().setForeground(Color.WHITE);
			accountsTable.getTableHeader().setBackground(new Color(88, 56, 39));

			JScrollPane scrollPane = new JScrollPane(accountsTable);
			scrollPane.setPreferredSize(new Dimension(750, 300));
			scrollPane.getViewport().setBackground(new Color(44, 26, 18)); // Background under table

			tablePanel.add(scrollPane, BorderLayout.CENTER);
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this, "Error fetching accounts: " + ex.getMessage());
		}
	}

	private void addAccount() {
	    JTextField accountNameField = new JTextField(20);
	    JTextField initialAmountField = new JTextField(20);

	    // Create a panel with padding and lighter background
	    JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
	    panel.setBackground(new Color(60, 34, 22)); // Slightly lighter color for better contrast

	    JLabel accountNameLabel = new JLabel("Account Name:");
	    accountNameLabel.setForeground(Color.WHITE);
	    accountNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
	    panel.add(accountNameLabel);
	    panel.add(accountNameField);

	    JLabel initialAmountLabel = new JLabel("Initial Amount:");
	    initialAmountLabel.setForeground(Color.WHITE);
	    initialAmountLabel.setFont(new Font("Arial", Font.BOLD, 14));
	    panel.add(initialAmountLabel);
	    panel.add(initialAmountField);

	    int option = JOptionPane.showConfirmDialog(
	        this,
	        panel,
	        "Enter Account Details",
	        JOptionPane.OK_CANCEL_OPTION,
	        JOptionPane.PLAIN_MESSAGE
	    );

	    if (option == JOptionPane.OK_OPTION) {
	        String accountName = accountNameField.getText();
	        String initialAmountText = initialAmountField.getText();

	        try {
	            double initialAmount = Double.parseDouble(initialAmountText);

	            if (!accountName.isEmpty()) {
	                accountsDAO.addAccount(userId, accountName, initialAmount);
	                JOptionPane.showMessageDialog(this, "Account added successfully.");
	                displayAccounts((JPanel) getContentPane().getComponent(0)); // Refresh account list
	            } else {
	                JOptionPane.showMessageDialog(this, "Account name cannot be empty.");
	            }
	        } catch (NumberFormatException ex) {
	            JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a valid number.");
	        } catch (SQLException ex) {
	            JOptionPane.showMessageDialog(this, "Error adding account: " + ex.getMessage());
	        }
	    }
	}

	private void updateSelectedAccount() {
	    int selectedRow = accountsTable.getSelectedRow();
	    if (selectedRow >= 0) {
	        int accountId = (int) accountsTable.getValueAt(selectedRow, 0);
	        String currentName = (String) accountsTable.getValueAt(selectedRow, 1);
	        String currentAmount = accountsTable.getValueAt(selectedRow, 2).toString();

	        JTextField newNameField = new JTextField(currentName, 20);
	        JTextField newAmountField = new JTextField(currentAmount, 20);

	        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
	        panel.setBackground(new Color(60, 34, 22)); // Slightly lighter color for better contrast

	        JLabel newNameLabel = new JLabel("New Account Name:");
	        newNameLabel.setForeground(Color.WHITE);
	        newNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
	        panel.add(newNameLabel);
	        panel.add(newNameField);

	        JLabel newAmountLabel = new JLabel("New Initial Amount:");
	        newAmountLabel.setForeground(Color.WHITE);
	        newAmountLabel.setFont(new Font("Arial", Font.BOLD, 14));
	        panel.add(newAmountLabel);
	        panel.add(newAmountField);

	        int option = JOptionPane.showConfirmDialog(
	            this,
	            panel,
	            "Update Account Details",
	            JOptionPane.OK_CANCEL_OPTION,
	            JOptionPane.PLAIN_MESSAGE
	        );

	        if (option == JOptionPane.OK_OPTION) {
	            String newAccountName = newNameField.getText();
	            String newAmountText = newAmountField.getText();

	            try {
	                double newAmount = Double.parseDouble(newAmountText);

	                if (!newAccountName.isEmpty()) {
	                    accountsDAO.updateAccount(accountId, newAccountName, newAmount);
	                    JOptionPane.showMessageDialog(this, "Account updated successfully.");
	                    displayAccounts((JPanel) getContentPane().getComponent(0)); // Refresh account list
	                } else {
	                    JOptionPane.showMessageDialog(this, "Account name cannot be empty.");
	                }
	            } catch (NumberFormatException ex) {
	                JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a valid number.");
	            } catch (SQLException ex) {
	                JOptionPane.showMessageDialog(this, "Error updating account: " + ex.getMessage());
	            }
	        }
	    } else {
	        JOptionPane.showMessageDialog(this, "Please select an account to update.");
	    }
	}


	private void deleteSelectedAccount() {
		int selectedRow = accountsTable.getSelectedRow();
		if (selectedRow >= 0) {
			int accountId = (int) accountsTable.getValueAt(selectedRow, 0);
			int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this account?",
					"Confirm Delete", JOptionPane.YES_NO_OPTION);

			if (confirm == JOptionPane.YES_OPTION) {
				try {
					accountsDAO.deleteAccount(accountId);
					JOptionPane.showMessageDialog(this, "Account deleted successfully.");
					displayAccounts((JPanel) getContentPane().getComponent(0)); // Refresh account list
				} catch (SQLException ex) {
					JOptionPane.showMessageDialog(this, "Error deleting account: " + ex.getMessage());
				}
			}
		} else {
			JOptionPane.showMessageDialog(this, "Please select an account to delete.");
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
