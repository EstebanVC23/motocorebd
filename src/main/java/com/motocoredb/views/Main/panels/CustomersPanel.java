package com.motocoredb.views.Main.panels;

import com.motocoredb.models.Customer;
import com.motocoredb.services.CustomerService;
import com.motocoredb.views.forms.customers.EditCustomerForm;
import com.motocoredb.views.utils.FormStyleManager;
import com.motocoredb.views.forms.customers.AddCustomerForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import javax.swing.RowFilter;

public class CustomersPanel extends JPanel {
    private final CustomerService customerService;
    private DefaultTableModel tableModel;
    private JTable customersTable;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField idFilterField;
    private JTextField nameFilterField;
    private JComboBox<String> statusFilterCombo;

    public CustomersPanel(CustomerService customerService) {
        this.customerService = customerService;
        initUI();
        loadCustomers();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(FormStyleManager.BACKGROUND_COLOR);

        // Panel superior con título y barra de herramientas CRUD
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(FormStyleManager.BACKGROUND_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        // Barra de herramientas CRUD
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(FormStyleManager.BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Gestión de Clientes");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(FormStyleManager.PRIMARY_COLOR);
        
        toolBar.add(titleLabel);
        toolBar.addSeparator();

        JButton refreshBtn = FormStyleManager.createSecondaryButton("Actualizar");
        refreshBtn.addActionListener(e -> loadCustomers());

        JButton addBtn = FormStyleManager.createPrimaryButton("Nuevo Cliente");
        addBtn.addActionListener(e -> showAddCustomerForm());

        JButton deleteBtn = FormStyleManager.createSecondaryButton("Eliminar Cliente");
        deleteBtn.addActionListener(e -> deleteCustomer());

        toolBar.add(refreshBtn);
        toolBar.addSeparator();
        toolBar.add(addBtn);
        toolBar.add(deleteBtn);
        topPanel.add(toolBar, BorderLayout.NORTH);

        // Panel de filtros mejorado con el mismo estilo de AlertPanel
        JPanel filterPanel = createFilterPanel();
        topPanel.add(filterPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);

        // Tabla de clientes
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Email", "Teléfono", "Dirección", "Estado"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        customersTable = new JTable(tableModel);
        customersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customersTable.getTableHeader().setReorderingAllowed(false);
        customersTable.setRowHeight(25);
        customersTable.setShowGrid(true);
        customersTable.setGridColor(Color.LIGHT_GRAY);
        customersTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        customersTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        customersTable.getTableHeader().setBackground(FormStyleManager.SECONDARY_COLOR);
        customersTable.getTableHeader().setForeground(Color.WHITE);

        // Configurar el sorter para filtrado
        sorter = new TableRowSorter<>(tableModel);
        customersTable.setRowSorter(sorter);

        // Doble clic para editar
        customersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editCustomer();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(customersTable);
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

        // Filtro por ID
        JLabel idLabel = FormStyleManager.createStyledLabel("ID:");
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        filterPanel.add(idLabel);
        
        idFilterField = new JTextField(5);
        idFilterField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        idFilterField.setPreferredSize(new Dimension(80, 30));
        filterPanel.add(idFilterField);

        // Filtro por Nombre
        JLabel nameLabel = FormStyleManager.createStyledLabel("Nombre:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        filterPanel.add(nameLabel);
        
        nameFilterField = new JTextField(15);
        nameFilterField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        nameFilterField.setPreferredSize(new Dimension(150, 30));
        filterPanel.add(nameFilterField);

        // Filtro por Estado
        JLabel statusLabel = FormStyleManager.createStyledLabel("Estado:");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        filterPanel.add(statusLabel);
        
        statusFilterCombo = new JComboBox<>(new String[]{"Todos", "Activo", "Inactivo"});
        statusFilterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusFilterCombo.setPreferredSize(new Dimension(120, 30));
        statusFilterCombo.setBackground(Color.WHITE);
        filterPanel.add(statusFilterCombo);

        // Botones para aplicar filtros
        JButton applyFilterBtn = FormStyleManager.createPrimaryButton("Aplicar");
        applyFilterBtn.setPreferredSize(new Dimension(100, 30));
        applyFilterBtn.addActionListener(e -> applyFilters());
        filterPanel.add(applyFilterBtn);

        // Botón para limpiar filtros
        JButton clearFilterBtn = FormStyleManager.createSecondaryButton("Limpiar");
        clearFilterBtn.setPreferredSize(new Dimension(100, 30));
        clearFilterBtn.addActionListener(e -> clearFilters());
        filterPanel.add(clearFilterBtn);

        return filterPanel;
    }

    private void loadCustomers() {
        try {
            List<Customer> customers = customerService.getAllCustomers();
            tableModel.setRowCount(0);
            for (Customer c : customers) {
                tableModel.addRow(new Object[]{
                        c.getCustomerId(),
                        c.getNameOrCompany(),
                        c.getEmail(),
                        c.getPhone(),
                        c.getAddress(),
                        c.getStatus()
                });
            }
            // Después de cargar los datos, aplicamos los filtros si hay alguno
            applyFilters();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar clientes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilters() {
        ArrayList<RowFilter<Object, Object>> filters = new ArrayList<>();
        
        // Filtro de ID
        String idFilter = idFilterField.getText().trim();
        if (!idFilter.isEmpty()) {
            try {
                int id = Integer.parseInt(idFilter);
                filters.add(RowFilter.numberFilter(RowFilter.ComparisonType.EQUAL, id, 0));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "El ID debe ser un número válido", "Error de filtro", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // Filtro de Nombre
        String nameFilter = nameFilterField.getText().trim();
        if (!nameFilter.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(nameFilter), 1));
        }
        
        // Filtro de Estado
        String statusFilter = (String) statusFilterCombo.getSelectedItem();
        if (statusFilter != null && !statusFilter.equals("Todos")) {
            filters.add(RowFilter.regexFilter("^" + statusFilter + "$", 5));
        }
        
        // Aplicar filtros combinados
        if (!filters.isEmpty()) {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        } else {
            sorter.setRowFilter(null);
        }
    }
    
    private void clearFilters() {
        idFilterField.setText("");
        nameFilterField.setText("");
        statusFilterCombo.setSelectedIndex(0);
        sorter.setRowFilter(null);
    }

    private void showAddCustomerForm() {
        AddCustomerForm form = new AddCustomerForm(customerService);
        form.setVisible(true);
        form.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                loadCustomers();
            }
        });
    }

    private void editCustomer() {
        int selectedRow = customersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para editar.");
            return;
        }

        // Convertir índice de vista a índice de modelo
        int modelRow = customersTable.convertRowIndexToModel(selectedRow);
        int customerId = (int) tableModel.getValueAt(modelRow, 0);
        Customer customer = customerService.getCustomerById(customerId);
        if (customer == null) {
            JOptionPane.showMessageDialog(this, "No se pudo obtener el cliente.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        EditCustomerForm form = new EditCustomerForm(customerService, customer);
        form.setVisible(true);
        form.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                loadCustomers();
            }
        });
    }

    private void deleteCustomer() {
        int selectedRow = customersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para eliminar.");
            return;
        }

        // Convertir índice de vista a índice de modelo
        int modelRow = customersTable.convertRowIndexToModel(selectedRow);
        int customerId = (int) tableModel.getValueAt(modelRow, 0);

        int confirmation = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar este cliente?",
                "Confirmación",
                JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            boolean success = customerService.deactivateCustomer(customerId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Cliente eliminado exitosamente.");
                loadCustomers();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el cliente.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}