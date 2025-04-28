package com.motocoredb.views.Main.Admin.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import com.motocoredb.services.SaleService;
import com.motocoredb.services.InventoryService;
import com.motocoredb.views.Main.Admin.panels.charts.EmployeeSalesBarChart;
import com.motocoredb.views.Main.Admin.panels.charts.SoldProductsBarChart;
import com.motocoredb.views.Main.Admin.panels.charts.SalesOverTimeChart;
import com.motocoredb.views.utils.FormStyleManager;

public class ReportPanel extends JPanel {
    private final SaleService saleService;
    private final InventoryService inventoryService;
    private JLabel incomeLabel;
    private JLabel expenseLabel;
    private JLabel netIncomeLabel;
    private JPanel employeeSalesPanel;   // Panel contenedor para el gráfico de empleados
    private JPanel soldProductsPanel;    // Panel contenedor para el gráfico de productos
    private JPanel salesTimePanel;       // Panel contenedor para el gráfico de ventas en tiempo
    private JPanel scrollablePanel;      // Panel que contiene los gráficos con scroll
    private JScrollPane scrollPane;      // Scroll pane mejorado

    public ReportPanel(SaleService saleService, InventoryService inventoryService) {
        this.saleService = saleService;
        this.inventoryService = inventoryService;

        setupUI(); // Configuración de la interfaz de usuario
    }

    private void setupUI() {
        setLayout(new BorderLayout(0, 10)); // Añadir espaciado entre componentes
        setBackground(FormStyleManager.BACKGROUND_COLOR); // Estilo de fondo
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Margen exterior

        JPanel topPanel = createTopPanel(); // Crear sectores principales
        scrollPane = createScrollableCharts(); // Panel con gráficos adicionales
        JPanel buttonPanel = createButtonPanel(); // Panel para el botón de actualización

        // Establecer tamaño preferido más pequeño para el panel superior
        topPanel.setPreferredSize(new Dimension(getWidth(), 140)); // Altura reducida de 180 a 140
        
        add(topPanel, BorderLayout.NORTH); // Sectores principales en la parte superior
        add(scrollPane, BorderLayout.CENTER); // Gráficos adicionales con scroll
        add(buttonPanel, BorderLayout.SOUTH); // Botón de actualización en la parte inferior
    }

    private JPanel createTopPanel() {
        // Usar FormStyleManager.RoundedPanel para un panel con esquinas redondeadas
        JPanel topPanel = new FormStyleManager.RoundedPanel(new GridLayout(1, 3, 15, 0), 12);
        topPanel.setBackground(FormStyleManager.SECONDARY_COLOR.brighter()); // Color de fondo más claro
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding interno reducido

        // Crear los paneles de información con esquinas redondeadas
        JPanel incomePanel = createInfoPanel("Ingresos", FormStyleManager.PRIMARY_COLOR);
        JPanel expensePanel = createInfoPanel("Gastos", FormStyleManager.ACCENT_COLOR);
        JPanel netIncomePanel = createInfoPanel("Ganancia neta", FormStyleManager.SUCCESS_COLOR);
        
        // Inicializar las etiquetas
        incomeLabel = new JLabel("$0.0", SwingConstants.CENTER);
        expenseLabel = new JLabel("$0.0", SwingConstants.CENTER);
        netIncomeLabel = new JLabel("$0.0", SwingConstants.CENTER);
        
        // Estilo para las etiquetas de valores
        styleValueLabel(incomeLabel);
        styleValueLabel(expenseLabel);
        styleValueLabel(netIncomeLabel);
        
        // Agregar las etiquetas a sus respectivos paneles
        incomePanel.add(incomeLabel, BorderLayout.CENTER);
        expensePanel.add(expenseLabel, BorderLayout.CENTER);
        netIncomePanel.add(netIncomeLabel, BorderLayout.CENTER);

        // Consultar datos dinámicos
        loadDynamicData();

        // Añadir componentes al topPanel (ahora solo 3 componentes, sin el gráfico)
        topPanel.add(incomePanel);
        topPanel.add(expensePanel);
        topPanel.add(netIncomePanel);

        return topPanel;
    }
    
    // Método auxiliar para crear paneles informativos con estilo consistente
    private JPanel createInfoPanel(String title, Color accentColor) {
        JPanel panel = new FormStyleManager.RoundedPanel(new BorderLayout(), 10);
        panel.setBackground(FormStyleManager.PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6)); // Padding interno reducido aún más (de 8 a 6)
        
