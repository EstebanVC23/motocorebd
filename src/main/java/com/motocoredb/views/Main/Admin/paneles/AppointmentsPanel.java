package com.motocoredb.views.Main.Admin.paneles;

import com.motocoredb.models.WorkshopAppointment;
import com.motocoredb.services.WorkshopService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AppointmentsPanel extends JPanel {
    private final WorkshopService workshopService;

    public AppointmentsPanel(WorkshopService workshopService) {
        this.workshopService = workshopService;
        setLayout(new BorderLayout());

        JPanel controlsPanel = new JPanel(new FlowLayout());
        JTextField dateField = new JTextField(10);
        JButton searchButton = new JButton("Buscar por fecha");

        searchButton.addActionListener(e -> {
            String date = dateField.getText();
            List<WorkshopAppointment> appointments = workshopService.getAppointmentsByDate(date);
            displayAppointments(appointments);
        });

        controlsPanel.add(dateField);
        controlsPanel.add(searchButton);

        add(controlsPanel, BorderLayout.NORTH);
    }

    private void displayAppointments(List<WorkshopAppointment> appointments) {
        JPanel appointmentsPanel = new JPanel();
        appointmentsPanel.setLayout(new BoxLayout(appointmentsPanel, BoxLayout.Y_AXIS));

        if (appointments != null && !appointments.isEmpty()) {
            for (WorkshopAppointment appointment : appointments) {
                JPanel appointmentPanel = new JPanel();
                appointmentPanel.setLayout(new BoxLayout(appointmentPanel, BoxLayout.X_AXIS));
                JLabel appointmentLabel = new JLabel("Cita ID: " + appointment.getAppointmentId() + " | Estado: " + appointment.getStatus());
                appointmentPanel.add(appointmentLabel);
                appointmentsPanel.add(appointmentPanel);
            }
        } else {
            JLabel noAppointmentsLabel = new JLabel("No se encontraron citas.");
            appointmentsPanel.add(noAppointmentsLabel);
        }

        JScrollPane scrollPane = new JScrollPane(appointmentsPanel);
        add(scrollPane, BorderLayout.CENTER);
    }
}