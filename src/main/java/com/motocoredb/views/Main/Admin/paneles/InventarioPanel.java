package com.motocoredb.views.Main.Admin.paneles;

import com.motocoredb.models.Product;
import com.motocoredb.services.ProductService;
import com.motocoredb.utils.Colors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class InventarioPanel extends JPanel {
    private final ProductService productService; // Usamos ProductService para las operaciones
    private DefaultTableModel tableModel;
    private JTable productsTable;

    public InventarioPanel(ProductService productService) {
        this.productService = productService; // Inyectamos el servicio
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
        addBtn.addActionListener(this::showAddProductDialog);

        JButton editBtn = new JButton("Editar");
        editBtn.addActionListener(this::showEditProductDialog);

        JButton deleteBtn = new JButton("Eliminar");
        deleteBtn.addActionListener(this::deleteProduct);

        JButton stockBtn = new JButton("Ajustar Stock");
        stockBtn.addActionListener(this::showStockDialog);

        toolBar.add(refreshBtn);
        toolBar.addSeparator();
        toolBar.add(addBtn);
        toolBar.add(editBtn);
        toolBar.add(deleteBtn);
        toolBar.add(stockBtn);

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
        scrollPane.setBorder(BorderFactory.createLineBorder(Colors.DARK_BLUE));
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
                    p.getCategory() != null ? p.getCategory().getCategoryName() : "Sin categoría", // Nombre de categoría
                    String.format("$%.2f", p.getPurchasePrice()),
                    String.format("$%.2f", p.getSalePrice()),
                    p.getCurrentStock(),
                    p.getMinStock(),
                    p.getSupplier() != null ? p.getSupplier().getCompanyName() : "Sin proveedor", // Nombre del proveedor
                    p.getStatus()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddProductDialog(ActionEvent e) {
        Product newProduct = new Product(); // Crear nuevo producto
        // Ventana de diálogo para ingresar datos (puedes implementar esto más tarde)
        JOptionPane.showMessageDialog(this, "Implementa el formulario para agregar productos.");
        // Ejemplo de creación usando datos ficticios:
        newProduct.setName("Producto Prueba");
        newProduct.setProductCode("PRD001");
        newProduct.setPurchasePrice(100.00);
        newProduct.setSalePrice(150.00);
        newProduct.setCurrentStock(50);
        newProduct.setMinStock(10);
        newProduct.setStatus("Active");

        if (productService.createProduct(newProduct)) {
            JOptionPane.showMessageDialog(this, "Producto agregado exitosamente.");
            loadProducts();
        } else {
            JOptionPane.showMessageDialog(this, "Error al agregar producto.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showEditProductDialog(ActionEvent e) {
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

        // Ventana de diálogo para editar (puedes implementar esto más tarde)
        JOptionPane.showMessageDialog(this, "Implementa el formulario para editar productos.");

        // Ejemplo de edición usando datos ficticios:
        product.setName("Producto Editado");
        product.setPurchasePrice(120.00);
        product.setSalePrice(180.00);

        if (productService.updateProduct(product)) {
            JOptionPane.showMessageDialog(this, "Producto actualizado exitosamente.");
            loadProducts();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar producto.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProduct(ActionEvent e) {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar.");
            return;
        }

        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirmation = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar este producto?", "Confirmación", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            if (productService.updateStock(productId, -1)) {
                JOptionPane.showMessageDialog(this, "Producto eliminado exitosamente.");
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar producto.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showStockDialog(ActionEvent e) {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para ajustar stock.");
            return;
        }

        int productId = (int) tableModel.getValueAt(selectedRow, 0);

        JTextField quantityField = new JTextField();
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.add(new JLabel("ID del Producto:"));
        panel.add(new JLabel(String.valueOf(productId)));
        panel.add(new JLabel("Cantidad a ajustar:"));
        panel.add(quantityField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Ajustar Stock", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int quantity;
            try {
                quantity = Integer.parseInt(quantityField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (productService.updateStock(productId, quantity)) {
                JOptionPane.showMessageDialog(this, "Stock ajustado exitosamente.");
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(this, "Error al ajustar el stock.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}