package com.motocoredb.views.forms.appointments;

import com.motocoredb.models.Customer;
import com.motocoredb.models.WorkshopAppointment;
import com.motocoredb.models.AppointmentService;
import com.motocoredb.services.WorkshopService;
import com.motocoredb.views.utils.FormStyleManager;
import com.toedter.calendar.JDateChooser;
import com.github.lgooddatepicker.components.TimePicker;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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
        super("Agendar Cita", 600, 800); // Ajustar tamaño y título del formulario
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

        // Configuración del ComboBox de clientes
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

        // Configuración de otros campos
        reasonField = FormStyleManager.createStyledTextField();
        motorcycleDescField = FormStyleManager.createStyledTextField();
        motorcyclePlateField = FormStyleManager.createStyledTextField();
        notesArea = new JTextArea(4, 20);

        // Configuración del ComboBox de estado
        statusCombo = new JComboBox<>(new String[]{"Scheduled", "In progress", "Completed", "Cancelled"});

        // Configuración de selectores de fecha y hora
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        timePicker = new TimePicker();

        // Agregar los campos al formulario
        addFormField("Cliente:", customerCombo, gbc, 0);
        addFormField("Razón de la visita:", reasonField, gbc, 1);
        addFormField("Descripción de motocicleta:", motorcycleDescField, gbc, 2);
        addFormField("Placa de motocicleta:", motorcyclePlateField, gbc, 3);
        addFormField("Estado:", statusCombo, gbc, 4);
        addFormField("Fecha:", dateChooser, gbc, 5);
        addFormField("Hora:", timePicker, gbc, 6);
        addFormField("Notas:", new JScrollPane(notesArea), gbc, 7);

        // Configuración mejorada para editar servicios
        JButton editServiceButton = FormStyleManager.createSecondaryButton("Editar Servicios");
        editServiceButton.addActionListener(e -> editServices());
        gbc.gridy = 8;
        formPanel.add(editServiceButton, gbc);

        // Botón para agregar servicios
        JButton addServiceButton = FormStyleManager.createSecondaryButton("Agregar Servicio");
        addServiceButton.addActionListener(e -> addService());
        gbc.gridy = 9;
        formPanel.add(addServiceButton, gbc);
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

    private void addService() {
        String serviceName = JOptionPane.showInputDialog(this, "Ingrese el nombre del servicio:");
        if (serviceName == null || serviceName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del servicio no puede estar vacío.");
            return;
        }
        double chargedPrice;
        try {
            chargedPrice = Double.parseDouble(JOptionPane.showInputDialog(this, "Ingrese el precio del servicio:"));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El precio ingresado es inválido.");
            return;
        }

        AppointmentService service = new AppointmentService(0, 0, 0, chargedPrice, serviceName);
        services.add(service);

        JOptionPane.showMessageDialog(this, "Servicio agregado: " + serviceName);
    }

    private void editServices() {
        // Diseño mejorado para editar servicios
        JPanel servicePanel = new JPanel(new GridLayout(0, 1));
        for (AppointmentService service : services) {
            JLabel serviceLabel = new JLabel("Servicio: " + service.getNotes() + ", Precio: " + service.getChargedPrice());
            servicePanel.add(serviceLabel);
        }

        JOptionPane.showMessageDialog(this, servicePanel, "Servicios Agregados", JOptionPane.INFORMATION_MESSAGE);
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
    
            // Obtener la hora desde el TimePicker
            String timeText = timePicker.getText();
            if (timeText == null || timeText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar una hora.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            // Convertir la hora al formato de 24 horas
            java.sql.Time timeIn24Hours;
            try {
                java.time.LocalTime localTime = timePicker.getTime(); // Obtener el tiempo como LocalTime directamente del TimePicker
                timeIn24Hours = java.sql.Time.valueOf(localTime); // Convertir LocalTime a Time
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Formato de hora inválido. Asegúrese de usar correctamente el selector.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            // Configurar la cita
            WorkshopAppointment appointment = new WorkshopAppointment();
            Customer selectedCustomer = (Customer) customerCombo.getSelectedItem();
            appointment.setCustomerId(selectedCustomer.getCustomerId());
            appointment.setVisitReason(reasonField.getText().trim());
            appointment.setMotorcycleDescription(motorcycleDescField.getText().trim());
            appointment.setMotorcyclePlate(motorcyclePlateField.getText().trim());
            appointment.setStatus((String) statusCombo.getSelectedItem());
            appointment.setScheduledDate(new java.sql.Date(dateChooser.getDate().getTime()));
            appointment.setScheduledTime(timeIn24Hours); // Guardar la hora en formato de 24 horas
            appointment.setNotes(notesArea.getText());
            appointment.setUserId(1);
    
            // Guardar la cita
            if (workshopService.createAppointment(appointment, services)) {
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