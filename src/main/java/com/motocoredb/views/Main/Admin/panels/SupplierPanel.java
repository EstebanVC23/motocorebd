package com.motocoredb.views.Main.Admin.panels;

import com.motocoredb.models.Supplier;
import com.motocoredb.services.SupplierService;
import com.motocoredb.views.forms.suppliers.AddSupplierForm;
import com.motocoredb.views.forms.suppliers.EditSupplierForm;
import com.motocoredb.views.forms.utils.FormStyleManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class SupplierPanel extends JPanel {
    private final SupplierService supplierService;
    private DefaultTableModel tableModel;
    private JTable suppliersTable;

    public SupplierPanel(SupplierService supplierService) {
        this.supplierService = supplierService;
        initUI();
        loadSuppliers();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(FormStyleManager.BACKGROUND_COLOR);

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

        JButton editBtn = FormStyleManager.createSecondaryButton("Editar Proveedor");
        editBtn.addActionListener(this::showEditSupplierForm);

        JButton deleteBtn = FormStyleManager.createSecondaryButton("Eliminar Proveedor");
        deleteBtn.addActionListener(this::deleteSupplier);

        toolBar.add(refreshBtn);
        toolBar.addSeparator();
        toolBar.add(addBtn);
        toolBar.add(editBtn);
        toolBar.add(deleteBtn);

        add(toolBar, BorderLayout.NORTH);

        // Tabla de proveedores
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Empresa", "NIT", "Contacto", "Teléfono", "Email", "Dirección", "Estado", "Creado"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        suppliersTable = new JTable(tableModel);
        suppliersTable.setRowHeight(25);
        suppliersTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        suppliersTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        suppliersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suppliersTable.setShowGrid(true);
        suppliersTable.setGridColor(Color.LIGHT_GRAY);

        JScrollPane scrollPane = new JScrollPane(suppliersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadSuppliers() {
        try {
            List<Supplier> suppliers = supplierService.getAllSuppliers();
            tableModel.setRowCount(0);
            for (Supplier s : suppliers) {
                tableModel.addRow(new Object[]{
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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar proveedores: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    private void showEditSupplierForm(ActionEvent e) {
        int selectedRow = suppliersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un proveedor para editar.");
            return;
        }

        int supplierId = (int) tableModel.getValueAt(selectedRow, 0);
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

        int supplierId = (int) tableModel.getValueAt(selectedRow, 0);
        Supplier supplier = supplierService.getSupplierById(supplierId);
        if (supplier == null) {
            JOptionPane.showMessageDialog(this, "No se pudo obtener el proveedor.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar este proveedor?",
                "Confirmación",
                JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            supplier.setStatus("Inactive"); // Eliminación lógica
            boolean success = supplierService.updateSupplier(supplier);
            if (success) {
                JOptionPane.showMessageDialog(this, "Proveedor eliminado exitosamente.");
                loadSuppliers();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el proveedor.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}