package com.motocoredb.views;

import com.motocoredb.controllers.AuthController;
import com.motocoredb.models.User;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {
    private final AuthController authController;
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame(AuthController authController) {
        this.authController = authController;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("MotoCoreDB - Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField(20);
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(20);
        panel.add(passwordField);

        loginButton = new JButton("Login");
        loginButton.addActionListener(this::performLogin);
        panel.add(loginButton);

        add(panel);
    }

    private void performLogin(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        User user = authController.login(username, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Login successful!");
            dispose();
            showMainApplication(user);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMainApplication(User user) {
        // Implement your main application window here
        // Based on user role (Admin, Seller, Mechanic) show different views
        JFrame mainFrame = new JFrame("MotoCoreDB - Welcome " + user.getFullName());
        mainFrame.setSize(800, 600);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
}