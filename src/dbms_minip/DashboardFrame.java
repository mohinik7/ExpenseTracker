package dbms_minip;

/*import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {
    private int userId; // Store the userId for future use in this frame

    public DashboardFrame(int userId) { // Accept userId as a parameter
        this.userId = userId;
        setTitle("User Dashboard");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Or use HIDE_ON_CLOSE if preferred
        setLocationRelativeTo(null);

        // Set layout to GridLayout for better alignment of buttons
        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10)); // 5 rows, 1 column, 10px gaps

        // Create buttons
        JButton accountsButton = new JButton("Manage Accounts");
        JButton categoriesButton = new JButton("Manage Categories");
        JButton budgetsButton = new JButton("Manage Budgets");
        JButton recordsButton = new JButton("View Records");

        // Action listener for "Manage Accounts" button
        accountsButton.addActionListener(e -> {
            AccountFrame accountsFrame = new AccountFrame(userId);
            accountsFrame.setVisible(true); // Show the AccountsFrame
            dispose(); // Close the current DashboardFrame
        });

        // Action listener for "Manage Categories" button
        categoriesButton.addActionListener(e -> {
            CategoryFrame categoriesFrame = new CategoryFrame(userId);
            categoriesFrame.setVisible(true); // Show the CategoriesFrame
            dispose(); // Close the current DashboardFrame
        });

        // Action listener for "Manage Budgets" button
        budgetsButton.addActionListener(e -> {
            BudgetFrame budgetFrame = new BudgetFrame(userId);  // Create BudgetFrame with userId
            budgetFrame.setVisible(true); // Show the BudgetFrame
            dispose(); // Close the current DashboardFrame
        });

        // Action listener for "View Records" button
        recordsButton.addActionListener(e -> {
            RecordFrame recordsFrame = new RecordFrame(userId);  // Create RecordsFrame with userId
            recordsFrame.setVisible(true); // Show the RecordsFrame
            dispose(); // Close the current DashboardFrame
        });

        // Add buttons to the panel
        panel.add(accountsButton);
        panel.add(categoriesButton);
        panel.add(budgetsButton); // Add Manage Budgets button
        panel.add(recordsButton);

        // Optionally add a Back button (if needed)
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            // Implement action to go back to the previous screen (if applicable)
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            dispose(); // Close DashboardFrame
        });
        panel.add(backButton);  // Add it at the end (optional)

        // Add the panel to the frame
        getContentPane().add(panel);
    }
}
*/

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {
    private int userId; // Store the userId for future use in this frame
    private JButton minimizeButton;

    public DashboardFrame(int userId) { // Accept userId as a parameter
        this.userId = userId;
        setTitle("User Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full-screen mode
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true); // Keeps window undecorated for clean look

        // Main panel with dark brown background
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(UIConstants.BACKGROUND_DARK);
        getContentPane().add(mainPanel, BorderLayout.CENTER);

        // Top panel for the minimize button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setBackground(UIConstants.BACKGROUND_DARK);
        topPanel.setPreferredSize(new Dimension(getWidth(), 40)); // Set height for top panel

        // Minimize button
        minimizeButton = new JButton("-");
        minimizeButton.setPreferredSize(new Dimension(45, 30));
        UIConstants.styleButton(minimizeButton);
        minimizeButton.addActionListener(e -> setState(JFrame.ICONIFIED)); // Minimize action
        topPanel.add(minimizeButton); // Add minimize button to top-right corner
        getContentPane().add(topPanel, BorderLayout.NORTH); // Add topPanel to the top of frame

        // Title label setup
        JLabel titleLabel = new JLabel("User Dashboard");
        UIConstants.styleTitleLabel(titleLabel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Panel to hold buttons in a Grid layout
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        buttonPanel.setBackground(UIConstants.BACKGROUND_DARK); // Dark brown to match mainPanel

        // Create and add buttons
        JButton accountsButton = new JButton("Manage Accounts");
        JButton categoriesButton = new JButton("Manage Categories");
        JButton budgetsButton = new JButton("Manage Budgets");
        JButton recordsButton = new JButton("View Records");
        JButton backButton = new JButton("Logout");

        UIConstants.styleButton(accountsButton);
        UIConstants.styleButton(categoriesButton);
        UIConstants.styleButton(budgetsButton);
        UIConstants.styleButton(recordsButton);
        UIConstants.styleButton(backButton);

        buttonPanel.add(accountsButton);
        buttonPanel.add(categoriesButton);
        buttonPanel.add(budgetsButton);
        buttonPanel.add(recordsButton);
        buttonPanel.add(backButton);

        // Set GridBag constraints and add buttonPanel to mainPanel
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(buttonPanel, gbc);

        // Action listeners for each button
        accountsButton.addActionListener(e -> {
            AccountFrame accountsFrame = new AccountFrame(userId);
            accountsFrame.setVisible(true);
            dispose();
        });

        categoriesButton.addActionListener(e -> {
            CategoryFrame categoriesFrame = new CategoryFrame(userId);
            categoriesFrame.setVisible(true);
            dispose();
        });

        budgetsButton.addActionListener(e -> {
            BudgetFrame budgetFrame = new BudgetFrame(userId);
            budgetFrame.setVisible(true);
            dispose();
        });

        recordsButton.addActionListener(e -> {
            RecordFrame recordsFrame = new RecordFrame(userId);
            recordsFrame.setVisible(true);
            dispose();
        });

        backButton.addActionListener(e -> {
            new LoginFrame().setVisible(true); // Redirect to login frame
            dispose(); // Close DashboardFrame
        });
    }

    public static void main(String[] args) {
        try {
            // Set look and feel to system default
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new DashboardFrame(1).setVisible(true));
    }
}
