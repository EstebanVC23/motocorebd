package com.motocoredb.views.Main.Admin.paneles;

import com.motocoredb.models.Product;
import com.motocoredb.services.ProductService;
import com.motocoredb.views.Main.Admin.paneles.forms.AddProductForm;
import com.motocoredb.views.Main.Admin.paneles.forms.EditProductForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class InventarioPanel extends JPanel {
    private final ProductService productService;
    private DefaultTableModel tableModel;
    private JTable productsTable;

    public InventarioPanel(ProductService productService) {
        this.productService = productService;
        initUI();
        loadProducts();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Barra de herramientas CRUD
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton refreshBtn = new JButton("Actualizar");
        refreshBtn.addActionListener(e -> loadProducts());

        JButton addBtn = new JButton("Nuevo Producto");
        addBtn.addActionListener(this::showAddProductForm);

        JButton editBtn = new JButton("Editar Producto");
        editBtn.addActionListener(this::showEditProductForm);

        JButton deleteBtn = new JButton("Eliminar Producto");
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
        productsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productsTable.getTableHeader().setReorderingAllowed(false);
        productsTable.setRowHeight(25);
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
                    p.getName(),
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
        new AddProductForm(productService).setVisible(true); // Abrir nueva ventana para agregar producto
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

        new EditProductForm(productService, product).setVisible(true); // Abrir nueva ventana para editar producto
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

        int confirmation = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar este producto?", "Confirmación", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            product.setStatus("Inactive");
            if (productService.updateProduct(product)) {
                JOptionPane.showMessageDialog(this, "Producto eliminado exitosamente.");
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el producto.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}