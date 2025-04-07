package dbms_minip;

import java.sql.*;
import java.util.Scanner;

public class UsersDAO {

	public int getUserByEmailAndPassword(String email, String password) throws SQLException {
		String sql = "SELECT user_id FROM users WHERE email = ? AND password = ?";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, email);
			stmt.setString(2, password);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int userId = rs.getInt("user_id");
					System.out.println("User found with user ID: " + userId);
					return userId; // Return userId if successful
				} else {
					System.out.println("Invalid email or password.");
					return -1; // Return -1 for invalid credentials
				}
			}
		}
	}

	// Add a user to the database
	public void addUser(String username, String email, String password) throws SQLException {
		String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, username);
			stmt.setString(2, email);
			stmt.setString(3, password);
			stmt.executeUpdate();
			System.out.println("User added successfully!");
		}
	}

	// Get user by ID
	public void getUserById(int userId) throws SQLException {
		String sql = "SELECT * FROM users WHERE user_id = ?";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, userId);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					System.out.println("User ID: " + rs.getInt("user_id"));
					System.out.println("Username: " + rs.getString("username"));
					System.out.println("Email: " + rs.getString("email"));
				} else {
					System.out.println("User not found!");
				}
			}
		}
	}

	// Menu-driven update method
	public void updateUser(int userId) throws SQLException {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Choose the field you want to update:");
		System.out.println("1. Username");
		System.out.println("2. Email");
		System.out.println("3. Password");
		System.out.print("Enter your choice (1/2/3): ");
		int choice = scanner.nextInt();
		scanner.nextLine(); // Consume newline

		String sql = null;
		String newValue = null;

		switch (choice) {
		case 1:
			System.out.print("Enter new username: ");
			newValue = scanner.nextLine();
			sql = "UPDATE users SET username = ? WHERE user_id = ?";
			break;
		case 2:
			System.out.print("Enter new email: ");
			newValue = scanner.nextLine();
			sql = "UPDATE users SET email = ? WHERE user_id = ?";
			break;
		case 3:
			System.out.print("Enter new password: ");
			newValue = scanner.nextLine();
			sql = "UPDATE users SET password = ? WHERE user_id = ?";
			break;
		default:
			System.out.println("Invalid choice! Please try again.");
			return;
		}

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, newValue);
			stmt.setInt(2, userId);
			int rowsUpdated = stmt.executeUpdate();
			if (rowsUpdated > 0) {
				System.out.println("User updated successfully!");
			} else {
				System.out.println("No user found with the given ID.");
			}
		}
	}

	// Delete user
	public void deleteUser(int userId) throws SQLException {
		String sql = "DELETE FROM users WHERE user_id = ?";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, userId);
			int rowsDeleted = stmt.executeUpdate();
			if (rowsDeleted > 0) {
				System.out.println("User deleted successfully!");
			} else {
				System.out.println("No user found with the given ID.");
			}
		}
	}
}
