package com.motocoredb.views.forms.products;

import com.motocoredb.models.Product;
import com.motocoredb.models.Supplier;
import com.motocoredb.models.ProductCategory;
import com.motocoredb.models.InventoryMovement;
import com.motocoredb.services.ProductService;
import com.motocoredb.services.SupplierService;
import com.motocoredb.views.utils.FormStyleManager;
import com.motocoredb.services.CategoryService;
import com.motocoredb.services.InventoryService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class EditProductForm extends ProductFormBase {
    private final Product product;
    private JTextField nameField;
    private JTextField descriptionField;
    private JTextField purchasePriceField;
    private JTextField salePriceField;
    private JTextField stockField;
    private JTextField minStockField;
    private JComboBox<Supplier> supplierCombo;
    private JComboBox<ProductCategory> categoryCombo;

    private final SupplierService supplierService;
    private final CategoryService categoryService;
    private final InventoryService inventoryService;

    public EditProductForm(ProductService productService, SupplierService supplierService, 
                           CategoryService categoryService, InventoryService inventoryService, 
                           Product product) {
        super(productService, "Editar Producto", 600, 800);
        this.product = product;
        this.supplierService = supplierService;
        this.categoryService = categoryService;
        this.inventoryService = inventoryService;
        initializeUI();
    }

    @Override
    protected void initializeUI() {
        JPanel headerPanel = createHeaderPanel("Editar Producto: " + product.getProductCode());
        JPanel infoPanel = createInfoPanel();

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setOpaque(false);
        northPanel.add(headerPanel, BorderLayout.NORTH);
        northPanel.add(infoPanel, BorderLayout.CENTER);

        formPanel = createFormPanel("Modificar Información");

        initializeFields();
        addFieldsToForm();
        setupButtons();

        JPanel statsPanel = createStatsPanel();

        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(formPanel, BorderLayout.CENTER);
        centerPanel.add(statsPanel, BorderLayout.SOUTH);

        mainPanel.add(northPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setOpaque(false);

        JLabel productIdLabel = new JLabel("ID: " + product.getProductCode());
        productIdLabel.setFont(FormStyleManager.FIELD_FONT);
        productIdLabel.setForeground(FormStyleManager.SECONDARY_COLOR);
        infoPanel.add(productIdLabel);

        return infoPanel;
    }

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

    private void initializeFields() {
        nameField = FormStyleManager.createStyledTextField();
        nameField.setText(product.getProductName());

        descriptionField = FormStyleManager.createStyledTextField();
        descriptionField.setText(product.getDescription());

        purchasePriceField = FormStyleManager.createStyledTextField();
        purchasePriceField.setText(String.valueOf(product.getPurchasePrice()));

        salePriceField = FormStyleManager.createStyledTextField();
        salePriceField.setText(String.valueOf(product.getSalePrice()));

        stockField = FormStyleManager.createStyledTextField();
        stockField.setText(String.valueOf(product.getCurrentStock()));

        minStockField = FormStyleManager.createStyledTextField();
        minStockField.setText(String.valueOf(product.getMinStock()));

        supplierCombo = new JComboBox<>();
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        suppliers.forEach(supplierCombo::addItem);
        supplierCombo.setSelectedItem(product.getSupplier());
        supplierCombo.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value.getCompanyName());
            return label;
        });

        categoryCombo = new JComboBox<>();
        List<ProductCategory> categories = categoryService.getAllCategories();
        categories.forEach(categoryCombo::addItem);
        categoryCombo.setSelectedItem(product.getCategory());
        categoryCombo.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value.getCategoryName());
            return label;
        });
    }

    private void addFieldsToForm() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        formPanel.add(FormStyleManager.createStyledLabel("Nombre:"), gbc);

        gbc.gridy = 1;
        formPanel.add(FormStyleManager.createStyledLabel("Descripción:"), gbc);

        gbc.gridy = 2;
        formPanel.add(FormStyleManager.createStyledLabel("Precio de Compra:"), gbc);

        gbc.gridy = 3;
        formPanel.add(FormStyleManager.createStyledLabel("Precio de Venta:"), gbc);

        gbc.gridy = 4;
        formPanel.add(FormStyleManager.createStyledLabel("Stock Actual:"), gbc);

        gbc.gridy = 5;
        formPanel.add(FormStyleManager.createStyledLabel("Stock Mínimo:"), gbc);

        gbc.gridy = 6;
        formPanel.add(FormStyleManager.createStyledLabel("Proveedor:"), gbc);

        gbc.gridy = 7;
        formPanel.add(FormStyleManager.createStyledLabel("Categoría:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        formPanel.add(nameField, gbc);

        gbc.gridy = 1;
        formPanel.add(descriptionField, gbc);

        gbc.gridy = 2;
        formPanel.add(purchasePriceField, gbc);

        gbc.gridy = 3;
        formPanel.add(salePriceField, gbc);

        gbc.gridy = 4;
        formPanel.add(stockField, gbc);

        gbc.gridy = 5;
        formPanel.add(minStockField, gbc);

        gbc.gridy = 6;
        formPanel.add(supplierCombo, gbc);

        gbc.gridy = 7;
        formPanel.add(categoryCombo, gbc);
    }

    private void setupButtons() {
        JButton saveButton = FormStyleManager.createPrimaryButton("Actualizar");
        JButton cancelButton = FormStyleManager.createSecondaryButton("Cancelar");

        saveButton.addActionListener((ActionEvent e) -> updateProduct());
        cancelButton.addActionListener((ActionEvent e) -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
    }

    private void updateProduct() {
        try {
            int previousStock = product.getCurrentStock();

            product.setProductName(nameField.getText());
            product.setDescription(descriptionField.getText());
            product.setPurchasePrice(Double.parseDouble(purchasePriceField.getText()));
            product.setSalePrice(Double.parseDouble(salePriceField.getText()));
            product.setCurrentStock(Integer.parseInt(stockField.getText()));
            product.setMinStock(Integer.parseInt(minStockField.getText()));

            Supplier selectedSupplier = (Supplier) supplierCombo.getSelectedItem();
            product.setSupplier(selectedSupplier);

            ProductCategory selectedCategory = (ProductCategory) categoryCombo.getSelectedItem();
            product.setCategory(selectedCategory);

            if (productService.updateProduct(product)) {
                // Registrar un movimiento de inventario si el stock actual ha cambiado
                if (product.getCurrentStock() != previousStock) {
                    InventoryMovement movement = new InventoryMovement();
                    movement.setProductId(product.getProductId());
                    movement.setMovementType(product.getCurrentStock() > previousStock ? "In" : "Out");
                    movement.setQuantity(Math.abs(product.getCurrentStock() - previousStock));
                    movement.setReferenceId(product.getProductId());
                    movement.setReferenceType("Adjustment");
                    movement.setNotes("Actualización del producto desde el formulario de edición");
                    movement.setUserId(1); // Reemplazar con ID real del usuario

                    inventoryService.recordMovement(movement);
                }

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