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
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.data.category.DefaultCategoryDataset;
import com.motocoredb.services.SaleService;
import com.motocoredb.views.utils.FormStyleManager;

public class SoldProductsBarChart {
    private final SaleService saleService;
    
    public SoldProductsBarChart(SaleService saleService) {
        this.saleService = saleService;
    }
    
    public JPanel getChartPanel() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Lógica para obtener datos del servicio
        saleService.getSoldProducts().forEach((product, quantity) -> {
            dataset.addValue(quantity, "Unidades Vendidas", product);
        });
        
        // Crear el gráfico con orientación horizontal para mejor visualización de nombres de productos
        JFreeChart barChart = ChartFactory.createBarChart(
                "Productos Más Vendidos",            // título
                "Producto",                          // etiqueta eje X
                "Unidades Vendidas",                 // etiqueta eje Y
                dataset,                             // datos
                PlotOrientation.HORIZONTAL,          // orientación horizontal
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
        renderer.setSeriesPaint(0, FormStyleManager.SECONDARY_COLOR);
        renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());
        renderer.setShadowVisible(false);
        renderer.setItemMargin(0.05);
        
        // Añadir etiquetas a las barras
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelFont(FormStyleManager.FIELD_FONT);
        
        // Personalizar los ejes
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryMargin(0.25);
        domainAxis.setLabelFont(FormStyleManager.LABEL_FONT);
        domainAxis.setTickLabelFont(FormStyleManager.FIELD_FONT);
        domainAxis.setMaximumCategoryLabelLines(2); // Permite múltiples líneas para nombres largos
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setLabelFont(FormStyleManager.LABEL_FONT);
        rangeAxis.setTickLabelFont(FormStyleManager.FIELD_FONT);
        
        // Personalizar el título - corregido el constructor
        TextTitle title = new TextTitle("Productos Más Vendidos");
        title.setFont(FormStyleManager.TITLE_FONT);
        title.setPaint(FormStyleManager.PRIMARY_COLOR);
        barChart.setTitle(title);
        
        barChart.setBackgroundPaint(FormStyleManager.BACKGROUND_COLOR);
        barChart.getLegend().setItemFont(FormStyleManager.FIELD_FONT);
        
        // Crear el panel del gráfico
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        chartPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        chartPanel.setBackground(FormStyleManager.BACKGROUND_COLOR);
        
        return chartPanel;
    }
}