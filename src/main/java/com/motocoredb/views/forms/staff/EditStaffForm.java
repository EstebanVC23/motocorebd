package com.motocoredb.views.forms.staff;

import com.motocoredb.models.Staff;
import com.motocoredb.services.StaffService;
import com.motocoredb.views.forms.NumericDocumentFilter;
import com.motocoredb.views.utils.FormStyleManager;

import javax.swing.*;
import javax.swing.text.AbstractDocument;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditStaffForm extends StaffFormBase {
    private final Staff staff;
    private JTextField fullNameField;
    private JTextField identityDocumentField;
    private JComboBox<String> positionCombo;
    private JComboBox<String> statusCombo;
    private JTextField specialtyField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField addressField;
    private JTextField hireDateField;

    public EditStaffForm(StaffService staffService, Staff staff) {
        super(staffService, "Editar Personal", 600, 700);
        this.staff = staff;
        initializeUI();
    }

    @Override
    protected void initializeUI() {
        // Crear panel de encabezado
        JPanel headerPanel = createHeaderPanel("Editar Personal: " + staff.getFullName());
        
        // Panel de información del personal
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

    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setOpaque(false);
        
        JLabel staffIdLabel = new JLabel("ID: " + staff.getStaffId());
        staffIdLabel.setFont(FormStyleManager.FIELD_FONT);
        staffIdLabel.setForeground(FormStyleManager.SECONDARY_COLOR);
        infoPanel.add(staffIdLabel);
        
        return infoPanel;
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setOpaque(false);
        
        JLabel statusLabel = new JLabel("Estado: " + staff.getStatus());
        statusLabel.setFont(FormStyleManager.FIELD_FONT);
        statusLabel.setForeground(
            staff.getStatus().equalsIgnoreCase("Active") ? 
            FormStyleManager.SUCCESS_COLOR : FormStyleManager.ERROR_COLOR
        );
        statsPanel.add(statusLabel);
        
        return statsPanel;
    }

    private void initializeFields() {
        fullNameField = FormStyleManager.createStyledTextField();
        fullNameField.setText(staff.getFullName());
    
        // Campo de documento de identidad (solo números)
        identityDocumentField = FormStyleManager.createStyledTextField();
        identityDocumentField.setText(staff.getIdentityDocument());
        ((AbstractDocument) identityDocumentField.getDocument()).setDocumentFilter(new NumericDocumentFilter()); // Restringir a números
    
        // Crear JComboBox para "puesto"
        positionCombo = new JComboBox<>(new String[] { "Mecánico", "Vendedor", "Administrador" });
        positionCombo.setSelectedItem(staff.getPosition()); // Seleccionar el puesto actual
    
        // Crear JComboBox para estado
        statusCombo = new JComboBox<>(new String[] { "Active", "Inactive" });
        statusCombo.setSelectedItem(staff.getStatus()); // Seleccionar el estado actual
    
        specialtyField = FormStyleManager.createStyledTextField(); // Campo libre para especialidad
        specialtyField.setText(staff.getSpecialty());
    
        // Campo de teléfono (solo números)
        phoneField = FormStyleManager.createStyledTextField();
        phoneField.setText(staff.getPhone());
        ((AbstractDocument) phoneField.getDocument()).setDocumentFilter(new NumericDocumentFilter()); // Restringir a números
    
        emailField = FormStyleManager.createStyledTextField();
        emailField.setText(staff.getEmail());
    
        addressField = FormStyleManager.createStyledTextField();
        addressField.setText(staff.getAddress());
    
        hireDateField = FormStyleManager.createStyledTextField();
        hireDateField.setToolTipText("Formato: yyyy-MM-dd");
    
        if (staff.getHireDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            hireDateField.setText(sdf.format(staff.getHireDate()));
        }
    }


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
        
        gbc.gridy = 8;
        formPanel.add(FormStyleManager.createStyledLabel("Estado:"), gbc);
        
        // Segunda columna (campos)
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        formPanel.add(fullNameField, gbc);
        
        gbc.gridy = 1;
        formPanel.add(identityDocumentField, gbc);
        
        gbc.gridy = 2;
        formPanel.add(positionCombo, gbc); // Usar JComboBox
        
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
        
        gbc.gridy = 8;
        formPanel.add(statusCombo, gbc);
    }

    private void setupButtons() {
        JButton saveButton = FormStyleManager.createPrimaryButton("Actualizar");
        JButton cancelButton = FormStyleManager.createSecondaryButton("Cancelar");
        
        saveButton.addActionListener((ActionEvent e) -> updateStaff());
        cancelButton.addActionListener((ActionEvent e) -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
    }

    private void updateStaff() {
        try {
            // Actualizar los valores del objeto staff con los datos del formulario
            staff.setFullName(fullNameField.getText());
            staff.setIdentityDocument(identityDocumentField.getText());
    
            if (positionCombo.getSelectedIndex() != -1) {
                staff.setPosition((String) positionCombo.getSelectedItem());
            } else {
                throw new Exception("Debe seleccionar un puesto.");
            }
    
            staff.setSpecialty(specialtyField.getText());
            staff.setPhone(phoneField.getText());
            staff.setEmail(emailField.getText());
            staff.setAddress(addressField.getText());
            
            // Actualizar el estado del staff
            if (statusCombo.getSelectedIndex() != -1) {
                staff.setStatus((String) statusCombo.getSelectedItem());
            } else {
                throw new Exception("Debe seleccionar un estado.");
            }
    
            // Convertir la fecha ingresada a java.sql.Date
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date utilDate = sdf.parse(hireDateField.getText()); // Convierte de String a java.util.Date
                java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime()); // Convierte a java.sql.Date
                staff.setHireDate(sqlDate); // Actualiza la fecha en el objeto Staff
            } catch (ParseException ex) {
                throw new Exception("Formato de fecha inválido. Use yyyy-MM-dd");
            }
    
            // Actualizar el objeto staff en la base de datos
            if (staffService.updateStaff(staff)) {
                FormStyleManager.showSuccessDialog(this, "Personal actualizado exitosamente");
                dispose();
            } else {
                FormStyleManager.showErrorDialog(this, "Error al actualizar personal");
            }
        } catch (Exception ex) {
            FormStyleManager.showErrorDialog(this, "Error en los datos ingresados: " + ex.getMessage());
        }
    }
}