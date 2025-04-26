package com.motocoredb.views.forms.suppliers;

import com.motocoredb.services.SupplierService;
import com.motocoredb.views.forms.utils.FormStyleManager;

import javax.swing.*;
import java.awt.*;

public abstract class SupplierFormBase extends JFrame {
    protected final SupplierService supplierService;
    protected JPanel containerPanel;
    protected FormStyleManager.RoundedPanel mainPanel;
    protected JPanel formPanel;
    protected JPanel buttonPanel;

    public SupplierFormBase(SupplierService supplierService, String title, int width, int height) {
        this.supplierService = supplierService;
        initializeFrame(title, width, height);
        setupPanels();
    }

    protected void initializeFrame(String title, int width, int height) {
        setTitle(title);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    protected void setupPanels() {
        containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(FormStyleManager.BACKGROUND_COLOR);
        containerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        mainPanel = new FormStyleManager.RoundedPanel(new BorderLayout(15, 15), 15);
        mainPanel.setBackground(FormStyleManager.PANEL_COLOR);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1, true),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setOpaque(false);
        
        containerPanel.add(mainPanel, BorderLayout.CENTER);
        add(containerPanel);
        
        getRootPane().setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    protected JPanel createHeaderPanel(String title, String iconPath) {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setOpaque(false);
        
        if (iconPath != null && !iconPath.isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
                JLabel iconLabel = new JLabel(icon);
                headerPanel.add(iconLabel, BorderLayout.WEST);
            } catch (Exception e) {
                // No es crítico si no se puede cargar el icono
            }
        }
        
        JLabel titleLabel = FormStyleManager.createHeaderLabel(title);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        return headerPanel;
    }

    protected JPanel createFormPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(FormStyleManager.createTitledBorder(title));
        return panel;
    }
    
    protected abstract void initializeUI();
}
