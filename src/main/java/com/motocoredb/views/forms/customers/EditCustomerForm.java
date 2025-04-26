package com.motocoredb.views.forms.customers;

import com.motocoredb.models.Customer;
import com.motocoredb.services.CustomerService;
import com.motocoredb.views.forms.utils.FormStyleManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class EditCustomerForm extends CustomerFormBase {
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;
    private JTextField identityDocumentField;
    private JComboBox<String> customerTypeCombo;
    private final Customer customer;

    public EditCustomerForm(CustomerService customerService, Customer customer) {
        super(customerService, "Editar Cliente", 600, 620);
        this.customer = customer;
        initializeUI();
    }

    @Override
    protected void initializeUI() {
        // Crear panel de encabezado
        JPanel headerPanel = createHeaderPanel("Editar Cliente: " + customer.getNameOrCompany(), "/icons/customer_edit.png");
        
        // Panel de información del cliente
        JPanel infoPanel = createInfoPanel();
        
        // Panel norte combinado
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setOpaque(false);
        northPanel.add(headerPanel, BorderLayout.NORTH);
        northPanel.add(infoPanel, BorderLayout.CENTER);
        
        // Crear panel de formulario
        formPanel = createFormPanel("Modificar Información");
        
        // Inicializar campos
        initializeFields();
        
        // Configurar campos en el formulario
        addFieldsToForm();
        
        // Panel de estado
        JPanel statsPanel = createStatsPanel();
        
        // Panel central combinado
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(formPanel, BorderLayout.CENTER);
        centerPanel.add(statsPanel, BorderLayout.SOUTH);
        
        // Configurar botones
        setupButtons();
        
        // Ensamblar formulario
        mainPanel.add(northPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Crea el panel de información del cliente
     */
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setOpaque(false);
        
        JLabel customerIdLabel = new JLabel("ID: " + customer.getCustomerId());
        customerIdLabel.setFont(FormStyleManager.FIELD_FONT);
        customerIdLabel.setForeground(FormStyleManager.SECONDARY_COLOR);
        infoPanel.add(customerIdLabel);
        
        return infoPanel;
    }
    
    /**
     * Crea el panel de estadísticas/estado del cliente
     */
    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setOpaque(false);
        
        JLabel statusLabel = new JLabel("Estado: " + customer.getStatus());
        statusLabel.setFont(FormStyleManager.FIELD_FONT);
        statusLabel.setForeground(
            customer.getStatus().equalsIgnoreCase("Active") ? 
            FormStyleManager.SUCCESS_COLOR : FormStyleManager.ERROR_COLOR
        );
        statsPanel.add(statusLabel);
        
        return statsPanel;
    }
    
    /**
     * Inicializa los campos del formulario con los valores del cliente
     */
    private void initializeFields() {
        nameField = FormStyleManager.createStyledTextField();
        nameField.setText(customer.getNameOrCompany());
        
        emailField = FormStyleManager.createStyledTextField();
        emailField.setText(customer.getEmail());
        
        phoneField = FormStyleManager.createStyledTextField();
        phoneField.setText(customer.getPhone());
        
        addressField = FormStyleManager.createStyledTextField();
        addressField.setText(customer.getAddress());
        
        identityDocumentField = FormStyleManager.createStyledTextField();
        identityDocumentField.setText(customer.getIdentityDocument());
        
        customerTypeCombo = new JComboBox<>(new String[]{"Individual", "Corporativo"});
        customerTypeCombo.setFont(FormStyleManager.FIELD_FONT);
        customerTypeCombo.setBackground(new Color(245, 245, 245));
        customerTypeCombo.setBorder(new FormStyleManager.RoundedCornerBorder());
        customerTypeCombo.setSelectedItem(customer.getCustomerType());
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
        JButton saveButton = FormStyleManager.createPrimaryButton("Actualizar");
        JButton cancelButton = FormStyleManager.createSecondaryButton("Cancelar");
        
        saveButton.addActionListener((ActionEvent e) -> updateCustomer());
        cancelButton.addActionListener((ActionEvent e) -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
    }
    
    /**
     * Actualiza el cliente con los datos del formulario
     */
    private void updateCustomer() {
        try {
            customer.setNameOrCompany(nameField.getText());
            customer.setEmail(emailField.getText());
            customer.setPhone(phoneField.getText());
            customer.setAddress(addressField.getText());
            customer.setIdentityDocument(identityDocumentField.getText());
            customer.setCustomerType((String) customerTypeCombo.getSelectedItem());

            if (customerService.updateCustomer(customer)) {
                FormStyleManager.showSuccessDialog(this, "Cliente actualizado exitosamente");
                dispose();
            } else {
                FormStyleManager.showErrorDialog(this, "Error al actualizar cliente");
            }
        } catch (Exception ex) {
            FormStyleManager.showErrorDialog(this, "Error en los datos ingresados: " + ex.getMessage());
        }
    }
}