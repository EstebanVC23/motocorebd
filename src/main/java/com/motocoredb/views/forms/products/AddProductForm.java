package com.motocoredb.views.forms.products;

import com.motocoredb.models.Product;
import com.motocoredb.services.ProductService;
import com.motocoredb.views.forms.utils.FormStyleManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AddProductForm extends ProductFormBase {
    private JTextField codeField;
    private JTextField nameField;
    private JTextField purchasePriceField;
    private JTextField salePriceField;
    private JTextField stockField;
    private JTextField minStockField;

    public AddProductForm(ProductService productService) {
        super(productService, "Agregar Nuevo Producto", 600, 620);
        initializeUI();
    }

    @Override
    protected void initializeUI() {
        // Crear panel de encabezado
        JPanel headerPanel = createHeaderPanel("Agregar Nuevo Producto", "/icons/product_add.png");
        
        // Crear panel de formulario
        formPanel = createFormPanel("Información del Producto");
        
        // Inicializar campos
        initializeFields();
        
        // Configurar campos en el formulario
        addFieldsToForm();
        
        // Configurar botones
        setupButtons();
        
        // Ensamblar formulario
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Inicializa los campos del formulario
     */
    private void initializeFields() {
        codeField = FormStyleManager.createStyledTextField();
        nameField = FormStyleManager.createStyledTextField();
        purchasePriceField = FormStyleManager.createStyledTextField();
        salePriceField = FormStyleManager.createStyledTextField();
        stockField = FormStyleManager.createStyledTextField();
        minStockField = FormStyleManager.createStyledTextField();
    }
    
    /**
     * Añade los campos al panel del formulario
     */
    private void addFieldsToForm() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Primera columna (etiquetas)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        formPanel.add(FormStyleManager.createStyledLabel("Código:"), gbc);
        
        gbc.gridy = 1;
        formPanel.add(FormStyleManager.createStyledLabel("Nombre:"), gbc);
        
        gbc.gridy = 2;
        formPanel.add(FormStyleManager.createStyledLabel("Precio de Compra:"), gbc);
        
        gbc.gridy = 3;
        formPanel.add(FormStyleManager.createStyledLabel("Precio de Venta:"), gbc);
        
        gbc.gridy = 4;
        formPanel.add(FormStyleManager.createStyledLabel("Stock Actual:"), gbc);
        
        gbc.gridy = 5;
        formPanel.add(FormStyleManager.createStyledLabel("Stock Mínimo:"), gbc);
        
        // Segunda columna (campos)
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        formPanel.add(codeField, gbc);
        
        gbc.gridy = 1;
        formPanel.add(nameField, gbc);
        
        gbc.gridy = 2;
        formPanel.add(purchasePriceField, gbc);
        
        gbc.gridy = 3;
        formPanel.add(salePriceField, gbc);
        
        gbc.gridy = 4;
        formPanel.add(stockField, gbc);
        
        gbc.gridy = 5;
        formPanel.add(minStockField, gbc);
    }
    
    /**
     * Configura los botones y sus acciones
     */
    private void setupButtons() {
        JButton saveButton = FormStyleManager.createPrimaryButton("Guardar");
        JButton cancelButton = FormStyleManager.createSecondaryButton("Cancelar");
        
        saveButton.addActionListener((ActionEvent e) -> saveProduct());
        cancelButton.addActionListener((ActionEvent e) -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
    }
    
    /**
     * Guarda el producto con los datos del formulario
     */
    private void saveProduct() {
        try {
            Product newProduct = new Product();
            newProduct.setProductCode(codeField.getText());
            newProduct.setProductName(nameField.getText());
            newProduct.setPurchasePrice(Double.parseDouble(purchasePriceField.getText()));
            newProduct.setSalePrice(Double.parseDouble(salePriceField.getText()));
            newProduct.setCurrentStock(Integer.parseInt(stockField.getText()));
            newProduct.setMinStock(Integer.parseInt(minStockField.getText()));
            newProduct.setStatus("Active");

            if (productService.createProduct(newProduct)) {
                FormStyleManager.showSuccessDialog(this, "Producto agregado exitosamente");
                dispose();
            } else {
                FormStyleManager.showErrorDialog(this, "Error al agregar producto");
            }
        } catch (Exception ex) {
            FormStyleManager.showErrorDialog(this, "Error en los datos ingresados: " + ex.getMessage());
        }
    }
}

