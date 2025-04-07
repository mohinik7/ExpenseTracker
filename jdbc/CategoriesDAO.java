package dbms_minip;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriesDAO {
    // CREATE
    public void addCategory(int userId, String categoryName) throws SQLException {
        String sql = "INSERT INTO Categories (user_id, category_name) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, categoryName);
            stmt.executeUpdate();
        }
    }

    // READ
    public void getCategoryById(int categoryId) throws SQLException {
        String sql = "SELECT * FROM Categories WHERE category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("Category ID: " + rs.getInt("category_id"));
                System.out.println("User ID: " + rs.getInt("user_id"));
                System.out.println("Category Name: " + rs.getString("category_name"));
            }
        }
    }

    // UPDATE
    public void updateCategoryName(int categoryId, String newCategoryName) throws SQLException {
        String sql = "UPDATE Categories SET category_name = ? WHERE category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newCategoryName);
            stmt.setInt(2, categoryId);
            stmt.executeUpdate();
        }
    }

    // DELETE
    public void deleteCategory(int categoryId) throws SQLException {
        String sql = "DELETE FROM Categories WHERE category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            stmt.executeUpdate();
        }
    }

	public List<Category> getAllCategoriesByUserId(int userId) throws SQLException {
	    String sql = "SELECT * FROM Categories WHERE user_id = ?";
	    List<Category> categories = new ArrayList<>();
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, userId);
	        ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {
	            int categoryId = rs.getInt("category_id");
	            String categoryName = rs.getString("category_name");
	            categories.add(new Category(categoryId, userId, categoryName));
	        }
	    }
	    return categories;
	}
	
	

}
