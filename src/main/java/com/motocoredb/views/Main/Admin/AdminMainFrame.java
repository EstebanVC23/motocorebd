package com.motocoredb.views.Main.Admin;

import com.motocoredb.controllers.AuthController;
import com.motocoredb.models.User;
import com.motocoredb.views.Main.Admin.paneles.*;
import com.motocoredb.views.Auth.LoginFrame;
import com.motocoredb.views.Main.navbar.Navbar;
import com.motocoredb.services.ProductService;
import com.motocoredb.dao.impl.ProductDaoImpl;

import javax.swing.*;
import java.awt.*;

public class AdminMainFrame extends JFrame {
    private final AuthController authController;
    private final User usuario;
    private CardLayout cardLayout;
    private JPanel mainContentPanel;

    public AdminMainFrame(User usuario, AuthController authController) {
        this.usuario = usuario;
        this.authController = authController;
        initUI();
    }

    private void initUI() {
        setTitle("MotoCoreDB - Panel de Administración");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Configuración principal
        setLayout(new BorderLayout());

        // Panel de navegación superior
        add(createNavBar(), BorderLayout.NORTH);

        // Panel de contenido principal con CardLayout
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);

        // Registrar todos los paneles
        registerPanels();

        add(mainContentPanel, BorderLayout.CENTER);

        // Barra de estado inferior
        add(createStatusBar(), BorderLayout.SOUTH);
    }

    private void registerPanels() {
        // Crear instancia de ProductService con su DAO
        ProductService productService;
        try {
            productService = new ProductService(new ProductDaoImpl());
        } catch (Exception e) {
            throw new RuntimeException("Error al inicializar ProductService: " + e.getMessage(), e);
        }
    
        // Registrar paneles con sus dependencias
        mainContentPanel.add(new InventarioPanel(productService), "Inventario");
        mainContentPanel.add(new PerfilPanel(usuario), "Perfil");
    }

    private JPanel createNavBar() {
        // Opciones del navbar
        String[] opciones = {"Inventario", "Clientes", "Proveedores", "Ventas", "Taller", "Reportes", "Perfil"};
        return new Navbar(opciones, this::cambiarPanel, this::cerrarSesion);
    }

    private JPanel createStatusBar() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusPanel.setBackground(new Color(51, 51, 51));

        JLabel statusLabel = new JLabel(
            String.format("Usuario: %s | Rol: %s | Último acceso: %s",
                usuario.getUsername(),
                usuario.getRole(),
                usuario.getLastLogin() != null ? usuario.getLastLogin().toString() : "Nunca"
            )
        );
        statusLabel.setForeground(Color.WHITE);

        statusPanel.add(statusLabel, BorderLayout.WEST);
        return statusPanel;
    }

    private void cambiarPanel(String panelName) {
        cardLayout.show(mainContentPanel, panelName);
    }

    private void cerrarSesion() {
        authController.logout();
        dispose();
        new LoginFrame(authController).setVisible(true);
    }
}