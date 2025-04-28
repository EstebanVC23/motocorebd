package com.motocoredb.views.forms.staff;

import com.motocoredb.services.StaffService;
import com.motocoredb.views.utils.FormStyleManager;

import javax.swing.*;
import java.awt.*;

public abstract class StaffFormBase extends JFrame {
    protected final StaffService staffService;
    protected JPanel containerPanel;
    protected FormStyleManager.RoundedPanel mainPanel;
    protected JPanel formPanel;
    protected JPanel buttonPanel;

    public StaffFormBase(StaffService staffService, String title, int width, int height) {
        this.staffService = staffService;
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
        containerPanel.setBackground(FormStyleManager.BACKGROUND_COLOR);
        containerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel redondeado para el contenido principal
        mainPanel = new FormStyleManager.RoundedPanel(new BorderLayout(15, 15), 15);
        mainPanel.setBackground(FormStyleManager.PANEL_COLOR);
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

    protected JPanel createHeaderPanel(String title) {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = FormStyleManager.createHeaderLabel(title);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        return headerPanel;
    }

    /**
     * Crea el panel de formulario con GridBagLayout
     */
    protected JPanel createFormPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(FormStyleManager.createTitledBorder(title));
        return panel;
    }
    
    /**
     * Método abstracto que debe implementarse en las clases derivadas
     */
    protected abstract void initializeUI();
}