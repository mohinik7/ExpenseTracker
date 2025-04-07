package dbms_minip;

import java.sql.*;
import java.util.ArrayList;

public class RecordsDAO {
    private Connection connection;

    public RecordsDAO() {
        try {
            // Replace with your own database connection details
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbms_minip?useSSL=false", "root", "root");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Fetch records for a specific user by filtering on category and account user ID
    public ArrayList<Record> getRecordsForUser(int userId) throws SQLException {
        ArrayList<Record> records = new ArrayList<>();
        String query = "SELECT r.record_id, r.date, r.time, c.category_name, a.account_name, r.amount, r.notes " +
                       "FROM records r " +
                       "JOIN categories c ON r.category_id = c.category_id " +
                       "JOIN accounts a ON r.account_id = a.account_id " +
                       "WHERE c.user_id = ? AND a.user_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, userId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                int recordId = resultSet.getInt("record_id");
                Date date = resultSet.getDate("date");
                Time time = resultSet.getTime("time");
                String categoryName = resultSet.getString("category_name");
                String accountName = resultSet.getString("account_name");
                double amount = resultSet.getDouble("amount");
                String notes = resultSet.getString("notes");
                
                records.add(new Record(recordId, date, time, categoryName, accountName, amount, notes));
            }
        }
        return records;
    }

    // Retrieve all category names associated with the user for dropdown
    public ArrayList<String> getUserCategories(int userId) throws SQLException {
        ArrayList<String> categories = new ArrayList<>();
        String query = "SELECT category_name FROM categories WHERE user_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                categories.add(resultSet.getString("category_name"));
            }
        }
        return categories;
    }

    // Retrieve all account names associated with the user for dropdown
    public ArrayList<String> getUserAccounts(int userId) throws SQLException {
        ArrayList<String> accounts = new ArrayList<>();
        String query = "SELECT account_name FROM accounts WHERE user_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                accounts.add(resultSet.getString("account_name"));
            }
        }
        return accounts;
    }

    // Get category ID by category name for a specific user
    public int getCategoryIdByName(String categoryName, int userId) throws SQLException {
        String query = "SELECT category_id FROM categories WHERE category_name = ? AND user_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, categoryName);
            statement.setInt(2, userId);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt("category_id");
            } else {
                throw new SQLException("Category not found for the user.");
            }
        }
    }

    // Get account ID by account name for a specific user
    public int getAccountIdByName(String accountName, int userId) throws SQLException {
        String query = "SELECT account_id FROM accounts WHERE account_name = ? AND user_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, accountName);
            statement.setInt(2, userId);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt("account_id");
            } else {
                throw new SQLException("Account not found for the user.");
            }
        }
    }

    // Method to add a new record with category ID, account ID, amount, and notes
    public void addRecord(int categoryId, int accountId, double amount, String notes) throws SQLException {
        String query = "INSERT INTO records (category_id, account_id, amount, notes) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, categoryId);
            statement.setInt(2, accountId);
            statement.setDouble(3, amount);
            statement.setString(4, notes);
            statement.executeUpdate();
        }
    }

    // Method to update a specific record
    public void updateRecord(int recordId, int categoryId, int accountId, double amount, String notes) throws SQLException {
        String query = "UPDATE records SET category_id = ?, account_id = ?, amount = ?, notes = ? WHERE record_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, categoryId);
            statement.setInt(2, accountId);
            statement.setDouble(3, amount);
            statement.setString(4, notes);
            statement.setInt(5, recordId);
            statement.executeUpdate();
        }
    }

    // Method to delete a specific record
    public void deleteRecord(int recordId) throws SQLException {
        String query = "DELETE FROM records WHERE record_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, recordId);
            statement.executeUpdate();
        }
    }
}










