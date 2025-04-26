package com.motocoredb.views.forms.staff;

import com.motocoredb.models.Staff;
import com.motocoredb.services.StaffService;
import com.motocoredb.views.forms.utils.FormStyleManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddStaffForm extends StaffFormBase {
    private JTextField fullNameField;
    private JTextField identityDocumentField;
    private JTextField positionField;
    private JTextField specialtyField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField addressField;
    private JTextField hireDateField;

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
        positionField = FormStyleManager.createStyledTextField();
        specialtyField = FormStyleManager.createStyledTextField();
        phoneField = FormStyleManager.createStyledTextField();
        emailField = FormStyleManager.createStyledTextField();
        addressField = FormStyleManager.createStyledTextField();
        hireDateField = FormStyleManager.createStyledTextField();
        hireDateField.setToolTipText("Formato: yyyy-MM-dd");
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
        formPanel.add(FormStyleManager.createStyledLabel("Especialidad:"), gbc);
        
        gbc.gridy = 4;
        formPanel.add(FormStyleManager.createStyledLabel("Teléfono:"), gbc);
        
        gbc.gridy = 5;
        formPanel.add(FormStyleManager.createStyledLabel("Email:"), gbc);
        
        gbc.gridy = 6;
        formPanel.add(FormStyleManager.createStyledLabel("Dirección:"), gbc);
        
        gbc.gridy = 7;
        formPanel.add(FormStyleManager.createStyledLabel("Fecha de Contratación:"), gbc);
        
        // Segunda columna (campos)
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        formPanel.add(fullNameField, gbc);
        
        gbc.gridy = 1;
        formPanel.add(identityDocumentField, gbc);
        
        gbc.gridy = 2;
        formPanel.add(positionField, gbc);
        
        gbc.gridy = 3;
        formPanel.add(specialtyField, gbc);
        
        gbc.gridy = 4;
        formPanel.add(phoneField, gbc);
        
        gbc.gridy = 5;
        formPanel.add(emailField, gbc);
        
        gbc.gridy = 6;
        formPanel.add(addressField, gbc);
        
        gbc.gridy = 7;
        formPanel.add(hireDateField, gbc);
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
    
    /**
     * Guarda el personal con los datos del formulario
     */
    private void saveStaff() {
        try {
            Staff newStaff = new Staff();
            newStaff.setFullName(fullNameField.getText());
            newStaff.setIdentityDocument(identityDocumentField.getText());
            newStaff.setPosition(positionField.getText());
            newStaff.setSpecialty(specialtyField.getText());
            newStaff.setPhone(phoneField.getText());
            newStaff.setEmail(emailField.getText());
            newStaff.setAddress(addressField.getText());
            
            // Convertir el texto de fecha a java.sql.Date
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date utilDate = sdf.parse(hireDateField.getText());
                java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                newStaff.setHireDate(sqlDate);
            } catch (ParseException ex) {
                throw new Exception("Formato de fecha inválido. Use yyyy-MM-dd");
            }
            
            newStaff.setStatus("Active");

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