package dbms_minip;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetsDAO {

    // CREATE
    public void addBudget(int userId, int categoryId, double budgetAmount, Date startDate, Date endDate)
            throws SQLException {
        // Validate inputs
        String userIdError = ValidationUtils.validateId(userId, "User");
        if (userIdError != null) {
            throw new IllegalArgumentException(userIdError);
        }

        String categoryIdError = ValidationUtils.validateId(categoryId, "Category");
        if (categoryIdError != null) {
            throw new IllegalArgumentException(categoryIdError);
        }

        String amountError = ValidationUtils.validatePositiveValue(budgetAmount, "Budget amount");
        if (amountError != null) {
            throw new IllegalArgumentException(amountError);
        }

        String dateError = ValidationUtils.validateDateRange(startDate, endDate);
        if (dateError != null) {
            throw new IllegalArgumentException(dateError);
        }

        // Check if the category belongs to the user
        if (!isCategoryBelongsToUser(userId, categoryId)) {
            throw new IllegalArgumentException("The category does not belong to this user");
        }

        // Check for overlapping budget periods
        if (hasOverlappingBudget(userId, categoryId, startDate, endDate)) {
            throw new IllegalArgumentException("There is already a budget for this category during this time period");
        }

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
        } catch (SQLException e) {
            System.out.println("Error adding budget: " + e.getMessage());
            throw e;
        }
    }

    // Check if category belongs to user
    private boolean isCategoryBelongsToUser(int userId, int categoryId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM categories WHERE category_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Check for overlapping budget periods
    private boolean hasOverlappingBudget(int userId, int categoryId, Date startDate, Date endDate) throws SQLException {
        String sql = "SELECT COUNT(*) FROM budgets WHERE user_id = ? AND category_id = ? " +
                "AND ((start_date <= ? AND end_date >= ?) OR " + // New budget starts during existing budget
                "(start_date <= ? AND end_date >= ?) OR " + // New budget ends during existing budget
                "(start_date >= ? AND end_date <= ?))"; // New budget is entirely within existing budget
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, categoryId);
            stmt.setDate(3, endDate); // End date of new budget is after start date of existing
            stmt.setDate(4, startDate); // Start date of new budget is before end date of existing
            stmt.setDate(5, startDate); // Start date of new budget is before end date of existing
            stmt.setDate(6, endDate); // End date of new budget is after start date of existing
            stmt.setDate(7, startDate); // New budget starts after existing starts
            stmt.setDate(8, endDate); // New budget ends before existing ends
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // READ
    public Budget getBudget(int userId, int categoryId) throws SQLException {
        // Validate inputs
        String userIdError = ValidationUtils.validateId(userId, "User");
        if (userIdError != null) {
            throw new IllegalArgumentException(userIdError);
        }

        String categoryIdError = ValidationUtils.validateId(categoryId, "Category");
        if (categoryIdError != null) {
            throw new IllegalArgumentException(categoryIdError);
        }

        String sql = "SELECT b.*, c.category_name FROM budgets b JOIN categories c ON b.category_id = c.category_id " +
                "WHERE b.user_id = ? AND b.category_id = ? " +
                "AND CURRENT_DATE BETWEEN b.start_date AND b.end_date"; // Get current active budget

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, categoryId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int budgetId = rs.getInt("user_id"); // Using user_id as composite key with category_id
                    String categoryName = rs.getString("category_name");
                    double budgetAmount = rs.getDouble("budget_amount");
                    Date startDate = rs.getDate("start_date");
                    Date endDate = rs.getDate("end_date");

                    System.out.println("User ID: " + rs.getInt("user_id"));
                    System.out.println("Category ID: " + rs.getInt("category_id"));
                    System.out.println("Category Name: " + categoryName);
                    System.out.println("Budget Amount: " + budgetAmount);
                    System.out.println("Start Date: " + startDate);
                    System.out.println("End Date: " + endDate);
                    System.out.println("Created At: " + rs.getTimestamp("created_at"));

                    return new Budget(categoryId, categoryName, budgetAmount, startDate, endDate);
                } else {
                    System.out.println("No active budget found for this category!");
                    return null;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving budget: " + e.getMessage());
            throw e;
        }
    }

    // UPDATE
    public void updateBudgetAmount(int userId, int categoryId, double newBudgetAmount) throws SQLException {
        // Validate inputs
        String userIdError = ValidationUtils.validateId(userId, "User");
        if (userIdError != null) {
            throw new IllegalArgumentException(userIdError);
        }

        String categoryIdError = ValidationUtils.validateId(categoryId, "Category");
        if (categoryIdError != null) {
            throw new IllegalArgumentException(categoryIdError);
        }

        String amountError = ValidationUtils.validatePositiveValue(newBudgetAmount, "Budget amount");
        if (amountError != null) {
            throw new IllegalArgumentException(amountError);
        }

        // Update only the current active budget
        String sql = "UPDATE budgets SET budget_amount = ? " +
                "WHERE user_id = ? AND category_id = ? " +
                "AND CURRENT_DATE BETWEEN start_date AND end_date"; // Only update active budget

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, newBudgetAmount);
            stmt.setInt(2, userId);
            stmt.setInt(3, categoryId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Budget amount updated successfully!");
            } else {
                System.out.println("No active budget found for the given user and category.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating budget: " + e.getMessage());
            throw e;
        }
    }

    // Update budget date range
    public void updateBudgetDateRange(int userId, int categoryId, Date newStartDate, Date newEndDate)
            throws SQLException {
        // Validate inputs
        String userIdError = ValidationUtils.validateId(userId, "User");
        if (userIdError != null) {
            throw new IllegalArgumentException(userIdError);
        }

        String categoryIdError = ValidationUtils.validateId(categoryId, "Category");
        if (categoryIdError != null) {
            throw new IllegalArgumentException(categoryIdError);
        }

        String dateError = ValidationUtils.validateDateRange(newStartDate, newEndDate);
        if (dateError != null) {
            throw new IllegalArgumentException(dateError);
        }

        // Check if the new date range overlaps with other budgets (except the current
        // one)
        if (hasOverlappingBudgetForUpdate(userId, categoryId, newStartDate, newEndDate)) {
            throw new IllegalArgumentException("The new date range overlaps with another budget for this category");
        }

        // Update the current active budget
        String sql = "UPDATE budgets SET start_date = ?, end_date = ? " +
                "WHERE user_id = ? AND category_id = ? " +
                "AND CURRENT_DATE BETWEEN start_date AND end_date"; // Only update active budget

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, newStartDate);
            stmt.setDate(2, newEndDate);
            stmt.setInt(3, userId);
            stmt.setInt(4, categoryId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Budget date range updated successfully!");
            } else {
                System.out.println("No active budget found for the given user and category.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating budget date range: " + e.getMessage());
            throw e;
        }
    }

    // Check for overlapping budgets for update (excluding the current active
    // budget)
    private boolean hasOverlappingBudgetForUpdate(int userId, int categoryId, Date startDate, Date endDate)
            throws SQLException {
        String sql = "SELECT COUNT(*) FROM budgets WHERE user_id = ? AND category_id = ? " +
                "AND NOT (CURRENT_DATE BETWEEN start_date AND end_date) " + // Exclude current active budget
                "AND ((start_date <= ? AND end_date >= ?) OR " +
                "(start_date <= ? AND end_date >= ?) OR " +
                "(start_date >= ? AND end_date <= ?))";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, categoryId);
            stmt.setDate(3, endDate);
            stmt.setDate(4, startDate);
            stmt.setDate(5, startDate);
            stmt.setDate(6, endDate);
            stmt.setDate(7, startDate);
            stmt.setDate(8, endDate);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // DELETE
    public void deleteBudget(int userId, int categoryId) throws SQLException {
        // Validate inputs
        String userIdError = ValidationUtils.validateId(userId, "User");
        if (userIdError != null) {
            throw new IllegalArgumentException(userIdError);
        }

        String categoryIdError = ValidationUtils.validateId(categoryId, "Category");
        if (categoryIdError != null) {
            throw new IllegalArgumentException(categoryIdError);
        }

        // Delete only the active budget
        String sql = "DELETE FROM budgets WHERE user_id = ? AND category_id = ? " +
                "AND CURRENT_DATE BETWEEN start_date AND end_date"; // Only delete active budget

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, categoryId);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Budget deleted successfully!");
            } else {
                System.out.println("No active budget found for the given user and category.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting budget: " + e.getMessage());
            throw e;
        }
    }

    // Get all active budgets for a user
    public List<Budget> getAllActiveBudgets(int userId) throws SQLException {
        // Validate user ID
        String userIdError = ValidationUtils.validateId(userId, "User");
        if (userIdError != null) {
            throw new IllegalArgumentException(userIdError);
        }

        List<Budget> budgets = new ArrayList<>();
        String sql = "SELECT b.category_id, c.category_name, b.budget_amount, b.start_date, b.end_date " +
                "FROM budgets b " +
                "JOIN categories c ON b.category_id = c.category_id " +
                "WHERE b.user_id = ? AND CURRENT_DATE BETWEEN b.start_date AND b.end_date " +
                "ORDER BY c.category_name";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
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
        } catch (SQLException e) {
            System.out.println("Error retrieving budgets: " + e.getMessage());
            throw e;
        }
        return budgets;
    }

    // Get all budgets (past, current, and future) for a user
    public List<Budget> getAllBudgets(int userId) throws SQLException {
        // Validate user ID
        String userIdError = ValidationUtils.validateId(userId, "User");
        if (userIdError != null) {
            throw new IllegalArgumentException(userIdError);
        }

        List<Budget> budgets = new ArrayList<>();
        String sql = "SELECT b.category_id, c.category_name, b.budget_amount, b.start_date, b.end_date " +
                "FROM budgets b " +
                "JOIN categories c ON b.category_id = c.category_id " +
                "WHERE b.user_id = ? " +
                "ORDER BY b.start_date DESC, c.category_name";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
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
        } catch (SQLException e) {
            System.out.println("Error retrieving budgets: " + e.getMessage());
            throw e;
        }
        return budgets;
    }

    // Get all category names for a user
    public List<String> getAllCategoryNames(int userId) throws SQLException {
        // Validate user ID
        String userIdError = ValidationUtils.validateId(userId, "User");
        if (userIdError != null) {
            throw new IllegalArgumentException(userIdError);
        }

        List<String> categoryNames = new ArrayList<>();
        String sql = "SELECT category_name FROM categories WHERE user_id = ? ORDER BY category_name";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categoryNames.add(rs.getString("category_name"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving category names: " + e.getMessage());
            throw e;
        }
        return categoryNames;
    }

    // Get category ID by name
    public int getCategoryIdByName(String categoryName) throws SQLException {
        // Validate input
        String nameError = ValidationUtils.validateRequiredField(categoryName, "Category name");
        if (nameError != null) {
            throw new IllegalArgumentException(nameError);
        }

        String sql = "SELECT category_id FROM categories WHERE category_name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categoryName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("category_id");
                } else {
                    throw new SQLException("Category not found for name: " + categoryName);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving category ID: " + e.getMessage());
            throw e;
        }
    }

    // Get category name by ID
    public String getCategoryNameById(int categoryId) throws SQLException {
        // Validate input
        String idError = ValidationUtils.validateId(categoryId, "Category");
        if (idError != null) {
            throw new IllegalArgumentException(idError);
        }

        String sql = "SELECT category_name FROM categories WHERE category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("category_name");
                } else {
                    throw new SQLException("Category not found for ID: " + categoryId);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving category name: " + e.getMessage());
            throw e;
        }
    }

    // Calculate current spending for a budget
    public double getCurrentSpending(int userId, int categoryId, Date startDate, Date endDate) throws SQLException {
        // Validate inputs
        String userIdError = ValidationUtils.validateId(userId, "User");
        if (userIdError != null) {
            throw new IllegalArgumentException(userIdError);
        }

        String categoryIdError = ValidationUtils.validateId(categoryId, "Category");
        if (categoryIdError != null) {
            throw new IllegalArgumentException(categoryIdError);
        }

        String sql = "SELECT SUM(amount) FROM records " +
                "WHERE category_id = ? AND account_id IN (SELECT account_id FROM accounts WHERE user_id = ?) " +
                "AND date BETWEEN ? AND ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            stmt.setInt(2, userId);
            stmt.setDate(3, startDate);
            stmt.setDate(4, endDate);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double amount = rs.getDouble(1);
                    return (rs.wasNull()) ? 0.0 : amount;
                }
                return 0.0;
            }
        } catch (SQLException e) {
            System.out.println("Error calculating current spending: " + e.getMessage());
            throw e;
        }
    }

    // Get budget utilization percentage
    public double getBudgetUtilizationPercentage(int userId, int categoryId) throws SQLException {
        Budget budget = getBudget(userId, categoryId);
        if (budget == null) {
            return 0.0;
        }

        double currentSpending = getCurrentSpending(userId, categoryId, budget.getStartDate(), budget.getEndDate());
        if (budget.getBudgetAmount() == 0) {
            return 0.0;
        }

        return (currentSpending / budget.getBudgetAmount()) * 100;
    }
}
