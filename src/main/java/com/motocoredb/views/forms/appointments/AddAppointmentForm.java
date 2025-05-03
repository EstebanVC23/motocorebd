package com.motocoredb.views.forms.appointments;

import com.motocoredb.models.Customer;
import com.motocoredb.models.WorkshopAppointment;
import com.motocoredb.dao.impl.AlertDaoImpl;
import com.motocoredb.models.Alert;
import com.motocoredb.models.AppointmentService;
import com.motocoredb.services.AlertService;
import com.motocoredb.services.WorkshopService;
import com.motocoredb.views.utils.FormStyleManager;
import com.toedter.calendar.JDateChooser;
import com.github.lgooddatepicker.components.TimePicker;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

public class AddAppointmentForm extends AppointmentFormBase {
    private final WorkshopService workshopService;
    private final List<Customer> customers;
    private JComboBox<Customer> customerCombo;
    private JTextField reasonField;
    private JTextField motorcycleDescField;
    private JTextField motorcyclePlateField;
    private JComboBox<String> statusCombo;
    private JTextArea notesArea;
    private JDateChooser dateChooser;
    private TimePicker timePicker;
    private List<AppointmentService> services;

    public AddAppointmentForm(WorkshopService workshopService, List<Customer> customers) {
        super("Agendar Cita", 600, 800);
        this.workshopService = workshopService;
        this.services = new ArrayList<>();
        this.customers = customers;
        initializeForm();
    }

    @Override
    protected void initializeForm() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        customerCombo = new JComboBox<>();
        customerCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Customer) {
                    setText(((Customer) value).getNameOrCompany());
                }
                return this;
            }
        });
        populateCustomerCombo();

        reasonField = FormStyleManager.createStyledTextField();
        motorcycleDescField = FormStyleManager.createStyledTextField();
        motorcyclePlateField = FormStyleManager.createStyledTextField();
        notesArea = new JTextArea(4, 20);

        statusCombo = new JComboBox<>(new String[]{"Scheduled", "In progress", "Completed", "Cancelled"});

        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        timePicker = new TimePicker();

        addFormField("Cliente:", customerCombo, gbc, 0);
        addFormField("Razón de la visita:", reasonField, gbc, 1);
        addFormField("Descripción de motocicleta:", motorcycleDescField, gbc, 2);
        addFormField("Placa de motocicleta:", motorcyclePlateField, gbc, 3);
        addFormField("Estado:", statusCombo, gbc, 4);
        addFormField("Fecha:", dateChooser, gbc, 5);
        addFormField("Hora:", timePicker, gbc, 6);
        addFormField("Notas:", new JScrollPane(notesArea), gbc, 7);
    }

    private void populateCustomerCombo() {
        customerCombo.removeAllItems();
        for (Customer customer : customers) {
            customerCombo.addItem(customer);
        }
    }

    private void addFormField(String label, Component field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(FormStyleManager.createStyledLabel(label), gbc);

        gbc.gridx = 1;
        formPanel.add(field, gbc);
    }

    @Override
    protected void onSave() {
        try {
            if (customerCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (dateChooser.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar una fecha.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String timeText = timePicker.getText();
            if (timeText == null || timeText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar una hora.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            java.sql.Time timeIn24Hours;
            try {
                java.time.LocalTime localTime = timePicker.getTime();
                timeIn24Hours = java.sql.Time.valueOf(localTime);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Formato de hora inválido. Asegúrese de usar correctamente el selector.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            WorkshopAppointment appointment = new WorkshopAppointment();
            Customer selectedCustomer = (Customer) customerCombo.getSelectedItem();
            appointment.setCustomerId(selectedCustomer.getCustomerId());
            appointment.setVisitReason(reasonField.getText().trim());
            appointment.setMotorcycleDescription(motorcycleDescField.getText().trim());
            appointment.setMotorcyclePlate(motorcyclePlateField.getText().trim());
            appointment.setStatus((String) statusCombo.getSelectedItem());
            appointment.setScheduledDate(new java.sql.Date(dateChooser.getDate().getTime()));
            appointment.setScheduledTime(timeIn24Hours);
            appointment.setNotes(notesArea.getText());
            appointment.setUserId(1);

            if (workshopService.createAppointment(appointment, services)) {
                try {
                    AlertService alertService = new AlertService(new AlertDaoImpl());

                    Alert newAlert = new Alert();
                    newAlert.setAlertType("Upcoming appointment");
                    newAlert.setMessage("Nueva cita agendada para el cliente '" + selectedCustomer.getNameOrCompany() +
                                        "' con la motocicleta placa '" + appointment.getMotorcyclePlate() + "' el día " +
                                        appointment.getScheduledDate());
                    newAlert.setGeneratedAt(new Timestamp(System.currentTimeMillis()));
                    newAlert.setStatus("Pending");
                    newAlert.setReferenceId(appointment.getAppointmentId());
                    newAlert.setReferenceType("Appointment");

                    boolean alertCreated = alertService.createAlert(newAlert);
                    if (!alertCreated) {
                        JOptionPane.showMessageDialog(this, "Error al generar la alerta para la cita agendada.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception alertEx) {
                    JOptionPane.showMessageDialog(this, "Error al procesar la alerta: " + alertEx.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }

                FormStyleManager.showSuccessDialog(this, "Cita agendada exitosamente.");
                dispose();
            } else {
                FormStyleManager.showErrorDialog(this, "Error al agendar la cita. Verifica los datos.");
            }
        } catch (Exception ex) {
            FormStyleManager.showErrorDialog(this, "Error en los datos ingresados: " + ex.getMessage());
        }
    }

    @Override
    protected void onCancel() {
        dispose();
    }
}