//import java.sql.*;
//import java.sql.Date;
//import java.util.*;
//
//
//public class RecordsDAO {
//    // CREATE
//    public void addRecord(int categoryId, int accountId, double amount, String notes) throws SQLException {
//        String sql = "INSERT INTO Records (category_id, account_id, Amount, Notes) VALUES ( ?, ?, ?, ?)";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1, categoryId);
//            stmt.setInt(2, accountId);
//            stmt.setDouble(3, amount);
//            stmt.setString(4, notes);
//            stmt.executeUpdate();
//        }
//    }
//
//    // READ
//    public void getRecordById(int recordId) throws SQLException {
//        String sql = "SELECT * FROM Records WHERE record_id = ?";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1, recordId);
//            ResultSet rs = stmt.executeQuery();
//            while (rs.next()) {
//                System.out.println("Record ID: " + rs.getInt("record_id"));
//                System.out.println("Date: " + rs.getDate("Date"));
//                System.out.println("Time: " + rs.getTime("Time"));
//                System.out.println("Category ID: " + rs.getInt("category_id"));
//                System.out.println("Account ID: " + rs.getInt("account_id"));
//                System.out.println("Amount: " + rs.getDouble("Amount"));
//                System.out.println("Notes: " + rs.getString("Notes"));
//            }
//        }
//    }
//
//    // UPDATE
//    public void updateRecord(int recordId) throws SQLException {
//        Scanner scanner = new Scanner(System.in);
//        
//        while (true) {
//            System.out.println("\nChoose the field you want to update:");
//            System.out.println("1. Date");
//            System.out.println("2. Time");
//            System.out.println("3. Category ID");
//            System.out.println("4. Account ID");
//            System.out.println("5. Amount");
//            System.out.println("6. Notes");
//            System.out.println("7. Exit");
//
//            int choice = scanner.nextInt();
//            scanner.nextLine(); // Consume newline character
//
//            String sql = "";
//            switch (choice) {
//                case 1:
//                    System.out.print("Enter new date (yyyy-mm-dd): ");
//                    String newDate = scanner.nextLine();
//                    sql = "UPDATE Records SET date = ? WHERE record_id = ?";
//                    try (Connection conn = DatabaseConnection.getConnection();
//                         PreparedStatement stmt = conn.prepareStatement(sql)) {
//                        stmt.setDate(1, Date.valueOf(newDate));
//                        stmt.setInt(2, recordId);
//                        executeUpdate(stmt);
//                    }
//                    break;
//
//                case 2:
//                    System.out.print("Enter new time (HH:mm:ss): ");
//                    String newTime = scanner.nextLine();
//                    sql = "UPDATE Records SET time = ? WHERE record_id = ?";
//                    try (Connection conn = DatabaseConnection.getConnection();
//                         PreparedStatement stmt = conn.prepareStatement(sql)) {
//                        stmt.setTime(1, Time.valueOf(newTime));
//                        stmt.setInt(2, recordId);
//                        executeUpdate(stmt);
//                    }
//                    break;
//
//                case 3:
//                    System.out.print("Enter new Category ID: ");
//                    int newCategoryId = scanner.nextInt();
//                    sql = "UPDATE Records SET category_id = ? WHERE record_id = ?";
//                    try (Connection conn = DatabaseConnection.getConnection();
//                         PreparedStatement stmt = conn.prepareStatement(sql)) {
//                        stmt.setInt(1, newCategoryId);
//                        stmt.setInt(2, recordId);
//                        executeUpdate(stmt);
//                    }
//                    break;
//
//                case 4:
//                    System.out.print("Enter new Account ID: ");
//                    int newAccountId = scanner.nextInt();
//                    sql = "UPDATE Records SET account_id = ? WHERE record_id = ?";
//                    try (Connection conn = DatabaseConnection.getConnection();
//                         PreparedStatement stmt = conn.prepareStatement(sql)) {
//                        stmt.setInt(1, newAccountId);
//                        stmt.setInt(2, recordId);
//                        executeUpdate(stmt);
//                    }
//                    break;
//
//                case 5:
//                    System.out.print("Enter new Amount: ");
//                    double newAmount = scanner.nextDouble();
//                    sql = "UPDATE Records SET amount = ? WHERE record_id = ?";
//                    try (Connection conn = DatabaseConnection.getConnection();
//                         PreparedStatement stmt = conn.prepareStatement(sql)) {
//                        stmt.setDouble(1, newAmount);
//                        stmt.setInt(2, recordId);
//                        executeUpdate(stmt);
//                    }
//                    break;
//
//                case 6:
//                    System.out.print("Enter new Notes: ");
//                    String newNotes = scanner.nextLine();
//                    sql = "UPDATE Records SET notes = ? WHERE record_id = ?";
//                    try (Connection conn = DatabaseConnection.getConnection();
//                         PreparedStatement stmt = conn.prepareStatement(sql)) {
//                        stmt.setString(1, newNotes);
//                        stmt.setInt(2, recordId);
//                        executeUpdate(stmt);
//                    }
//                    break;
//
//                case 7:
//                    System.out.println("Exiting update menu.");
//                    return;
//
//                default:
//                    System.out.println("Invalid choice. Please select a valid option.");
//                    continue;
//            }
//        }
//    }
//
//    // Method to execute update and print status
//    private void executeUpdate(PreparedStatement stmt) throws SQLException {
//        int rowsUpdated = stmt.executeUpdate();
//        if (rowsUpdated > 0) {
//            System.out.println("Record updated successfully.");
//        } else {
//            System.out.println("No record found with the given ID.");
//        }
//    }
//
//
//    // DELETE
//    public void deleteRecord(int recordId) throws SQLException {
//        String sql = "DELETE FROM Records WHERE record_id = ?";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1, recordId);
//            stmt.executeUpdate();
//        }
//    }
//    
//    public String getCategoryNameById(int categoryId) {
//        String categoryName = "";
//        String sql = "SELECT name FROM Category WHERE category_id = ?";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setInt(1, categoryId);
//            ResultSet rs = pstmt.executeQuery();
//
//            if (rs.next()) {
//                categoryName = rs.getString("name");
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return categoryName;
//    }
//    
//    public String getAccountNameById(int accountId) {
//        String accountName = "";
//        String sql = "SELECT account_name FROM Accounts WHERE account_id = ?";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setInt(1, accountId);
//            ResultSet rs = pstmt.executeQuery();
//
//            if (rs.next()) {
//                accountName = rs.getString("account_name");
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return accountName;
//    }
//    
//    
//    
//    
//}
