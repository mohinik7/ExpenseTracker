package dbms_minip;

import java.sql.*;
import java.util.*;

public class AccountsDAO {

	// CREATE
	public void addAccount(int userId, String accountName, double initialAmount) throws SQLException {
		String sql = "INSERT INTO Accounts (user_id, account_name, initial_Amount) VALUES (?, ?, ?)";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, userId);
			stmt.setString(2, accountName);
			stmt.setDouble(3, initialAmount);
			stmt.executeUpdate();
			System.out.println("Account added successfully!");
		}
	}

	// READ
	public void getAccountById(int accountId) throws SQLException {
		String sql = "SELECT * FROM Accounts WHERE account_id = ?";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, accountId);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					System.out.println("Account ID: " + rs.getInt("account_id"));
					System.out.println("User ID: " + rs.getInt("user_id"));
					System.out.println("Account Name: " + rs.getString("account_name"));
					System.out.println("Initial Amount: " + rs.getDouble("initial_Amount"));
				} else {
					System.out.println("Account not found!");
				}
			}
		}
	}

	// Menu-driven update method
	/*public void updateAccount(int accountId) throws SQLException {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Choose the field you want to update:");
		System.out.println("1. Account Name");
		System.out.println("2. Initial Amount");
		System.out.print("Enter your choice (1/2): ");
		int choice = scanner.nextInt();
		scanner.nextLine(); // Consume newline

		String sql = null;
		if (choice == 1) {
			System.out.print("Enter new account name: ");
			String newAccountName = scanner.nextLine();
			sql = "UPDATE Accounts SET account_name = ? WHERE account_id = ?";

			try (Connection conn = DatabaseConnection.getConnection();
					PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setString(1, newAccountName);
				stmt.setInt(2, accountId);
				int rowsUpdated = stmt.executeUpdate();
				if (rowsUpdated > 0) {
					System.out.println("Account name updated successfully!");
				} else {
					System.out.println("No account found with the given ID.");
				}
			}
		} else if (choice == 2) {
			System.out.print("Enter new initial amount: ");
			double newInitialAmount = scanner.nextDouble();
			sql = "UPDATE Accounts SET initial_Amount = ? WHERE account_id = ?";

			try (Connection conn = DatabaseConnection.getConnection();
					PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setDouble(1, newInitialAmount);
				stmt.setInt(2, accountId);
				int rowsUpdated = stmt.executeUpdate();
				if (rowsUpdated > 0) {
					System.out.println("Initial amount updated successfully!");
				} else {
					System.out.println("No account found with the given ID.");
				}
			}
		} else {
			System.out.println("Invalid choice! Please try again.");
		}
	}
	*/
	
	
	public void updateAccount(int accountId, String accountName, double initialAmount) throws SQLException {
	    String sql = "UPDATE Accounts SET account_name = ?, initial_Amount = ? WHERE account_id = ?";
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, accountName);
	        stmt.setDouble(2, initialAmount);
	        stmt.setInt(3, accountId);
	        int rowsUpdated = stmt.executeUpdate();
	        if (rowsUpdated > 0) {
	            System.out.println("Account updated successfully!");
	        } else {
	            System.out.println("No account found with the given ID.");
	        }
	    }
	}


	// DELETE
	public boolean deleteAccount(int accountId) throws SQLException {
	    String sql = "DELETE FROM Accounts WHERE account_id = ?";
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        stmt.setInt(1, accountId);
	        int rowsDeleted = stmt.executeUpdate();

	        return rowsDeleted > 0;  // Return true if an account was deleted
	    }
	}


	
	
	public List<Account> getAllAccountsByUserId(int userId) throws SQLException {
	    List<Account> accounts = new ArrayList<>();
	    String sql = "SELECT * FROM Accounts WHERE user_id = ?";
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, userId);
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                int accountId = rs.getInt("account_id");
	                String accountName = rs.getString("account_name");
	                double initialAmount = rs.getDouble("initial_Amount");
	                accounts.add(new Account(accountId, userId, accountName, initialAmount));
	            }
	        }
	    }
	    return accounts;
	}
	
	
}
