package com.motocoredb.views.forms.customers;

import com.motocoredb.models.Customer;
import com.motocoredb.services.CustomerService;
import com.motocoredb.views.utils.FormStyleManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AddCustomerForm extends CustomerFormBase {
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;
    private JTextField identityDocumentField;
    private JComboBox<String> customerTypeCombo;

    public AddCustomerForm(CustomerService customerService) {
        super(customerService, "Agregar Nuevo Cliente", 600, 550);
        initializeUI();
    }

    @Override
    protected void initializeUI() {
        // Crear panel de encabezado
        JPanel headerPanel = createHeaderPanel("Agregar Nuevo Cliente");
        
        // Crear panel de formulario
        formPanel = createFormPanel("Información del Cliente");
        
        // Inicializar campos
        initializeFields();
        
        // Configurar campos en el formulario
        addFieldsToForm();
        
        // Configurar botones
        setupButtons();
        
        // Ensamblar formulario
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Inicializa los campos del formulario
     */
    private void initializeFields() {
        nameField = FormStyleManager.createStyledTextField();
        emailField = FormStyleManager.createStyledTextField();
        phoneField = FormStyleManager.createStyledTextField();
        addressField = FormStyleManager.createStyledTextField();
        identityDocumentField = FormStyleManager.createStyledTextField();
        
        customerTypeCombo = new JComboBox<>(new String[]{"Individual", "Corporativo"});
        customerTypeCombo.setFont(FormStyleManager.FIELD_FONT);
        customerTypeCombo.setBackground(new Color(245, 245, 245));
        customerTypeCombo.setBorder(new FormStyleManager.RoundedCornerBorder());
    }
    
    /**
     * Añade los campos al panel del formulario
     */
    private void addFieldsToForm() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Primera columna (etiquetas)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        formPanel.add(FormStyleManager.createStyledLabel("Nombre o Empresa:"), gbc);
        
        gbc.gridy = 1;
        formPanel.add(FormStyleManager.createStyledLabel("Email:"), gbc);
        
        gbc.gridy = 2;
        formPanel.add(FormStyleManager.createStyledLabel("Teléfono:"), gbc);
        
        gbc.gridy = 3;
        formPanel.add(FormStyleManager.createStyledLabel("Dirección:"), gbc);
        
        gbc.gridy = 4;
        formPanel.add(FormStyleManager.createStyledLabel("Documento de Identidad:"), gbc);
        
        gbc.gridy = 5;
        formPanel.add(FormStyleManager.createStyledLabel("Tipo de Cliente:"), gbc);
        
        // Segunda columna (campos)
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        formPanel.add(nameField, gbc);
        
        gbc.gridy = 1;
        formPanel.add(emailField, gbc);
        
        gbc.gridy = 2;
        formPanel.add(phoneField, gbc);
        
        gbc.gridy = 3;
        formPanel.add(addressField, gbc);
        
        gbc.gridy = 4;
        formPanel.add(identityDocumentField, gbc);
        
        gbc.gridy = 5;
        formPanel.add(customerTypeCombo, gbc);
    }
    
    /**
     * Configura los botones y sus acciones
     */
    private void setupButtons() {
        JButton saveButton = FormStyleManager.createPrimaryButton("Guardar");
        JButton cancelButton = FormStyleManager.createSecondaryButton("Cancelar");
        
        saveButton.addActionListener((ActionEvent e) -> saveCustomer());
        cancelButton.addActionListener((ActionEvent e) -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
    }
    
    /**
     * Guarda el cliente con los datos del formulario
     */
    private void saveCustomer() {
        try {
            Customer newCustomer = new Customer();
            newCustomer.setNameOrCompany(nameField.getText());
            newCustomer.setEmail(emailField.getText());
            newCustomer.setPhone(phoneField.getText());
            newCustomer.setAddress(addressField.getText());
            newCustomer.setIdentityDocument(identityDocumentField.getText());
            newCustomer.setCustomerType((String) customerTypeCombo.getSelectedItem());
            newCustomer.setStatus("Active");

            if (customerService.createCustomer(newCustomer)) {
                FormStyleManager.showSuccessDialog(this, "Cliente agregado exitosamente");
                dispose();
            } else {
                FormStyleManager.showErrorDialog(this, "Error al agregar cliente");
            }
        } catch (Exception ex) {
            FormStyleManager.showErrorDialog(this, "Error en los datos ingresados: " + ex.getMessage());
        }
    }
}