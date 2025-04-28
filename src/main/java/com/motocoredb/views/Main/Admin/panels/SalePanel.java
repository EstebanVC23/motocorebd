package com.motocoredb.views.Main.Admin.panels;

import com.motocoredb.models.Customer;
import com.motocoredb.models.Product;
import com.motocoredb.models.Sale;
import com.motocoredb.models.SaleDetail;
import com.motocoredb.models.User;
import com.motocoredb.services.SaleService;
import com.motocoredb.services.UserService;
import com.motocoredb.services.ProductService;
import com.motocoredb.views.forms.sales.AddSaleForm;
import com.motocoredb.views.forms.sales.EditSaleForm;
import com.motocoredb.views.utils.FormStyleManager;
import com.motocoredb.services.CustomerService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class SalePanel extends JPanel {
    private final SaleService saleService;
    private final ProductService productService;
    private final CustomerService customerService;
    private final UserService userService;

    private JTable salesTable;
    private DefaultTableModel tableModel;

    public SalePanel(SaleService saleService, ProductService productService, CustomerService customerService, UserService userService) {
        this.userService = userService; // Inicializar el servicio de usuarios
        this.saleService = saleService;
        this.productService = productService;
        this.customerService = customerService;
        initUI();
        loadSales();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(FormStyleManager.BACKGROUND_COLOR);

        // Barra de herramientas
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(FormStyleManager.BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Gestión de Ventas");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(FormStyleManager.PRIMARY_COLOR);
        toolBar.add(titleLabel);
        toolBar.addSeparator();

        JButton refreshBtn = FormStyleManager.createSecondaryButton("Actualizar");
        refreshBtn.addActionListener(e -> loadSales());

        JButton addBtn = FormStyleManager.createPrimaryButton("Nueva Venta");
        addBtn.addActionListener(this::openAddSaleForm);

        JButton cancelBtn = FormStyleManager.createSecondaryButton("Cancelar Venta");
        cancelBtn.addActionListener(e -> cancelSale());

        toolBar.add(refreshBtn);
        toolBar.addSeparator();
        toolBar.add(addBtn);
        toolBar.add(cancelBtn);

        add(toolBar, BorderLayout.NORTH);

        // Tabla de ventas
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Cliente", "Usuario", "Fecha", "Producto", "Método de Pago", "Total", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        salesTable = new JTable(tableModel);
        salesTable.setRowHeight(25);
        salesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        salesTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        salesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        salesTable.setShowGrid(true);
        salesTable.setGridColor(Color.LIGHT_GRAY);

        // Doble clic para editar
        salesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openEditSaleForm();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadSales() {
        try {
            tableModel.setRowCount(0);
    
            // Obtener la lista de ventas con depuración
            List<Sale> sales = saleService.getSalesByDateRange(null, null);
            System.out.println("Ventas obtenidas: " + sales.size());  // Depuración
    
            if (sales.isEmpty()) {
                System.out.println("No se encontraron ventas en la base de datos");
                return; // Si no hay ventas, terminamos aquí
            }
    
            // Obtener todos los usuarios y clientes
            List<User> users = userService.getAllUsers();
            List<Customer> customers = customerService.getAllCustomers();
    
            for (Sale sale : sales) {
                // Buscar el nombre del cliente
                String customerName = customers.stream()
                        .filter(c -> c.getCustomerId() == sale.getCustomerId())
                        .map(Customer::getNameOrCompany)
                        .findFirst()
                        .orElse("Cliente desconocido");
    
                // Buscar el nombre del usuario
                String userName = users.stream()
                        .filter(u -> u.getUserId() == sale.getUserId())
                        .map(User::getUsername)
                        .findFirst()
                        .orElse("Usuario desconocido");
    
                // Obtener los detalles de la venta
                List<SaleDetail> details = saleService.getSaleDetails(sale.getSaleId());
                String productName = "Sin productos";
                
                if (details != null && !details.isEmpty()) {
                    Product product = productService.getProductById(details.get(0).getProductId());
                    if (product != null) {
                        productName = product.getProductName();
                    }
                }
    
                // Agregar una fila a la tabla
                tableModel.addRow(new Object[]{
                        sale.getSaleId(),
                        customerName,
                        userName,
                        sale.getSaleDate(),
                        productName,
                        sale.getPaymentMethod(),
                        String.format("%.2f", sale.getTotal()),
                        sale.getStatus()
                });
            }
            
            // Notificar cambios en el modelo de tabla
            tableModel.fireTableDataChanged();
            salesTable.repaint();
            
        } catch (Exception e) {
            e.printStackTrace(); // Para mostrar el stack trace completo
            JOptionPane.showMessageDialog(this, "Error al cargar ventas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openAddSaleForm(ActionEvent e) {
        AddSaleForm form = new AddSaleForm(saleService, customerService, productService);
        form.setVisible(true);
        form.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                loadSales();
            }
        });
    }

    private void openEditSaleForm() {
        int selectedRow = salesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una venta para editar.");
            return;
        }
    
        int saleId = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            // Obtener la venta por su ID
            Sale sale = saleService.getSaleById(saleId);
            if (sale == null) {
                JOptionPane.showMessageDialog(this, "No se encontró la venta seleccionada.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            // Obtener los detalles de la venta (usando getSaleDetails que devuelve una lista)
            List<SaleDetail> saleDetails = saleService.getSaleDetails(saleId);
            if (saleDetails == null || saleDetails.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron los detalles de la venta seleccionada.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            // Pasar el primer detalle o modificar EditSaleForm para manejar una lista
            EditSaleForm form = new EditSaleForm(saleService, customerService, productService, sale, saleDetails.get(0));
            form.setVisible(true);
            form.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    loadSales(); // Recargar la tabla de ventas tras cerrar el formulario
                }
            });
        } catch (Exception e) {
            e.printStackTrace(); // Añadir esto para ver el stack trace completo
            JOptionPane.showMessageDialog(this, "Error al obtener datos de la venta: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelSale() {
        int selectedRow = salesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una venta para cancelar.");
            return;
        }

        int saleId = (int) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea cancelar esta venta?", "Confirmar Cancelación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Obtener la venta por su ID
                Sale sale = saleService.getSaleById(saleId);
                if (sale == null) {
                    JOptionPane.showMessageDialog(this, "No se encontró la venta seleccionada.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Obtener los detalles de la venta
                List<SaleDetail> saleDetails = saleService.getSaleDetails(saleId); // Método para obtener todos los detalles de la venta
                if (saleDetails.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No se encontraron los detalles de la venta seleccionada.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Devolver el stock de todos los productos asociados a los detalles de la venta
                for (SaleDetail saleDetail : saleDetails) {
                    Product product = productService.getProductById(saleDetail.getProductId());
                    if (product != null) {
                        productService.increaseStock(product.getProductId(), saleDetail.getQuantity());
                    }
                }

                // Cambiar estado de la venta a "Cancelled"
                sale.setStatus("Cancelled");
                boolean success = saleService.updateSale(sale);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Venta cancelada correctamente y el stock de los productos ha sido actualizado.");
                    loadSales();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo cancelar la venta.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al cancelar la venta: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}