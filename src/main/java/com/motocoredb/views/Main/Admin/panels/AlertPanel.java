package com.motocoredb.views.Main.Admin.panels;

import com.motocoredb.models.Alert;
import com.motocoredb.models.Product;
import com.motocoredb.models.WorkshopAppointment;
import com.motocoredb.services.AlertService;
import com.motocoredb.services.ProductService;
import com.motocoredb.services.WorkshopService;
import com.motocoredb.views.forms.alerts.ConfigureAlertForm;
import com.motocoredb.views.utils.FormStyleManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

public class AlertPanel extends JPanel {
    private final AlertService alertService;
    private final ProductService productService;
    private final WorkshopService workshopService;
    
    private DefaultTableModel tableModel;
    private JTable alertsTable;
    private JPanel summaryPanel;
    private JLabel pendingAlertsCount;
    private JLabel stockAlertsCount;
    private JLabel appointmentAlertsCount;
    private Timer refreshTimer;
    
    public AlertPanel(AlertService alertService, ProductService productService, WorkshopService workshopService) {
        this.alertService = alertService;
        this.productService = productService;
        this.workshopService = workshopService;
        
        initUI();
        loadAlerts();
        startAutoRefresh();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(FormStyleManager.BACKGROUND_COLOR);
        
        // Panel superior con título y botones
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Panel central con la tabla de alertas
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);
        
        // Panel inferior con resumen de alertas
        summaryPanel = createSummaryPanel();
        add(summaryPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(FormStyleManager.BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("Panel de Alertas");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(FormStyleManager.PRIMARY_COLOR);
        panel.add(titleLabel, BorderLayout.WEST);
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setBackground(FormStyleManager.BACKGROUND_COLOR);
        
        JButton refreshBtn = FormStyleManager.createSecondaryButton("Actualizar");
        refreshBtn.addActionListener(e -> loadAlerts());
        
        JButton configureBtn = FormStyleManager.createPrimaryButton("Configurar Alertas");
        configureBtn.addActionListener(e -> showConfigureAlertForm());
        
        buttonsPanel.add(refreshBtn);
        buttonsPanel.add(configureBtn);
        panel.add(buttonsPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(FormStyleManager.BACKGROUND_COLOR);
        
        // Filtro de alertas
        JPanel filterPanel = createFilterPanel();
        panel.add(filterPanel, BorderLayout.NORTH);
        
        // Tabla de alertas
        tableModel = new DefaultTableModel(
            new Object[]{"ID", "Tipo", "Mensaje", "Generada", "Estado", "Referencia", "Acciones"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Solo la columna de acciones es editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 6) {
                    return JPanel.class;
                }
                return Object.class;
            }
        };
        
        alertsTable = new JTable(tableModel);
        alertsTable.setRowHeight(35);
        alertsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        alertsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        alertsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        alertsTable.setShowGrid(true);
        alertsTable.setGridColor(Color.LIGHT_GRAY);
        
        // Renderer personalizado para la columna de estado
        alertsTable.getColumnModel().getColumn(4).setCellRenderer(new StatusCellRenderer());
        
        // Renderer personalizado para la columna de acciones
        alertsTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonsRenderer());
        alertsTable.addMouseListener(new ButtonsMouseListener());
        
        // Doble clic para ver detalles
        alertsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewAlertDetails();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(alertsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(FormStyleManager.BACKGROUND_COLOR);
        
        JLabel filterLabel = new JLabel("Filtrar por:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(filterLabel);
        
        String[] filterOptions = {"Todas", "Pendientes", "Leídas", "Resueltas", "Stock Bajo", "Citas Próximas"};
        JComboBox<String> filterCombo = new JComboBox<>(filterOptions);
        filterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filterCombo.addActionListener(e -> applyFilter((String) filterCombo.getSelectedItem()));
        panel.add(filterCombo);
        
        return panel;
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 10));
        panel.setBackground(FormStyleManager.PANEL_COLOR); // Cambiado de BACKGROUND_LIGHT_COLOR a PANEL_COLOR
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FormStyleManager.SECONDARY_COLOR), // Cambiado de PRIMARY_COLOR_LIGHT a SECONDARY_COLOR
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Contadores de alertas
        pendingAlertsCount = createSummaryLabel("Pendientes", "0");
        stockAlertsCount = createSummaryLabel("Stock Bajo", "0");
        appointmentAlertsCount = createSummaryLabel("Citas Próximas", "0");
        
