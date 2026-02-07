package com.BankingApp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple bank management application for employees to perform CRUD operations on customer accounts.
 * This application uses Java Swing for the UI and MySQL for the database.
 *
 * --- HOW TO COMPILE AND RUN ---
 * 1.  Make sure you have JDK and MySQL Server installed.
 * 2.  Download the MySQL JDBC Driver (Connector/J) JAR file.
 * 3.  Create the database and table using the provided 'database_setup.sql' script.
 * 4.  Update the DB_URL, DB_USER, and DB_PASSWORD constants in this file with your MySQL credentials.
 *
 * 5.  Compile the code from your terminal:
 * javac -cp ".;path/to/mysql-connector-j-X.X.XX.jar" BankApp.java
 * (Replace 'path/to/mysql-connector-j-X.X.XX.jar' with the actual path to your driver)
 *
 * 6.  Run the application from your terminal:
 * java -cp ".;path/to/mysql-connector-j-X.X.XX.jar" BankApp
 */
public class BankApp extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// --- DATABASE CONNECTION DETAILS ---
    // !!! IMPORTANT: UPDATE THESE VALUES TO MATCH YOUR MYSQL SETUP !!!
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bank_db";
    private static final String DB_USER = "root"; // Your MySQL username
    private static final String DB_PASSWORD = "meoclavezz"; // Your MySQL password

    // --- UI COMPONENTS ---
    private JTextField accNumberField, nameField, dobField, phoneField, balanceField;
    private JTextArea transactionHistoryArea;
    private JButton insertButton, fetchButton, updateButton, deleteButton, clearButton;

    private Connection connection;

    public BankApp() {
        // --- FRAME SETUP ---
        setTitle("Bank Account Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
        setLayout(new BorderLayout(10, 10));

        // --- DATABASE CONNECTION ---
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to connect to the database. Please check your connection details and ensure the server is running.",
                    "Database Connection Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1); // Exit if cannot connect to DB
        }

        // --- PANELS SETUP ---
        JPanel formPanel = createFormPanel();
        JPanel buttonPanel = createButtonPanel();
        JPanel historyPanel = createHistoryPanel();

        // Add panels to the frame
        add(formPanel, BorderLayout.NORTH);
        add(historyPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add padding to the main frame
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Account Holder Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Account Number
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Account Number:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        accNumberField = new JTextField(15);
        panel.add(accNumberField, gbc);

        // Row 1: Fetch Button
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.0;
        fetchButton = new JButton("Fetch Details");
        panel.add(fetchButton, gbc);

        // Row 2: Full Name
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2;
        nameField = new JTextField();
        panel.add(nameField, gbc);

        // Row 3: Date of Birth
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Date of Birth (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2;
        dobField = new JTextField();
        panel.add(dobField, gbc);

        // Row 4: Phone Number
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 2;
        phoneField = new JTextField();
        panel.add(phoneField, gbc);


        // Row 5: Balance
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Balance (₹):"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.gridwidth = 2;
        balanceField = new JTextField();
        panel.add(balanceField, gbc);

        return panel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Transaction History"));
        transactionHistoryArea = new JTextArea(10, 50);
        transactionHistoryArea.setEditable(true); // Allow employees to add notes
        JScrollPane scrollPane = new JScrollPane(transactionHistoryArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        insertButton = new JButton("Insert New Account");
        updateButton = new JButton("Update Account");
        deleteButton = new JButton("Delete Account");
        clearButton = new JButton("Clear Fields");

        panel.add(insertButton);
        panel.add(updateButton);
        panel.add(deleteButton);
        panel.add(clearButton);

        // --- ACTION LISTENERS ---
        ButtonHandler handler = new ButtonHandler();
        insertButton.addActionListener(handler);
        fetchButton.addActionListener(handler);
        updateButton.addActionListener(handler);
        deleteButton.addActionListener(handler);
        clearButton.addActionListener(handler);

        return panel;
    }

    private void clearFields() {
        accNumberField.setText("");
        nameField.setText("");
        dobField.setText("");
        phoneField.setText("");
        balanceField.setText("");
        transactionHistoryArea.setText("");
        accNumberField.setEditable(true);
    }

    // --- HANDLER FOR BUTTON CLICKS ---
    private class ButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            switch (command) {
                case "Insert New Account":
                    insertAccount();
                    break;
                case "Fetch Details":
                    fetchAccount();
                    break;
                case "Update Account":
                    updateAccount();
                    break;
                case "Delete Account":
                    deleteAccount();
                    break;
                case "Clear Fields":
                    clearFields();
                    break;
            }
        }
    }
    
    // --- DATABASE OPERATIONS ---

    private void insertAccount() {
        String sql = "INSERT INTO accounts (account_number, full_name, date_of_birth, phone_number, balance, transaction_history) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String accNum = accNumberField.getText();
            if (accNum.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Account Number is required.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            pstmt.setString(1, accNum);
            pstmt.setString(2, nameField.getText());
            pstmt.setDate(3, java.sql.Date.valueOf(dobField.getText())); // Assumes YYYY-MM-DD format
            pstmt.setString(4, phoneField.getText());
            pstmt.setDouble(5, Double.parseDouble(balanceField.getText()));
            
            // Add a creation timestamp to the transaction history
            String initialHistory = "Account created on " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + ".\n"
                                  + "Initial Balance: " + balanceField.getText() + "\n---\n"
                                  + transactionHistoryArea.getText();
            pstmt.setString(6, initialHistory);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid balance format. Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error inserting account: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void fetchAccount() {
        String accNum = accNumberField.getText();
        if (accNum.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an Account Number to fetch.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, accNum);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("full_name"));
                dobField.setText(rs.getDate("date_of_birth").toString());
                phoneField.setText(rs.getString("phone_number"));
                balanceField.setText(String.valueOf(rs.getDouble("balance")));
                transactionHistoryArea.setText(rs.getString("transaction_history"));
                accNumberField.setEditable(false); // Lock account number field after fetching
                JOptionPane.showMessageDialog(this, "Account details fetched.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Account not found.", "Not Found", JOptionPane.WARNING_MESSAGE);
                clearFields();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching account: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void updateAccount() {
        String sql = "UPDATE accounts SET full_name = ?, date_of_birth = ?, phone_number = ?, balance = ?, transaction_history = ? WHERE account_number = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String accNum = accNumberField.getText();
            if (accNum.isEmpty() || accNumberField.isEditable()) {
                 JOptionPane.showMessageDialog(this, "Please fetch an account before updating.", "Operation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            pstmt.setString(1, nameField.getText());
            pstmt.setDate(2, java.sql.Date.valueOf(dobField.getText()));
            pstmt.setString(3, phoneField.getText());
            pstmt.setDouble(4, Double.parseDouble(balanceField.getText()));
            pstmt.setString(5, transactionHistoryArea.getText());
            pstmt.setString(6, accNum);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Account updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                 JOptionPane.showMessageDialog(this, "Could not find the account to update.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid balance format. Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating account: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void deleteAccount() {
        String accNum = accNumberField.getText();
         if (accNum.isEmpty() || accNumberField.isEditable()) {
             JOptionPane.showMessageDialog(this, "Please fetch an account before deleting.", "Operation Error", JOptionPane.ERROR_MESSAGE);
             return;
         }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete account " + accNum + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM accounts WHERE account_number = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, accNum);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Account deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Account could not be found or deleted.", "Deletion Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting account: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


    public static void main(String[] args) {
        // Ensure the UI is created on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // Set a modern Look and Feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new BankApp().setVisible(true);
        });
    }
}
