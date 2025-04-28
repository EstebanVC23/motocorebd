package com.motocoredb.views.Main.panels.charts;

import javax.swing.*;
import java.awt.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.category.DefaultCategoryDataset;
import com.motocoredb.services.SaleService;
import com.motocoredb.views.utils.FormStyleManager;

public class EmployeeSalesBarChart {
    private final SaleService saleService;
    
    public EmployeeSalesBarChart(SaleService saleService) {
        this.saleService = saleService;
    }
    
    public JPanel getChartPanel() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Lógica para obtener datos del servicio
        saleService.getTopEmployees().forEach((employee, sales) -> {
            dataset.addValue(sales, "Ventas", employee);
        });
        
        // Crear el gráfico con orientación vertical
        JFreeChart barChart = ChartFactory.createBarChart(
                "Rendimiento de Ventas por Empleado",  // título principal
                "Empleado",                           // etiqueta eje X
                "Productos Vendidos",                 // etiqueta eje Y
                dataset,                              // datos
                PlotOrientation.VERTICAL,             // orientación
                true,                                // mostrar leyenda
                true,                                // usar tooltips
                false                                // usar URLs
        );
        
        // Personalizar el aspecto del gráfico
        CategoryPlot plot = barChart.getCategoryPlot();
        
        // Personalizar el fondo
        plot.setBackgroundPaint(FormStyleManager.PANEL_COLOR);
        plot.setOutlinePaint(FormStyleManager.PRIMARY_COLOR);
        plot.setDomainGridlinePaint(new Color(220, 220, 220));
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        
        // Personalizar el renderer de barras
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, FormStyleManager.PRIMARY_COLOR);
        renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());
        renderer.setShadowVisible(false);
        renderer.setDrawBarOutline(true);
        renderer.setDefaultOutlinePaint(new Color(60, 60, 60, 100));
        renderer.setItemMargin(0.1);
        
        // Personalizar los ejes
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryMargin(0.25);
        domainAxis.setLabelFont(FormStyleManager.LABEL_FONT);
        domainAxis.setTickLabelFont(FormStyleManager.FIELD_FONT);
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setLabelFont(FormStyleManager.LABEL_FONT);
        rangeAxis.setTickLabelFont(FormStyleManager.FIELD_FONT);
        
        // Personalizar el título - corregido el constructor
        TextTitle title = new TextTitle("Rendimiento de Ventas por Empleado");
        title.setFont(FormStyleManager.TITLE_FONT);
        title.setPaint(FormStyleManager.PRIMARY_COLOR);
        barChart.setTitle(title);
        
        barChart.setBackgroundPaint(FormStyleManager.BACKGROUND_COLOR);
        
        // Crear el panel del gráfico
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        chartPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        chartPanel.setBackground(FormStyleManager.BACKGROUND_COLOR);
        
        return chartPanel;
    }
}