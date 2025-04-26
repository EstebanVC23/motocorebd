package com.motocoredb.views.forms.products;

import com.motocoredb.models.Product;
import com.motocoredb.services.ProductService;
import com.motocoredb.views.forms.utils.FormStyleManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class EditProductForm extends ProductFormBase {
    private final Product product;
    private JTextField nameField;
    private JTextField purchasePriceField;
    private JTextField salePriceField;
    private JTextField stockField;
    private JTextField minStockField;

    public EditProductForm(ProductService productService, Product product) {
        super(productService, "Editar Producto", 600, 650);
        this.product = product;
        initializeUI();
    }

    @Override
    protected void initializeUI() {
        // Crear panel de encabezado
        JPanel headerPanel = createHeaderPanel("Editar Producto: " + product.getProductCode(), "/icons/product_edit.png");
        
        // Panel de información del producto
        JPanel infoPanel = createInfoPanel();
        
        // Panel norte combinado
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setOpaque(false);
        northPanel.add(headerPanel, BorderLayout.NORTH);
        northPanel.add(infoPanel, BorderLayout.CENTER);
        
        // Crear panel de formulario
        formPanel = createFormPanel("Modificar Información");
        
        // Inicializar campos
        initializeFields();
        
        // Configurar campos en el formulario
        addFieldsToForm();
        
        // Panel de estado
        JPanel statsPanel = createStatsPanel();
        
        // Panel central combinado
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(formPanel, BorderLayout.CENTER);
        centerPanel.add(statsPanel, BorderLayout.SOUTH);
        
        // Configurar botones
        setupButtons();
        
        // Ensamblar formulario
        mainPanel.add(northPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Crea el panel de información del producto
     */
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setOpaque(false);
        
        JLabel productIdLabel = new JLabel("ID: " + product.getProductCode());
        productIdLabel.setFont(FormStyleManager.FIELD_FONT);
        productIdLabel.setForeground(FormStyleManager.SECONDARY_COLOR);
        infoPanel.add(productIdLabel);
        
        return infoPanel;
    }
    
    /**
     * Crea el panel de estadísticas/estado del producto
     */
    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setOpaque(false);
        
        JLabel statusLabel = new JLabel("Estado: " + product.getStatus());
        statusLabel.setFont(FormStyleManager.FIELD_FONT);
        statusLabel.setForeground(
            product.getStatus().equalsIgnoreCase("Active") ? 
            FormStyleManager.SUCCESS_COLOR : FormStyleManager.ERROR_COLOR
        );
        statsPanel.add(statusLabel);
        
        return statsPanel;
    }
    
    /**
     * Inicializa los campos del formulario con los valores del producto
     */
    private void initializeFields() {
        nameField = FormStyleManager.createStyledTextField();
        nameField.setText(product.getName());
        
        purchasePriceField = FormStyleManager.createStyledTextField();
        purchasePriceField.setText(String.valueOf(product.getPurchasePrice()));
        
        salePriceField = FormStyleManager.createStyledTextField();
        salePriceField.setText(String.valueOf(product.getSalePrice()));
        
        stockField = FormStyleManager.createStyledTextField();
        stockField.setText(String.valueOf(product.getCurrentStock()));
        
        minStockField = FormStyleManager.createStyledTextField();
        minStockField.setText(String.valueOf(product.getMinStock()));
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
        formPanel.add(FormStyleManager.createStyledLabel("Nombre:"), gbc);
        
        gbc.gridy = 1;
        formPanel.add(FormStyleManager.createStyledLabel("Precio de Compra:"), gbc);
        
        gbc.gridy = 2;
        formPanel.add(FormStyleManager.createStyledLabel("Precio de Venta:"), gbc);
        
        gbc.gridy = 3;
        formPanel.add(FormStyleManager.createStyledLabel("Stock Actual:"), gbc);
        
        gbc.gridy = 4;
        formPanel.add(FormStyleManager.createStyledLabel("Stock Mínimo:"), gbc);
        
        // Segunda columna (campos)
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        formPanel.add(nameField, gbc);
        
        gbc.gridy = 1;
        formPanel.add(purchasePriceField, gbc);
        
        gbc.gridy = 2;
        formPanel.add(salePriceField, gbc);
        
        gbc.gridy = 3;
        formPanel.add(stockField, gbc);
        
        gbc.gridy = 4;
        formPanel.add(minStockField, gbc);
    }
    
    /**
     * Configura los botones y sus acciones
     */
    private void setupButtons() {
        JButton saveButton = FormStyleManager.createPrimaryButton("Actualizar");
        JButton cancelButton = FormStyleManager.createSecondaryButton("Cancelar");
        
        saveButton.addActionListener((ActionEvent e) -> updateProduct());
        cancelButton.addActionListener((ActionEvent e) -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
    }
    
    /**
     * Actualiza el producto con los datos del formulario
     */
    private void updateProduct() {
        try {
            product.setName(nameField.getText());
            product.setPurchasePrice(Double.parseDouble(purchasePriceField.getText()));
            product.setSalePrice(Double.parseDouble(salePriceField.getText()));
            product.setCurrentStock(Integer.parseInt(stockField.getText()));
            product.setMinStock(Integer.parseInt(minStockField.getText()));

            if (productService.updateProduct(product)) {
                FormStyleManager.showSuccessDialog(this, "Producto actualizado exitosamente");
                dispose();
            } else {
                FormStyleManager.showErrorDialog(this, "Error al actualizar producto");
            }
        } catch (Exception ex) {
            FormStyleManager.showErrorDialog(this, "Error en los datos ingresados: " + ex.getMessage());
        }
    }
}
