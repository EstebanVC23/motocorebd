package com.motocoredb.views.Main.Admin.paneles.forms;

import com.motocoredb.models.Product;
import com.motocoredb.services.ProductService;
import com.motocoredb.views.Main.Admin.paneles.forms.utils.ProductFormStyleManager;

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
        super(productService, "Agregar Nuevo Producto", 600, 600);
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
        codeField = ProductFormStyleManager.createStyledTextField();
        nameField = ProductFormStyleManager.createStyledTextField();
        purchasePriceField = ProductFormStyleManager.createStyledTextField();
        salePriceField = ProductFormStyleManager.createStyledTextField();
        stockField = ProductFormStyleManager.createStyledTextField();
        minStockField = ProductFormStyleManager.createStyledTextField();
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
        formPanel.add(ProductFormStyleManager.createStyledLabel("Código:"), gbc);
        
        gbc.gridy = 1;
        formPanel.add(ProductFormStyleManager.createStyledLabel("Nombre:"), gbc);
        
        gbc.gridy = 2;
        formPanel.add(ProductFormStyleManager.createStyledLabel("Precio de Compra:"), gbc);
        
        gbc.gridy = 3;
        formPanel.add(ProductFormStyleManager.createStyledLabel("Precio de Venta:"), gbc);
        
        gbc.gridy = 4;
        formPanel.add(ProductFormStyleManager.createStyledLabel("Stock Actual:"), gbc);
        
        gbc.gridy = 5;
        formPanel.add(ProductFormStyleManager.createStyledLabel("Stock Mínimo:"), gbc);
        
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
        JButton saveButton = ProductFormStyleManager.createPrimaryButton("Guardar");
        JButton cancelButton = ProductFormStyleManager.createSecondaryButton("Cancelar");
        
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
            newProduct.setName(nameField.getText());
            newProduct.setPurchasePrice(Double.parseDouble(purchasePriceField.getText()));
            newProduct.setSalePrice(Double.parseDouble(salePriceField.getText()));
            newProduct.setCurrentStock(Integer.parseInt(stockField.getText()));
            newProduct.setMinStock(Integer.parseInt(minStockField.getText()));
            newProduct.setStatus("Active");

            if (productService.createProduct(newProduct)) {
                ProductFormStyleManager.showSuccessDialog(this, "Producto agregado exitosamente");
                dispose();
            } else {
                ProductFormStyleManager.showErrorDialog(this, "Error al agregar producto");
            }
        } catch (Exception ex) {
            ProductFormStyleManager.showErrorDialog(this, "Error en los datos ingresados: " + ex.getMessage());
        }
    }
}

