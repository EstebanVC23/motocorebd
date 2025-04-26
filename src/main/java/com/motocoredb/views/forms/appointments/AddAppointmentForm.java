package com.motocoredb.views.forms.appointments;

import com.motocoredb.models.WorkshopAppointment;
import com.motocoredb.services.WorkshopService;

import javax.swing.*;
import java.awt.*;

public class AddAppointmentForm extends AppointmentFormBase {

    private JTextField customerIdField;
    private JTextField visitReasonField;
    private JTextField motorcycleDescriptionField;
    private JTextField motorcyclePlateField;
    private JComboBox<String> statusComboBox;
    private JTextArea notesTextArea;

    public AddAppointmentForm(WorkshopService appointmentService) {
        super(appointmentService, "Agregar Cita", 600, 400);
    }

    @Override
    protected void initializeUI() {
        formPanel = createFormPanel("Detalles de la Cita");

        // Definir campos del formulario
        customerIdField = new JTextField(20);
        visitReasonField = new JTextField(20);
        motorcycleDescriptionField = new JTextField(20);
        motorcyclePlateField = new JTextField(20);

        String[] statuses = {"Scheduled", "In progress", "Completed", "Cancelled"};
        statusComboBox = new JComboBox<>(statuses);

        notesTextArea = new JTextArea(5, 20);
        notesTextArea.setLineWrap(true);
        notesTextArea.setWrapStyleWord(true);

        // Layout del formulario
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("ID Cliente:"), gbc);

        gbc.gridx = 1;
        formPanel.add(customerIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Razón de la visita:"), gbc);

        gbc.gridx = 1;
        formPanel.add(visitReasonField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Descripción de la motocicleta:"), gbc);

        gbc.gridx = 1;
        formPanel.add(motorcycleDescriptionField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Placa de la motocicleta:"), gbc);

        gbc.gridx = 1;
        formPanel.add(motorcyclePlateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Estado:"), gbc);

        gbc.gridx = 1;
        formPanel.add(statusComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Notas:"), gbc);

        gbc.gridx = 1;
        formPanel.add(new JScrollPane(notesTextArea), gbc);

        // Botones de acción
        JButton saveButton = new JButton("Guardar");
        saveButton.addActionListener(e -> saveAppointment());

        buttonPanel.add(saveButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void saveAppointment() {
        try {
            // Crear una nueva cita con los datos del formulario
            WorkshopAppointment appointment = new WorkshopAppointment(
                    0, // ID será generado en la base de datos
                    Integer.parseInt(customerIdField.getText()),
                    null, // La fecha y hora pueden ser seleccionadas en otro campo o establecerse por defecto
                    null,
                    visitReasonField.getText(),
                    motorcycleDescriptionField.getText(),
                    motorcyclePlateField.getText(),
                    (String) statusComboBox.getSelectedItem(),
                    0, // userId a completar
                    notesTextArea.getText()
            );
            // Aquí llamarías a tu servicio para guardar la cita
            appointmentService.createAppointment(appointment, null);
            JOptionPane.showMessageDialog(this, "Cita agregada exitosamente");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al agregar la cita: " + ex.getMessage());
        }
    }
}
