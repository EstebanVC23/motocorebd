package com.motocoredb.views.forms.products;

import com.motocoredb.models.Product;
import com.motocoredb.models.Supplier;
import com.motocoredb.models.ProductCategory;
import com.motocoredb.models.InventoryMovement;
import com.motocoredb.models.Purchase;
import com.motocoredb.models.PurchaseDetail;
import com.motocoredb.services.ProductService;
import com.motocoredb.services.SupplierService;
import com.motocoredb.views.forms.NumericDocumentFilter;
import com.motocoredb.views.utils.FormStyleManager;
import com.motocoredb.services.CategoryService;
import com.motocoredb.services.InventoryService;
import com.motocoredb.services.PurchaseService;

import javax.swing.*;
import javax.swing.text.AbstractDocument;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Timestamp;
import java.util.List;

public class AddProductForm extends ProductFormBase {
    private JTextField codeField;
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
    private final PurchaseService purchaseService;

    public AddProductForm(ProductService productService, SupplierService supplierService, 
                        CategoryService categoryService, InventoryService inventoryService, 
                        PurchaseService purchaseService) {
        super(productService, "Agregar Nuevo Producto", 600, 800);
        this.supplierService = supplierService;
        this.categoryService = categoryService;
        this.inventoryService = inventoryService;
        this.purchaseService = purchaseService;
        initializeUI();
    }


