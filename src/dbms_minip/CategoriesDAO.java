package dbms_minip;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriesDAO {
    // CREATE
    public void addCategory(int userId, String categoryName) throws SQLException {
        // Validate inputs
        String userIdError = ValidationUtils.validateId(userId, "User");
        if (userIdError != null) {
            throw new IllegalArgumentException(userIdError);
        }

        String nameError = ValidationUtils.validateRequiredField(categoryName, "Category name");
        if (nameError != null) {
            throw new IllegalArgumentException(nameError);
        }

        // Check if category name already exists for this user
        if (isCategoryNameExists(userId, categoryName)) {
            throw new IllegalArgumentException("A category with this name already exists for this user");
        }

        String sql = "INSERT INTO Categories (user_id, category_name) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, categoryName);
            stmt.executeUpdate();
            System.out.println("Category added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding category: " + e.getMessage());
            throw e;
        }
    }

    // Check if category name exists for a user
    private boolean isCategoryNameExists(int userId, String categoryName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Categories WHERE user_id = ? AND category_name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, categoryName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // READ
    public void getCategoryById(int categoryId) throws SQLException {
        // Validate input
        String idError = ValidationUtils.validateId(categoryId, "Category");
        if (idError != null) {
            throw new IllegalArgumentException(idError);
        }

        String sql = "SELECT * FROM Categories WHERE category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Category ID: " + rs.getInt("category_id"));
                    System.out.println("User ID: " + rs.getInt("user_id"));
                    System.out.println("Category Name: " + rs.getString("category_name"));
                } else {
                    System.out.println("Category not found!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving category: " + e.getMessage());
            throw e;
        }
    }

    // Get category details
    private Category getCategoryDetails(int categoryId) throws SQLException {
        String sql = "SELECT * FROM Categories WHERE category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("category_id");
                    int userId = rs.getInt("user_id");
                    String name = rs.getString("category_name");
                    return new Category(id, userId, name);
                }
                return null;
            }
        }
    }

    // UPDATE
    public void updateCategoryName(int categoryId, String newCategoryName) throws SQLException {
        // Validate inputs
        String idError = ValidationUtils.validateId(categoryId, "Category");
        if (idError != null) {
            throw new IllegalArgumentException(idError);
        }

        String nameError = ValidationUtils.validateRequiredField(newCategoryName, "Category name");
        if (nameError != null) {
            throw new IllegalArgumentException(nameError);
        }

        // Get current category details for validation
        Category currentCategory = getCategoryDetails(categoryId);
        if (currentCategory == null) {
            throw new IllegalArgumentException("Category not found");
        }

        // Check if new name would conflict with existing categories
        if (!currentCategory.getCategoryName().equals(newCategoryName) &&
                isCategoryNameExists(currentCategory.getUserId(), newCategoryName)) {
            throw new IllegalArgumentException("A category with this name already exists for this user");
        }

        String sql = "UPDATE Categories SET category_name = ? WHERE category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newCategoryName);
            stmt.setInt(2, categoryId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Category updated successfully!");
            } else {
                System.out.println("Category not found!");
            }
        } catch (SQLException e) {
            System.out.println("Error updating category: " + e.getMessage());
            throw e;
        }
    }

    // Check if category is used in records or budgets
    public boolean isCategoryInUse(int categoryId) throws SQLException {
        boolean inUse = false;

        // Check if used in records
        String recordsSql = "SELECT COUNT(*) FROM records WHERE category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(recordsSql)) {
            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                }
            }
        }

        // Check if used in budgets
        String budgetsSql = "SELECT COUNT(*) FROM budgets WHERE category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(budgetsSql)) {
            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                }
            }
        }

        return inUse;
    }

    // DELETE with referential integrity
    public void deleteCategory(int categoryId) throws SQLException {
        // Validate input
        String idError = ValidationUtils.validateId(categoryId, "Category");
        if (idError != null) {
            throw new IllegalArgumentException(idError);
        }

        // Check if the category exists
        Category category = getCategoryDetails(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Category not found");
        }

        // Check if category is in use
        if (isCategoryInUse(categoryId)) {
            throw new IllegalStateException(
                    "Cannot delete category that is in use. Remove related records and budgets first.");
        }

        // Use a transaction to delete the category
        Connection conn = null;
        try {
            conn = DatabaseConnection.getTransactionConnection();
            String sql = "DELETE FROM Categories WHERE category_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, categoryId);
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    DatabaseConnection.commitTransaction(conn);
                    System.out.println("Category deleted successfully!");
                } else {
                    DatabaseConnection.rollbackTransaction(conn);
                    System.out.println("Category not found!");
                }
            }
        } catch (SQLException e) {
            DatabaseConnection.rollbackTransaction(conn);
            System.out.println("Error deleting category: " + e.getMessage());
            throw e;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public List<Category> getAllCategoriesByUserId(int userId) throws SQLException {
        // Validate user ID
        String userIdError = ValidationUtils.validateId(userId, "User");
        if (userIdError != null) {
            throw new IllegalArgumentException(userIdError);
        }

        String sql = "SELECT * FROM Categories WHERE user_id = ? ORDER BY category_name";
        List<Category> categories = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int categoryId = rs.getInt("category_id");
                    String categoryName = rs.getString("category_name");
                    categories.add(new Category(categoryId, userId, categoryName));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving categories: " + e.getMessage());
            throw e;
        }
        return categories;
    }

    // Get category ID by name
    public int getCategoryIdByName(int userId, String categoryName) throws SQLException {
        // Validate inputs
        String userIdError = ValidationUtils.validateId(userId, "User");
        if (userIdError != null) {
            throw new IllegalArgumentException(userIdError);
        }

        String nameError = ValidationUtils.validateRequiredField(categoryName, "Category name");
        if (nameError != null) {
            throw new IllegalArgumentException(nameError);
        }

        String sql = "SELECT category_id FROM Categories WHERE user_id = ? AND category_name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, categoryName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("category_id");
                } else {
                    throw new SQLException("Category not found: " + categoryName);
                }
            }
        }
    }
}