        panel.add(pendingAlertsCount);
        panel.add(new JSeparator(JSeparator.VERTICAL));
        panel.add(stockAlertsCount);
        panel.add(new JSeparator(JSeparator.VERTICAL));
        panel.add(appointmentAlertsCount);
        
        return panel;
    }
    
    private JLabel createSummaryLabel(String title, String value) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(FormStyleManager.TEXT_COLOR);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(FormStyleManager.PRIMARY_COLOR);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(valueLabel, BorderLayout.CENTER);
        
        return valueLabel; // Devolvemos la etiqueta de valor para poder actualizarla más tarde
    }
    
    private void loadAlerts() {
        try {
            List<Alert> alerts = alertService.getPendingAlerts();
            tableModel.setRowCount(0);
            
            int pendingCount = 0;
            int stockCount = 0;
            int appointmentCount = 0;
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            
            for (Alert alert : alerts) {
                // Actualizar contadores según el tipo de alerta
                if ("Pending".equals(alert.getStatus())) {
                    pendingCount++;
                }
                
                if ("Low stock".equals(alert.getAlertType())) {
                    stockCount++;
                } else if ("Upcoming appointment".equals(alert.getAlertType())) {
                    appointmentCount++;
                }
                
                // Crear panel de botones de acción
                JPanel buttonPanel = createActionButtons(alert);
                
                tableModel.addRow(new Object[]{
                    alert.getAlertId(),
                    alert.getAlertType(),
                    alert.getMessage(),
                    dateFormat.format(alert.getGeneratedAt()),
                    alert.getStatus(),
                    alert.getReferenceType() + " #" + alert.getReferenceId(),
                    buttonPanel
                });
            }
            
            // Actualizar etiquetas de resumen
            updateSummaryLabels(pendingCount, stockCount, appointmentCount);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar las alertas: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateSummaryLabels(int pendingCount, int stockCount, int appointmentCount) {
        pendingAlertsCount.setText(String.valueOf(pendingCount));
        stockAlertsCount.setText(String.valueOf(stockCount));
        appointmentAlertsCount.setText(String.valueOf(appointmentCount));
        
        // Cambiar color según cantidad de alertas
        Color pendingColor = pendingCount > 5 ? FormStyleManager.ERROR_COLOR : 
                            (pendingCount > 0 ? FormStyleManager.SECONDARY_COLOR : FormStyleManager.SUCCESS_COLOR);
        pendingAlertsCount.setForeground(pendingColor);
    }
    
    private JPanel createActionButtons(Alert alert) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panel.setOpaque(false);
        
        JButton markReadBtn = new JButton("Leída");
        markReadBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        markReadBtn.setBackground(FormStyleManager.SECONDARY_COLOR);
        markReadBtn.setForeground(Color.WHITE);
        markReadBtn.setBorderPainted(false);
        markReadBtn.setFocusPainted(false);
        markReadBtn.putClientProperty("alertId", alert.getAlertId());
        markReadBtn.putClientProperty("action", "read");
        
        JButton resolveBtn = new JButton("Resolver");
        resolveBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resolveBtn.setBackground(FormStyleManager.SUCCESS_COLOR);
        resolveBtn.setForeground(Color.WHITE);
        resolveBtn.setBorderPainted(false);
        resolveBtn.setFocusPainted(false);
        resolveBtn.putClientProperty("alertId", alert.getAlertId());
        resolveBtn.putClientProperty("action", "resolve");
        resolveBtn.putClientProperty("reference", alert.getReferenceType());
        resolveBtn.putClientProperty("referenceId", alert.getReferenceId());
        
        // Desactivar botones si ya está resuelta o leída
        if ("Resolved".equals(alert.getStatus())) {
            markReadBtn.setEnabled(false);
            resolveBtn.setEnabled(false);
        } else if ("Read".equals(alert.getStatus())) {
            markReadBtn.setEnabled(false);
        }
        
        panel.add(markReadBtn);
        panel.add(resolveBtn);
        
        return panel;
    }
    
    private void applyFilter(String filterType) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        alertsTable.setRowSorter(sorter);
        
        if ("Todas".equals(filterType)) {
            sorter.setRowFilter(null);
        } else if ("Pendientes".equals(filterType)) {
            sorter.setRowFilter(RowFilter.regexFilter("Pending", 4));
        } else if ("Leídas".equals(filterType)) {
            sorter.setRowFilter(RowFilter.regexFilter("Read", 4));
        } else if ("Resueltas".equals(filterType)) {
            sorter.setRowFilter(RowFilter.regexFilter("Resolved", 4));
        } else if ("Stock Bajo".equals(filterType)) {
            sorter.setRowFilter(RowFilter.regexFilter("Low stock", 1));
        } else if ("Citas Próximas".equals(filterType)) {
            sorter.setRowFilter(RowFilter.regexFilter("Upcoming appointment", 1));
        }
    }
    
    private void showConfigureAlertForm() {
        ConfigureAlertForm form = new ConfigureAlertForm(productService, workshopService);
        form.setVisible(true);
        form.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                loadAlerts();
            }
        });
    }
    
    private void viewAlertDetails() {
        int selectedRow = alertsTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        // Convertir el índice de la fila seleccionada en caso de que la tabla esté filtrada
        int modelRow = alertsTable.convertRowIndexToModel(selectedRow);
        
        int alertId = (int) tableModel.getValueAt(modelRow, 0);
        String referenceInfo = (String) tableModel.getValueAt(modelRow, 5);
        
        String[] refParts = referenceInfo.split(" #");
        String refType = refParts[0];
        int refId = Integer.parseInt(refParts[1]);
        
        if ("Product".equals(refType)) {
            showProductDetails(refId);
        } else if ("Appointment".equals(refType)) {
            showAppointmentDetails(refId);
        }
        
        // Marcar la alerta como leída si está pendiente
        if ("Pending".equals(tableModel.getValueAt(modelRow, 4))) {
            alertService.markAlertAsRead(alertId);
            loadAlerts();
        }
    }
    
    private void showProductDetails(int productId) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            JOptionPane.showMessageDialog(this, "No se pudo obtener el producto.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        StringBuilder message = new StringBuilder();
        message.append("Detalles del Producto:\n\n");
        message.append("Código: ").append(product.getProductCode()).append("\n");
        message.append("Nombre: ").append(product.getProductName()).append("\n");
        message.append("Descripción: ").append(product.getDescription()).append("\n");
        message.append("Categoría: ").append(product.getCategory().getCategoryName()).append("\n");
        message.append("Precio de compra: $").append(String.format("%.2f", product.getPurchasePrice())).append("\n");
        message.append("Precio de venta: $").append(String.format("%.2f", product.getSalePrice())).append("\n");
        message.append("Stock actual: ").append(product.getCurrentStock()).append("\n");
        message.append("Stock mínimo: ").append(product.getMinStock()).append("\n");
        message.append("Proveedor: ").append(product.getSupplier().getCompanyName()).append("\n");
        message.append("Estado: ").append(product.getStatus());
        
        JOptionPane.showMessageDialog(this, message.toString(), "Detalles del Producto", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showAppointmentDetails(int appointmentId) {
        WorkshopAppointment appointment = workshopService.getAppointmentById(appointmentId);
        if (appointment == null) {
            JOptionPane.showMessageDialog(this, "No se pudo obtener la cita.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        StringBuilder message = new StringBuilder();
        message.append("Detalles de la Cita:\n\n");
        message.append("Cliente ID: ").append(appointment.getCustomerId()).append("\n");
        message.append("Fecha: ").append(appointment.getScheduledDate()).append("\n");
        message.append("Hora: ").append(appointment.getScheduledTime()).append("\n");
        message.append("Motivo: ").append(appointment.getVisitReason()).append("\n");
        message.append("Moto: ").append(appointment.getMotorcycleDescription()).append("\n");
        message.append("Placa: ").append(appointment.getMotorcyclePlate()).append("\n");
        message.append("Estado: ").append(appointment.getStatus()).append("\n");
        message.append("Notas: ").append(appointment.getNotes());
        
        JOptionPane.showMessageDialog(this, message.toString(), "Detalles de la Cita", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void handleButtonAction(int row, int col, Point point) {
        if (col != 6) return; // Solo procesar clics en la columna de acciones
        
        int modelRow = alertsTable.convertRowIndexToModel(row);
        Object value = tableModel.getValueAt(modelRow, col);
        
        if (value instanceof JPanel) {
            JPanel panel = (JPanel) value;
            for (Component comp : panel.getComponents()) {
                if (comp instanceof JButton) {
                    JButton button = (JButton) comp;
                    Rectangle rect = button.getBounds();
                    
                    if (rect.contains(point)) {
                        int alertId = (int) button.getClientProperty("alertId");
                        String action = (String) button.getClientProperty("action");
                        
                        if ("read".equals(action)) {
                            alertService.markAlertAsRead(alertId);
                            loadAlerts();
                        } else if ("resolve".equals(action)) {
                            // Si es una alerta de stock bajo, podríamos mostrar un formulario para hacer un pedido
                            String refType = (String) button.getClientProperty("reference");
                            int refId = (int) button.getClientProperty("referenceId");
                            
                            if ("Product".equals(refType)) {
                                handleLowStockAlert(refId);
                            } else if ("Appointment".equals(refType)) {
                                handleAppointmentAlert(refId);
                            }
                            
                            alertService.resolveAlert(alertId);
                            loadAlerts();
                        }
                        break;
                    }
                }
            }
        }
    }
    
    private void handleLowStockAlert(int productId) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            JOptionPane.showMessageDialog(this, "No se pudo obtener el producto.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Aquí se podría abrir un formulario de pedido rápido
        String message = "¿Desea realizar un pedido para el producto " + product.getProductName() + "?";
        int response = JOptionPane.showConfirmDialog(this, message, "Resolver Alerta de Stock", JOptionPane.YES_NO_OPTION);
        
        if (response == JOptionPane.YES_OPTION) {
            // Implementar lógica para abrir formulario de pedido
            JOptionPane.showMessageDialog(this, "Abriendo formulario de pedido...", "Información", JOptionPane.INFORMATION_MESSAGE);
            // Aquí iría la llamada al formulario de pedido
        }
    }
    
    private void handleAppointmentAlert(int appointmentId) {
        WorkshopAppointment appointment = workshopService.getAppointmentById(appointmentId);
        if (appointment == null) {
            JOptionPane.showMessageDialog(this, "No se pudo obtener la cita.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Aquí se podría abrir un formulario para gestionar la cita
        String message = "¿Desea confirmar la cita para el día " + appointment.getScheduledDate() + " a las " + appointment.getScheduledTime() + "?";
        int response = JOptionPane.showConfirmDialog(this, message, "Confirmar Cita", JOptionPane.YES_NO_OPTION);
        
        if (response == JOptionPane.YES_OPTION) {
            // Implementar lógica para confirmar cita
            JOptionPane.showMessageDialog(this, "Enviando confirmación al cliente...", "Información", JOptionPane.INFORMATION_MESSAGE);
            // Aquí iría la llamada al servicio para confirmar la cita
        }
    }
    
    private void startAutoRefresh() {
        // Configurar un timer para actualizar las alertas cada 5 minutos
        refreshTimer = new Timer(300000, e -> loadAlerts());
        refreshTimer.start();
    }
    
    // Método para detener el timer cuando el panel se cierra
    public void stopAutoRefresh() {
        if (refreshTimer != null && refreshTimer.isRunning()) {
            refreshTimer.stop();
        }
    }
    
    // Clase interna para renderizar el estado con colores
    private class StatusCellRenderer extends JLabel implements TableCellRenderer {
        public StatusCellRenderer() {
            setOpaque(true);
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String status = (String) value;
            
            if ("Pending".equals(status)) {
                setBackground(FormStyleManager.SECONDARY_COLOR); // Cambiado de WARNING_COLOR a SECONDARY_COLOR
                setForeground(Color.WHITE);
            } else if ("Read".equals(status)) {
                setBackground(FormStyleManager.PRIMARY_COLOR); // Cambiado de INFO_COLOR a PRIMARY_COLOR
                setForeground(Color.WHITE);
            } else if ("Resolved".equals(status)) {
                setBackground(FormStyleManager.SUCCESS_COLOR);
                setForeground(Color.WHITE);
            }
            
            setText(status);
            return this;
        }
    }
    
    // Clase interna para renderizar los botones de acción
    private class ButtonsRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return (JPanel) value;
        }
    }
    
    // Clase interna para manejar eventos de ratón en los botones
    private class ButtonsMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int row = alertsTable.rowAtPoint(e.getPoint());
            int col = alertsTable.columnAtPoint(e.getPoint());
            
            if (row >= 0 && col >= 0) {
                handleButtonAction(row, col, e.getPoint());
            }
        }
    }
}