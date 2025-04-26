package com.motocoredb;

import com.motocoredb.controllers.AuthController;
import com.motocoredb.dao.impl.UserDaoImpl;
import com.motocoredb.services.AuthService;
import com.motocoredb.utils.DBConnection;
import com.motocoredb.utils.LoggerUtil;
import com.motocoredb.views.Auth.LoginFrame;

import org.slf4j.Logger;

import java.sql.SQLException;

/**
 * Hay que corregir el panel de appointments
 * falta el panel estadisticas y ventas
 * faltan las otras vistas segun los usuarios
 * falta el nivel de acceso a la base de datos en sql
 * si se puede, hay que hacer un panel de configuracion
 * falta panel de registrar ususairo
 * si se puede, se puede subir la base de datos a un servidor
 * falta imagen de login de la empresa
 * falta readme
 */

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

    private static void initializeDatabase() {
        try {
            // La configuración ahora se carga automáticamente en el bloque static
            DBConnection.getConnection(); // Test connection
            logger.info("Database connection established successfully");
        } catch (SQLException e) {
            logger.error("Database initialization failed", e);
            showErrorAndExit("Cannot connect to database. Check configuration.");
        } catch (Exception e) {
            logger.error("Configuration loading failed", e);
            showErrorAndExit("Failed to load database configuration");
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
            java.awt.EventQueue.invokeLater(() -> {
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