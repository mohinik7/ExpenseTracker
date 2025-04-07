package dbms_minip;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

public class RecordsDAO {
    public RecordsDAO() {
        // Empty constructor - no need to initialize a connection here
    }

    // Fetch records for a specific user by filtering on category and account user
    // ID
    public ArrayList<Record> getRecordsForUser(int userId) throws SQLException {
        // Validate user ID
        String userIdError = ValidationUtils.validateId(userId, "User");
        if (userIdError != null) {
            throw new IllegalArgumentException(userIdError);
        }

        ArrayList<Record> records = new ArrayList<>();
        String query = "SELECT r.record_id, r.date, r.time, c.category_name, a.account_name, r.amount, r.notes " +
                "FROM records r " +
                "JOIN categories c ON r.category_id = c.category_id " +
                "JOIN accounts a ON r.account_id = a.account_id " +
                "WHERE c.user_id = ? AND a.user_id = ? " +
                "ORDER BY r.date DESC, r.time DESC";

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            statement.setInt(2, userId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int recordId = resultSet.getInt("record_id");
                java.sql.Date date = resultSet.getDate("date");
                Time time = resultSet.getTime("time");
                String categoryName = resultSet.getString("category_name");
                String accountName = resultSet.getString("account_name");
                double amount = resultSet.getDouble("amount");
                String notes = resultSet.getString("notes");

                records.add(new Record(recordId, date, time, categoryName, accountName, amount, notes));
            }
        } finally {
            // Close resources in reverse order
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            DatabaseConnection.closeConnection(connection);
        }
        return records;
    }

    // Retrieve all category names associated with the user for dropdown
    public ArrayList<String> getUserCategories(int userId) throws SQLException {
        // Validate user ID
        String userIdError = ValidationUtils.validateId(userId, "User");
        if (userIdError != null) {
            throw new IllegalArgumentException(userIdError);
        }

        ArrayList<String> categories = new ArrayList<>();
        String query = "SELECT category_name FROM categories WHERE user_id = ? ORDER BY category_name";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    categories.add(resultSet.getString("category_name"));
                }
            }
        }
        return categories;
    }

    // Retrieve all account names associated with the user for dropdown
    public ArrayList<String> getUserAccounts(int userId) throws SQLException {
        // Validate user ID
        String userIdError = ValidationUtils.validateId(userId, "User");
        if (userIdError != null) {
            throw new IllegalArgumentException(userIdError);
        }

        ArrayList<String> accounts = new ArrayList<>();
        String query = "SELECT account_name FROM accounts WHERE user_id = ? ORDER BY account_name";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    accounts.add(resultSet.getString("account_name"));
                }
            }
        }
        return accounts;
    }

    // Get category ID by category name for a specific user
    public int getCategoryIdByName(String categoryName, int userId) throws SQLException {
        // Validate inputs
        String nameError = ValidationUtils.validateRequiredField(categoryName, "Category name");
        if (nameError != null) {
            throw new IllegalArgumentException(nameError);
        }

        String userIdError = ValidationUtils.validateId(userId, "User");
        if (userIdError != null) {
            throw new IllegalArgumentException(userIdError);
        }

        String query = "SELECT category_id FROM categories WHERE category_name = ? AND user_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, categoryName);
            statement.setInt(2, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("category_id");
                } else {
                    throw new SQLException("Category not found for the user.");
                }
            }
        }
    }

    // Get account ID by account name for a specific user
    public int getAccountIdByName(String accountName, int userId) throws SQLException {
        // Validate inputs
        String nameError = ValidationUtils.validateRequiredField(accountName, "Account name");
        if (nameError != null) {
            throw new IllegalArgumentException(nameError);
        }

        String userIdError = ValidationUtils.validateId(userId, "User");
        if (userIdError != null) {
            throw new IllegalArgumentException(userIdError);
        }

        String query = "SELECT account_id FROM accounts WHERE account_name = ? AND user_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, accountName);
            statement.setInt(2, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("account_id");
                } else {
                    throw new SQLException("Account not found for the user.");
                }
            }
        }
    }

    // Method to add a new record with category ID, account ID, amount, and notes
    // Uses a transaction to update account balance
    public void addRecord(int categoryId, int accountId, double amount, String notes) throws SQLException {
        // Validate inputs
        String categoryIdError = ValidationUtils.validateId(categoryId, "Category");
        if (categoryIdError != null) {
            throw new IllegalArgumentException(categoryIdError);
        }

        String accountIdError = ValidationUtils.validateId(accountId, "Account");
        if (accountIdError != null) {
            throw new IllegalArgumentException(accountIdError);
        }

        // Note: We don't validate amount to be positive because expenses could be
        // negative

        // Get the current account balance
        Connection conn = null;
        try {
            conn = DatabaseConnection.getTransactionConnection();

            // Insert the record
            String insertRecordSql = "INSERT INTO records (category_id, account_id, amount, notes, date, time) VALUES (?, ?, ?, ?, CURRENT_DATE, CURRENT_TIME)";
            try (PreparedStatement stmt = conn.prepareStatement(insertRecordSql)) {
                stmt.setInt(1, categoryId);
                stmt.setInt(2, accountId);
                stmt.setDouble(3, amount);
                stmt.setString(4, notes);
                stmt.executeUpdate();
            }

            // Update the account balance
            String updateAccountSql = "UPDATE accounts SET initial_Amount = initial_Amount + ? WHERE account_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateAccountSql)) {
                stmt.setDouble(1, amount);
                stmt.setInt(2, accountId);
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected == 0) {
                    // Account not found, rollback
                    DatabaseConnection.rollbackTransaction(conn);
                    throw new SQLException("Account not found. Record not added.");
                }

                // Commit the transaction
                DatabaseConnection.commitTransaction(conn);
                System.out.println("Record added successfully!");
            }
        } catch (SQLException e) {
            // Rollback the transaction in case of error
            DatabaseConnection.rollbackTransaction(conn);
            System.out.println("Error adding record: " + e.getMessage());
            throw e;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    // Method to update a specific record
    // Uses a transaction to update account balance
    public void updateRecord(int recordId, int categoryId, int accountId, double amount, String notes)
            throws SQLException {
        // Validate inputs
        String recordIdError = ValidationUtils.validateId(recordId, "Record");
        if (recordIdError != null) {
            throw new IllegalArgumentException(recordIdError);
        }

        String categoryIdError = ValidationUtils.validateId(categoryId, "Category");
        if (categoryIdError != null) {
            throw new IllegalArgumentException(categoryIdError);
        }

        String accountIdError = ValidationUtils.validateId(accountId, "Account");
        if (accountIdError != null) {
            throw new IllegalArgumentException(accountIdError);
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getTransactionConnection();

            // First, get the current record details to calculate the difference
            double currentAmount = 0;
            int currentAccountId = 0;

            String getRecordSql = "SELECT amount, account_id FROM records WHERE record_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(getRecordSql)) {
                stmt.setInt(1, recordId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        currentAmount = rs.getDouble("amount");
                        currentAccountId = rs.getInt("account_id");
                    } else {
                        // Record not found
                        throw new SQLException("Record not found.");
                    }
                }
            }

            // Calculate amount difference
            double amountDifference = amount - currentAmount;

            // Update the record
            String updateRecordSql = "UPDATE records SET category_id = ?, account_id = ?, amount = ?, notes = ?, date = CURRENT_DATE, time = CURRENT_TIME WHERE record_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateRecordSql)) {
                stmt.setInt(1, categoryId);
                stmt.setInt(2, accountId);
                stmt.setDouble(3, amount);
                stmt.setString(4, notes);
                stmt.setInt(5, recordId);
                stmt.executeUpdate();
            }

            // If the account changed, we need to update both accounts
            if (accountId != currentAccountId) {
                // Decrease the balance of the old account
                String updateOldAccountSql = "UPDATE accounts SET initial_Amount = initial_Amount - ? WHERE account_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateOldAccountSql)) {
                    stmt.setDouble(1, currentAmount);
                    stmt.setInt(2, currentAccountId);
                    stmt.executeUpdate();
                }

                // Increase the balance of the new account
                String updateNewAccountSql = "UPDATE accounts SET initial_Amount = initial_Amount + ? WHERE account_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateNewAccountSql)) {
                    stmt.setDouble(1, amount);
                    stmt.setInt(2, accountId);
                    stmt.executeUpdate();
                }
            } else {
                // Same account, just update the balance with the difference
                String updateAccountSql = "UPDATE accounts SET initial_Amount = initial_Amount + ? WHERE account_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateAccountSql)) {
                    stmt.setDouble(1, amountDifference);
                    stmt.setInt(2, accountId);
                    stmt.executeUpdate();
                }
            }

            // Commit the transaction
            DatabaseConnection.commitTransaction(conn);
            System.out.println("Record updated successfully!");
        } catch (SQLException e) {
            // Rollback the transaction in case of error
            DatabaseConnection.rollbackTransaction(conn);
            System.out.println("Error updating record: " + e.getMessage());
            throw e;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    // Method to delete a specific record
    // Uses a transaction to update account balance
    public void deleteRecord(int recordId) throws SQLException {
        // Validate record ID
        String recordIdError = ValidationUtils.validateId(recordId, "Record");
        if (recordIdError != null) {
            throw new IllegalArgumentException(recordIdError);
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getTransactionConnection();

            // First, get the current record details to update the account balance
            double currentAmount = 0;
            int accountId = 0;

            String getRecordSql = "SELECT amount, account_id FROM records WHERE record_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(getRecordSql)) {
                stmt.setInt(1, recordId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        currentAmount = rs.getDouble("amount");
                        accountId = rs.getInt("account_id");
                    } else {
                        // Record not found
                        throw new SQLException("Record not found.");
                    }
                }
            }

            // Delete the record
            String deleteRecordSql = "DELETE FROM records WHERE record_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteRecordSql)) {
                stmt.setInt(1, recordId);
                int rowsDeleted = stmt.executeUpdate();

                if (rowsDeleted == 0) {
                    // Record not found (should not happen due to previous check)
                    throw new SQLException("Record not found. No deletion occurred.");
                }
            }

            // Update the account balance (subtract the amount)
            String updateAccountSql = "UPDATE accounts SET initial_Amount = initial_Amount - ? WHERE account_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateAccountSql)) {
                stmt.setDouble(1, currentAmount);
                stmt.setInt(2, accountId);
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected == 0) {
                    // Account not found
                    DatabaseConnection.rollbackTransaction(conn);
                    throw new SQLException("Account not found. Transaction failed.");
                }

                // Commit the transaction
                DatabaseConnection.commitTransaction(conn);
                System.out.println("Record deleted successfully!");
            }
        } catch (SQLException e) {
            // Rollback the transaction in case of error
            DatabaseConnection.rollbackTransaction(conn);
            System.out.println("Error deleting record: " + e.getMessage());
            throw e;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    // Get filtered records by date range
    public ArrayList<Record> getFilteredRecords(int userId, java.sql.Date startDate, java.sql.Date endDate,
            Integer categoryId, Integer accountId) throws SQLException {
        // Validate user ID
        String userIdError = ValidationUtils.validateId(userId, "User");
        if (userIdError != null) {
            throw new IllegalArgumentException(userIdError);
        }

        // Build the query with filters
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT r.record_id, r.date, r.time, c.category_name, a.account_name, r.amount, r.notes ")
                .append("FROM records r ")
                .append("JOIN categories c ON r.category_id = c.category_id ")
                .append("JOIN accounts a ON r.account_id = a.account_id ")
                .append("WHERE c.user_id = ? AND a.user_id = ?");

        // Add date range filter if provided
        if (startDate != null && endDate != null) {
            String dateError = ValidationUtils.validateDateRange(startDate, endDate);
            if (dateError != null) {
                throw new IllegalArgumentException(dateError);
            }
            queryBuilder.append(" AND r.date BETWEEN ? AND ?");
        }

        // Add category filter if provided
        if (categoryId != null) {
            String categoryIdError = ValidationUtils.validateId(categoryId, "Category");
            if (categoryIdError != null) {
                throw new IllegalArgumentException(categoryIdError);
            }
            queryBuilder.append(" AND r.category_id = ?");
        }

        // Add account filter if provided
        if (accountId != null) {
            String accountIdError = ValidationUtils.validateId(accountId, "Account");
            if (accountIdError != null) {
                throw new IllegalArgumentException(accountIdError);
            }
            queryBuilder.append(" AND r.account_id = ?");
        }

        // Add ordering
        queryBuilder.append(" ORDER BY r.date DESC, r.time DESC");

        ArrayList<Record> records = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(queryBuilder.toString());

            int paramIndex = 1;
            statement.setInt(paramIndex++, userId);
            statement.setInt(paramIndex++, userId);

            if (startDate != null && endDate != null) {
                statement.setDate(paramIndex++, startDate);
                statement.setDate(paramIndex++, endDate);
            }

            if (categoryId != null) {
                statement.setInt(paramIndex++, categoryId);
            }

            if (accountId != null) {
                statement.setInt(paramIndex++, accountId);
            }

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int recordId = resultSet.getInt("record_id");
                java.sql.Date date = resultSet.getDate("date");
                Time time = resultSet.getTime("time");
                String categoryName = resultSet.getString("category_name");
                String accountName = resultSet.getString("account_name");
                double amount = resultSet.getDouble("amount");
                String notes = resultSet.getString("notes");

                records.add(new Record(recordId, date, time, categoryName, accountName, amount, notes));
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            DatabaseConnection.closeConnection(connection);
        }

        return records;
    }

    // Get total income for a user
    public double getTotalIncome(int userId) throws SQLException {
        // Validate user ID
        String userIdError = ValidationUtils.validateId(userId, "User");
        if (userIdError != null) {
            throw new IllegalArgumentException(userIdError);
        }

        String query = "SELECT SUM(r.amount) FROM records r " +
                "JOIN accounts a ON r.account_id = a.account_id " +
                "WHERE a.user_id = ? AND r.amount > 0";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    double total = resultSet.getDouble(1);
                    return resultSet.wasNull() ? 0 : total;
                }
                return 0;
            }
        }
    }

    // Get total expenses for a user
    public double getTotalExpenses(int userId) throws SQLException {
        // Validate user ID
        String userIdError = ValidationUtils.validateId(userId, "User");
        if (userIdError != null) {
            throw new IllegalArgumentException(userIdError);
        }

        String query = "SELECT SUM(ABS(r.amount)) FROM records r " +
                "JOIN accounts a ON r.account_id = a.account_id " +
                "WHERE a.user_id = ? AND r.amount < 0";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    double total = resultSet.getDouble(1);
                    return resultSet.wasNull() ? 0 : total;
                }
                return 0;
            }
        }
    }

    // Get spending by category for a user (for reporting)
    public ArrayList<Object[]> getSpendingByCategory(int userId) throws SQLException {
        // Validate user ID
        String userIdError = ValidationUtils.validateId(userId, "User");
        if (userIdError != null) {
            throw new IllegalArgumentException(userIdError);
        }

        ArrayList<Object[]> categorySpending = new ArrayList<>();
        String query = "SELECT c.category_name, SUM(ABS(r.amount)) as total " +
                "FROM records r " +
                "JOIN categories c ON r.category_id = c.category_id " +
                "JOIN accounts a ON r.account_id = a.account_id " +
                "WHERE a.user_id = ? AND r.amount < 0 " +
                "GROUP BY c.category_name " +
                "ORDER BY total DESC";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String categoryName = resultSet.getString("category_name");
                    double total = resultSet.getDouble("total");
                    categorySpending.add(new Object[] { categoryName, total });
                }
            }
        }
        return categorySpending;
    }
}
