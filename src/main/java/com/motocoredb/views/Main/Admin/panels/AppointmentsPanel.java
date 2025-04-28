package com.motocoredb.views.Main.Admin.panels;

import com.motocoredb.models.Customer;
import com.motocoredb.models.WorkshopAppointment;
import com.motocoredb.services.WorkshopService;
import com.motocoredb.views.forms.appointments.AddAppointmentForm;
import com.motocoredb.views.forms.appointments.EditAppointmentForm;
import com.motocoredb.views.utils.FormStyleManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class AppointmentsPanel extends JPanel {
    private final WorkshopService workshopService;
    private DefaultTableModel tableModel;
    private JTable workshopTable;

    public AppointmentsPanel(WorkshopService workshopService) {
        this.workshopService = workshopService;
        initUI();
        loadWorkshopAppointment();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(FormStyleManager.BACKGROUND_COLOR);

        // Barra de herramientas CRUD
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(FormStyleManager.BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Gestión de Citas");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(FormStyleManager.PRIMARY_COLOR);
        toolBar.add(titleLabel);
        toolBar.addSeparator();

        JButton refreshBtn = FormStyleManager.createSecondaryButton("Actualizar");
        refreshBtn.addActionListener(e -> loadWorkshopAppointment());

        JButton addBtn = FormStyleManager.createPrimaryButton("Nueva Cita");
        addBtn.addActionListener(e -> showAddAppointmentForm());

        JButton deleteBtn = FormStyleManager.createSecondaryButton("Cancelar Cita");
        deleteBtn.addActionListener(e -> deleteAppointment());

        toolBar.add(refreshBtn);
        toolBar.addSeparator();
        toolBar.add(addBtn);
        toolBar.add(deleteBtn); // Eliminamos el botón de editar
        add(toolBar, BorderLayout.NORTH);

        // Tabla de citas
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Cliente", "Moto", "Placa", "Fecha", "Hora", "Usuario", "Notas"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        workshopTable = new JTable(tableModel);
        workshopTable.setRowHeight(25);
        workshopTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        workshopTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        workshopTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        workshopTable.setShowGrid(true);
        workshopTable.setGridColor(Color.LIGHT_GRAY);

        // Doble clic para editar
        workshopTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editAppointment();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(workshopTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadWorkshopAppointment() {
        try {
            List<WorkshopAppointment> appointments = workshopService.getAllAppointments();
            List<Customer> customers = workshopService.getAllCustomers(); // Obtener lista de clientes
            tableModel.setRowCount(0);
    
            for (WorkshopAppointment a : appointments) {
                // Buscar el nombre del cliente usando customerId
                String customerName = customers.stream()
                        .filter(c -> c.getCustomerId() == a.getCustomerId())
                        .map(Customer::getNameOrCompany)
                        .findFirst()
                        .orElse("Cliente desconocido");
    
                // Simulación para obtener el nombre del usuario (reemplazar según lógica específica)
                String userName = "Usuario " + a.getUserId(); // Puedes adaptar esto para obtener el nombre real del usuario desde un servicio o lista
    
                tableModel.addRow(new Object[]{
                        a.getAppointmentId(),
                        customerName, // Mostrar el nombre del cliente en lugar del ID
                        a.getMotorcycleDescription(),
                        a.getMotorcyclePlate(),
                        a.getScheduledDate(),
                        a.getScheduledTime(),
                        userName, // Mostrar el nombre del usuario si está disponible
                        a.getNotes()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar citas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddAppointmentForm() {
        // Obtener la lista completa de clientes desde el servicio
        List<Customer> customers = workshopService.getAllCustomers();
    
        // Crear el formulario con la lista de clientes
        AddAppointmentForm form = new AddAppointmentForm(workshopService, customers);
        form.setVisible(true);
        form.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                loadWorkshopAppointment(); // Recarga las citas al cerrar el formulario
            }
        });
    }

    private void editAppointment() {
        int selectedRow = workshopTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita para editar.");
            return;
        }
    
        // Obtener el ID de la cita seleccionada
        int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);
        WorkshopAppointment appointment = workshopService.getAppointmentById(appointmentId);
        if (appointment == null) {
            JOptionPane.showMessageDialog(this, "No se pudo obtener la cita.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // Obtener la lista completa de clientes desde el servicio
        List<Customer> customers = workshopService.getAllCustomers();
    
        // Crear el formulario de edición y pasar la lista de clientes y la cita actual
        EditAppointmentForm form = new EditAppointmentForm(workshopService, appointment, customers);
        form.setVisible(true);
        form.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                loadWorkshopAppointment(); // Recarga las citas al cerrar el formulario
            }
        });
    }

    private void deleteAppointment() {
        int selectedRow = workshopTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita para cancelar.");
            return;
        }

        int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);

        int confirmation = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea cancelar esta cita?",
                "Confirmar Cancelación",
                JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            boolean success = workshopService.updateAppointmentStatus(appointmentId, "Cancelled");
            if (success) {
                JOptionPane.showMessageDialog(this, "Cita cancelada correctamente.");
                loadWorkshopAppointment();
            } else {
                JOptionPane.showMessageDialog(this, "Error al cancelar la cita.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}