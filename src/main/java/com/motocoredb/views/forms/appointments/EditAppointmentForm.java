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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

public class EditAppointmentForm extends AppointmentFormBase {
    private final WorkshopService workshopService;
    private final WorkshopAppointment appointment;
    private final List<Customer> customers;
    private List<AppointmentService> services;
    
    private JComboBox<Customer> customerCombo;
    private JTextField reasonField;
    private JTextField motorcycleDescField;
    private JTextField motorcyclePlateField;
    private JComboBox<String> statusCombo;
    private JTextArea notesArea;
    private JDateChooser dateChooser;
    private TimePicker timePicker;

    public EditAppointmentForm(WorkshopService workshopService, WorkshopAppointment appointment, List<Customer> customers) {
        super("Editar Cita", 600, 800); // Tamaño y título del formulario
        this.workshopService = workshopService;
        this.appointment = appointment; // Cita a editar
        this.customers = customers;
        this.services = workshopService.getAppointmentServices(appointment.getAppointmentId()); // Servicios asociados
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

        // Cargar los datos de la cita existente
        loadAppointmentData();
    }

    private void loadAppointmentData() {
        // Seleccionar el cliente correspondiente
        for (int i = 0; i < customerCombo.getItemCount(); i++) {
            Customer customer = customerCombo.getItemAt(i);
            if (customer.getCustomerId() == appointment.getCustomerId()) {
                customerCombo.setSelectedIndex(i);
                break;
            }
        }

        // Cargar los demás datos de la cita
        reasonField.setText(appointment.getVisitReason());
        motorcycleDescField.setText(appointment.getMotorcycleDescription());
        motorcyclePlateField.setText(appointment.getMotorcyclePlate());
        
        // Seleccionar el estado correspondiente
        statusCombo.setSelectedItem(appointment.getStatus());
        
        // Cargar fecha y hora
        dateChooser.setDate(appointment.getScheduledDate());
        
        // Convertir java.sql.Time a LocalTime para el TimePicker
        if (appointment.getScheduledTime() != null) {
            LocalTime localTime = appointment.getScheduledTime().toLocalTime();
            timePicker.setTime(localTime);
        }
        
        // Cargar notas
        notesArea.setText(appointment.getNotes());
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

        AppointmentService service = new AppointmentService(0, appointment.getAppointmentId(), 0, chargedPrice, serviceName);
        services.add(service);

        JOptionPane.showMessageDialog(this, "Servicio agregado: " + serviceName);
    }

    private void editServices() {
        if (services.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay servicios para editar.", "Sin servicios", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Panel para mostrar y editar servicios
        JPanel servicePanel = new JPanel(new GridLayout(0, 3, 10, 5));
        servicePanel.add(new JLabel("Servicio", SwingConstants.CENTER));
        servicePanel.add(new JLabel("Precio", SwingConstants.CENTER));
        servicePanel.add(new JLabel("Acción", SwingConstants.CENTER));
        
        List<AppointmentService> servicesToRemove = new ArrayList<>();
        
        for (AppointmentService service : services) {
            JLabel serviceLabel = new JLabel(service.getNotes());
            JLabel priceLabel = new JLabel(String.format("%.2f", service.getChargedPrice()));
            JButton removeButton = new JButton("Eliminar");
            
            removeButton.addActionListener(e -> {
                servicesToRemove.add(service);
                JOptionPane.showMessageDialog(this, "Servicio marcado para eliminación: " + service.getNotes());
            });
            
            servicePanel.add(serviceLabel);
            servicePanel.add(priceLabel);
            servicePanel.add(removeButton);
        }
        
        JScrollPane scrollPane = new JScrollPane(servicePanel);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        
        int result = JOptionPane.showConfirmDialog(this, scrollPane, "Editar Servicios", 
                                                  JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION && !servicesToRemove.isEmpty()) {
            services.removeAll(servicesToRemove);
            JOptionPane.showMessageDialog(this, "Servicios actualizados correctamente.");
        }
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

            // Actualizar los datos de la cita existente
            Customer selectedCustomer = (Customer) customerCombo.getSelectedItem();
            appointment.setCustomerId(selectedCustomer.getCustomerId());
            appointment.setVisitReason(reasonField.getText().trim());
            appointment.setMotorcycleDescription(motorcycleDescField.getText().trim());
            appointment.setMotorcyclePlate(motorcyclePlateField.getText().trim());
            String oldStatus = appointment.getStatus();
            String newStatus = (String) statusCombo.getSelectedItem();
            appointment.setStatus(newStatus);
            appointment.setScheduledDate(new java.sql.Date(dateChooser.getDate().getTime()));
            appointment.setScheduledTime(timeIn24Hours);
            appointment.setNotes(notesArea.getText());

            // Guardar la cita actualizada
            if (workshopService.updateAppointment(appointment, services)) {
                // Crear alerta si la cita se marca como "Programada" (Scheduled)
                if ("Scheduled".equals(newStatus) && !"Scheduled".equals(oldStatus)) {
                    try {
                        AlertService alertService = new AlertService(new AlertDaoImpl()); // Inicializar el servicio de alertas

                        Alert newAlert = new Alert();
                        newAlert.setAlertType("Upcoming appointment");
                        newAlert.setMessage("La cita para el cliente '" + selectedCustomer.getNameOrCompany() +
                                            "' con la motocicleta placa '" + appointment.getMotorcyclePlate() + "' fue actualizada para el día " +
                                            appointment.getScheduledDate() + " a las " + timeIn24Hours + ".");
                        newAlert.setGeneratedAt(new Timestamp(System.currentTimeMillis()));
                        newAlert.setStatus("Pending");
                        newAlert.setReferenceId(appointment.getAppointmentId()); // ID de la cita como referencia
                        newAlert.setReferenceType("Appointment");

                        boolean alertCreated = alertService.createAlert(newAlert); // Registrar la alerta
                        if (!alertCreated) {
                            JOptionPane.showMessageDialog(this, "Error al generar la alerta para la cita actualizada.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception alertEx) {
                        JOptionPane.showMessageDialog(this, "Error al procesar la alerta: " + alertEx.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

                FormStyleManager.showSuccessDialog(this, "Cita actualizada exitosamente.");
                dispose();
            } else {
                FormStyleManager.showErrorDialog(this, "Error al actualizar la cita. Verifica los datos.");
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