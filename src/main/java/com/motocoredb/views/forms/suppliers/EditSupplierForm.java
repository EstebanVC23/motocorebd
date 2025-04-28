package com.motocoredb.views.forms.suppliers;

import com.motocoredb.models.Supplier;
import com.motocoredb.services.SupplierService;
import com.motocoredb.views.forms.NumericDocumentFilter;
import com.motocoredb.views.utils.FormStyleManager;

import javax.swing.*;
import javax.swing.text.AbstractDocument;

import java.awt.*;
import java.awt.event.ActionEvent;

public class EditSupplierForm extends SupplierFormBase {
    private final Supplier supplier;

    private JTextField nameField;
    private JTextField contactField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField addressField;
    private JComboBox<String> statusCombo; // Añadido para gestionar el estado

    public EditSupplierForm(SupplierService supplierService, Supplier supplier) {
        super(supplierService, "Editar Proveedor", 500, 550); // Ajusté la altura para el nuevo campo
        this.supplier = supplier;
        initializeUI();
        loadSupplierData();
    }

    @Override
    protected void initializeUI() {
        JPanel headerPanel = createHeaderPanel("Editar Proveedor");

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
        ((AbstractDocument) phoneField.getDocument()).setDocumentFilter(new NumericDocumentFilter()); // Aplicar filtro para solo números

        emailField = FormStyleManager.createStyledTextField();

        addressField = FormStyleManager.createStyledTextField();
        
        // Crear ComboBox para el estado
        statusCombo = new JComboBox<>(new String[] { "Active", "Inactive" });
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
        
        gbc.gridy = 5;
        formPanel.add(FormStyleManager.createStyledLabel("Estado:"), gbc);

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
        
        gbc.gridy = 5;
        formPanel.add(statusCombo, gbc);
    }

    private void setupButtons() {
        JButton saveButton = FormStyleManager.createPrimaryButton("Guardar Cambios");
        JButton cancelButton = FormStyleManager.createSecondaryButton("Cancelar");

        saveButton.addActionListener(this::updateSupplier);
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
    }

    private void loadSupplierData() {
        nameField.setText(supplier.getCompanyName());
        contactField.setText(supplier.getContactPerson());
        phoneField.setText(supplier.getContactPhone());
        emailField.setText(supplier.getContactEmail());
        addressField.setText(supplier.getAddress());
        
        // Cargar el estado actual
        statusCombo.setSelectedItem(supplier.getStatus());
    }

    private void updateSupplier(ActionEvent e) {
        try {
            supplier.setCompanyName(nameField.getText());
            supplier.setContactPerson(contactField.getText());
            supplier.setContactPhone(phoneField.getText());
            supplier.setContactEmail(emailField.getText());
            supplier.setAddress(addressField.getText());
            
            // Actualizar el estado del proveedor
            if (statusCombo.getSelectedIndex() != -1) {
                supplier.setStatus((String) statusCombo.getSelectedItem());
            } else {
                throw new Exception("Debe seleccionar un estado.");
            }

            if (supplierService.updateSupplier(supplier)) {
                FormStyleManager.showSuccessDialog(this, "Proveedor actualizado correctamente");
                dispose();
            } else {
                FormStyleManager.showErrorDialog(this, "Error al actualizar proveedor");
            }
        } catch (Exception ex) {
            FormStyleManager.showErrorDialog(this, "Error en los datos ingresados: " + ex.getMessage());
        }
    }
}