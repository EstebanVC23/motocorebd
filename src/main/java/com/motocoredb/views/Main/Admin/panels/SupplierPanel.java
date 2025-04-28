package com.motocoredb.views.Main.Admin.panels;

import com.motocoredb.models.Supplier;
import com.motocoredb.services.SupplierService;
import com.motocoredb.views.forms.suppliers.AddSupplierForm;
import com.motocoredb.views.forms.suppliers.EditSupplierForm;
import com.motocoredb.views.utils.FormStyleManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SupplierPanel extends JPanel {
    private final SupplierService supplierService;
    private DefaultTableModel tableModel;
    private JTable suppliersTable;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTextField companyFilterField;
    private JTextField nitFilterField;
    private JTextField contactFilterField;
    private JComboBox<String> statusFilterCombo;

    public SupplierPanel(SupplierService supplierService) {
        this.supplierService = supplierService;
        initUI();
        loadSuppliers();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(FormStyleManager.BACKGROUND_COLOR);

        // Panel superior con título y barra de herramientas
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(FormStyleManager.BACKGROUND_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Barra de herramientas CRUD
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(FormStyleManager.BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Gestión de Proveedores");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(FormStyleManager.PRIMARY_COLOR);
        toolBar.add(titleLabel);
        toolBar.addSeparator();

        JButton refreshBtn = FormStyleManager.createSecondaryButton("Actualizar");
        refreshBtn.addActionListener(e -> loadSuppliers());

        JButton addBtn = FormStyleManager.createPrimaryButton("Nuevo Proveedor");
        addBtn.addActionListener(this::showAddSupplierForm);

        JButton deleteBtn = FormStyleManager.createSecondaryButton("Eliminar Proveedor");
        deleteBtn.addActionListener(this::deleteSupplier);

        toolBar.add(refreshBtn);
        toolBar.addSeparator();
        toolBar.add(addBtn);
        toolBar.add(deleteBtn);
        
        topPanel.add(toolBar, BorderLayout.NORTH);

        // Panel de filtros mejorado
        JPanel filterPanel = createFilterPanel();
        topPanel.add(filterPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);

        // Tabla de proveedores
        tableModel = new DefaultTableModel(
                new Object[] { "ID", "Empresa", "NIT", "Contacto", "Teléfono", "Email", "Dirección", "Estado",
                        "Creado" },
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        suppliersTable = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        suppliersTable.setRowSorter(rowSorter);
        suppliersTable.setRowHeight(30);
        suppliersTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        suppliersTable.getTableHeader().setBackground(FormStyleManager.SECONDARY_COLOR);
        suppliersTable.getTableHeader().setForeground(Color.WHITE);
        suppliersTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        suppliersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suppliersTable.setShowGrid(true);
        suppliersTable.setGridColor(new Color(225, 225, 225));
        suppliersTable.setSelectionBackground(new Color(232, 240, 254));

        // Doble clic para editar
        suppliersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSupplier();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(suppliersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(FormStyleManager.SECONDARY_COLOR, 1));
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
        
        // Empresa
        JLabel companyLabel = new JLabel("Empresa:");
        companyLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        companyFilterField = new JTextField(15);
        companyFilterField.setPreferredSize(new Dimension(150, 30));
        companyFilterField.putClientProperty("JTextField.placeholderText", "Buscar por empresa...");
        
        // NIT
        JLabel nitLabel = new JLabel("NIT:");
        nitLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nitFilterField = new JTextField(15);
        nitFilterField.setPreferredSize(new Dimension(150, 30));
        nitFilterField.putClientProperty("JTextField.placeholderText", "Buscar por NIT...");
        
        // Contacto
        JLabel contactLabel = new JLabel("Contacto:");
        contactLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        contactFilterField = new JTextField(15);
        contactFilterField.setPreferredSize(new Dimension(150, 30));
        contactFilterField.putClientProperty("JTextField.placeholderText", "Buscar por contacto...");
        
        // Estado
        JLabel statusLabel = new JLabel("Estado:");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusFilterCombo = new JComboBox<>(new String[]{"Todos", "Active", "Inactive"});
        statusFilterCombo.setPreferredSize(new Dimension(150, 30));
        statusFilterCombo.setBackground(Color.WHITE);
        
        // Botones
        JButton applyFilterBtn = FormStyleManager.createPrimaryButton("Aplicar");
        applyFilterBtn.setPreferredSize(new Dimension(100, 30));
        applyFilterBtn.addActionListener(e -> applyFilters());
        
        JButton clearFilterBtn = FormStyleManager.createSecondaryButton("Limpiar");
        clearFilterBtn.setPreferredSize(new Dimension(100, 30));
        clearFilterBtn.addActionListener(e -> clearFilters());
        
        // Primera fila de filtros
        JPanel filterRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterRow1.setBackground(FormStyleManager.PANEL_COLOR);
        filterRow1.add(companyLabel);
        filterRow1.add(companyFilterField);
        filterRow1.add(nitLabel);
        filterRow1.add(nitFilterField);
        
        // Segunda fila de filtros
        JPanel filterRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterRow2.setBackground(FormStyleManager.PANEL_COLOR);
        filterRow2.add(contactLabel);
        filterRow2.add(contactFilterField);
        filterRow2.add(statusLabel);
        filterRow2.add(statusFilterCombo);
        
        // Fila de botones
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonRow.setBackground(FormStyleManager.PANEL_COLOR);
        buttonRow.add(clearFilterBtn);
        buttonRow.add(applyFilterBtn);
        
        // Añadir todo al panel principal
        filterPanel.setLayout(new BorderLayout());
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(FormStyleManager.PANEL_COLOR);
        centerPanel.add(filterRow1);
        centerPanel.add(filterRow2);
        
        filterPanel.add(centerPanel, BorderLayout.CENTER);
        filterPanel.add(buttonRow, BorderLayout.SOUTH);
        
        return filterPanel;
    }

    private void applyFilters() {
        RowFilter<DefaultTableModel, Object> companyFilter = null;
        RowFilter<DefaultTableModel, Object> nitFilter = null;
        RowFilter<DefaultTableModel, Object> contactFilter = null;
        RowFilter<DefaultTableModel, Object> statusFilter = null;
        
        // Lista para almacenar todos los filtros
        List<RowFilter<DefaultTableModel, Object>> filters = new ArrayList<>();
        
        // Filtro por empresa (columna 1)
        if (!companyFilterField.getText().trim().isEmpty()) {
            companyFilter = RowFilter.regexFilter("(?i)" + Pattern.quote(companyFilterField.getText().trim()), 1);
            filters.add(companyFilter);
        }
        
        // Filtro por NIT (columna 2)
        if (!nitFilterField.getText().trim().isEmpty()) {
            nitFilter = RowFilter.regexFilter("(?i)" + Pattern.quote(nitFilterField.getText().trim()), 2);
            filters.add(nitFilter);
        }
        
        // Filtro por contacto (columna 3)
        if (!contactFilterField.getText().trim().isEmpty()) {
            contactFilter = RowFilter.regexFilter("(?i)" + Pattern.quote(contactFilterField.getText().trim()), 3);
            filters.add(contactFilter);
        }
        
        // Filtro por estado (columna 7)
        if (statusFilterCombo.getSelectedIndex() > 0) {
            String status = (String) statusFilterCombo.getSelectedItem();
            statusFilter = RowFilter.regexFilter("^" + status + "$", 7);
            filters.add(statusFilter);
        }
        
        // Aplicar filtros
        if (!filters.isEmpty()) {
            rowSorter.setRowFilter(RowFilter.andFilter(filters));
        } else {
            rowSorter.setRowFilter(null);
        }
    }

    private void clearFilters() {
        companyFilterField.setText("");
        nitFilterField.setText("");
        contactFilterField.setText("");
        statusFilterCombo.setSelectedIndex(0);
        rowSorter.setRowFilter(null);
    }

    private void loadSuppliers() {
        try {
            List<Supplier> suppliers = supplierService.getAllSuppliers();
            tableModel.setRowCount(0);
            for (Supplier s : suppliers) {
                tableModel.addRow(new Object[] {
                        s.getSupplierId(),
                        s.getCompanyName(),
                        s.getTaxId(),
                        s.getContactPerson(),
                        s.getContactPhone(),
                        s.getContactEmail(),
                        s.getAddress(),
                        s.getStatus(),
                        s.getCreatedAt()
                });
            }
            // Limpiar filtros al cargar nuevos datos
            clearFilters();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar proveedores: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddSupplierForm(ActionEvent e) {
        AddSupplierForm form = new AddSupplierForm(supplierService);
        form.setVisible(true);
        form.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                loadSuppliers();
            }
        });
    }

    private void editSupplier() {
        int selectedRow = suppliersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un proveedor para editar.");
            return;
        }

        // Convertir el índice de fila del view al modelo debido al sorter
        int modelRow = suppliersTable.convertRowIndexToModel(selectedRow);
        int supplierId = (int) tableModel.getValueAt(modelRow, 0);
        
        Supplier supplier = supplierService.getSupplierById(supplierId);
        if (supplier == null) {
            JOptionPane.showMessageDialog(this, "No se pudo obtener el proveedor.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        EditSupplierForm form = new EditSupplierForm(supplierService, supplier);
        form.setVisible(true);
        form.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                loadSuppliers();
            }
        });
    }

    private void deleteSupplier(ActionEvent e) {
        int selectedRow = suppliersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un proveedor para eliminar.");
            return;
        }

        // Convertir el índice de fila del view al modelo debido al sorter
        int modelRow = suppliersTable.convertRowIndexToModel(selectedRow);
        int supplierId = (int) tableModel.getValueAt(modelRow, 0);
        
        Supplier supplier = supplierService.getSupplierById(supplierId);
        if (supplier == null) {
            JOptionPane.showMessageDialog(this, "No se pudo obtener el proveedor.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar este proveedor?",
                "Confirmación", JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            supplier.setStatus("Inactive"); // Eliminación lógica
            boolean success = supplierService.updateSupplier(supplier);
            if (success) {
                JOptionPane.showMessageDialog(this, "Proveedor eliminado exitosamente.");
                loadSuppliers(); // Recargar la lista de proveedores tras la eliminación
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el proveedor.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}