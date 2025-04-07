package dbms_minip;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/dbms_minip?useSSL=false";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    /**
     * Get a new database connection
     * 
     * @return A connection to the database
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        // Default behavior is auto-commit
        conn.setAutoCommit(true);
        return conn;
    }

    /**
     * Get a new database connection with transaction support (autocommit off)
     * 
     * @return A connection with autoCommit set to false
     * @throws SQLException if a database access error occurs
     */
    public static Connection getTransactionConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        // Set autoCommit to false for transaction support
        conn.setAutoCommit(false);
        return conn;
    }

    /**
     * Safely commit a transaction
     * 
     * @param connection The connection to commit
     */
    public static void commitTransaction(Connection connection) {
        if (connection != null) {
            try {
                connection.commit();
            } catch (SQLException e) {
                System.out.println("Error committing transaction: " + e.getMessage());
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    System.out.println("Error rolling back transaction: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Safely rollback a transaction
     * 
     * @param connection The connection to rollback
     */
    public static void rollbackTransaction(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                System.out.println("Error rolling back transaction: " + e.getMessage());
            }
        }
    }

    /**
     * Safely close a connection
     * 
     * @param connection The connection to close
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