    @Override
    protected void initializeUI() {
        JPanel headerPanel = createHeaderPanel("Agregar Nuevo Producto");
        formPanel = createFormPanel("Información del Producto");

        initializeFields();
        addFieldsToForm();
        setupButtons();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void initializeFields() {
        codeField = FormStyleManager.createStyledTextField();
        nameField = FormStyleManager.createStyledTextField();
        descriptionField = FormStyleManager.createStyledTextField();

        purchasePriceField = FormStyleManager.createStyledTextField();
        ((AbstractDocument) purchasePriceField.getDocument()).setDocumentFilter(new NumericDocumentFilter()); // Solo números

        salePriceField = FormStyleManager.createStyledTextField();
        ((AbstractDocument) salePriceField.getDocument()).setDocumentFilter(new NumericDocumentFilter()); // Solo números

        stockField = FormStyleManager.createStyledTextField();
        ((AbstractDocument) stockField.getDocument()).setDocumentFilter(new NumericDocumentFilter()); // Solo números

        minStockField = FormStyleManager.createStyledTextField();
        minStockField.setText("5");
        ((AbstractDocument) minStockField.getDocument()).setDocumentFilter(new NumericDocumentFilter()); // Solo números

        supplierCombo = new JComboBox<>();
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        suppliers.forEach(supplierCombo::addItem);
        supplierCombo.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value.getCompanyName());
            return label;
        });

        categoryCombo = new JComboBox<>();
        List<ProductCategory> categories = categoryService.getAllCategories();
        categories.forEach(categoryCombo::addItem);
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

        // Primera columna (etiquetas)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        formPanel.add(FormStyleManager.createStyledLabel("Código:"), gbc);

        gbc.gridy = 1;
        formPanel.add(FormStyleManager.createStyledLabel("Nombre:"), gbc);

        gbc.gridy = 2;
        formPanel.add(FormStyleManager.createStyledLabel("Descripción:"), gbc);

        gbc.gridy = 3;
        formPanel.add(FormStyleManager.createStyledLabel("Precio de Compra:"), gbc);

        gbc.gridy = 4;
        formPanel.add(FormStyleManager.createStyledLabel("Precio de Venta:"), gbc);

        gbc.gridy = 5;
        formPanel.add(FormStyleManager.createStyledLabel("Stock Actual:"), gbc);

        gbc.gridy = 6;
        formPanel.add(FormStyleManager.createStyledLabel("Stock Mínimo:"), gbc);

        gbc.gridy = 7;
        formPanel.add(FormStyleManager.createStyledLabel("Proveedor:"), gbc);

        gbc.gridy = 8;
        formPanel.add(FormStyleManager.createStyledLabel("Categoría:"), gbc);

        // Segunda columna (campos)
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        formPanel.add(codeField, gbc);

        gbc.gridy = 1;
        formPanel.add(nameField, gbc);

        gbc.gridy = 2;
        formPanel.add(descriptionField, gbc);

        gbc.gridy = 3;
        formPanel.add(purchasePriceField, gbc);

        gbc.gridy = 4;
        formPanel.add(salePriceField, gbc);

        gbc.gridy = 5;
        formPanel.add(stockField, gbc);

        gbc.gridy = 6;
        formPanel.add(minStockField, gbc);

        gbc.gridy = 7;
        formPanel.add(supplierCombo, gbc);

        gbc.gridy = 8;
        formPanel.add(categoryCombo, gbc);
    }

    private void setupButtons() {
        JButton saveButton = FormStyleManager.createPrimaryButton("Guardar");
        JButton cancelButton = FormStyleManager.createSecondaryButton("Cancelar");

        saveButton.addActionListener((ActionEvent e) -> saveProduct());
        cancelButton.addActionListener((ActionEvent e) -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
    }

    private void saveProduct() {
        try {
            // Crear un nuevo producto con los datos del formulario
            Product newProduct = new Product();
            newProduct.setProductCode(codeField.getText());
            newProduct.setProductName(nameField.getText());
            newProduct.setDescription(descriptionField.getText());
            newProduct.setPurchasePrice(Double.parseDouble(purchasePriceField.getText()));
            newProduct.setSalePrice(Double.parseDouble(salePriceField.getText()));
            newProduct.setCurrentStock(Integer.parseInt(stockField.getText()));
            newProduct.setMinStock(Integer.parseInt(minStockField.getText()));
            newProduct.setStatus("Active");
            newProduct.setCreatedAt(new Timestamp(System.currentTimeMillis()));
    
            // Asociar proveedor y categoría seleccionados
            Supplier selectedSupplier = (Supplier) supplierCombo.getSelectedItem();
            if (selectedSupplier != null) {
                newProduct.setSupplier(selectedSupplier);
            } else {
                FormStyleManager.showErrorDialog(this, "Debe seleccionar un proveedor.");
                return;
            }
    
            ProductCategory selectedCategory = (ProductCategory) categoryCombo.getSelectedItem();
            if (selectedCategory != null) {
                newProduct.setCategory(selectedCategory);
            } else {
                FormStyleManager.showErrorDialog(this, "Debe seleccionar una categoría.");
                return;
            }
    
            // Registrar el producto en la base de datos
            if (productService.createProduct(newProduct)) {
                // Registrar movimiento de inventario
                InventoryMovement movement = new InventoryMovement();
                movement.setProductId(newProduct.getProductId());
                movement.setMovementType("In");
                movement.setQuantity(newProduct.getCurrentStock());
                movement.setReferenceId(newProduct.getProductId()); // Usar ID del producto como referencia
                movement.setReferenceType("Adjustment");
                movement.setNotes("Movimiento inicial al agregar producto");
                movement.setUserId(1); // Cambiar por el ID real del usuario
    
                if (!inventoryService.recordMovement(movement)) {
                    FormStyleManager.showErrorDialog(this, "Error al registrar el movimiento de inventario.");
                    return;
                }
    
                // Registrar la compra y los detalles
                Purchase newPurchase = new Purchase();
                newPurchase.setInvoiceNumber("AUTO-" + System.currentTimeMillis()); // Generar número automático de factura
                newPurchase.setSupplierId(selectedSupplier.getSupplierId());
                newPurchase.setUserId(1); // Cambiar por el ID real del usuario
                newPurchase.setSubtotal(newProduct.getPurchasePrice() * newProduct.getCurrentStock());
                newPurchase.setTax(newPurchase.getSubtotal() * 0.19); // Ejemplo de 19% de IVA
                newPurchase.setTotal(newPurchase.getSubtotal() + newPurchase.getTax());
                newPurchase.setStatus("Received");
                newPurchase.setNotes("Compra generada automáticamente tras agregar el producto.");
    
                PurchaseDetail purchaseDetail = new PurchaseDetail();
                purchaseDetail.setProductId(newProduct.getProductId());
                purchaseDetail.setQuantity(newProduct.getCurrentStock());
                purchaseDetail.setUnitPrice(newProduct.getPurchasePrice());
                purchaseDetail.setSubtotal(purchaseDetail.getQuantity() * purchaseDetail.getUnitPrice());
    
                if (!purchaseService.createPurchase(newPurchase, List.of(purchaseDetail))) {
                    FormStyleManager.showErrorDialog(this, "Error al registrar la compra y sus detalles.");
                    return;
                }
    
                // Mostrar mensaje de éxito
                FormStyleManager.showSuccessDialog(this, "Producto agregado exitosamente");
                dispose();
            } else {
                FormStyleManager.showErrorDialog(this, "Error al agregar el producto.");
            }
        } catch (Exception ex) {
            FormStyleManager.showErrorDialog(this, "Error en los datos ingresados: " + ex.getMessage());
        }
    }
}