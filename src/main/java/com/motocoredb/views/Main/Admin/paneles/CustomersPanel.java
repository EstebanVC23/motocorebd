package com.motocoredb.views.Main.Admin.paneles;

import com.motocoredb.models.Customer;
import com.motocoredb.services.CustomerService;
import com.motocoredb.views.forms.customers.EditCustomerForm;
import com.motocoredb.views.forms.customers.AddCustomerForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
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

        JButton refreshBtn = new JButton("Actualizar");
        refreshBtn.addActionListener(e -> loadCustomers());

        JButton addBtn = new JButton("Nuevo Cliente");
        addBtn.addActionListener(this::showAddCustomerForm);

        JButton editBtn = new JButton("Editar Cliente");
        editBtn.addActionListener(this::showEditCustomerForm);

        JButton deleteBtn = new JButton("Eliminar Cliente");
        deleteBtn.addActionListener(this::deleteCustomer);

        toolBar.add(refreshBtn);
        toolBar.addSeparator();
        toolBar.add(addBtn);
        toolBar.add(editBtn);
        toolBar.add(deleteBtn);

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

    private void showAddCustomerForm(ActionEvent e) {
        new AddCustomerForm(customerService).setVisible(true); // Abrir nueva ventana para agregar cliente
    }

    private void showEditCustomerForm(ActionEvent e) {
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

        new EditCustomerForm(customerService, customer).setVisible(true); // Abrir nueva ventana para editar cliente
    }

    private void deleteCustomer(ActionEvent e) {
        int selectedRow = customersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para eliminar.");
            return;
        }

        int customerId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirmation = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar este cliente?", "Confirmación", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            if (customerService.deactivateCustomer(customerId)) {
                JOptionPane.showMessageDialog(this, "Cliente eliminado exitosamente.");
                loadCustomers();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el cliente.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}