package com.motocoredb.views.forms.staff;

import com.motocoredb.models.Staff;
import com.motocoredb.services.StaffService;
import com.motocoredb.views.utils.FormStyleManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Date;

public class AddStaffForm extends StaffFormBase {
    private JTextField fullNameField;
    private JTextField identityDocumentField;
    private JComboBox<String> positionCombo; // Cambiado a JComboBox
    private JTextField specialtyField; // Texto libre para especialidad
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField addressField;

    public AddStaffForm(StaffService staffService) {
        super(staffService, "Agregar Nuevo Personal", 600, 700);
        initializeUI();
    }

    @Override
    protected void initializeUI() {
        // Crear panel de encabezado
        JPanel headerPanel = createHeaderPanel("Agregar Nuevo Personal");
        
        // Crear panel de formulario
        formPanel = createFormPanel("Información del Personal");
        
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
        fullNameField = FormStyleManager.createStyledTextField();
        identityDocumentField = FormStyleManager.createStyledTextField();

        // Crear JComboBox para "puesto"
        positionCombo = new JComboBox<>(new String[] { "Mecánico", "Vendedor", "Administrador" });
        positionCombo.setSelectedIndex(-1); // Sin selección inicial

        specialtyField = FormStyleManager.createStyledTextField(); // Campo libre para especialidad
        phoneField = FormStyleManager.createStyledTextField();
        emailField = FormStyleManager.createStyledTextField();
        addressField = FormStyleManager.createStyledTextField();
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
        formPanel.add(FormStyleManager.createStyledLabel("Nombre Completo:"), gbc);
        
        gbc.gridy = 1;
        formPanel.add(FormStyleManager.createStyledLabel("Documento de Identidad:"), gbc);
        
        gbc.gridy = 2;
        formPanel.add(FormStyleManager.createStyledLabel("Puesto:"), gbc);
        
        gbc.gridy = 3;
        formPanel.add(FormStyleManager.createStyledLabel("Especialidad (opcional):"), gbc);
        
        gbc.gridy = 4;
        formPanel.add(FormStyleManager.createStyledLabel("Teléfono:"), gbc);
        
        gbc.gridy = 5;
        formPanel.add(FormStyleManager.createStyledLabel("Email:"), gbc);
        
        gbc.gridy = 6;
        formPanel.add(FormStyleManager.createStyledLabel("Dirección:"), gbc);
        
        // Segunda columna (campos)
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        formPanel.add(fullNameField, gbc);
        
        gbc.gridy = 1;
        formPanel.add(identityDocumentField, gbc);
        
        gbc.gridy = 2;
        formPanel.add(positionCombo, gbc); // Agregar JComboBox
        
        gbc.gridy = 3;
        formPanel.add(specialtyField, gbc);
        
        gbc.gridy = 4;
        formPanel.add(phoneField, gbc);
        
        gbc.gridy = 5;
        formPanel.add(emailField, gbc);
        
        gbc.gridy = 6;
        formPanel.add(addressField, gbc);
    }

    /**
     * Configura los botones y sus acciones
     */
    private void setupButtons() {
        JButton saveButton = FormStyleManager.createPrimaryButton("Guardar");
        JButton cancelButton = FormStyleManager.createSecondaryButton("Cancelar");
        
        saveButton.addActionListener((ActionEvent e) -> saveStaff());
        cancelButton.addActionListener((ActionEvent e) -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
    }

    private void saveStaff() {
        try {
            Staff newStaff = new Staff();
            newStaff.setFullName(fullNameField.getText());
            newStaff.setIdentityDocument(identityDocumentField.getText());
    
            if (positionCombo.getSelectedIndex() != -1) {
                newStaff.setPosition((String) positionCombo.getSelectedItem());
            } else {
                throw new Exception("Debe seleccionar un puesto.");
            }
    
            newStaff.setSpecialty(specialtyField.getText());
            newStaff.setPhone(phoneField.getText());
            newStaff.setEmail(emailField.getText());
            newStaff.setAddress(addressField.getText());
            newStaff.setHireDate(new Date(System.currentTimeMillis()));
            newStaff.setStatus("Active");
    
            // Asignar `userId = 0` si no hay usuario asociado
            newStaff.setUserId(0);
    
            if (staffService.createStaff(newStaff)) {
                FormStyleManager.showSuccessDialog(this, "Personal agregado exitosamente");
                dispose();
            } else {
                FormStyleManager.showErrorDialog(this, "Error al agregar personal");
            }
        } catch (Exception ex) {
            FormStyleManager.showErrorDialog(this, "Error en los datos ingresados: " + ex.getMessage());
        }
    }
}