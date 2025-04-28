package com.motocoredb.views.forms.suppliers;

import com.motocoredb.models.Supplier;
import com.motocoredb.services.SupplierService;
import com.motocoredb.views.utils.FormStyleManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AddSupplierForm extends SupplierFormBase {
    private JTextField nameField;
    private JTextField contactField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField addressField;

    public AddSupplierForm(SupplierService supplierService) {
        super(supplierService, "Agregar Nuevo Proveedor", 500, 500);
        initializeUI();
    }

    @Override
    protected void initializeUI() {
        JPanel headerPanel = createHeaderPanel("Agregar Nuevo Proveedor");
        
        formPanel = createFormPanel("Información del Proveedor");
        initializeFields();
        addFieldsToForm();
        
        setupButtons();
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void initializeFields() {
        nameField = FormStyleManager.createStyledTextField();
        contactField = FormStyleManager.createStyledTextField();
        phoneField = FormStyleManager.createStyledTextField();
        emailField = FormStyleManager.createStyledTextField();
        addressField = FormStyleManager.createStyledTextField();
    }

    private void addFieldsToForm() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        formPanel.add(FormStyleManager.createStyledLabel("Nombre:"), gbc);
        
        gbc.gridy = 1;
        formPanel.add(FormStyleManager.createStyledLabel("Contacto:"), gbc);
        
        gbc.gridy = 2;
        formPanel.add(FormStyleManager.createStyledLabel("Teléfono:"), gbc);
        
        gbc.gridy = 3;
        formPanel.add(FormStyleManager.createStyledLabel("Email:"), gbc);
        
        gbc.gridy = 4;
        formPanel.add(FormStyleManager.createStyledLabel("Dirección:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        formPanel.add(nameField, gbc);
        
        gbc.gridy = 1;
        formPanel.add(contactField, gbc);
        
        gbc.gridy = 2;
        formPanel.add(phoneField, gbc);
        
        gbc.gridy = 3;
        formPanel.add(emailField, gbc);
        
        gbc.gridy = 4;
        formPanel.add(addressField, gbc);
    }

    private void setupButtons() {
        JButton saveButton = FormStyleManager.createPrimaryButton("Guardar");
        JButton cancelButton = FormStyleManager.createSecondaryButton("Cancelar");
        
        saveButton.addActionListener((ActionEvent e) -> saveSupplier());
        cancelButton.addActionListener((ActionEvent e) -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
    }

    private void saveSupplier() {
        try {
            Supplier newSupplier = new Supplier();
            newSupplier.setCompanyName(nameField.getText());
            newSupplier.setContactPerson(contactField.getText());
            newSupplier.setContactPhone(phoneField.getText());
            newSupplier.setContactEmail(emailField.getText());
            newSupplier.setAddress(addressField.getText());
            newSupplier.setStatus("Active");

            if (supplierService.createSupplier(newSupplier)) {
                FormStyleManager.showSuccessDialog(this, "Proveedor agregado exitosamente");
                dispose();
            } else {
                FormStyleManager.showErrorDialog(this, "Error al agregar proveedor");
            }
        } catch (Exception ex) {
            FormStyleManager.showErrorDialog(this, "Error en los datos ingresados: " + ex.getMessage());
        }
    }
}