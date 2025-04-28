package com.motocoredb.views.Main.panels;

import com.motocoredb.dao.impl.AlertDaoImpl;
import com.motocoredb.dao.impl.CategoryDaoImpl;
import com.motocoredb.dao.impl.InventoryDaoImpl;
import com.motocoredb.dao.impl.PurchaseDaoImpl;
import com.motocoredb.dao.impl.SupplierDaoImpl;
import com.motocoredb.models.Alert;
import com.motocoredb.models.Product;
import com.motocoredb.models.ProductCategory;
import com.motocoredb.models.Purchase;
import com.motocoredb.models.PurchaseDetail;
import com.motocoredb.models.Supplier;
import com.motocoredb.services.AlertService;
import com.motocoredb.services.CategoryService;
import com.motocoredb.services.InventoryService;
import com.motocoredb.services.ProductService;
import com.motocoredb.services.PurchaseService;
import com.motocoredb.services.SupplierService;
import com.motocoredb.views.forms.products.AddProductForm;
import com.motocoredb.views.forms.products.EditProductForm;
import com.motocoredb.views.utils.FormStyleManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
import java.util.stream.Collectors;

public class InventoryPanel extends JPanel {
    private final ProductService productService;
    private DefaultTableModel tableModel;
    private JTable productsTable;
    private TableRowSorter<DefaultTableModel> sorter;
    private List<Product> allProducts;
    
    // Filter components
    private JTextField idFilterField;
    private JTextField codeFilterField;
    private JTextField nameFilterField;
    private JComboBox<String> categoryFilterCombo;
    private JTextField minPriceField;
    private JTextField maxPriceField;
    private JComboBox<String> stockStatusCombo;
    private JComboBox<String> supplierFilterCombo;
    private JComboBox<String> statusFilterCombo;
    private JButton applyFiltersBtn;
    private JButton clearFiltersBtn;

    // Services
    private CategoryService categoryService;
    private SupplierService supplierService;

    public InventoryPanel(ProductService productService, SupplierService supplierService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.supplierService = supplierService;
        this.allProducts = new ArrayList<>();
        initUI();
        loadProducts();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(FormStyleManager.BACKGROUND_COLOR);

        // Barra de herramientas CRUD con título
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(FormStyleManager.BACKGROUND_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        
        JLabel titleLabel = new JLabel("Gestión de Inventario");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(FormStyleManager.PRIMARY_COLOR);
        
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(FormStyleManager.BACKGROUND_COLOR);
        toolBar.add(titleLabel);
        toolBar.addSeparator();

        JButton refreshBtn = FormStyleManager.createSecondaryButton("Actualizar");
        refreshBtn.addActionListener(e -> loadProducts());

        JButton addBtn = FormStyleManager.createPrimaryButton("Nuevo Producto");
        addBtn.addActionListener(e -> showAddProductForm());

        JButton deleteBtn = FormStyleManager.createSecondaryButton("Eliminar Producto");
        deleteBtn.addActionListener(e -> deleteProduct());

        toolBar.add(refreshBtn);
        toolBar.addSeparator();
        toolBar.add(addBtn);
        toolBar.add(deleteBtn);
        
        topPanel.add(toolBar, BorderLayout.NORTH);
        
        // Panel de filtros mejorado
        JPanel filterPanel = createFilterPanel();
        topPanel.add(filterPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);

        // Tabla de productos
        tableModel = new DefaultTableModel(
            new Object[]{"ID", "Código", "Nombre", "Categoría", "Compra", "Venta", "Stock", "Mínimo", "Proveedor", "Estado"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productsTable = new JTable(tableModel);
        productsTable.setRowHeight(30);
        productsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        productsTable.getTableHeader().setBackground(FormStyleManager.SECONDARY_COLOR);
        productsTable.getTableHeader().setForeground(Color.WHITE);
        productsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        productsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productsTable.setSelectionBackground(new Color(232, 240, 254));
        productsTable.setShowGrid(true);
        productsTable.setGridColor(new Color(225, 225, 225));

        // Doble clic para editar
        productsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editProduct();
                }
            }
        });

