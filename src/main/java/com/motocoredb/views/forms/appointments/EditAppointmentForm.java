package com.motocoredb.views.forms.appointments;

import com.motocoredb.services.WorkshopService;
import com.motocoredb.models.WorkshopAppointment;

import javax.swing.*;
import java.awt.*;

public class EditAppointmentForm extends AppointmentFormBase {
    private int appointmentId;
    private JTextField customerIdField;
    private JTextField visitReasonField;
    private JTextField motorcycleDescriptionField;
    private JTextField motorcyclePlateField;
    private JComboBox<String> statusComboBox;
    private JTextArea notesTextArea;

    // Constructor con WorkshopAppointment
    public EditAppointmentForm(WorkshopService appointmentService, WorkshopAppointment appointment) {
        super(appointmentService, "Editar Cita", 600, 400);
        this.appointmentId = appointment.getAppointmentId(); // Guarda el ID de la cita
        initializeUI(appointment); // Inicializa la UI usando la cita proporcionada
    }

    // Implementación de initializeUI sin argumentos
    @Override
    protected void initializeUI() {
        formPanel = createFormPanel("Detalles de la Cita");

        // Crear los campos de formulario
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

        // Botón para guardar cambios
        JButton saveButton = new JButton("Guardar Cambios");
        saveButton.addActionListener(e -> editAppointment());

        buttonPanel.add(saveButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    // Cargar los valores en el formulario con la cita proporcionada
    private void initializeUI(WorkshopAppointment appointment) {
        // Inicializar los campos con los datos de la cita
        customerIdField.setText(String.valueOf(appointment.getCustomerId()));
        visitReasonField.setText(appointment.getVisitReason());
        motorcycleDescriptionField.setText(appointment.getMotorcycleDescription());
        motorcyclePlateField.setText(appointment.getMotorcyclePlate());
        statusComboBox.setSelectedItem(appointment.getStatus());
        notesTextArea.setText(appointment.getNotes());
    }

    private void editAppointment() {
        // Lógica para actualizar la cita
        WorkshopAppointment updatedAppointment = new WorkshopAppointment(
                appointmentId,
                Integer.parseInt(customerIdField.getText()),
                new java.sql.Date(System.currentTimeMillis()), // Fecha de la cita
                new java.sql.Time(System.currentTimeMillis()),  // Hora de la cita
                visitReasonField.getText(),
                motorcycleDescriptionField.getText(),
                motorcyclePlateField.getText(),
                (String) statusComboBox.getSelectedItem(),
                1, // Usuario, ejemplo
                notesTextArea.getText()
        );

        // Llamada al servicio para actualizar la cita
        boolean success = appointmentService.updateAppointmentStatus(updatedAppointment.getAppointmentId(), updatedAppointment.getStatus());
        if (success) {
            JOptionPane.showMessageDialog(this, "Cita actualizada exitosamente.");
            dispose(); // Cerrar el formulario después de actualizar
        } else {
            JOptionPane.showMessageDialog(this, "Hubo un error al actualizar la cita.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
