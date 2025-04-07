package dbms_minip;



import java.sql.*;
import java.util.ArrayList;
import java.util.List;



public class BudgetsDAO {
    
    // CREATE
    public void addBudget(int userId, int categoryId, double budgetAmount, Date startDate, Date endDate) throws SQLException {
        String sql = "INSERT INTO budgets (user_id, category_id, budget_amount, start_date, end_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, categoryId);
            stmt.setDouble(3, budgetAmount);
            stmt.setDate(4, startDate);
            stmt.setDate(5, endDate);
            stmt.executeUpdate();
            System.out.println("Budget added successfully!");
        }
    }

    // READ
    public void getBudget(int userId, int categoryId) throws SQLException {
        String sql = "SELECT * FROM budgets WHERE user_id = ? AND category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, categoryId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("User ID: " + rs.getInt("user_id"));
                System.out.println("Category ID: " + rs.getInt("category_id"));
                System.out.println("Budget Amount: " + rs.getDouble("budget_amount"));
                System.out.println("Start Date: " + rs.getDate("start_date"));
                System.out.println("End Date: " + rs.getDate("end_date"));
                System.out.println("Created At: " + rs.getTimestamp("created_at"));
            } else {
                System.out.println("Budget not found!");
            }
        }
    }

    
    // UPDATE
    public void updateBudgetAmount(int userId, int categoryId, double newBudgetAmount) throws SQLException {
        String sql = "UPDATE budgets SET budget_amount = ? WHERE user_id = ? AND category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, newBudgetAmount);
            stmt.setInt(2, userId);
            stmt.setInt(3, categoryId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Budget amount updated successfully!");
            } else {
                System.out.println("No budget found for the given user and category.");
            }
        }
    }

    // DELETE
    public void deleteBudget(int userId, int categoryId) throws SQLException {
        String sql = "DELETE FROM budgets WHERE user_id = ? AND category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, categoryId);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Budget deleted successfully!");
            } else {
                System.out.println("No budget found for the given user and category.");
            }
        }
    }
    
    public List<Budget> getAllBudgets(int userId) throws SQLException {
        List<Budget> budgets = new ArrayList<>();
        String sql = "SELECT b.category_id, c.category_name, b.budget_amount, b.start_date, b.end_date " +
                     "FROM budgets b " +
                     "JOIN categories c ON b.category_id = c.category_id " +
                     "WHERE b.user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int categoryId = rs.getInt("category_id");
                String categoryName = rs.getString("category_name");
                double budgetAmount = rs.getDouble("budget_amount");
                Date startDate = rs.getDate("start_date");
                Date endDate = rs.getDate("end_date");

                // Create a Budget object and add it to the list
                Budget budget = new Budget(categoryId, categoryName, budgetAmount, startDate, endDate);
                budgets.add(budget);
            }
        }
        return budgets;
    }
    
    
    public List<String> getAllCategoryNames(int userId) throws SQLException {
        List<String> categoryNames = new ArrayList<>();
        String sql = "SELECT category_name FROM categories WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                categoryNames.add(rs.getString("category_name"));
            }
        }
        return categoryNames;
    }
    
    public int getCategoryIdByName(String categoryName) throws SQLException {
        String sql = "SELECT category_id FROM categories WHERE category_name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categoryName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("category_id");
            } else {
                throw new SQLException("Category not found for name: " + categoryName);
            }
        }
    }

    public String getCategoryNameById(int categoryId) throws SQLException {
        String sql = "SELECT category_name FROM categories WHERE category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("category_name");
            } else {
                throw new SQLException("Category not found for ID: " + categoryId);
            }
        }
    }

}
