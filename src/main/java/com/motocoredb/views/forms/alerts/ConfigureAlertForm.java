package com.motocoredb.views.forms.alerts;

import com.motocoredb.models.Product;
import com.motocoredb.models.WorkshopAppointment;
import com.motocoredb.services.ProductService;
import com.motocoredb.services.WorkshopService;
import com.motocoredb.views.utils.FormStyleManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ConfigureAlertForm extends JFrame {
    private final ProductService productService;
    private final WorkshopService workshopService;
    
    private JSpinner minStockThresholdSpinner;
    private JSpinner appointmentReminderDaysSpinner;
    private JCheckBox enableStockAlertsCheckbox;
    private JCheckBox enableAppointmentAlertsCheckbox;
    private JComboBox<String> notificationMethodCombo;
    
    public ConfigureAlertForm(ProductService productService, WorkshopService workshopService) {
        this.productService = productService;
        this.workshopService = workshopService;
        
        setTitle("Configuración de Alertas");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        initUI();
        loadConfiguration();
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(FormStyleManager.BACKGROUND_COLOR);
        
        // Panel de título
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(FormStyleManager.BACKGROUND_COLOR);
        JLabel titleLabel = new JLabel("Configuración de Alertas del Sistema");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(FormStyleManager.PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Panel de contenido
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(FormStyleManager.BACKGROUND_COLOR);
        
        // Panel de alertas de inventario
        JPanel inventoryPanel = createInventoryAlertPanel();
        contentPanel.add(inventoryPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        
        // Panel de alertas de citas
        JPanel appointmentPanel = createAppointmentAlertPanel();
        contentPanel.add(appointmentPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        
        // Panel de notificaciones
        JPanel notificationPanel = createNotificationPanel();
        contentPanel.add(notificationPanel);
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(FormStyleManager.BACKGROUND_COLOR);
        
        JButton cancelButton = FormStyleManager.createSecondaryButton("Cancelar");
        cancelButton.addActionListener(e -> dispose());
        
        JButton saveButton = FormStyleManager.createPrimaryButton("Guardar Configuración");
        saveButton.addActionListener(e -> saveConfiguration());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createInventoryAlertPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(FormStyleManager.PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FormStyleManager.SECONDARY_COLOR),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Título del panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setOpaque(false);
        
        enableStockAlertsCheckbox = new JCheckBox("Activar alertas de inventario");
        enableStockAlertsCheckbox.setFont(new Font("Segoe UI", Font.BOLD, 16));
        enableStockAlertsCheckbox.setForeground(FormStyleManager.PRIMARY_COLOR);
        enableStockAlertsCheckbox.setOpaque(false);
        titlePanel.add(enableStockAlertsCheckbox);
        
        panel.add(titlePanel, BorderLayout.NORTH);
        
        // Contenido del panel
        JPanel contentPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        contentPanel.setOpaque(false);
        
        JLabel thresholdLabel = new JLabel("Umbral de stock mínimo:");
        thresholdLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(thresholdLabel);
        
        minStockThresholdSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 50, 1));
        minStockThresholdSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(minStockThresholdSpinner);
        
        JLabel alertTypeLabel = new JLabel("Aplicar alerta cuando:");
        alertTypeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(alertTypeLabel);
        
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPanel.setOpaque(false);
        
        JRadioButton exactMatchRadio = new JRadioButton("El stock sea igual al mínimo");
        exactMatchRadio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        exactMatchRadio.setOpaque(false);
        
        JRadioButton belowMatchRadio = new JRadioButton("El stock sea menor al mínimo");
        belowMatchRadio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        belowMatchRadio.setOpaque(false);
        belowMatchRadio.setSelected(true);
        
        ButtonGroup alertTypeGroup = new ButtonGroup();
        alertTypeGroup.add(exactMatchRadio);
        alertTypeGroup.add(belowMatchRadio);
        
        radioPanel.add(exactMatchRadio);
        radioPanel.add(belowMatchRadio);
        contentPanel.add(radioPanel);
        
        // Lista de productos con stock crítico
        JLabel criticalProductsLabel = new JLabel("Productos en estado crítico:");
        criticalProductsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(criticalProductsLabel);
        
        JButton viewCriticalButton = FormStyleManager.createSecondaryButton("Ver lista");
        viewCriticalButton.addActionListener(e -> showCriticalProductsList());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(viewCriticalButton);
        contentPanel.add(buttonPanel);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createAppointmentAlertPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(FormStyleManager.PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FormStyleManager.SECONDARY_COLOR),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Título del panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setOpaque(false);
        
        enableAppointmentAlertsCheckbox = new JCheckBox("Activar alertas de citas");
        enableAppointmentAlertsCheckbox.setFont(new Font("Segoe UI", Font.BOLD, 16));
        enableAppointmentAlertsCheckbox.setForeground(FormStyleManager.PRIMARY_COLOR);
        enableAppointmentAlertsCheckbox.setOpaque(false);
        titlePanel.add(enableAppointmentAlertsCheckbox);
        
        panel.add(titlePanel, BorderLayout.NORTH);
        
        // Contenido del panel
        JPanel contentPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        contentPanel.setOpaque(false);
        
        JLabel reminderLabel = new JLabel("Recordatorio de citas (días antes):");
        reminderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(reminderLabel);
        
        appointmentReminderDaysSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 7, 1));
        appointmentReminderDaysSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(appointmentReminderDaysSpinner);
        
        JLabel alertEventsLabel = new JLabel("Eventos que generan alertas:");
        alertEventsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(alertEventsLabel);
        
        JPanel checkBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        checkBoxPanel.setOpaque(false);
        
        JCheckBox newAppointmentCheck = new JCheckBox("Nueva cita");
        newAppointmentCheck.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        newAppointmentCheck.setOpaque(false);
        newAppointmentCheck.setSelected(true);
        
        JCheckBox reminderCheck = new JCheckBox("Recordatorio");
        reminderCheck.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reminderCheck.setOpaque(false);
        reminderCheck.setSelected(true);
        
        JCheckBox statusChangeCheck = new JCheckBox("Cambio de estado");
        statusChangeCheck.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusChangeCheck.setOpaque(false);
        statusChangeCheck.setSelected(true);
        
        checkBoxPanel.add(newAppointmentCheck);
        checkBoxPanel.add(reminderCheck);
        checkBoxPanel.add(statusChangeCheck);
        contentPanel.add(checkBoxPanel);
        
        // Lista de citas próximas
        JLabel upcomingAppointmentsLabel = new JLabel("Citas próximas:");
        upcomingAppointmentsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(upcomingAppointmentsLabel);
        
        JButton viewAppointmentsButton = FormStyleManager.createSecondaryButton("Ver agenda");
        viewAppointmentsButton.addActionListener(e -> showUpcomingAppointments());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(viewAppointmentsButton);
        contentPanel.add(buttonPanel);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createNotificationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(FormStyleManager.PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FormStyleManager.SECONDARY_COLOR),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Título del panel
        JLabel titleLabel = new JLabel("Configuración de Notificaciones");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(FormStyleManager.PRIMARY_COLOR);
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        
        panel.add(titlePanel, BorderLayout.NORTH);
        
        // Contenido del panel
        JPanel contentPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        contentPanel.setOpaque(false);
        
        JLabel methodLabel = new JLabel("Método de notificación:");
        methodLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(methodLabel);
        
        String[] notificationMethods = {"Alerta en sistema", "Correo electrónico", "Ambos"};
        notificationMethodCombo = new JComboBox<>(notificationMethods);
        notificationMethodCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(notificationMethodCombo);
        
        JLabel frequencyLabel = new JLabel("Frecuencia de verificación:");
        frequencyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(frequencyLabel);
        
        String[] frequencies = {"Cada 15 minutos", "Cada hora", "Cada 4 horas", "Diariamente"};
        JComboBox<String> frequencyCombo = new JComboBox<>(frequencies);
        frequencyCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(frequencyCombo);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void showCriticalProductsList() {
        try {
            // Usamos el método correcto del servicio para obtener productos con bajo stock
            List<Product> criticalProducts = productService.getLowStockProducts();
            
            if (criticalProducts.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No hay productos en estado crítico de stock.", 
                    "Inventario", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            JPanel panel = new JPanel(new BorderLayout());
            panel.setPreferredSize(new Dimension(500, 300));
            
            DefaultListModel<String> listModel = new DefaultListModel<>();
            for (Product product : criticalProducts) {
                listModel.addElement(product.getProductName() + " - Stock: " + 
                                     product.getCurrentStock() + " (Mínimo: " + 
                                     product.getMinStock() + ")");
            }
            
            JList<String> productList = new JList<>(listModel);
            productList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            JScrollPane scrollPane = new JScrollPane(productList);
            panel.add(scrollPane, BorderLayout.CENTER);
            
            JOptionPane.showMessageDialog(this, 
                panel, 
                "Productos con Stock Crítico", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar la lista de productos: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showUpcomingAppointments() {
        try {
            // Obtenemos todas las citas y filtramos las próximas
            List<WorkshopAppointment> upcomingAppointments = workshopService.getAllAppointments();
            
            if (upcomingAppointments == null || upcomingAppointments.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No hay citas programadas próximamente.", 
                    "Agenda", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            JPanel panel = new JPanel(new BorderLayout());
            panel.setPreferredSize(new Dimension(500, 300));
            
            DefaultListModel<String> listModel = new DefaultListModel<>();
            for (WorkshopAppointment appointment : upcomingAppointments) {
                listModel.addElement("Cliente ID: " + appointment.getCustomerId() + 
                                    " - Fecha: " + appointment.getScheduledDate() + 
                                    " - Hora: " + appointment.getScheduledTime() + 
                                    " - " + appointment.getVisitReason());
            }
            
            JList<String> appointmentList = new JList<>(listModel);
            appointmentList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            JScrollPane scrollPane = new JScrollPane(appointmentList);
            panel.add(scrollPane, BorderLayout.CENTER);
            
            JOptionPane.showMessageDialog(this, 
                panel, 
                "Citas Programadas", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar la agenda: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadConfiguration() {
        // Aquí cargaríamos la configuración actual desde la base de datos
        // Por ahora simulamos valores predeterminados
        minStockThresholdSpinner.setValue(5);
        appointmentReminderDaysSpinner.setValue(2);
        enableStockAlertsCheckbox.setSelected(true);
        enableAppointmentAlertsCheckbox.setSelected(true);
        notificationMethodCombo.setSelectedIndex(0);
    }
    
    private void saveConfiguration() {
        try {
            // Validar configuración
            if (!enableStockAlertsCheckbox.isSelected() && !enableAppointmentAlertsCheckbox.isSelected()) {
                JOptionPane.showMessageDialog(this, 
                    "Debe activar al menos un tipo de alerta.", 
                    "Validación", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Aquí guardaríamos la configuración en la base de datos
            JOptionPane.showMessageDialog(this, 
                "Configuración guardada exitosamente.", 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);
            
            dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al guardar la configuración: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}