        // Etiqueta de título en la parte superior
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(FormStyleManager.HEADING_FONT);
        titleLabel.setForeground(accentColor);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        return panel;
    }
    
    // Método auxiliar para estilizar etiquetas de valores
    private void styleValueLabel(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Fuente reducida de 22 a 20
        label.setForeground(FormStyleManager.TEXT_COLOR);
    }

    private void loadDynamicData() {
        // Obtener ingresos desde SQL - ventas representan ingresos
        double income = saleService.getTotalIncome(); 
        incomeLabel.setText("$" + String.format("%.2f", income));

        // Obtener gastos desde inventario - valor total de compras en inventario
        double expenses = inventoryService.getTotalPurchaseValue(); 
        expenseLabel.setText("$" + String.format("%.2f", expenses));

        // Calcular ganancia neta: ingresos (ventas) - gastos (compras)
        double netIncome = income - expenses;
        netIncomeLabel.setText("$" + String.format("%.2f", netIncome));
        
        // Cambiar el color del texto de acuerdo al valor
        if (netIncome > 0) {
            netIncomeLabel.setForeground(FormStyleManager.SUCCESS_COLOR);
        } else if (netIncome < 0) {
            netIncomeLabel.setForeground(FormStyleManager.ERROR_COLOR);
        } else {
            netIncomeLabel.setForeground(FormStyleManager.TEXT_COLOR);
        }
    }

    private JScrollPane createScrollableCharts() {
        scrollablePanel = new JPanel();
        scrollablePanel.setLayout(new GridLayout(3, 1, 0, 15)); // Cambiado de 2 a 3 filas para incluir el gráfico de empleados
        scrollablePanel.setBackground(FormStyleManager.BACKGROUND_COLOR);
        scrollablePanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5)); // Margen interno

        // Contenedores para los gráficos con bordes titulados
        // Añadir el panel de ventas por empleado al contenedor principal de gráficos
        employeeSalesPanel = new JPanel(new BorderLayout());
        employeeSalesPanel.setBorder(FormStyleManager.createTitledBorder("Ventas por Empleado"));
        employeeSalesPanel.setBackground(FormStyleManager.PANEL_COLOR);
        
        soldProductsPanel = new JPanel(new BorderLayout());
        soldProductsPanel.setBorder(FormStyleManager.createTitledBorder("Productos Más Vendidos"));
        soldProductsPanel.setBackground(FormStyleManager.PANEL_COLOR);
        
        salesTimePanel = new JPanel(new BorderLayout());
        salesTimePanel.setBorder(FormStyleManager.createTitledBorder("Ventas a lo Largo del Tiempo"));
        salesTimePanel.setBackground(FormStyleManager.PANEL_COLOR);
        
        // Inicializar los gráficos
        updateEmployeeSalesChart();
        updateSoldProductsChart();
        updateSalesOverTimeChart();

        // Agregar gráficos al panel scrollable en el orden deseado
        scrollablePanel.add(employeeSalesPanel);
        scrollablePanel.add(soldProductsPanel);
        scrollablePanel.add(salesTimePanel);

        // Colocar el panel scrollable dentro de un JScrollPane mejorado
        JScrollPane scrollPane = new JScrollPane(scrollablePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Incremento de unidad más sensible
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Quitar borde del scroll
        
        // Mejorar sensibilidad del scroll con la rueda del ratón - VELOCIDAD MEDIA
        scrollPane.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                // Multiplicador para hacer el scroll más moderado
                int scrollMultiplier = 2;
                
                // Crear un nuevo evento con incremento amplificado
                MouseWheelEvent amplifiedEvent = new MouseWheelEvent(
                    e.getComponent(), e.getID(), e.getWhen(), e.getModifiersEx(),
                    e.getX(), e.getY(), e.getXOnScreen(), e.getYOnScreen(),
                    e.getClickCount(), e.isPopupTrigger(), e.getScrollType(),
                    e.getScrollAmount(), e.getWheelRotation() * scrollMultiplier
                );
                
                // Enviar el evento amplificado al listener original
                for (MouseWheelListener listener : scrollPane.getMouseWheelListeners()) {
                    if (listener != this) {
                        listener.mouseWheelMoved(amplifiedEvent);
                    }
                }
            }
        });
        
        return scrollPane;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(FormStyleManager.BACKGROUND_COLOR);
        
        // Crear botón mejorado usando FormStyleManager
        JButton refreshButton = FormStyleManager.createPrimaryButton("Actualizar Datos");
        
        // Añadir acción al botón de actualización
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshAllData();
            }
        });
        
        buttonPanel.add(refreshButton);
        return buttonPanel;
    }
    
    private void refreshAllData() {
        // Mostrar feedback visual de que se está actualizando
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        // Actualizar etiquetas de datos financieros
        loadDynamicData();
        
        // Actualizar todos los gráficos recreándolos
        updateEmployeeSalesChart();
        updateSoldProductsChart();
        updateSalesOverTimeChart();
        
        // Revalidar y repintar todos los paneles para asegurar que se actualicen visualmente
        employeeSalesPanel.revalidate();
        employeeSalesPanel.repaint();
        soldProductsPanel.revalidate();
        soldProductsPanel.repaint();
        salesTimePanel.revalidate();
        salesTimePanel.repaint();
        
        // Restaurar cursor
        setCursor(Cursor.getDefaultCursor());
        
        // Mensaje de confirmación
        FormStyleManager.showSuccessDialog(this, "Datos actualizados correctamente");
    }
    
    // Métodos para actualizar cada gráfico individualmente
    private void updateEmployeeSalesChart() {
        // Limpiar el panel
        employeeSalesPanel.removeAll();
        
        // Crear un nuevo gráfico con datos actualizados
        EmployeeSalesBarChart chart = new EmployeeSalesBarChart(saleService);
        JPanel chartPanel = chart.getChartPanel();
        
        // Añadir el nuevo gráfico al panel
        employeeSalesPanel.add(chartPanel, BorderLayout.CENTER);
    }
    
    private void updateSoldProductsChart() {
        // Limpiar el panel
        soldProductsPanel.removeAll();
        
        // Crear un nuevo gráfico con datos actualizados
        SoldProductsBarChart chart = new SoldProductsBarChart(saleService);
        JPanel chartPanel = chart.getChartPanel();
        
        // Añadir el nuevo gráfico al panel
        soldProductsPanel.add(chartPanel, BorderLayout.CENTER);
    }
    
    private void updateSalesOverTimeChart() {
        // Limpiar el panel
        salesTimePanel.removeAll();
        
        // Crear un nuevo gráfico con datos actualizados
        SalesOverTimeChart chart = new SalesOverTimeChart(saleService);
        JPanel chartPanel = chart.getChartPanel();
        
        // Añadir el nuevo gráfico al panel
        salesTimePanel.add(chartPanel, BorderLayout.CENTER);
    }
}