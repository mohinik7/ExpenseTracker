package dbms_minip;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class RegisterFrame extends JFrame {
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton registerButton;
    private JButton backButton;
    private JButton minimizeButton;

    public RegisterFrame() {
        setTitle("Register");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full-screen mode
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true); // Keeps window undecorated for clean look

        // Main panel setup with dark brown background
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(44, 26, 18)); // Dark brown
        mainPanel.setLayout(new GridBagLayout());
        getContentPane().add(mainPanel, BorderLayout.CENTER);

        // Top panel for the minimize button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setBackground(new Color(44, 26, 18));
        topPanel.setPreferredSize(new Dimension(getWidth(), 40)); // Set height for top panel

        // Minimize button
        minimizeButton = new JButton("-");
        minimizeButton.setPreferredSize(new Dimension(45, 30));
        customizeButton(minimizeButton);
        minimizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setState(JFrame.ICONIFIED); // Minimize the window
            }
        });

        topPanel.add(minimizeButton); // Add minimize button to top-right corner
        getContentPane().add(topPanel, BorderLayout.NORTH); // Add topPanel to the top of frame

        // Title label setup
        JLabel titleLabel = new JLabel("Register New Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Username label and text field setup
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameField.setBackground(new Color(66, 44, 33)); // Darker shade for text field background
        usernameField.setForeground(Color.WHITE);
        usernameField.setCaretColor(Color.WHITE); // White cursor for visibility
        gbc.gridx = 1;
        gbc.gridy = 1;
        mainPanel.add(usernameField, gbc);

        // Email label and text field setup
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(emailLabel, gbc);

        emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 16));
        emailField.setBackground(new Color(66, 44, 33));
        emailField.setForeground(Color.WHITE);
        emailField.setCaretColor(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 2;
        mainPanel.add(emailField, gbc);

        // Password label and text field setup
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setBackground(new Color(66, 44, 33));
        passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 3;
        mainPanel.add(passwordField, gbc);

        // Register and Back buttons setup
        registerButton = new JButton("Register");
        backButton = new JButton("Back to Login");

        // Customize buttons for aesthetic look
        customizeButton(registerButton);
        customizeButton(backButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        mainPanel.add(registerButton, gbc);

        gbc.gridy = 5;
        mainPanel.add(backButton, gbc);

        UsersDAO usersDAO = new UsersDAO();

        // Action listener for the register button
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                try {
                    usersDAO.addUser(username, email, password);
                    JOptionPane.showMessageDialog(null, "Registration successful!");
                    new LoginFrame().setVisible(true); // Redirect to login
                    dispose(); // Close the register frame
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            }
        });

        // Action listener for the back button to go back to login frame
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginFrame().setVisible(true); // Redirect to login
                dispose(); // Close the register frame
            }
        });
    }

    private void customizeButton(JButton button) {
        button.setBackground(new Color(88, 56, 39)); // Dark brown button color
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterFrame().setVisible(true));
    }
}