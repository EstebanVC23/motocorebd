package com.motocoredb.views.Main.Admin.paneles;

import com.motocoredb.models.Supplier;
import com.motocoredb.services.SupplierService;
import com.motocoredb.views.forms.suppliers.AddSupplierForm;
import com.motocoredb.views.forms.suppliers.EditSupplierForm;

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

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton refreshBtn = new JButton("Actualizar");
        refreshBtn.addActionListener(e -> loadSuppliers());

        JButton addBtn = new JButton("Nuevo Proveedor");
        addBtn.addActionListener(this::showAddSupplierForm);

        JButton editBtn = new JButton("Editar Proveedor");
        editBtn.addActionListener(this::showEditSupplierForm);

        JButton deleteBtn = new JButton("Eliminar Proveedor");
        deleteBtn.addActionListener(this::deleteSupplier);

        toolBar.add(refreshBtn);
        toolBar.addSeparator();
        toolBar.add(addBtn);
        toolBar.add(editBtn);
        toolBar.add(deleteBtn);

        add(toolBar, BorderLayout.NORTH);

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
        suppliersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suppliersTable.getTableHeader().setReorderingAllowed(false);
        suppliersTable.setRowHeight(25);
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
        new AddSupplierForm(supplierService).setVisible(true);
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

        new EditSupplierForm(supplierService, supplier).setVisible(true);
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

        int confirmation = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar este proveedor?", "Confirmación", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            supplier.setStatus("Inactive");
            if (supplierService.updateSupplier(supplier)) {
                JOptionPane.showMessageDialog(this, "Proveedor eliminado exitosamente.");
                loadSuppliers();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el proveedor.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

