package dbms_minip;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton minimizeButton;
    private JLabel statusLabel;

    public LoginFrame() {
        setTitle("Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full-screen mode
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true); // Keeps window undecorated for clean look

        // Main panel setup with dark brown background
        JPanel panel = new JPanel();
        panel.setBackground(new Color(44, 26, 18)); // Dark brown
        panel.setLayout(new GridBagLayout());
        getContentPane().add(panel);

        // Top panel for custom minimize button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setBackground(new Color(44, 26, 18));

        // Minimize button
        minimizeButton = new JButton("-");
        minimizeButton.setPreferredSize(new Dimension(45, 30));
        minimizeButton.setBackground(new Color(88, 56, 39)); // Dark brown button color
        minimizeButton.setForeground(Color.WHITE);
        minimizeButton.setFont(new Font("Arial", Font.BOLD, 16));
        minimizeButton.setFocusPainted(false);
        minimizeButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        minimizeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        minimizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setState(JFrame.ICONIFIED); // Minimize the window
            }
        });

        topPanel.add(minimizeButton);
        getContentPane().add(topPanel, BorderLayout.NORTH); // Adding topPanel to BorderLayout.NORTH

        // Title label setup
        JLabel titleLabel = new JLabel("Login to Your Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        panel.add(titleLabel, gbc);

        // Email label and text field setup
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(emailLabel, gbc);

        emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 16));
        // VERY high contrast - pure white background with pure black text
        emailField.setBackground(Color.WHITE);
        emailField.setForeground(Color.BLACK);
        emailField.setCaretColor(Color.BLACK);
        // Add a visible border
        emailField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(emailField, gbc);

        // Password label and text field setup
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        // VERY high contrast - pure white background with pure black text
        passwordField.setBackground(Color.WHITE);
        passwordField.setForeground(Color.BLACK);
        passwordField.setCaretColor(Color.BLACK);
        // Add a visible border
        passwordField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(passwordField, gbc);

        // Status label for showing login status
        statusLabel = new JLabel("");
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(statusLabel, gbc);

        // Login and Register button setup
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        // Customize buttons for aesthetic look
        loginButton.setBackground(new Color(88, 56, 39));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        registerButton.setBackground(new Color(88, 56, 39));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.setFocusPainted(false);
        registerButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        panel.add(loginButton, gbc);

        gbc.gridy = 5;
        panel.add(registerButton, gbc);

        // Add key listeners to enable login with Enter key
        passwordField.addActionListener(e -> loginButton.doClick());

        UsersDAO usersDAO = new UsersDAO();

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String email = emailField.getText();
                    String password = new String(passwordField.getPassword());

                    // Input validation
                    if (email.isEmpty() || password.isEmpty()) {
                        statusLabel.setText("Please enter both email and password");
                        return;
                    }

                    // Simple email validation
                    if (!email.contains("@") || !email.contains(".")) {
                        statusLabel.setText("Please enter a valid email address");
                        return;
                    }

                    // Set cursor to wait cursor during login attempt
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    loginButton.setEnabled(false);
                    statusLabel.setText("Logging in...");

                    int userId = usersDAO.getUserByEmailAndPassword(email, password);
                    if (userId != -1) {
                        new DashboardFrame(userId).setVisible(true);
                        dispose();
                    } else {
                        statusLabel.setText("Invalid credentials. Please try again.");
                        loginButton.setEnabled(true);
                        setCursor(Cursor.getDefaultCursor());
                    }
                } catch (SQLException ex) {
                    statusLabel.setText("Database error: " + ex.getMessage());
                    loginButton.setEnabled(true);
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RegisterFrame().setVisible(true);
                dispose();
            }
        });
    }

    public static void main(String[] args) {
        try {
            // Try Metal look and feel which has better contrast
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
