package dbms_minip;

import java.sql.*;
import java.util.*;

public class AccountsDAO {

	// CREATE
	public void addAccount(int userId, String accountName, double initialAmount) throws SQLException {
		// Validate inputs
		String userIdError = ValidationUtils.validateId(userId, "User");
		if (userIdError != null) {
			throw new IllegalArgumentException(userIdError);
		}

		String accountNameError = ValidationUtils.validateRequiredField(accountName, "Account name");
		if (accountNameError != null) {
			throw new IllegalArgumentException(accountNameError);
		}

		String amountError = ValidationUtils.validatePositiveValue(initialAmount, "Initial amount");
		if (amountError != null) {
			throw new IllegalArgumentException(amountError);
		}

		// Check if account name already exists for this user
		if (isAccountNameExists(userId, accountName)) {
			throw new IllegalArgumentException("An account with this name already exists for this user");
		}

		String sql = "INSERT INTO Accounts (user_id, account_name, initial_Amount) VALUES (?, ?, ?)";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, userId);
			stmt.setString(2, accountName);
			stmt.setDouble(3, initialAmount);
			stmt.executeUpdate();
			System.out.println("Account added successfully!");
		} catch (SQLException e) {
			System.out.println("Error adding account: " + e.getMessage());
			throw e;
		}
	}

	// Check if account name exists for a user
	private boolean isAccountNameExists(int userId, String accountName) throws SQLException {
		String sql = "SELECT COUNT(*) FROM Accounts WHERE user_id = ? AND account_name = ?";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, userId);
			stmt.setString(2, accountName);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}
		}
		return false;
	}

	// READ
	public void getAccountById(int accountId) throws SQLException {
		String idError = ValidationUtils.validateId(accountId, "Account");
		if (idError != null) {
			throw new IllegalArgumentException(idError);
		}

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
		} catch (SQLException e) {
			System.out.println("Error retrieving account: " + e.getMessage());
			throw e;
		}
	}

	// UPDATE
	public void updateAccount(int accountId, String accountName, double initialAmount) throws SQLException {
		// Validate inputs
		String idError = ValidationUtils.validateId(accountId, "Account");
		if (idError != null) {
			throw new IllegalArgumentException(idError);
		}

		String accountNameError = ValidationUtils.validateRequiredField(accountName, "Account name");
		if (accountNameError != null) {
			throw new IllegalArgumentException(accountNameError);
		}

		String amountError = ValidationUtils.validatePositiveValue(initialAmount, "Initial amount");
		if (amountError != null) {
			throw new IllegalArgumentException(amountError);
		}

		// Get the current account data for validation
		Account currentAccount = getAccountDetails(accountId);
		if (currentAccount == null) {
			throw new IllegalArgumentException("Account not found");
		}

		// If the account name is changing, make sure it doesn't conflict
		if (!accountName.equals(currentAccount.getAccountName()) &&
				isAccountNameExists(currentAccount.getUserId(), accountName)) {
			throw new IllegalArgumentException("An account with this name already exists for this user");
		}

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
		} catch (SQLException e) {
			System.out.println("Error updating account: " + e.getMessage());
			throw e;
		}
	}

	// Get account details
	private Account getAccountDetails(int accountId) throws SQLException {
		String sql = "SELECT * FROM Accounts WHERE account_id = ?";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, accountId);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int id = rs.getInt("account_id");
					int userId = rs.getInt("user_id");
					String name = rs.getString("account_name");
					double amount = rs.getDouble("initial_Amount");
					return new Account(id, userId, name, amount);
				}
				return null;
			}
		}
	}

	// DELETE - With transaction to maintain referential integrity
	public boolean deleteAccount(int accountId) throws SQLException {
		// Validate input
		String idError = ValidationUtils.validateId(accountId, "Account");
		if (idError != null) {
			throw new IllegalArgumentException(idError);
		}

		// Check if the account exists
		Account account = getAccountDetails(accountId);
		if (account == null) {
			throw new IllegalArgumentException("Account not found");
		}

		// Use a transaction to delete account and related records
		Connection conn = null;
		try {
			conn = DatabaseConnection.getTransactionConnection();

			// First, delete any records using this account
			String deleteRecordsSql = "DELETE FROM records WHERE account_id = ?";
			try (PreparedStatement stmt = conn.prepareStatement(deleteRecordsSql)) {
				stmt.setInt(1, accountId);
				stmt.executeUpdate();
			}

			// Then delete the account
			String deleteAccountSql = "DELETE FROM Accounts WHERE account_id = ?";
			try (PreparedStatement stmt = conn.prepareStatement(deleteAccountSql)) {
				stmt.setInt(1, accountId);
				int rowsDeleted = stmt.executeUpdate();

				if (rowsDeleted > 0) {
					// Commit the transaction
					DatabaseConnection.commitTransaction(conn);
					System.out.println("Account and associated records deleted successfully");
					return true;
				} else {
					// Rollback as account not found (should not happen due to previous check)
					DatabaseConnection.rollbackTransaction(conn);
					return false;
				}
			}
		} catch (SQLException e) {
			// Rollback the transaction in case of error
			DatabaseConnection.rollbackTransaction(conn);
			System.out.println("Error deleting account: " + e.getMessage());
			throw e;
		} finally {
			DatabaseConnection.closeConnection(conn);
		}
	}

	// Get all accounts for a user
	public List<Account> getAllAccountsByUserId(int userId) throws SQLException {
		// Validate user ID
		String userIdError = ValidationUtils.validateId(userId, "User");
		if (userIdError != null) {
			throw new IllegalArgumentException(userIdError);
		}

		List<Account> accounts = new ArrayList<>();
		String sql = "SELECT * FROM Accounts WHERE user_id = ? ORDER BY account_name";
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
		} catch (SQLException e) {
			System.out.println("Error retrieving accounts: " + e.getMessage());
			throw e;
		}
		return accounts;
	}

	// Get total balance across all accounts for a user
	public double getTotalBalance(int userId) throws SQLException {
		// Validate user ID
		String userIdError = ValidationUtils.validateId(userId, "User");
		if (userIdError != null) {
			throw new IllegalArgumentException(userIdError);
		}

		String sql = "SELECT SUM(initial_Amount) FROM Accounts WHERE user_id = ?";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, userId);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getDouble(1);
				}
				return 0.0;
			}
		} catch (SQLException e) {
			System.out.println("Error calculating total balance: " + e.getMessage());
			throw e;
		}
	}
}
