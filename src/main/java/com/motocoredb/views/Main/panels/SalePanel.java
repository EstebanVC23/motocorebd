package com.motocoredb.views.Main.panels;

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
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import javax.swing.RowFilter;
import javax.swing.text.NumberFormatter;

public class SalePanel extends JPanel {
    private final SaleService saleService;
    private final ProductService productService;
    private final CustomerService customerService;
    private final UserService userService;

    private JTable salesTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    
    // Componentes de filtrado
    private JComboBox<String> productFilterCombo;
    private JComboBox<String> customerFilterCombo;
    private JComboBox<String> statusFilterCombo;
    private JComboBox<String> paymentMethodFilterCombo;
    private JFormattedTextField minPriceField;
    private JFormattedTextField maxPriceField;

    public SalePanel(SaleService saleService, ProductService productService, CustomerService customerService, UserService userService) {
        this.userService = userService;
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

        // Panel superior que contendrá la barra de herramientas y el panel de filtros
        JPanel topPanel = new JPanel(new BorderLayout(5, 15)); // Incrementado espacio vertical entre componentes
        topPanel.setBackground(FormStyleManager.BACKGROUND_COLOR);

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

        topPanel.add(toolBar, BorderLayout.NORTH);

        // Panel de filtros mejorado con el estilo de AlertPanel
        JPanel filterPanel = createFilterPanel();
        topPanel.add(filterPanel, BorderLayout.CENTER);
        
        // Añadir un panel vacío para crear espacio adicional entre el panel de filtros y la tabla
        JPanel spacerPanel = new JPanel();
        spacerPanel.setPreferredSize(new Dimension(1, 20)); // Alto de 20 píxeles para separación
        spacerPanel.setBackground(FormStyleManager.BACKGROUND_COLOR);
        topPanel.add(spacerPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);

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
        salesTable.getTableHeader().setBackground(FormStyleManager.SECONDARY_COLOR);
        salesTable.getTableHeader().setForeground(Color.WHITE);
        salesTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        salesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        salesTable.setSelectionBackground(new Color(232, 240, 254));
        salesTable.setShowGrid(true);
        salesTable.setGridColor(new Color(225, 225, 225));

        // Configurar el sorter para filtrado
        sorter = new TableRowSorter<>(tableModel);
        salesTable.setRowSorter(sorter);

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
            BorderFactory.createEmptyBorder(10, 10, 15, 10) // Aumentado el padding inferior
        ));

        // Configuración del formatter para los campos de precio
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Double.class);
        formatter.setMinimum(0.0);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);

        // Filtro por producto
        JLabel productLabel = FormStyleManager.createStyledLabel("Producto:");
        productLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        filterPanel.add(productLabel);
        
        productFilterCombo = new JComboBox<>();
        productFilterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        productFilterCombo.setPreferredSize(new Dimension(150, 30));
        productFilterCombo.setBackground(Color.WHITE);
        productFilterCombo.addItem("Todos");
        filterPanel.add(productFilterCombo);

        // Filtro por cliente
        JLabel customerLabel = FormStyleManager.createStyledLabel("Cliente:");
        customerLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        filterPanel.add(customerLabel);
        
        customerFilterCombo = new JComboBox<>();
        customerFilterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        customerFilterCombo.setPreferredSize(new Dimension(150, 30));
        customerFilterCombo.setBackground(Color.WHITE);
        customerFilterCombo.addItem("Todos");
        filterPanel.add(customerFilterCombo);

        // Filtro por estado
        JLabel statusLabel = FormStyleManager.createStyledLabel("Estado:");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        filterPanel.add(statusLabel);
        
        statusFilterCombo = new JComboBox<>();
        statusFilterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusFilterCombo.setPreferredSize(new Dimension(120, 30));
        statusFilterCombo.setBackground(Color.WHITE);
        statusFilterCombo.addItem("Todos");
        statusFilterCombo.addItem("Completed");
        statusFilterCombo.addItem("Pending");
        statusFilterCombo.addItem("Cancelled");
        filterPanel.add(statusFilterCombo);

        // Filtro por método de pago
        JLabel paymentLabel = FormStyleManager.createStyledLabel("Método pago:");
        paymentLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        filterPanel.add(paymentLabel);
        
        paymentMethodFilterCombo = new JComboBox<>();
        paymentMethodFilterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        paymentMethodFilterCombo.setPreferredSize(new Dimension(120, 30));
        paymentMethodFilterCombo.setBackground(Color.WHITE);
        paymentMethodFilterCombo.addItem("Todos");
        paymentMethodFilterCombo.addItem("Efectivo");
        paymentMethodFilterCombo.addItem("Tarjeta");
        paymentMethodFilterCombo.addItem("Transferencia");
        filterPanel.add(paymentMethodFilterCombo);

        // Filtro por rango de precio
        JLabel minPriceLabel = FormStyleManager.createStyledLabel("Precio mín:");
        minPriceLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        filterPanel.add(minPriceLabel);
        
        minPriceField = new JFormattedTextField(formatter);
        minPriceField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        minPriceField.setPreferredSize(new Dimension(80, 30));
        filterPanel.add(minPriceField);

        JLabel maxPriceLabel = FormStyleManager.createStyledLabel("Precio máx:");
        maxPriceLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        filterPanel.add(maxPriceLabel);
        
        maxPriceField = new JFormattedTextField(formatter);
        maxPriceField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        maxPriceField.setPreferredSize(new Dimension(80, 30));
        filterPanel.add(maxPriceField);

        // Panel separado para los botones para asegurar que estén bien visibles
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonPanel.setBackground(FormStyleManager.PANEL_COLOR);

        // Botones de filtro con tamaño aumentado
        JButton applyFilterBtn = FormStyleManager.createPrimaryButton("Aplicar");
        applyFilterBtn.setPreferredSize(new Dimension(120, 35)); // Tamaño aumentado
        applyFilterBtn.addActionListener(e -> applyFilters());
        buttonPanel.add(applyFilterBtn);

        JButton clearFilterBtn = FormStyleManager.createSecondaryButton("Limpiar");
        clearFilterBtn.setPreferredSize(new Dimension(120, 35)); // Tamaño aumentado
        clearFilterBtn.addActionListener(e -> clearFilters());
        buttonPanel.add(clearFilterBtn);

        // Añadir panel de botones como una nueva fila ocupando todo el ancho
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(FormStyleManager.PANEL_COLOR);
        wrapperPanel.add(filterPanel, BorderLayout.CENTER);
        wrapperPanel.add(buttonPanel, BorderLayout.SOUTH);

        return wrapperPanel;
    }

    private void loadSales() {
        try {
            tableModel.setRowCount(0);
            
            // Limpiar y repoblar los combos de filtrado
            populateFilterCombos();
    
            // Obtener la lista de ventas con depuración
            List<Sale> sales = saleService.getSalesByDateRange(null, null);
            System.out.println("Ventas obtenidas: " + sales.size());
    
            if (sales.isEmpty()) {
                System.out.println("No se encontraron ventas en la base de datos");
                return;
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
            
            // Aplicar filtros si hay alguno activo
            applyFilters();
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar ventas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFilterCombos() {
        try {
            // Mantener las selecciones actuales
            String selectedProduct = productFilterCombo.getSelectedItem() != null ? 
                    productFilterCombo.getSelectedItem().toString() : "Todos";
            String selectedCustomer = customerFilterCombo.getSelectedItem() != null ? 
                    customerFilterCombo.getSelectedItem().toString() : "Todos";
            
            // Restablecer los combos
            productFilterCombo.removeAllItems();
            customerFilterCombo.removeAllItems();
            
            // Añadir la opción "Todos"
            productFilterCombo.addItem("Todos");
            customerFilterCombo.addItem("Todos");
            
            // Poblar el combo de productos
            List<Product> products = productService.getAllProducts();
            for (Product product : products) {
                productFilterCombo.addItem(product.getProductName());
            }
            
            // Poblar el combo de clientes
            List<Customer> customers = customerService.getAllCustomers();
            for (Customer customer : customers) {
                customerFilterCombo.addItem(customer.getNameOrCompany());
            }
            
            // Restaurar las selecciones previas si existen en las nuevas listas
            setComboItem(productFilterCombo, selectedProduct);
            setComboItem(customerFilterCombo, selectedCustomer);
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar datos para filtros: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void setComboItem(JComboBox<String> combo, String item) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).equals(item)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
        combo.setSelectedIndex(0); // Si no se encuentra, seleccionar "Todos"
    }

    private void applyFilters() {
        ArrayList<RowFilter<Object, Object>> filters = new ArrayList<>();
        
        // Filtro de producto
        String productFilter = (String) productFilterCombo.getSelectedItem();
        if (productFilter != null && !productFilter.equals("Todos")) {
            filters.add(RowFilter.regexFilter("^" + Pattern.quote(productFilter) + "$", 4));
        }
        
        // Filtro de cliente
        String customerFilter = (String) customerFilterCombo.getSelectedItem();
        if (customerFilter != null && !customerFilter.equals("Todos")) {
            filters.add(RowFilter.regexFilter("^" + Pattern.quote(customerFilter) + "$", 1));
        }
        
        // Filtro de estado
        String statusFilter = (String) statusFilterCombo.getSelectedItem();
        if (statusFilter != null && !statusFilter.equals("Todos")) {
            filters.add(RowFilter.regexFilter("^" + Pattern.quote(statusFilter) + "$", 7));
        }
        
        // Filtro de método de pago
        String paymentMethodFilter = (String) paymentMethodFilterCombo.getSelectedItem();
        if (paymentMethodFilter != null && !paymentMethodFilter.equals("Todos")) {
            filters.add(RowFilter.regexFilter("^" + Pattern.quote(paymentMethodFilter) + "$", 5));
        }
        
        // Filtro de rango de precio
        Double minPrice = null;
        Double maxPrice = null;
        
        try {
            if (!minPriceField.getText().isEmpty()) {
                minPrice = NumberFormat.getInstance().parse(minPriceField.getText()).doubleValue();
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "El precio mínimo no es válido", 
                    "Error de filtro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            if (!maxPriceField.getText().isEmpty()) {
                maxPrice = NumberFormat.getInstance().parse(maxPriceField.getText()).doubleValue();
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "El precio máximo no es válido", 
                    "Error de filtro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (minPrice != null || maxPrice != null) {
            final Double finalMinPrice = minPrice;
            final Double finalMaxPrice = maxPrice;
            
            RowFilter<Object, Object> priceFilter = new RowFilter<Object, Object>() {
                @Override
                public boolean include(Entry<? extends Object, ? extends Object> entry) {
                    String priceStr = (String) entry.getValue(6);
                    try {
                        // Eliminar el formato para obtener el valor numérico
                        priceStr = priceStr.replace(",", ".");
                        double price = Double.parseDouble(priceStr);
                        
                        if (finalMinPrice != null && price < finalMinPrice) {
                            return false;
                        }
                        
                        if (finalMaxPrice != null && price > finalMaxPrice) {
                            return false;
                        }
                        
                        return true;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }
            };
            
            filters.add(priceFilter);
        }
        
        // Aplicar todos los filtros combinados
        if (!filters.isEmpty()) {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        } else {
            sorter.setRowFilter(null);
        }
    }
    
    private void clearFilters() {
        productFilterCombo.setSelectedItem("Todos");
        customerFilterCombo.setSelectedItem("Todos");
        statusFilterCombo.setSelectedItem("Todos");
        paymentMethodFilterCombo.setSelectedItem("Todos");
        minPriceField.setValue(null);
        maxPriceField.setValue(null);
        sorter.setRowFilter(null);
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
    
        // Convertir índice de vista a índice de modelo
        int modelRow = salesTable.convertRowIndexToModel(selectedRow);
        int saleId = (int) tableModel.getValueAt(modelRow, 0);
        try {
            // Obtener la venta por su ID
            Sale sale = saleService.getSaleById(saleId);
            if (sale == null) {
                JOptionPane.showMessageDialog(this, "No se encontró la venta seleccionada.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            // Obtener los detalles de la venta
            List<SaleDetail> saleDetails = saleService.getSaleDetails(saleId);
            if (saleDetails == null || saleDetails.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron los detalles de la venta seleccionada.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            EditSaleForm form = new EditSaleForm(saleService, customerService, productService, sale, saleDetails.get(0));
            form.setVisible(true);
            form.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    loadSales();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al obtener datos de la venta: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelSale() {
        int selectedRow = salesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una venta para cancelar.");
            return;
        }

        // Convertir índice de vista a índice de modelo
        int modelRow = salesTable.convertRowIndexToModel(selectedRow);
        int saleId = (int) tableModel.getValueAt(modelRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea cancelar esta venta?", 
                "Confirmar Cancelación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Obtener la venta por su ID
                Sale sale = saleService.getSaleById(saleId);
                if (sale == null) {
                    JOptionPane.showMessageDialog(this, "No se encontró la venta seleccionada.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Obtener los detalles de la venta
                List<SaleDetail> saleDetails = saleService.getSaleDetails(saleId);
                if (saleDetails.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No se encontraron los detalles de la venta seleccionada.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Devolver el stock de todos los productos
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
                    JOptionPane.showMessageDialog(this, 
                            "Venta cancelada correctamente y el stock de los productos ha sido actualizado.");
                    loadSales();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo cancelar la venta.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al cancelar la venta: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}