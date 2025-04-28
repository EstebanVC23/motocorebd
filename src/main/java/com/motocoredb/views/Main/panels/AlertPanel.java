package com.motocoredb.views.Main.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import com.motocoredb.services.AlertService;
import com.motocoredb.services.WorkshopService;
import com.motocoredb.models.Alert;
import com.motocoredb.models.WorkshopAppointment;
import com.motocoredb.views.utils.FormStyleManager;

public class AlertPanel extends JPanel {

    private final AlertService alertService;
    private final WorkshopService workshopService;

    private JComboBox<String> filterComboBox;
    private JTable alertTable;
    private DefaultTableModel tableModel;

    public AlertPanel(AlertService alertService, WorkshopService workshopService) {
        this.alertService = alertService;
        this.workshopService = workshopService;

        initUI();
        enableDoubleClickEvent();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(FormStyleManager.BACKGROUND_COLOR);

        // Panel superior con título y botón de actualización
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(FormStyleManager.BACKGROUND_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        
        JLabel titleLabel = new JLabel("Gestión de Alertas");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(FormStyleManager.PRIMARY_COLOR);
        
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(FormStyleManager.BACKGROUND_COLOR);
        toolBar.add(titleLabel);
        toolBar.addSeparator();
        
        JButton refreshButton = FormStyleManager.createPrimaryButton("Actualizar");
        refreshButton.addActionListener(e -> loadAlertData((String) filterComboBox.getSelectedItem()));
        toolBar.add(refreshButton);
        
        topPanel.add(toolBar, BorderLayout.NORTH);
        
        // Panel de filtros mejorado
        JPanel filterPanel = createFilterPanel();
        topPanel.add(filterPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);

        // Tabla de alertas
        JScrollPane tableScrollPane = createAlertTable();
        add(tableScrollPane, BorderLayout.CENTER);

        // Cargar datos iniciales
        loadAlertData("Todas"); // Muestra todas las alertas inicialmente
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(FormStyleManager.PANEL_COLOR);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(FormStyleManager.PRIMARY_COLOR, 1, true),
                "Filtros",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 14),
                FormStyleManager.PRIMARY_COLOR
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel filterLabel = FormStyleManager.createStyledLabel("Tipo de Alerta:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        filterComboBox = new JComboBox<>(new String[] {
                "Todas",
                "Leídas",
                "Por Bajo Stock",
                "Citas Pendientes",
                "Citas Próximas"
        });
        filterComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterComboBox.setPreferredSize(new Dimension(180, 30));
        filterComboBox.setBackground(Color.WHITE);
        filterComboBox.addActionListener(e -> loadAlertData((String) filterComboBox.getSelectedItem()));

        JButton applyFilterBtn = FormStyleManager.createPrimaryButton("Aplicar");
        applyFilterBtn.setPreferredSize(new Dimension(100, 30));
        applyFilterBtn.addActionListener(e -> loadAlertData((String) filterComboBox.getSelectedItem()));

        JButton clearFilterBtn = FormStyleManager.createSecondaryButton("Limpiar");
        clearFilterBtn.setPreferredSize(new Dimension(100, 30));
        clearFilterBtn.addActionListener(e -> {
            filterComboBox.setSelectedItem("Todas");
            loadAlertData("Todas");
        });

        filterPanel.add(filterLabel);
        filterPanel.add(filterComboBox);
        filterPanel.add(applyFilterBtn);
        filterPanel.add(clearFilterBtn);

