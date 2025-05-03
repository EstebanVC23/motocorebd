package com.motocoredb.views.forms.sales;

import com.motocoredb.models.Sale;
import com.motocoredb.models.SaleDetail;
import com.motocoredb.dao.impl.AlertDaoImpl;
import com.motocoredb.models.Alert;
import com.motocoredb.models.Customer;
import com.motocoredb.models.Product;
import com.motocoredb.services.SaleService;
import com.motocoredb.utils.SessionManager;
import com.motocoredb.views.forms.NumericDocumentFilter;
import com.motocoredb.views.utils.FormStyleManager;
import com.motocoredb.services.AlertService;
import com.motocoredb.services.CustomerService;
import com.motocoredb.services.ProductService;

import javax.swing.*;
import javax.swing.text.AbstractDocument;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class AddSaleForm extends SaleFormBase {
    private final CustomerService customerService;
    private final ProductService productService;

    private JComboBox<Customer> customerCombo;
    private JComboBox<Product> productCombo;
    private JComboBox<String> paymentMethodCombo;
    private JTextField quantityField;
    private JTextField totalField;
    private JTextField discountField;
    private JTextArea notesField;

    public AddSaleForm(SaleService saleService, CustomerService customerService, ProductService productService) {
        super(saleService, "Registrar Nueva Venta", 750, 700);
        this.customerService = customerService;
        this.productService = productService;
        initializeUI();
    }

    @Override
    protected void initializeUI() {
        JPanel headerPanel = createHeaderPanel("Registrar Nueva Venta");
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
        customerService.getAllCustomers().forEach(customerCombo::addItem);
        customerCombo.setFont(FormStyleManager.FIELD_FONT);
        customerCombo.setPreferredSize(new Dimension(300, 30));
        customerCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Customer) {
                    setText(((Customer) value).getNameOrCompany());
                }
                return this;
            }
        });

        productCombo = new JComboBox<>();
        productService.getAllProducts().forEach(productCombo::addItem);
        productCombo.setFont(FormStyleManager.FIELD_FONT);
        productCombo.setPreferredSize(new Dimension(300, 30));
        productCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Product) {
                    setText(((Product) value).getProductName());
                }
                return this;
            }
        });

        quantityField = FormStyleManager.createStyledTextField();
        quantityField.setToolTipText("Ingrese la cantidad vendida");
        quantityField.setPreferredSize(new Dimension(300, 30));
        ((AbstractDocument) quantityField.getDocument()).setDocumentFilter(new NumericDocumentFilter());
        quantityField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateTotalField();
            }
        });

        totalField = FormStyleManager.createStyledTextField();
        totalField.setEditable(false);
        totalField.setBackground(new Color(240, 240, 240));
        totalField.setPreferredSize(new Dimension(300, 30));
        totalField.setToolTipText("Calculado automáticamente según cantidad y precio");

        discountField = FormStyleManager.createStyledTextField();
        discountField.setToolTipText("Ingrese el descuento (opcional)");
        discountField.setPreferredSize(new Dimension(300, 30));
        ((AbstractDocument) discountField.getDocument()).setDocumentFilter(new NumericDocumentFilter());

        notesField = new JTextArea();
        notesField.setLineWrap(true);
        notesField.setWrapStyleWord(true);
        notesField.setFont(FormStyleManager.FIELD_FONT);
        notesField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        notesField.setToolTipText("Ingrese notas adicionales (opcional)");

        paymentMethodCombo = new JComboBox<>(new String[]{"Cash", "Card", "Transfer", "Other"});
        paymentMethodCombo.setFont(FormStyleManager.FIELD_FONT);
        paymentMethodCombo.setPreferredSize(new Dimension(300, 30));
        paymentMethodCombo.setToolTipText("Seleccione el método de pago");
    }


    private void addFieldsToForm() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(FormStyleManager.createStyledLabel("Cliente:"), gbc);

        gbc.gridy = 1;
        formPanel.add(FormStyleManager.createStyledLabel("Producto:"), gbc);

        gbc.gridy = 2;
        formPanel.add(FormStyleManager.createStyledLabel("Cantidad:"), gbc);

        gbc.gridy = 3;
        formPanel.add(FormStyleManager.createStyledLabel("Total:"), gbc);

        gbc.gridy = 4;
        formPanel.add(FormStyleManager.createStyledLabel("Descuento:"), gbc);

        gbc.gridy = 5;
        formPanel.add(FormStyleManager.createStyledLabel("Notas:"), gbc);

        gbc.gridy = 6;
        formPanel.add(FormStyleManager.createStyledLabel("Método de Pago:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(customerCombo, gbc);

        gbc.gridy = 1;
        formPanel.add(productCombo, gbc);

        gbc.gridy = 2;
        formPanel.add(quantityField, gbc);

        gbc.gridy = 3;
        formPanel.add(totalField, gbc);

        gbc.gridy = 4;
        formPanel.add(discountField, gbc);

        gbc.gridy = 5;
        formPanel.add(new JScrollPane(notesField), gbc);

        gbc.gridy = 6;
        formPanel.add(paymentMethodCombo, gbc);
    }

    private void setupButtons() {
        JButton saveButton = FormStyleManager.createPrimaryButton("Registrar");
        JButton cancelButton = FormStyleManager.createSecondaryButton("Cancelar");

        saveButton.addActionListener(e -> saveSale());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
    }

    private void updateTotalField() {
        try {
            Product selectedProduct = (Product) productCombo.getSelectedItem();
            int quantity = Integer.parseInt(quantityField.getText());
            double subtotal = selectedProduct.getSalePrice() * quantity;
            totalField.setText(String.format("%.2f", subtotal));
        } catch (Exception ex) {
            totalField.setText("0.00");
        }
    }

    private void saveSale() {
        try {
            Sale sale = new Sale();

            String invoiceNumber = "INV-" + System.currentTimeMillis();
            sale.setInvoiceNumber(invoiceNumber);

            Customer customer = (Customer) customerCombo.getSelectedItem();
            sale.setCustomerId(customer.getCustomerId());

            ZonedDateTime colombiaDateTime = ZonedDateTime.now(ZoneId.of("America/Bogota"));
            sale.setSaleDate(Timestamp.from(colombiaDateTime.toInstant()));

            int userId = SessionManager.getCurrentUserId();
            sale.setUserId(userId);

            sale.setPaymentMethod((String) paymentMethodCombo.getSelectedItem());

            double subtotal = Double.parseDouble(totalField.getText());
            sale.setSubtotal(subtotal);
            double tax = subtotal * 0.19;
            sale.setTax(tax);

            double discount = discountField.getText().isEmpty() ? 0.0 : Double.parseDouble(discountField.getText());
            sale.setDiscount(discount);

            double total = subtotal + tax - discount;
            sale.setTotal(total);

            String notes = notesField.getText().isEmpty() ? null : notesField.getText();
            sale.setNotes(notes);

            sale.setStatus("Completed");

            List<SaleDetail> saleDetails = new ArrayList<>();
            Product selectedProduct = (Product) productCombo.getSelectedItem();
            SaleDetail detail = new SaleDetail();
            detail.setProductId(selectedProduct.getProductId());
            detail.setQuantity(Integer.parseInt(quantityField.getText()));
            detail.setUnitPrice(selectedProduct.getSalePrice());
            detail.setSubtotal(detail.getQuantity() * detail.getUnitPrice());
            saleDetails.add(detail);

            if (saleService.createSale(sale, saleDetails)) {
                customer.setPurchaseCount(customer.getPurchaseCount() + 1);
                customerService.updateCustomer(customer);

                productService.reduceStock(selectedProduct.getProductId(), detail.getQuantity());

                int updatedStock = selectedProduct.getCurrentStock() - detail.getQuantity();
                if (updatedStock <= selectedProduct.getMinStock()) {
                    AlertService alertService = new AlertService(new AlertDaoImpl());
                    
                    Alert newAlert = new Alert();
                    newAlert.setAlertType("Low stock");
                    newAlert.setMessage("El producto '" + selectedProduct.getProductName() + "' ha alcanzado o bajado del nivel mínimo de stock.");
                    newAlert.setGeneratedAt(new Timestamp(System.currentTimeMillis()));
                    newAlert.setStatus("Pending");
                    newAlert.setReferenceId(selectedProduct.getProductId());
                    newAlert.setReferenceType("Product");

                    boolean alertCreated = alertService.createAlert(newAlert);
                    if (!alertCreated) {
                        FormStyleManager.showErrorDialog(this, "Error al generar la alerta de bajo stock.");
                    }
                }

                FormStyleManager.showSuccessDialog(this, "Venta registrada exitosamente.");
                dispose();
            } else {
                FormStyleManager.showErrorDialog(this, "Error al registrar la venta");
            }
        } catch (Exception e) {
            FormStyleManager.showErrorDialog(this, "Error en los datos ingresados: " + e.getMessage());
        }
    }
}