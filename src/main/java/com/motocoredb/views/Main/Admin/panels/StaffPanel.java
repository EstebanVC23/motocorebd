package com.motocoredb.views.Main.Admin.panels;

import com.motocoredb.models.Staff;
import com.motocoredb.services.StaffService;
import com.motocoredb.views.forms.staff.AddStaffForm;
import com.motocoredb.views.forms.staff.EditStaffForm;
import com.motocoredb.views.forms.utils.FormStyleManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
        setBackground(FormStyleManager.BACKGROUND_COLOR);

        // Barra de herramientas CRUD
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(FormStyleManager.BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Gestión del Personal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(FormStyleManager.PRIMARY_COLOR);
        toolBar.add(titleLabel);
        toolBar.addSeparator();

        JButton refreshBtn = FormStyleManager.createSecondaryButton("Actualizar");
        refreshBtn.addActionListener(e -> loadStaff());

        JButton addBtn = FormStyleManager.createPrimaryButton("Nuevo Personal");
        addBtn.addActionListener(this::showAddStaffForm); // Referencia corregida

        JButton deleteBtn = FormStyleManager.createSecondaryButton("Eliminar Personal");
        deleteBtn.addActionListener(this::deleteStaff); // Referencia corregida

        toolBar.add(refreshBtn);
        toolBar.addSeparator();
        toolBar.add(addBtn);
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
        staffTable.setRowHeight(25);
        staffTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        staffTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        staffTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        staffTable.setShowGrid(true);
        staffTable.setGridColor(Color.LIGHT_GRAY);

        // Doble clic para editar
        staffTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editStaff();
                }
            }
        });

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
        AddStaffForm form = new AddStaffForm(staffService);
        form.setVisible(true);
        form.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                loadStaff();
            }
        });
    }

    private void editStaff() {
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

        EditStaffForm form = new EditStaffForm(staffService, staff);
        form.setVisible(true);
        form.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                loadStaff();
            }
        });
    }

    private void deleteStaff(ActionEvent e) {
        int selectedRow = staffTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un miembro del personal para eliminar.");
            return;
        }

        int staffId = (int) tableModel.getValueAt(selectedRow, 0);

        int confirmation = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar este miembro del personal?",
                "Confirmación",
                JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            boolean success = staffService.deactivateStaff(staffId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Personal eliminado exitosamente.");
                loadStaff();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el personal.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}