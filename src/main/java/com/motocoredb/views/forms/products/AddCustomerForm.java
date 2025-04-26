package com.motocoredb.views.forms.products;

import com.motocoredb.models.Customer;
import com.motocoredb.services.CustomerService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AddCustomerForm extends JFrame {
    private final CustomerService customerService;

    public AddCustomerForm(CustomerService customerService) {
        this.customerService = customerService;
        initUI();
    }

    private void initUI() {
        setTitle("Agregar Nuevo Cliente");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField addressField = new JTextField();

        formPanel.add(new JLabel("Nombre:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Teléfono:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Dirección:"));
        formPanel.add(addressField);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Guardar");
        JButton cancelButton = new JButton("Cancelar");

        saveButton.addActionListener((ActionEvent e) -> {
            try {
                Customer newCustomer = new Customer();
                newCustomer.setNameOrCompany(nameField.getText());
                newCustomer.setEmail(emailField.getText());
                newCustomer.setPhone(phoneField.getText());
                newCustomer.setAddress(addressField.getText());
                newCustomer.setStatus("Active");

                if (customerService.createCustomer(newCustomer)) {
                    JOptionPane.showMessageDialog(this, "Cliente agregado exitosamente.");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al agregar cliente.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error en los datos ingresados: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener((ActionEvent e) -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
}
