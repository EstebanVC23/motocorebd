package com.motocoredb.views.Main;

import com.motocoredb.controllers.AuthController;
import com.motocoredb.models.Staff;
import com.motocoredb.models.User;
import com.motocoredb.views.Auth.LoginFrame;
import com.motocoredb.views.Main.navbar.Navbar;
import com.motocoredb.views.Main.panels.*;
import com.motocoredb.services.ProductService;
import com.motocoredb.services.StaffService;
import com.motocoredb.services.SupplierService;
import com.motocoredb.services.UserService;
import com.motocoredb.dao.impl.*;
import com.motocoredb.services.*;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AdminMainFrame extends JFrame {
    private final AuthController authController;
    private final User usuario;
    private StaffService staffService; // Declarar como atributo de clase
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
        staffService = new StaffService(new StaffDaoImpl()); // Asignar a la variable de clase
        SupplierService supplierService = new SupplierService(new SupplierDaoImpl());
        WorkshopService workshopService = new WorkshopService(new WorkshopDaoImpl(), new CustomerDaoImpl());
        UserService userService = new UserService(new UserDaoImpl());
        SaleService salesService = new SaleService(new SaleDaoImpl());
        InventoryService inventoryService = new InventoryService(new InventoryDaoImpl());
        AlertService alertService = new AlertService(new AlertDaoImpl());
        CategoryService categoryService = new CategoryService(new CategoryDaoImpl());

        // Agregar paneles al contenedor principal
        mainContentPanel.add(new InventoryPanel(productService, supplierService, categoryService), "Inventario");
        mainContentPanel.add(new CustomersPanel(customerService), "Clientes");
        mainContentPanel.add(new PerfilPanel(usuario), "Perfil");
        mainContentPanel.add(new StaffPanel(staffService), "Empleados");
        mainContentPanel.add(new SupplierPanel(supplierService), "Proveedores");
        mainContentPanel.add(new SalePanel(salesService, productService, customerService, userService), "Ventas");
        mainContentPanel.add(new AppointmentsPanel(workshopService), "Citas");
        mainContentPanel.add(new ReportPanel(salesService, inventoryService), "Reportes");

        // Agregar AlertPanel
        mainContentPanel.add(new AlertPanel(alertService, workshopService), "Alertas");
    }

    private JPanel createNavBar() {
        String[] opciones = {
            "Inventario", "Empleados", "Clientes", "Proveedores", "Ventas", "Citas", "Reportes", "Alertas", "Perfil"
        };

        return new Navbar(opciones, this::cambiarPanel);
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

        // Botón para cerrar sesión en la barra de estado
        JButton logoutButton = new JButton("Cerrar Sesión");
        logoutButton.setBackground(new Color(102, 102, 255));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.addActionListener(e -> cerrarSesion());
        statusPanel.add(logoutButton, BorderLayout.EAST);

        return statusPanel;
    }

    private void cambiarPanel(String panelName) {
        cardLayout.show(mainContentPanel, panelName);
    }

    private void cerrarSesion() {
        try {
            if (usuario != null && usuario.getUsername() != null) {
                boolean logoutSuccess = authController.updateLastLoginBeforeLogout(usuario.getUsername());
                if (!logoutSuccess) {
                    System.out.println("No se pudo actualizar la información de cierre de sesión");
                }
            }

            boolean staffRefreshed = refreshStaffData();
            if (!staffRefreshed) {
                System.out.println("No se pudo refrescar la información de Staff en la base de datos");
            }

            authController.logout();
            dispose();
            new LoginFrame(authController).setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "Error al cerrar sesión: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            dispose();
            new LoginFrame(authController).setVisible(true);
        }
    }

    private boolean refreshStaffData() {
        try {
            List<Staff> staffList = staffService.getAllStaffs(); // Utiliza el atributo staffService
            System.out.println("Registros de Staff refrescados: " + staffList.size());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showErrorDialog(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }
}