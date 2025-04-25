package com.motocoredb;

import com.motocoredb.config.DataBaseConfig;
import com.motocoredb.controllers.AuthController;
import com.motocoredb.dao.impl.UserDaoImpl;
import com.motocoredb.services.AuthService;
import com.motocoredb.utils.DBConnection;
import com.motocoredb.utils.LoggerUtil;
import com.motocoredb.views.LoginFrame;
import org.slf4j.Logger;

import java.io.IOException;
import java.sql.SQLException;

public class App {
    private static final Logger logger = LoggerUtil.getLogger(App.class);

    public static void main(String[] args) {
        try {
            initializeDatabase();
            initializeApplication();
        } catch (Exception e) {
            logger.error("Application startup failed", e);
            showErrorAndExit("Failed to initialize application");
        }
    }

    private static void initializeDatabase() throws IOException, SQLException {
        try {
            DataBaseConfig.loadConfiguration();
            DBConnection.getConnection(); // Test connection
            logger.info("Database connection established successfully");
        } catch (IOException | SQLException e) {
            logger.error("Database initialization failed", e);
            throw e; // Re-lanzamos la excepción para manejarla en el main
        }
    }

    private static void initializeApplication() {
        try {
            // Initialize DAOs
            UserDaoImpl userDao = new UserDaoImpl();
            
            // Initialize Services
            AuthService authService = new AuthService(userDao);
            
            // Initialize Controllers
            AuthController authController = new AuthController(authService);
            
            // Start the application with login screen
            javax.swing.SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame(authController);
                loginFrame.setVisible(true);
            });
            
            logger.info("Application initialized successfully");
        } catch (SQLException e) {
            logger.error("Application initialization failed", e);
            showErrorAndExit("Application initialization failed");
        }
    }

    private static void showErrorAndExit(String message) {
        javax.swing.JOptionPane.showMessageDialog(
            null,
            message,
            "Error",
            javax.swing.JOptionPane.ERROR_MESSAGE
        );
        System.exit(1);
    }
}