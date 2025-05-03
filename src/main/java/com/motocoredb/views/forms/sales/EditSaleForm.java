package com.motocoredb.views.forms.sales;

import com.motocoredb.models.Sale;
import com.motocoredb.models.SaleDetail;
import com.motocoredb.dao.impl.AlertDaoImpl;
import com.motocoredb.models.Alert;
import com.motocoredb.models.Customer;
import com.motocoredb.models.Product;
import com.motocoredb.services.SaleService;
import com.motocoredb.views.utils.FormStyleManager;
import com.motocoredb.services.AlertService;
import com.motocoredb.services.CustomerService;
import com.motocoredb.services.ProductService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.sql.Timestamp;

public class EditSaleForm extends SaleFormBase {
    private final CustomerService customerService;
    private final ProductService productService;
    private final Sale sale;
    private final SaleDetail saleDetail;

    private JComboBox<Customer> customerCombo;
    private JComboBox<String> statusCombo;
    private JLabel productLabel;
    private JLabel quantityLabel;
    private JTextArea notesField;

    public EditSaleForm(SaleService saleService, CustomerService customerService, ProductService productService, Sale sale, SaleDetail saleDetail) {
        super(saleService, "Editar Venta", 800, 650);
        this.customerService = customerService;
        this.productService = productService;
        this.sale = sale;
        this.saleDetail = saleDetail;
        initializeUI();
    }

    @Override
    protected void initializeUI() {
        JPanel headerPanel = createHeaderPanel("Editar Venta: #" + sale.getInvoiceNumber());
        formPanel = createFormPanel("Detalles de la Venta");

        initializeFields();
        addFieldsToForm();
        setupButtons();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void initializeFields() {
        customerCombo = new JComboBox<>();
        List<Customer> customers = customerService.getAllCustomers();
        customers.forEach(customerCombo::addItem);
        customerCombo.setSelectedItem(customerService.getCustomerById(sale.getCustomerId()));
        customerCombo.setFont(FormStyleManager.FIELD_FONT);
        customerCombo.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(((Customer) value).getNameOrCompany());
            return label;
        });

        Product product = productService.getProductById(saleDetail.getProductId());
        productLabel = new JLabel(product != null ? product.getProductName() : "Producto no encontrado");
        productLabel.setFont(FormStyleManager.FIELD_FONT);

        quantityLabel = new JLabel("Cantidad vendida: " + saleDetail.getQuantity());
        quantityLabel.setFont(FormStyleManager.FIELD_FONT);

        statusCombo = new JComboBox<>(new String[]{"Completed", "Cancelled"});
        statusCombo.setSelectedItem(sale.getStatus());
        statusCombo.setFont(FormStyleManager.FIELD_FONT);

        notesField = new JTextArea(5, 20);
        notesField.setLineWrap(true);
        notesField.setWrapStyleWord(true);
        notesField.setText(sale.getNotes() != null ? sale.getNotes() : "");
        notesField.setFont(FormStyleManager.FIELD_FONT);
    }

    private void addFieldsToForm() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(FormStyleManager.createStyledLabel("Cliente: "), gbc);

        gbc.gridy = 1;
        formPanel.add(FormStyleManager.createStyledLabel("Producto: "), gbc);

        gbc.gridy = 2;
        formPanel.add(FormStyleManager.createStyledLabel("Cantidad: "), gbc);

        gbc.gridy = 3;
        formPanel.add(FormStyleManager.createStyledLabel("Estado: "), gbc);

        gbc.gridy = 4;
        formPanel.add(FormStyleManager.createStyledLabel("Notas: "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(customerCombo, gbc);

        gbc.gridy = 1;
        formPanel.add(productLabel, gbc);

        gbc.gridy = 2;
        formPanel.add(quantityLabel, gbc);

        gbc.gridy = 3;
        formPanel.add(statusCombo, gbc);

        gbc.gridy = 4;
        formPanel.add(new JScrollPane(notesField), gbc);
    }

    private void setupButtons() {
        JButton saveButton = FormStyleManager.createPrimaryButton("Guardar Cambios");
        JButton cancelButton = FormStyleManager.createSecondaryButton("Cancelar");

        saveButton.addActionListener(this::updateSale);
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
    }

    private void updateSale(ActionEvent e) {
        try {
            sale.setCustomerId(((Customer) customerCombo.getSelectedItem()).getCustomerId());

            String newStatus = (String) statusCombo.getSelectedItem();
            String oldStatus = sale.getStatus();
            sale.setStatus(newStatus);

            sale.setNotes(notesField.getText().isEmpty() ? null : notesField.getText());

            Product product = productService.getProductById(saleDetail.getProductId());
            if (product != null) {
                if ("Completed".equals(oldStatus) && "Cancelled".equals(newStatus)) {
                    productService.increaseStock(product.getProductId(), saleDetail.getQuantity());
                } else if ("Cancelled".equals(oldStatus) && "Completed".equals(newStatus)) {
                    productService.reduceStock(product.getProductId(), saleDetail.getQuantity());

                    int updatedStock = product.getCurrentStock() - saleDetail.getQuantity();
                    if (updatedStock <= product.getMinStock()) {
                        AlertService alertService = new AlertService(new AlertDaoImpl());

                        Alert newAlert = new Alert();
                        newAlert.setAlertType("Low stock");
                        newAlert.setMessage("El producto '" + product.getProductName() + "' ha alcanzado o bajado del nivel mínimo de stock.");
                        newAlert.setGeneratedAt(new Timestamp(System.currentTimeMillis()));
                        newAlert.setStatus("Pending");
                        newAlert.setReferenceId(product.getProductId());
                        newAlert.setReferenceType("Product");

                        boolean alertCreated = alertService.createAlert(newAlert);
                        if (!alertCreated) {
                            FormStyleManager.showErrorDialog(this, "Error al generar la alerta de bajo stock.");
                        }
                    }
                }
            }

            if (saleService.updateSale(sale)) {
                FormStyleManager.showSuccessDialog(this, "Venta actualizada exitosamente.");
                dispose();
            } else {
                FormStyleManager.showErrorDialog(this, "Error al actualizar la venta.");
            }
        } catch (Exception ex) {
            FormStyleManager.showErrorDialog(this, "Error en los datos ingresados: " + ex.getMessage());
        }
    }
}