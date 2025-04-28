package com.motocoredb.views.Main.Admin.panels.charts;

import javax.swing.*;
import java.awt.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import com.motocoredb.services.SaleService;
import com.motocoredb.views.utils.FormStyleManager;

public class SalesOverTimeChart {
    private final SaleService saleService;
    
    public SalesOverTimeChart(SaleService saleService) {
        this.saleService = saleService;
    }
    
    public JPanel getChartPanel() {
        // Crear una serie temporal para las ventas mensuales
        TimeSeries series = new TimeSeries("Ventas Mensuales");
        
        // Obtener año actual para mostrar datos relevantes
        int currentYear = LocalDate.now().getYear();
        
        // Lógica para obtener datos del servicio utilizando el método existente
        saleService.getSalesOverTime().forEach((month, totalSales) -> {
            // Convertir java.time.Month a JFreeChart Month
            series.add(new Month(month.getValue(), currentYear), totalSales);
        });
        
        // Crear el dataset con la serie temporal
        TimeSeriesCollection dataset = new TimeSeriesCollection(series);
        
        // Crear el gráfico de líneas temporal
        JFreeChart lineChart = ChartFactory.createTimeSeriesChart(
                "Ventas Totales por Mes",    // título
                "Mes",                       // etiqueta eje X
                "Total Ventas ($)",          // etiqueta eje Y
                dataset,                     // datos
                true,                        // mostrar leyenda
                true,                        // usar tooltips
                false                        // usar URLs
        );
        
        // Personalizar el aspecto del gráfico
        XYPlot plot = lineChart.getXYPlot();
        
        // Personalizar el fondo
        plot.setBackgroundPaint(FormStyleManager.PANEL_COLOR);
        plot.setOutlinePaint(FormStyleManager.PRIMARY_COLOR);
        plot.setDomainGridlinePaint(new Color(220, 220, 220));
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        
        // Personalizar el renderer de líneas
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, FormStyleManager.PRIMARY_COLOR);
        renderer.setSeriesStroke(0, new BasicStroke(2.5f));
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-4, -4, 8, 8));
        
        // Personalizar el eje X (fechas)
        DateAxis domainAxis = (DateAxis) plot.getDomainAxis();
        domainAxis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
        domainAxis.setLabelFont(FormStyleManager.LABEL_FONT);
        domainAxis.setTickLabelFont(FormStyleManager.FIELD_FONT);
        
        // Personalizar el eje Y (ventas)
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
        rangeAxis.setLabelFont(FormStyleManager.LABEL_FONT);
        rangeAxis.setTickLabelFont(FormStyleManager.FIELD_FONT);
        
        // Auto-ajustar el rango del eje Y basado en los datos
        double maxSale = series.getMaxY();
        double upperBound = Math.ceil(maxSale * 1.1); // 10% más que el máximo para espacio
        rangeAxis.setRange(0, upperBound);
        
        // Personalizar el título
        TextTitle title = new TextTitle("Ventas Totales por Mes");
        title.setFont(FormStyleManager.TITLE_FONT);
        title.setPaint(FormStyleManager.PRIMARY_COLOR);
        lineChart.setTitle(title);
        
        // Personalizar el fondo general y la leyenda
        lineChart.setBackgroundPaint(FormStyleManager.BACKGROUND_COLOR);
        lineChart.getLegend().setItemFont(FormStyleManager.FIELD_FONT);
        lineChart.getLegend().setBackgroundPaint(FormStyleManager.PANEL_COLOR);
        
        // Crear el panel del gráfico
        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        chartPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        chartPanel.setBackground(FormStyleManager.BACKGROUND_COLOR);
        
        return chartPanel;
    }
}