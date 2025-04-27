package com.motocoredb.views.Main.Admin;

import com.motocoredb.controllers.AuthController;
import com.motocoredb.models.User;
import com.motocoredb.views.Auth.LoginFrame;
import com.motocoredb.views.Main.Admin.panels.*;
import com.motocoredb.views.Main.navbar.Navbar;
import com.motocoredb.services.ProductService;
import com.motocoredb.services.StaffService;
import com.motocoredb.services.SupplierService;
import com.motocoredb.dao.impl.ProductDaoImpl;
import com.motocoredb.services.CustomerService;
import com.motocoredb.dao.impl.CustomerDaoImpl;
import com.motocoredb.dao.impl.StaffDaoImpl;
import com.motocoredb.dao.impl.SupplierDaoImpl;
import com.motocoredb.dao.impl.WorkshopDaoImpl;
import com.motocoredb.services.WorkshopService;
import com.motocoredb.services.SaleService;
import com.motocoredb.dao.impl.SaleDaoImpl;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

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
        setLayout(new BorderLayout());
        add(createNavBar(), BorderLayout.NORTH);
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        try {
            registerPanels();
        } catch (SQLException e) {
            showErrorDialog("Error al registrar paneles: " + e.getMessage());
        }
        add(mainContentPanel, BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);
    }

    private void registerPanels() throws SQLException {
        // Inicializar servicios
        ProductService productService = new ProductService(new ProductDaoImpl());
        CustomerService customerService = new CustomerService(new CustomerDaoImpl());
        StaffService staffService = new StaffService(new StaffDaoImpl());
        SupplierService supplierService = new SupplierService(new SupplierDaoImpl());
        WorkshopService workshopService = new WorkshopService(new WorkshopDaoImpl()); // Pasar la conexión correctamente
        SaleService salesService = new SaleService(new SaleDaoImpl());

        // Agregar paneles al contenedor principal
        mainContentPanel.add(new InventoryPanel(productService), "Inventario");
        mainContentPanel.add(new CustomersPanel(customerService), "Clientes");
        mainContentPanel.add(new PerfilPanel(usuario), "Perfil");
        mainContentPanel.add(new StaffPanel(staffService), "Empleados");
        mainContentPanel.add(new SupplierPanel(supplierService), "Proveedores");

        // Panel de ventas con todos los servicios necesarios
        mainContentPanel.add(new SalePanel(salesService, productService, customerService), "Ventas");

        // Panel de citas con conexión inicializada
        mainContentPanel.add(new AppointmentsPanel(workshopService), "Citas");
    }

    private JPanel createNavBar() {
        String[] opciones = {"Inventario", "Empleados", "Clientes", "Proveedores", "Ventas", "Citas", "Reportes", "Perfil"};
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

    private void showErrorDialog(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }
}