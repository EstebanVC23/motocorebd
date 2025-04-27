package com.motocoredb.views.forms.sales;

import com.motocoredb.models.Sale;
import com.motocoredb.models.SaleDetail;
import com.motocoredb.models.Customer;
import com.motocoredb.models.Product;
import com.motocoredb.services.SaleService;
import com.motocoredb.services.CustomerService;
import com.motocoredb.services.ProductService;
import com.motocoredb.views.forms.utils.FormStyleManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

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
        JPanel headerPanel = createHeaderPanel("Editar Venta: #" + sale.getInvoiceNumber(), "/icons/sale_edit.png");
        formPanel = createFormPanel("Detalles de la Venta");

        initializeFields();
        addFieldsToForm();
        setupButtons();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void initializeFields() {
        // Campo para seleccionar cliente
        customerCombo = new JComboBox<>();
        List<Customer> customers = customerService.getAllCustomers();
        customers.forEach(customerCombo::addItem);
        customerCombo.setSelectedItem(customerService.getCustomerById(sale.getCustomerId()));
        customerCombo.setFont(FormStyleManager.FIELD_FONT);
        customerCombo.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(((Customer) value).getNameOrCompany());
            return label;
        });

        // Etiqueta para mostrar el nombre del producto
        Product product = productService.getProductById(saleDetail.getProductId());
        productLabel = new JLabel(product != null ? product.getProductName() : "Producto no encontrado");
        productLabel.setFont(FormStyleManager.FIELD_FONT);

        // Etiqueta para mostrar la cantidad
        quantityLabel = new JLabel("Cantidad vendida: " + saleDetail.getQuantity());
        quantityLabel.setFont(FormStyleManager.FIELD_FONT);

        // Desplegable para el estado de la venta
        statusCombo = new JComboBox<>(new String[]{"Completed", "Cancelled"});
        statusCombo.setSelectedItem(sale.getStatus());
        statusCombo.setFont(FormStyleManager.FIELD_FONT);

        // Campo de notas
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
            // Actualizar cliente
            sale.setCustomerId(((Customer) customerCombo.getSelectedItem()).getCustomerId());
    
            // Cambiar estado
            String newStatus = (String) statusCombo.getSelectedItem();
            String oldStatus = sale.getStatus();
            sale.setStatus(newStatus);
    
            // Actualizar notas
            sale.setNotes(notesField.getText().isEmpty() ? null : notesField.getText());
    
            // Manejo del stock según el cambio de estado
            Product product = productService.getProductById(saleDetail.getProductId());
            if (product != null) {
                if ("Completed".equals(oldStatus) && "Cancelled".equals(newStatus)) {
                    // Devolver stock
                    productService.increaseStock(product.getProductId(), saleDetail.getQuantity());
                } else if ("Cancelled".equals(oldStatus) && "Completed".equals(newStatus)) {
                    // Reducir stock
                    productService.reduceStock(product.getProductId(), saleDetail.getQuantity());
                }
            }
    
            // Guardar cambios
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