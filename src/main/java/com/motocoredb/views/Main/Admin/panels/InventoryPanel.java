package com.motocoredb.views.Main.Admin.panels;

import com.motocoredb.dao.impl.CategoryDaoImpl;
import com.motocoredb.dao.impl.InventoryDaoImpl;
import com.motocoredb.dao.impl.PurchaseDaoImpl;
import com.motocoredb.dao.impl.SupplierDaoImpl;
import com.motocoredb.models.Product;
import com.motocoredb.models.Purchase;
import com.motocoredb.models.PurchaseDetail;
import com.motocoredb.services.CategoryService;
import com.motocoredb.services.InventoryService;
import com.motocoredb.services.ProductService;
import com.motocoredb.services.PurchaseService;
import com.motocoredb.services.SupplierService;
import com.motocoredb.views.forms.products.AddProductForm;
import com.motocoredb.views.forms.products.EditProductForm;
import com.motocoredb.views.forms.utils.FormStyleManager;
import com.motocoredb.models.Supplier;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class InventoryPanel extends JPanel {
    private final ProductService productService;
    private DefaultTableModel tableModel;
    private JTable productsTable;

    public InventoryPanel(ProductService productService) {
        this.productService = productService;
        initUI();
        loadProducts();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(FormStyleManager.BACKGROUND_COLOR);

        // Barra de herramientas CRUD
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(FormStyleManager.BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Gestión de Inventario");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(FormStyleManager.PRIMARY_COLOR);
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
        toolBar.add(deleteBtn); // Eliminamos el botón de editar
        add(toolBar, BorderLayout.NORTH);

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
        productsTable.setRowHeight(25);
        productsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        productsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        productsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productsTable.setShowGrid(true);
        productsTable.setGridColor(Color.LIGHT_GRAY);

        // Doble clic para editar
        productsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editProduct();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(productsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadProducts() {
        try {
            List<Product> products = productService.getAllProducts();
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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
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
    
        int productId = (int) tableModel.getValueAt(selectedRow, 0);
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
    
                        loadProducts();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error al registrar la compra: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

        int productId = (int) tableModel.getValueAt(selectedRow, 0);
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