        return filterPanel;
    }

    private JScrollPane createAlertTable() {
        String[] columnNames = {"Id", "Tipo", "Mensaje", "Generada", "Estado", "Referencia", "Acciones"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Solo la columna "Acciones" es editable
            }
        };

        alertTable = new JTable(tableModel);
        alertTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        alertTable.setRowHeight(30);
        alertTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        alertTable.getTableHeader().setBackground(FormStyleManager.SECONDARY_COLOR);
        alertTable.getTableHeader().setForeground(Color.WHITE);
        alertTable.setSelectionBackground(new Color(232, 240, 254));
        alertTable.setGridColor(new Color(225, 225, 225));
        alertTable.setShowGrid(true);
        alertTable.setShowVerticalLines(true);

        JScrollPane scrollPane = new JScrollPane(alertTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(FormStyleManager.SECONDARY_COLOR, 1));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        return scrollPane;
    }

    private void loadAlertData(String filterType) {
        tableModel.setRowCount(0); // Limpiar la tabla antes de cargar datos

        // Manejar alertas según el filtro seleccionado
        List<Alert> alertData = alertService.getAlertsByFilter(filterType);
        for (Alert alert : alertData) {
            JButton actionButton = FormStyleManager.createPrimaryButton("Resolver");
            actionButton.addActionListener(e -> resolveAlert(alert.getAlertId()));

            tableModel.addRow(new Object[] {
                    alert.getAlertId(),       // Id
                    alert.getAlertType(),     // Tipo
                    alert.getMessage(),       // Mensaje
                    alert.getGeneratedAt(),   // Generada
                    alert.getStatus(),        // Estado
                    alert.getReferenceType(), // Referencia
                    actionButton              // Acciones
            });
        }

        // Manejar citas próximas si aplica el filtro
        if ("Citas Próximas".equals(filterType)) {
            List<WorkshopAppointment> appointments = workshopService.getAllAppointments();
            for (WorkshopAppointment appointment : appointments) {
                if ("Scheduled".equals(appointment.getStatus())) {
                    JButton actionButton = FormStyleManager.createPrimaryButton("Ver Detalles");
                    actionButton.addActionListener(e -> showAppointmentDetails(appointment));

                    tableModel.addRow(new Object[] {
                            appointment.getAppointmentId(), // Id
                            "Cita",                        // Tipo
                            appointment.getVisitReason(),  // Mensaje
                            appointment.getScheduledDate(),// Generada (fecha)
                            appointment.getStatus(),       // Estado
                            appointment.getMotorcyclePlate(), // Referencia
                            actionButton                   // Acciones
                    });
                }
            }
        }
    }

    private void enableDoubleClickEvent() {
        alertTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Doble clic
                    int selectedRow = alertTable.getSelectedRow();
                    if (selectedRow != -1) {
                        String type = (String) tableModel.getValueAt(selectedRow, 1); // Tipo
                        if ("Cita".equals(type)) {
                            int appointmentId = (int) tableModel.getValueAt(selectedRow, 0); // Obtener ID de cita
                            showAppointmentDetails(workshopService.getAppointmentById(appointmentId));
                        } else {
                            int alertId = (int) tableModel.getValueAt(selectedRow, 0); // Obtener ID del alerta
                            showAlertDetails(alertId);
                        }
                    }
                }
            }
        });
    }

    private void showAlertDetails(int alertId) {
        Alert alert = alertService.getAlertById(alertId);
        if (alert != null) {
            String details = String.format(
                "ID: %d\nTipo: %s\nMensaje: %s\nGenerada: %s\nEstado: %s\nReferencia: %s",
                alert.getAlertId(),
                alert.getAlertType(),
                alert.getMessage(),
                alert.getGeneratedAt(),
                alert.getStatus(),
                alert.getReferenceType()
            );

            JOptionPane.showMessageDialog(this, details, "Detalles del Alerta", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró información para el alerta seleccionado.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAppointmentDetails(WorkshopAppointment appointment) {
        if (appointment != null) {
            String details = String.format(
                "ID: %d\nFecha: %s\nHora: %s\nMotivo: %s\nMoto: %s (%s)\nEstado: %s\nNotas: %s",
                appointment.getAppointmentId(),
                appointment.getScheduledDate(),
                appointment.getScheduledTime(),
                appointment.getVisitReason(),
                appointment.getMotorcycleDescription(),
                appointment.getMotorcyclePlate(),
                appointment.getStatus(),
                appointment.getNotes()
            );

            JOptionPane.showMessageDialog(this, details, "Detalles de la Cita", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró información para la cita seleccionada.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resolveAlert(int alertId) {
        if (alertService.resolveAlert(alertId)) {
            JOptionPane.showMessageDialog(this, "Alerta resuelta exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            loadAlertData((String) filterComboBox.getSelectedItem()); // Recargar datos
        } else {
            JOptionPane.showMessageDialog(this, "Error al resolver la alerta.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}