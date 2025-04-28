package com.motocoredb.views.Main.panels;

import com.motocoredb.models.Staff;
import com.motocoredb.services.StaffService;
import com.motocoredb.views.forms.staff.AddStaffForm;
import com.motocoredb.views.forms.staff.EditStaffForm;
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
import java.util.stream.Collectors;

public class StaffPanel extends JPanel {
    private final StaffService staffService;
    private DefaultTableModel tableModel;
    private JTable staffTable;
    private TableRowSorter<DefaultTableModel> sorter;
    private List<Staff> allStaff;
    
    // Filter components
    private JTextField idFilterField;
    private JTextField nameFilterField;
    private JComboBox<String> positionFilterCombo;
    private JComboBox<String> statusFilterCombo;
    private JButton applyFiltersBtn;
    private JButton clearFiltersBtn;

    public StaffPanel(StaffService staffService) {
        this.staffService = staffService;
        this.allStaff = new ArrayList<>();
        initUI();
        loadStaff();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(FormStyleManager.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel superior con título y botones
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(FormStyleManager.BACKGROUND_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        
        JLabel titleLabel = new JLabel("Gestión del Personal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(FormStyleManager.PRIMARY_COLOR);
        
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(FormStyleManager.BACKGROUND_COLOR);
        toolBar.add(titleLabel);
        toolBar.addSeparator();
        
        JButton refreshBtn = FormStyleManager.createSecondaryButton("Actualizar");
        refreshBtn.addActionListener(e -> loadStaff());
        
        JButton addBtn = FormStyleManager.createPrimaryButton("Nuevo Personal");
        addBtn.addActionListener(this::showAddStaffForm);
        
        JButton deleteBtn = FormStyleManager.createSecondaryButton("Eliminar Personal");
        deleteBtn.addActionListener(this::deleteStaff);
        
        toolBar.add(refreshBtn);
        toolBar.addSeparator();
        toolBar.add(addBtn);
        toolBar.add(deleteBtn);
        
        topPanel.add(toolBar, BorderLayout.NORTH);
        
        // Panel de filtros mejorado
        JPanel filterPanel = createFilterPanel();
        topPanel.add(filterPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);

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
        staffTable.setRowHeight(30);
        staffTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        staffTable.getTableHeader().setBackground(FormStyleManager.SECONDARY_COLOR);
        staffTable.getTableHeader().setForeground(Color.WHITE);
        staffTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        staffTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        staffTable.setSelectionBackground(new Color(232, 240, 254));
        staffTable.setGridColor(new Color(225, 225, 225));
        staffTable.setShowGrid(true);
        staffTable.setShowVerticalLines(true);

        // Doble clic para editar
        staffTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editStaff();
                }
            }
        });

        sorter = new TableRowSorter<>(tableModel);
        staffTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(staffTable);
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

        // ID Filter
        JLabel idLabel = FormStyleManager.createStyledLabel("ID:");
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        idFilterField = new JTextField(5);
        idFilterField.setPreferredSize(new Dimension(80, 30));
        idFilterField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // Name Filter
        JLabel nameLabel = FormStyleManager.createStyledLabel("Nombre:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameFilterField = new JTextField(12);
        nameFilterField.setPreferredSize(new Dimension(150, 30));
        nameFilterField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // Position Filter
        JLabel positionLabel = FormStyleManager.createStyledLabel("Puesto:");
        positionLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        positionFilterCombo = new JComboBox<>();
        positionFilterCombo.setPreferredSize(new Dimension(150, 30));
        positionFilterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        positionFilterCombo.setBackground(Color.WHITE);
        
        // Status Filter
        JLabel statusLabel = FormStyleManager.createStyledLabel("Estado:");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusFilterCombo = new JComboBox<>(new String[]{"Todos", "Active", "Inactive"});
        statusFilterCombo.setPreferredSize(new Dimension(120, 30));
        statusFilterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusFilterCombo.setBackground(Color.WHITE);
        
        // Buttons
        applyFiltersBtn = FormStyleManager.createPrimaryButton("Aplicar");
        applyFiltersBtn.setPreferredSize(new Dimension(100, 30));
        applyFiltersBtn.addActionListener(e -> applyFilters());
        
        clearFiltersBtn = FormStyleManager.createSecondaryButton("Limpiar");
        clearFiltersBtn.setPreferredSize(new Dimension(100, 30));
        clearFiltersBtn.addActionListener(e -> clearFilters());
        
        // Agregar componentes al panel
        filterPanel.add(idLabel);
        filterPanel.add(idFilterField);
        filterPanel.add(nameLabel);
        filterPanel.add(nameFilterField);
        filterPanel.add(positionLabel);
        filterPanel.add(positionFilterCombo);
        filterPanel.add(statusLabel);
        filterPanel.add(statusFilterCombo);
        filterPanel.add(applyFiltersBtn);
        filterPanel.add(clearFiltersBtn);

        return filterPanel;
    }

    private void loadStaff() {
        try {
            allStaff = staffService.getAllStaffs();
            loadPositionsInCombo();
            populateTable(allStaff);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar personal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPositionsInCombo() {
        try {
            positionFilterCombo.removeAllItems();
            positionFilterCombo.addItem("Todos");
            
            // Extraer posiciones únicas de la lista de personal
            List<String> uniquePositions = allStaff.stream()
                .map(Staff::getPosition)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
            
            for (String position : uniquePositions) {
                positionFilterCombo.addItem(position);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar posiciones: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTable(List<Staff> staffList) {
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
    }

    private void applyFilters() {
        try {
            List<Staff> filteredStaff = new ArrayList<>(allStaff);
            
            // Filter by ID
            if (!idFilterField.getText().trim().isEmpty()) {
                try {
                    int id = Integer.parseInt(idFilterField.getText().trim());
                    filteredStaff = filteredStaff.stream()
                        .filter(s -> s.getStaffId() == id)
                        .collect(Collectors.toList());
                } catch (NumberFormatException e) {
                    // Ignore if not a valid number
                }
            }
            
            // Filter by name
            if (!nameFilterField.getText().trim().isEmpty()) {
                String name = nameFilterField.getText().trim().toLowerCase();
                filteredStaff = filteredStaff.stream()
                    .filter(s -> s.getFullName().toLowerCase().contains(name))
                    .collect(Collectors.toList());
            }
            
            // Filter by position
            if (positionFilterCombo.getSelectedIndex() > 0) {
                String position = (String) positionFilterCombo.getSelectedItem();
                filteredStaff = filteredStaff.stream()
                    .filter(s -> s.getPosition().equals(position))
                    .collect(Collectors.toList());
            }
            
            // Filter by status
            if (statusFilterCombo.getSelectedIndex() > 0) {
                String status = (String) statusFilterCombo.getSelectedItem();
                filteredStaff = filteredStaff.stream()
                    .filter(s -> s.getStatus().equals(status))
                    .collect(Collectors.toList());
            }
            
            // Update table with filtered staff
            populateTable(filteredStaff);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al aplicar filtros: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFilters() {
        idFilterField.setText("");
        nameFilterField.setText("");
        positionFilterCombo.setSelectedIndex(0);
        statusFilterCombo.setSelectedIndex(0);
        
        // Reload all staff
        populateTable(allStaff);
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

        int staffId = (int) tableModel.getValueAt(staffTable.convertRowIndexToModel(selectedRow), 0);
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

        int staffId = (int) tableModel.getValueAt(staffTable.convertRowIndexToModel(selectedRow), 0);

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