        sorter = new TableRowSorter<>(tableModel);
        productsTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(productsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(FormStyleManager.SECONDARY_COLOR, 1));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(FormStyleManager.PANEL_COLOR);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(FormStyleManager.PRIMARY_COLOR, 1, true),
                "Filtros",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 14),
                FormStyleManager.PRIMARY_COLOR
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Panel izquierdo para filtros básicos
        JPanel leftFilters = new JPanel(new GridLayout(4, 1, 5, 5));
        leftFilters.setBackground(FormStyleManager.PANEL_COLOR);
        
        // ID Filter
        JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        idPanel.setBackground(FormStyleManager.PANEL_COLOR);
        JLabel idLabel = FormStyleManager.createStyledLabel("ID:");
        idFilterField = new JTextField(10);
        idPanel.add(idLabel);
        idPanel.add(idFilterField);
        leftFilters.add(idPanel);
        
        // Code Filter
        JPanel codePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        codePanel.setBackground(FormStyleManager.PANEL_COLOR);
        JLabel codeLabel = FormStyleManager.createStyledLabel("Código:");
        codeFilterField = new JTextField(10);
        codePanel.add(codeLabel);
        codePanel.add(codeFilterField);
        leftFilters.add(codePanel);
        
        // Name Filter
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.setBackground(FormStyleManager.PANEL_COLOR);
        JLabel nameLabel = FormStyleManager.createStyledLabel("Nombre:");
        nameFilterField = new JTextField(10);
        namePanel.add(nameLabel);
        namePanel.add(nameFilterField);
        leftFilters.add(namePanel);
        
        // Category Filter
        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        categoryPanel.setBackground(FormStyleManager.PANEL_COLOR);
        JLabel categoryLabel = FormStyleManager.createStyledLabel("Categoría:");
        categoryFilterCombo = new JComboBox<>();
        categoryFilterCombo.setPreferredSize(new Dimension(150, 30));
        categoryFilterCombo.setBackground(Color.WHITE);
        categoryPanel.add(categoryLabel);
        categoryPanel.add(categoryFilterCombo);
        leftFilters.add(categoryPanel);
        
        // Panel central para más filtros
        JPanel centerFilters = new JPanel(new GridLayout(4, 1, 5, 5));
        centerFilters.setBackground(FormStyleManager.PANEL_COLOR);
        
        // Price Range Filter
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pricePanel.setBackground(FormStyleManager.PANEL_COLOR);
        JLabel priceLabel = FormStyleManager.createStyledLabel("Precio:");
        minPriceField = new JTextField(5);
        maxPriceField = new JTextField(5);
        pricePanel.add(priceLabel);
        pricePanel.add(new JLabel("Min:"));
        pricePanel.add(minPriceField);
        pricePanel.add(new JLabel("Max:"));
        pricePanel.add(maxPriceField);
        centerFilters.add(pricePanel);
        
        // Stock Status Filter
        JPanel stockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        stockPanel.setBackground(FormStyleManager.PANEL_COLOR);
        JLabel stockLabel = FormStyleManager.createStyledLabel("Stock:");
        stockStatusCombo = new JComboBox<>(new String[]{"Todos", "Stock bajo", "Stock normal"});
        stockStatusCombo.setPreferredSize(new Dimension(150, 30));
        stockStatusCombo.setBackground(Color.WHITE);
        stockPanel.add(stockLabel);
        stockPanel.add(stockStatusCombo);
        centerFilters.add(stockPanel);
        
        // Supplier Filter
        JPanel supplierPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        supplierPanel.setBackground(FormStyleManager.PANEL_COLOR);
        JLabel supplierLabel = FormStyleManager.createStyledLabel("Proveedor:");
        supplierFilterCombo = new JComboBox<>();
        supplierFilterCombo.setPreferredSize(new Dimension(150, 30));
        supplierFilterCombo.setBackground(Color.WHITE);
        supplierPanel.add(supplierLabel);
        supplierPanel.add(supplierFilterCombo);
        centerFilters.add(supplierPanel);
        
        // Status Filter
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(FormStyleManager.PANEL_COLOR);
        JLabel statusLabel = FormStyleManager.createStyledLabel("Estado:");
        statusFilterCombo = new JComboBox<>(new String[]{"Todos", "Active", "Inactive"});
        statusFilterCombo.setPreferredSize(new Dimension(150, 30));
        statusFilterCombo.setBackground(Color.WHITE);
        statusPanel.add(statusLabel);
        statusPanel.add(statusFilterCombo);
        centerFilters.add(statusPanel);
        
        // Panel de botones
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonsPanel.setBackground(FormStyleManager.PANEL_COLOR);
        
        applyFiltersBtn = FormStyleManager.createPrimaryButton("Aplicar");
        applyFiltersBtn.setPreferredSize(new Dimension(100, 30));
        applyFiltersBtn.addActionListener(e -> applyFilters());
        
        clearFiltersBtn = FormStyleManager.createSecondaryButton("Limpiar");
        clearFiltersBtn.setPreferredSize(new Dimension(100, 30));
        clearFiltersBtn.addActionListener(e -> clearFilters());
        
        buttonsPanel.add(applyFiltersBtn);
        buttonsPanel.add(clearFiltersBtn);
        
        // Agregar todos los paneles
        filterPanel.add(leftFilters);
        filterPanel.add(centerFilters);
        filterPanel.add(buttonsPanel);
        
        return filterPanel;
    }

    private void loadProducts() {
        try {
            allProducts = productService.getAllProducts();
            loadCategoriesInCombo();
            loadSuppliersInCombo();
            populateTable(allProducts);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCategoriesInCombo() {
        try {
            categoryFilterCombo.removeAllItems();
            categoryFilterCombo.addItem("Todas");
            
            List<ProductCategory> categories = categoryService.getAllCategories();
            for (ProductCategory category : categories) {
                categoryFilterCombo.addItem(category.getCategoryName());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar categorías: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSuppliersInCombo() {
        try {
            supplierFilterCombo.removeAllItems();
            supplierFilterCombo.addItem("Todos");
            
            List<Supplier> suppliers = supplierService.getAllSuppliers();
            for (Supplier supplier : suppliers) {
                supplierFilterCombo.addItem(supplier.getCompanyName());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar proveedores: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTable(List<Product> products) {
        tableModel.setRowCount(0);
        for (Product p : products) {
            tableModel.addRow(new Object[]{
                p.getProductId(),
                p.getProductCode(),
                p.getProductName(),
                p.getCategory() != null ? p.getCategory().getCategoryName() : "Sin categoría",
                String.format("$%.2f", p.getPurchasePrice()),
                String.format("$%.2f", p.getSalePrice()),
                p.getCurrentStock(),
                p.getMinStock(),
                p.getSupplier() != null ? p.getSupplier().getCompanyName() : "Sin proveedor",
                p.getStatus()
            });
        }
    }

    private void applyFilters() {
        try {
            List<Product> filteredProducts = new ArrayList<>(allProducts);
            
            // Filter by ID
            if (!idFilterField.getText().trim().isEmpty()) {
                try {
                    int id = Integer.parseInt(idFilterField.getText().trim());
                    filteredProducts = filteredProducts.stream()
                        .filter(p -> p.getProductId() == id)
                        .collect(Collectors.toList());
                } catch (NumberFormatException e) {
                    // Ignore if not a valid number
                }
            }
            
            // Filter by code
            if (!codeFilterField.getText().trim().isEmpty()) {
                String code = codeFilterField.getText().trim().toLowerCase();
                filteredProducts = filteredProducts.stream()
                    .filter(p -> p.getProductCode().toLowerCase().contains(code))
                    .collect(Collectors.toList());
            }
            
            // Filter by name
            if (!nameFilterField.getText().trim().isEmpty()) {
                String name = nameFilterField.getText().trim().toLowerCase();
                filteredProducts = filteredProducts.stream()
                    .filter(p -> p.getProductName().toLowerCase().contains(name))
                    .collect(Collectors.toList());
            }
            
            // Filter by category
            if (categoryFilterCombo.getSelectedIndex() > 0) {
                String category = (String) categoryFilterCombo.getSelectedItem();
                filteredProducts = filteredProducts.stream()
                    .filter(p -> p.getCategory() != null && p.getCategory().getCategoryName().equals(category))
                    .collect(Collectors.toList());
            }
            
            // Filter by price range
            if (!minPriceField.getText().trim().isEmpty()) {
                try {
                    double minPrice = Double.parseDouble(minPriceField.getText().trim());
                    filteredProducts = filteredProducts.stream()
                        .filter(p -> p.getPurchasePrice() >= minPrice || p.getSalePrice() >= minPrice)
                        .collect(Collectors.toList());
                } catch (NumberFormatException e) {
                    // Ignore if not a valid number
                }
            }
            
            if (!maxPriceField.getText().trim().isEmpty()) {
                try {
                    double maxPrice = Double.parseDouble(maxPriceField.getText().trim());
                    filteredProducts = filteredProducts.stream()
                        .filter(p -> p.getPurchasePrice() <= maxPrice || p.getSalePrice() <= maxPrice)
                        .collect(Collectors.toList());
                } catch (NumberFormatException e) {
                    // Ignore if not a valid number
                }
            }
            
            // Filter by stock status
            if (stockStatusCombo.getSelectedIndex() > 0) {
                String stockStatus = (String) stockStatusCombo.getSelectedItem();
                if ("Stock bajo".equals(stockStatus)) {
                    filteredProducts = filteredProducts.stream()
                        .filter(p -> p.getCurrentStock() <= p.getMinStock())
                        .collect(Collectors.toList());
                } else if ("Stock normal".equals(stockStatus)) {
                    filteredProducts = filteredProducts.stream()
                        .filter(p -> p.getCurrentStock() > p.getMinStock())
                        .collect(Collectors.toList());
                }
            }
            
            // Filter by supplier
            if (supplierFilterCombo.getSelectedIndex() > 0) {
                String supplier = (String) supplierFilterCombo.getSelectedItem();
                filteredProducts = filteredProducts.stream()
                    .filter(p -> p.getSupplier() != null && p.getSupplier().getCompanyName().equals(supplier))
                    .collect(Collectors.toList());
            }
            
            // Filter by status
            if (statusFilterCombo.getSelectedIndex() > 0) {
                String status = (String) statusFilterCombo.getSelectedItem();
                filteredProducts = filteredProducts.stream()
                    .filter(p -> p.getStatus().equals(status))
                    .collect(Collectors.toList());
            }
            
            // Update table with filtered products
            populateTable(filteredProducts);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al aplicar filtros: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFilters() {
        idFilterField.setText("");
        codeFilterField.setText("");
        nameFilterField.setText("");
        categoryFilterCombo.setSelectedIndex(0);
        minPriceField.setText("");
        maxPriceField.setText("");
        stockStatusCombo.setSelectedIndex(0);
        supplierFilterCombo.setSelectedIndex(0);
        statusFilterCombo.setSelectedIndex(0);
        
        // Reload all products
        populateTable(allProducts);
    }

    private void showAddProductForm() {
        try {
            // Inicializar los servicios correctamente con sus DAOs
            SupplierService supplierService = new SupplierService(new SupplierDaoImpl());
            CategoryService categoryService = new CategoryService(new CategoryDaoImpl());
            InventoryService inventoryService = new InventoryService(new InventoryDaoImpl());
            PurchaseService purchaseService = new PurchaseService(new PurchaseDaoImpl()); // Agregar PurchaseService
    
            // Crear el formulario pasando todas las dependencias necesarias
            AddProductForm form = new AddProductForm(productService, supplierService, categoryService, inventoryService, purchaseService);
            form.setVisible(true);
            form.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    loadProducts();
                }
            });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al abrir el formulario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editProduct() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para editar.");
            return;
        }

        int productId = (int) tableModel.getValueAt(productsTable.convertRowIndexToModel(selectedRow), 0);
        Product product = productService.getProductById(productId);
        if (product == null) {
            JOptionPane.showMessageDialog(this, "No se pudo obtener el producto.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Inicializar los servicios necesarios
            SupplierService supplierService = new SupplierService(new SupplierDaoImpl());
            CategoryService categoryService = new CategoryService(new CategoryDaoImpl());
            InventoryService inventoryService = new InventoryService(new InventoryDaoImpl());
            PurchaseService purchaseService = new PurchaseService(new PurchaseDaoImpl());
            AlertService alertService = new AlertService(new AlertDaoImpl()); // Inicializar AlertService

            // Crear el formulario de edición
            EditProductForm form = new EditProductForm(productService, supplierService, categoryService, inventoryService, product);
            form.setVisible(true);
            form.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    try {
                        // Lógica para llenar Purchases y PurchaseDetails
                        Supplier supplier = product.getSupplier();
                        if (supplier != null) {
                            Purchase newPurchase = new Purchase();
                            newPurchase.setInvoiceNumber("AUTO-" + System.currentTimeMillis());
                            newPurchase.setSupplierId(supplier.getSupplierId());
                            newPurchase.setUserId(1); // ID del usuario actual
                            newPurchase.setSubtotal(product.getPurchasePrice() * product.getCurrentStock());
                            newPurchase.setTax(newPurchase.getSubtotal() * 0.19); // Ejemplo: IVA 19%
                            newPurchase.setTotal(newPurchase.getSubtotal() + newPurchase.getTax());
                            newPurchase.setStatus("Received");
                            newPurchase.setNotes("Compra generada automáticamente tras editar el producto.");

                            // Crear detalles de la compra
                            PurchaseDetail purchaseDetail = new PurchaseDetail();
                            purchaseDetail.setProductId(product.getProductId());
                            purchaseDetail.setQuantity(product.getCurrentStock());
                            purchaseDetail.setUnitPrice(product.getPurchasePrice());
                            purchaseDetail.setSubtotal(purchaseDetail.getQuantity() * purchaseDetail.getUnitPrice());

                            // Registrar compra y detalles
                            boolean purchaseCreated = purchaseService.createPurchase(newPurchase, List.of(purchaseDetail));
                            if (!purchaseCreated) {
                                JOptionPane.showMessageDialog(null, "Error al registrar la compra y detalles.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }

                        // Generar alerta si el stock es igual o menor que el mínimo
                        if (product.getCurrentStock() <= product.getMinStock()) {
                            Alert newAlert = new Alert();
                            newAlert.setAlertType("Low stock");
                            newAlert.setMessage("El producto '" + product.getProductName() + "' ha alcanzado el nivel mínimo de stock.");
                            newAlert.setGeneratedAt(new Timestamp(System.currentTimeMillis())); // Fecha actual
                            newAlert.setStatus("Pending");
                            newAlert.setReferenceId(product.getProductId());
                            newAlert.setReferenceType("Product");

                            boolean alertCreated = alertService.createAlert(newAlert); // Registrar la alerta
                            if (!alertCreated) {
                                JOptionPane.showMessageDialog(null, "Error al generar la alerta de bajo stock.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }

                        loadProducts(); // Recargar la tabla de productos
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error al registrar la compra o alerta: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al abrir el formulario de edición: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProduct() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar.");
            return;
        }

        int productId = (int) tableModel.getValueAt(productsTable.convertRowIndexToModel(selectedRow), 0);
        Product product = productService.getProductById(productId);
        if (product == null) {
            JOptionPane.showMessageDialog(this, "No se pudo obtener el producto.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar este producto?",
                "Confirmación",
                JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            product.setStatus("Inactive"); // Eliminación lógica
            boolean success = productService.updateProduct(product);
            if (success) {
                JOptionPane.showMessageDialog(this, "Producto eliminado exitosamente.");
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el producto.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}