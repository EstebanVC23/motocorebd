package com.motocoredb.views.Main.Admin.panels;

import com.motocoredb.models.Customer;
import com.motocoredb.models.WorkshopAppointment;
import com.motocoredb.services.WorkshopService;
import com.motocoredb.views.forms.appointments.AddAppointmentForm;
import com.motocoredb.views.forms.appointments.EditAppointmentForm;
import com.motocoredb.views.utils.FormStyleManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;
import javax.swing.RowFilter;

public class AppointmentsPanel extends JPanel {
    private final WorkshopService workshopService;
    private DefaultTableModel tableModel;
    private JTable workshopTable;
    private TableRowSorter<DefaultTableModel> sorter;
    
    // Componentes de filtrado
    private JComboBox<String> customerFilterCombo;
    private JTextField plateFilterField;
    private JFormattedTextField fromDateField;
    private JFormattedTextField toDateField;
    private SimpleDateFormat dateFormat;

    public AppointmentsPanel(WorkshopService workshopService) {
        this.workshopService = workshopService;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        initUI();
        loadWorkshopAppointment();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(FormStyleManager.BACKGROUND_COLOR);

        // Panel superior que contendrá la barra de herramientas y el panel de filtros
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(FormStyleManager.BACKGROUND_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

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
        toolBar.add(deleteBtn);
        
        topPanel.add(toolBar, BorderLayout.NORTH);

        // Panel de filtros estilizado (similar al AlertPanel)
        JPanel filterPanel = createFilterPanel();
        topPanel.add(filterPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);

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
        workshopTable.setRowHeight(30);
        workshopTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        workshopTable.getTableHeader().setBackground(FormStyleManager.SECONDARY_COLOR);
        workshopTable.getTableHeader().setForeground(Color.WHITE);
        workshopTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        workshopTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        workshopTable.setSelectionBackground(new Color(232, 240, 254));
        workshopTable.setShowGrid(true);
        workshopTable.setGridColor(new Color(225, 225, 225));

        // Configurar el sorter para filtrado
        sorter = new TableRowSorter<>(tableModel);
        workshopTable.setRowSorter(sorter);

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
        scrollPane.setBorder(BorderFactory.createLineBorder(FormStyleManager.SECONDARY_COLOR, 1));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);
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

        // Filtro por cliente (ComboBox)
        JLabel customerLabel = FormStyleManager.createStyledLabel("Cliente:");
        customerLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        customerFilterCombo = new JComboBox<>();
        customerFilterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        customerFilterCombo.setPreferredSize(new Dimension(180, 30));
        customerFilterCombo.setBackground(Color.WHITE);
        customerFilterCombo.addItem("Todos");

        // Filtro por placa
        JLabel plateLabel = FormStyleManager.createStyledLabel("Placa:");
        plateLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        plateFilterField = new JTextField(10);
        plateFilterField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        plateFilterField.setPreferredSize(new Dimension(120, 30));

        // Filtro por fecha - desde
        JLabel fromDateLabel = FormStyleManager.createStyledLabel("Desde:");
        fromDateLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        fromDateField = new JFormattedTextField(dateFormat);
        fromDateField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fromDateField.setPreferredSize(new Dimension(120, 30));
        fromDateField.setToolTipText("Formato: AAAA-MM-DD");

        // Filtro por fecha - hasta
        JLabel toDateLabel = FormStyleManager.createStyledLabel("Hasta:");
        toDateLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        toDateField = new JFormattedTextField(dateFormat);
        toDateField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        toDateField.setPreferredSize(new Dimension(120, 30));
        toDateField.setToolTipText("Formato: AAAA-MM-DD");

        // Botones de filtro
        JButton applyFilterBtn = FormStyleManager.createPrimaryButton("Aplicar");
        applyFilterBtn.setPreferredSize(new Dimension(100, 30));
        applyFilterBtn.addActionListener(e -> applyFilters());

        JButton clearFilterBtn = FormStyleManager.createSecondaryButton("Limpiar");
        clearFilterBtn.setPreferredSize(new Dimension(100, 30));
        clearFilterBtn.addActionListener(e -> clearFilters());

        // Añadir componentes al panel de filtros
        filterPanel.add(customerLabel);
        filterPanel.add(customerFilterCombo);
        filterPanel.add(plateLabel);
        filterPanel.add(plateFilterField);
        filterPanel.add(fromDateLabel);
        filterPanel.add(fromDateField);
        filterPanel.add(toDateLabel);
        filterPanel.add(toDateField);
        filterPanel.add(applyFilterBtn);
        filterPanel.add(clearFilterBtn);

        return filterPanel;
    }

