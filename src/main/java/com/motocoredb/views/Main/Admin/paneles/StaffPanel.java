package com.motocoredb.views.Main.Admin.paneles;

import com.motocoredb.models.Staff;
import com.motocoredb.services.StaffService;
import com.motocoredb.views.forms.staff.AddStaffForm;
import com.motocoredb.views.forms.staff.EditStaffForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class StaffPanel extends JPanel {
    private final StaffService staffService;
    private DefaultTableModel tableModel;
    private JTable staffTable;

    public StaffPanel(StaffService staffService) {
        this.staffService = staffService;
        initUI();
        loadStaff();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Barra de herramientas CRUD
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton refreshBtn = new JButton("Actualizar");
        refreshBtn.addActionListener(e -> loadStaff());

        JButton addBtn = new JButton("Nuevo Personal");
        addBtn.addActionListener(this::showAddStaffForm);

        JButton editBtn = new JButton("Editar Personal");
        editBtn.addActionListener(this::showEditStaffForm);

        JButton deleteBtn = new JButton("Eliminar Personal");
        deleteBtn.addActionListener(this::deleteStaff);

        toolBar.add(refreshBtn);
        toolBar.addSeparator();
        toolBar.add(addBtn);
        toolBar.add(editBtn);
        toolBar.add(deleteBtn);

        add(toolBar, BorderLayout.NORTH);

        // Tabla del personal
        tableModel = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Email", "Teléfono", "Puesto", "Estado"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        staffTable = new JTable(tableModel);
        staffTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        staffTable.getTableHeader().setReorderingAllowed(false);
        staffTable.setRowHeight(25);
        staffTable.setShowGrid(true);
        staffTable.setGridColor(Color.LIGHT_GRAY);

        JScrollPane scrollPane = new JScrollPane(staffTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadStaff() {
        try {
            List<Staff> staffList = staffService.getAllStaffs();
            tableModel.setRowCount(0);
            for (Staff s : staffList) {
                tableModel.addRow(new Object[]{
                    s.getStaffId(),
                    s.getFullName(),
                    s.getEmail(),
                    s.getPhone(),
                    s.getPosition(),
                    s.getStatus()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar personal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddStaffForm(ActionEvent e) {
        new AddStaffForm(staffService).setVisible(true);
    }

    private void showEditStaffForm(ActionEvent e) {
        int selectedRow = staffTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un miembro del personal para editar.");
            return;
        }

        int staffId = (int) tableModel.getValueAt(selectedRow, 0);
        Staff staff = staffService.getStaffById(staffId);
        if (staff == null) {
            JOptionPane.showMessageDialog(this, "No se pudo obtener el personal.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        new EditStaffForm(staffService, staff).setVisible(true);
    }

    private void deleteStaff(ActionEvent e) {
        int selectedRow = staffTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un miembro del personal para eliminar.");
            return;
        }

        int staffId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirmation = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar este miembro del personal?", "Confirmación", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            if (staffService.deactivateStaff(staffId)) {
                JOptionPane.showMessageDialog(this, "Personal eliminado exitosamente.");
                loadStaff();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el personal.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}