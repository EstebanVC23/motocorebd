package com.motocoredb.views.Main.Admin.panels;

import com.motocoredb.models.Product;
import com.motocoredb.services.ProductService;
import com.motocoredb.views.forms.products.AddProductForm;
import com.motocoredb.views.forms.products.EditProductForm;
import com.motocoredb.views.forms.utils.FormStyleManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
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
        addBtn.addActionListener(this::showAddProductForm);

        JButton editBtn = FormStyleManager.createSecondaryButton("Editar Producto");
        editBtn.addActionListener(this::showEditProductForm);

        JButton deleteBtn = FormStyleManager.createSecondaryButton("Eliminar Producto");
        deleteBtn.addActionListener(this::deleteProduct);

        toolBar.add(refreshBtn);
        toolBar.addSeparator();
        toolBar.add(addBtn);
        toolBar.add(editBtn);
        toolBar.add(deleteBtn);

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

    private void showAddProductForm(ActionEvent e) {
        AddProductForm form = new AddProductForm(productService);
        form.setVisible(true);
        form.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                loadProducts();
            }
        });
    }

    private void showEditProductForm(ActionEvent e) {
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

        EditProductForm form = new EditProductForm(productService, product);
        form.setVisible(true);
        form.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                loadProducts();
            }
        });
    }

    private void deleteProduct(ActionEvent e) {
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