    private void loadWorkshopAppointment() {
        try {
            List<WorkshopAppointment> appointments = workshopService.getAllAppointments();
            List<Customer> customers = workshopService.getAllCustomers();
            tableModel.setRowCount(0);
            
            // Limpiar y volver a llenar el combo de clientes
            customerFilterCombo.removeAllItems();
            customerFilterCombo.addItem("Todos");
            
            for (WorkshopAppointment a : appointments) {
                // Buscar el nombre del cliente usando customerId
                String customerName = customers.stream()
                        .filter(c -> c.getCustomerId() == a.getCustomerId())
                        .map(Customer::getNameOrCompany)
                        .findFirst()
                        .orElse("Cliente desconocido");
    
                // Simulación para obtener el nombre del usuario
                String userName = "Usuario " + a.getUserId();
    
                tableModel.addRow(new Object[]{
                        a.getAppointmentId(),
                        customerName,
                        a.getMotorcycleDescription(),
                        a.getMotorcyclePlate(),
                        a.getScheduledDate(),
                        a.getScheduledTime(),
                        userName,
                        a.getNotes()
                });
            }
            
            // Llenar el combobox de clientes sin duplicados
            customers.forEach(customer -> {
                boolean exists = false;
                for (int i = 0; i < customerFilterCombo.getItemCount(); i++) {
                    if (customer.getNameOrCompany().equals(customerFilterCombo.getItemAt(i))) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    customerFilterCombo.addItem(customer.getNameOrCompany());
                }
            });
            
            // Aplicar filtros si hay alguno activo
            applyFilters();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar citas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilters() {
        ArrayList<RowFilter<Object, Object>> filters = new ArrayList<>();
        
        // Filtro de cliente
        String customerFilter = (String) customerFilterCombo.getSelectedItem();
        if (customerFilter != null && !customerFilter.equals("Todos")) {
            filters.add(RowFilter.regexFilter("^" + Pattern.quote(customerFilter) + "$", 1));
        }
        
        // Filtro de placa
        String plateFilter = plateFilterField.getText().trim();
        if (!plateFilter.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(plateFilter), 3));
        }
        
        // Filtro de fecha - desde
        String fromDateStr = fromDateField.getText().trim();
        Date fromDate = null;
        if (!fromDateStr.isEmpty()) {
            try {
                fromDate = dateFormat.parse(fromDateStr);
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, 
                    "Formato de fecha 'Desde' incorrecto. Use AAAA-MM-DD", 
                    "Error de filtro", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // Filtro de fecha - hasta
        String toDateStr = toDateField.getText().trim();
        Date toDate = null;
        if (!toDateStr.isEmpty()) {
            try {
                toDate = dateFormat.parse(toDateStr);
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, 
                    "Formato de fecha 'Hasta' incorrecto. Use AAAA-MM-DD", 
                    "Error de filtro", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // Si tenemos fechas válidas, añadimos el filtro de rango de fechas
        final Date finalFromDate = fromDate;
        final Date finalToDate = toDate;
        
        if (finalFromDate != null || finalToDate != null) {
            RowFilter<Object, Object> dateFilter = new RowFilter<Object, Object>() {
                @Override
                public boolean include(Entry<? extends Object, ? extends Object> entry) {
                    String dateStr = (String) entry.getValue(4); // Columna de fecha
                    try {
                        Date rowDate = dateFormat.parse(dateStr);
                        
                        // Comprobar si está dentro del rango
                        if (finalFromDate != null && rowDate.before(finalFromDate)) {
                            return false;
                        }
                        
                        if (finalToDate != null && rowDate.after(finalToDate)) {
                            return false;
                        }
                        
                        return true;
                    } catch (ParseException e) {
                        return false;
                    }
                }
            };
            
            filters.add(dateFilter);
        }
        
        // Aplicar todos los filtros combinados
        if (!filters.isEmpty()) {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        } else {
            sorter.setRowFilter(null);
        }
    }
    
    private void clearFilters() {
        customerFilterCombo.setSelectedItem("Todos");
        plateFilterField.setText("");
        fromDateField.setText("");
        toDateField.setText("");
        sorter.setRowFilter(null);
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
                loadWorkshopAppointment();
            }
        });
    }

    private void editAppointment() {
        int selectedRow = workshopTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita para editar.");
            return;
        }
    
        // Convertir índice de vista a índice de modelo
        int modelRow = workshopTable.convertRowIndexToModel(selectedRow);
        int appointmentId = (int) tableModel.getValueAt(modelRow, 0);
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
                loadWorkshopAppointment();
            }
        });
    }

    private void deleteAppointment() {
        int selectedRow = workshopTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita para cancelar.");
            return;
        }

        // Convertir índice de vista a índice de modelo
        int modelRow = workshopTable.convertRowIndexToModel(selectedRow);
        int appointmentId = (int) tableModel.getValueAt(modelRow, 0);

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