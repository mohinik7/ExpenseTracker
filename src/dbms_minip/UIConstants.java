package dbms_minip;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import java.awt.Cursor;

/**
 * Utility class to maintain consistent UI colors and styles across the
 * application
 */
public class UIConstants {
    // Background colors
    public static final Color BACKGROUND_DARK = new Color(44, 26, 18); // Dark brown for panels
    public static final Color BACKGROUND_MID = new Color(88, 56, 39); // Medium brown for buttons

    // Text field colors - Using bright white background and solid black text for
    // maximum contrast
    public static final Color FIELD_BACKGROUND = Color.WHITE; // Pure white for input fields
    public static final Color FIELD_TEXT = Color.BLACK; // Pure black for text

    // Font colors
    public static final Color TEXT_LIGHT = Color.WHITE; // White text for dark backgrounds
    public static final Color TEXT_ERROR = new Color(255, 50, 50); // Bright red for error messages
    public static final Color TEXT_SUCCESS = new Color(0, 200, 0); // Bright green for success messages

    // Common fonts
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 32);
    public static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 18);
    public static final Font FIELD_FONT = new Font("Arial", Font.PLAIN, 16);
    public static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 16);
    public static final Font MESSAGE_FONT = new Font("Arial", Font.PLAIN, 14);

    // Common borders
    public static final Border BUTTON_BORDER = BorderFactory.createEmptyBorder(10, 10, 10, 10);

    /**
     * Apply standard styling to text input fields
     */
    public static void styleTextField(JTextField textField) {
        textField.setFont(FIELD_FONT);
        textField.setBackground(FIELD_BACKGROUND);
        textField.setForeground(FIELD_TEXT);
        textField.setCaretColor(Color.BLACK);
        // Add a border for better visibility
        textField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    }

    /**
     * Apply standard styling to buttons
     */
    public static void styleButton(JButton button) {
        button.setBackground(BACKGROUND_MID);
        button.setForeground(TEXT_LIGHT);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Apply standard styling to labels
     */
    public static void styleLabel(JLabel label) {
        label.setForeground(TEXT_LIGHT);
        label.setFont(LABEL_FONT);
    }

    /**
     * Apply standard styling to title labels
     */
    public static void styleTitleLabel(JLabel label) {
        label.setForeground(TEXT_LIGHT);
        label.setFont(TITLE_FONT);
    }

    /**
     * Apply standard styling to message/status labels
     */
    public static void styleMessageLabel(JLabel label) {
        label.setForeground(TEXT_ERROR);
        label.setFont(MESSAGE_FONT);
    }
}