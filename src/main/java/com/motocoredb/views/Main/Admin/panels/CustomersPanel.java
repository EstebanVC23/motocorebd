package com.motocoredb.views.Main.Admin.panels;

import com.motocoredb.models.Customer;
import com.motocoredb.services.CustomerService;
import com.motocoredb.views.forms.customers.EditCustomerForm;
import com.motocoredb.views.utils.FormStyleManager;
import com.motocoredb.views.forms.customers.AddCustomerForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class CustomersPanel extends JPanel {
    private final CustomerService customerService;
    private DefaultTableModel tableModel;
    private JTable customersTable;

    public CustomersPanel(CustomerService customerService) {
        this.customerService = customerService;
        initUI();
        loadCustomers();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

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
        toolBar.add(deleteBtn); // Eliminamos el botón de editar
        add(toolBar, BorderLayout.NORTH);

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
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        add(scrollPane, BorderLayout.CENTER);
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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar clientes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
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

        int customerId = (int) tableModel.getValueAt(selectedRow, 0);
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

        int customerId = (int) tableModel.getValueAt(selectedRow, 0);

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