package com.motocoredb.views.Main.Admin.paneles.forms;

import com.motocoredb.views.Main.Admin.paneles.forms.utils.ProductFormStyleManager;
import com.motocoredb.services.ProductService;

import javax.swing.*;
import java.awt.*;

public abstract class ProductFormBase extends JFrame {
    protected final ProductService productService;
    protected JPanel containerPanel;
    protected ProductFormStyleManager.RoundedPanel mainPanel;
    protected JPanel formPanel;
    protected JPanel buttonPanel;

    public ProductFormBase(ProductService productService, String title, int width, int height) {
        this.productService = productService;
        initializeFrame(title, width, height);
        setupPanels();
    }

    /**
     * Inicializa la configuración básica del formulario
     */
    protected void initializeFrame(String title, int width, int height) {
        setTitle(title);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    /**
     * Configura los paneles básicos del formulario
     */
    protected void setupPanels() {
        // Panel contenedor principal
        containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(ProductFormStyleManager.BACKGROUND_COLOR);
        containerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel redondeado para el contenido principal
        mainPanel = new ProductFormStyleManager.RoundedPanel(new BorderLayout(15, 15), 15);
        mainPanel.setBackground(ProductFormStyleManager.PANEL_COLOR);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1, true),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        
        // Panel para los botones
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setOpaque(false);
        
        containerPanel.add(mainPanel, BorderLayout.CENTER);
        add(containerPanel);
        
        // Borde decorativo para efectos visuales
        getRootPane().setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    /**
     * Crea un panel de encabezado con icono opcional
     */
    protected JPanel createHeaderPanel(String title, String iconPath) {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setOpaque(false);
        
        // Intentar cargar el icono si se proporciona la ruta
        if (iconPath != null && !iconPath.isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
                JLabel iconLabel = new JLabel(icon);
                headerPanel.add(iconLabel, BorderLayout.WEST);
            } catch (Exception e) {
                // No es crítico si no se puede cargar el icono
            }
        }
        
        JLabel titleLabel = ProductFormStyleManager.createHeaderLabel(title);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        return headerPanel;
    }

    /**
     * Crea el panel de formulario con GridBagLayout
     */
    protected JPanel createFormPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(ProductFormStyleManager.createTitledBorder(title));
        return panel;
    }
    
    /**
     * Método abstracto que debe implementarse en las clases derivadas
     */
    protected abstract void initializeUI();
}