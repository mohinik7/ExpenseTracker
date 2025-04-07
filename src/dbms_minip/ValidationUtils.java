package dbms_minip;

import java.sql.Date;
import java.util.regex.Pattern;

/**
 * Utility class for validating inputs throughout the application
 */
public class ValidationUtils {
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    /**
     * Validate that a string is not null or empty
     * 
     * @param value     The string to validate
     * @param fieldName The name of the field (for error message)
     * @return Error message if invalid, null if valid
     */
    public static String validateRequiredField(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return fieldName + " is required";
        }
        return null;
    }

    /**
     * Validate that a numeric value is positive
     * 
     * @param value     The value to validate
     * @param fieldName The name of the field (for error message)
     * @return Error message if invalid, null if valid
     */
    public static String validatePositiveValue(double value, String fieldName) {
        if (value <= 0) {
            return fieldName + " must be a positive value";
        }
        return null;
    }

    /**
     * Validate that an email address is in a valid format
     * 
     * @param email The email address to validate
     * @return Error message if invalid, null if valid
     */
    public static String validateEmail(String email) {
        String requiredCheck = validateRequiredField(email, "Email");
        if (requiredCheck != null) {
            return requiredCheck;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return "Please enter a valid email address";
        }
        return null;
    }

    /**
     * Validate that a password meets minimum security requirements
     * 
     * @param password The password to validate
     * @return Error message if invalid, null if valid
     */
    public static String validatePassword(String password) {
        String requiredCheck = validateRequiredField(password, "Password");
        if (requiredCheck != null) {
            return requiredCheck;
        }

        if (password.length() < 6) {
            return "Password must be at least 6 characters long";
        }
        return null;
    }

    /**
     * Validate that an end date is after a start date
     * 
     * @param startDate The start date
     * @param endDate   The end date
     * @return Error message if invalid, null if valid
     */
    public static String validateDateRange(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return "Both start date and end date are required";
        }

        if (endDate.before(startDate)) {
            return "End date must be after start date";
        }
        return null;
    }

    /**
     * Validate that the ID is valid (positive)
     * 
     * @param id         The ID to validate
     * @param entityName The name of the entity (for error message)
     * @return Error message if invalid, null if valid
     */
    public static String validateId(int id, String entityName) {
        if (id <= 0) {
            return "Invalid " + entityName + " ID";
        }
        return null;
    }
}