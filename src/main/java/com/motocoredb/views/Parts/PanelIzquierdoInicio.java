package com.motocoredb.views.Parts;

import javax.swing.*;
import java.awt.*;

/**
 * Clase PanelIzquierdoInicio que extiende JPanel.
 * Esta clase representa un panel con un título, un subtítulo y una imagen, con
 * un fondo personalizado.
 */
public class PanelIzquierdoInicio extends JPanel {
    private JLabel labelLogo; // Etiqueta para el título principal
    private String titulo; // Texto del título principal
    private String subtitulo; // Texto del subtítulo

    /**
     * Constructor de la clase PanelIzquierdoInicio.
     *
     * @param titulo    Texto del título principal.
     * @param subtitulo Texto del subtítulo.
     */
    public PanelIzquierdoInicio(String titulo, String subtitulo) {
        this.titulo = titulo;
        this.subtitulo = subtitulo;

        // Establece el diseño del panel como BorderLayout.
        setLayout(new BorderLayout());

        // Hace que el panel sea transparente.
        setOpaque(false);

        // Llama al método que crea y organiza los componentes del panel.
        crearPanel();
    }

    /**
     * Método privado que configura los componentes del panel.
     */
    private void crearPanel() {
        // Creación del título principal.
        labelLogo = new JLabel(titulo, JLabel.CENTER);
        labelLogo.setForeground(Color.WHITE); // Color del texto en blanco
        labelLogo.setFont(new Font("Segoe UI", Font.BOLD, 32)); // Fuente y tamaño del título

        // Creación del subtítulo.
        JLabel subTitulo = new JLabel(subtitulo, JLabel.CENTER);
        subTitulo.setForeground(new Color(236, 240, 241, 200)); // Color gris claro con transparencia
        subTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Fuente y tamaño del subtítulo

        // Panel central para organizar el contenido (logo y textos).
        JPanel panelCentro = new JPanel();
        panelCentro.setOpaque(false); // Hace el panel transparente
        panelCentro.setLayout(new BoxLayout(panelCentro, BoxLayout.Y_AXIS)); // Layout en forma de columna

        // Cargar la imagen desde los recursos.
        ImageIcon iconoOriginal = null;
        try {
            iconoOriginal = new ImageIcon(getClass().getResource("../Resourses/store.jpg"));
        } catch (Exception e) {
            System.out.println("No se pudo cargar la imagen: " + e.getMessage());
        }

        JLabel iconoLabel; // Etiqueta para la imagen o el placeholder

        if (iconoOriginal != null) {
            // Si la imagen se carga correctamente, se escala a 120x120 píxeles.
            Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            ImageIcon iconoEscalado = new ImageIcon(imagenEscalada);
            iconoLabel = new JLabel(iconoEscalado);
        } else {
            // Si no se encuentra la imagen, se crea un label vacío con tamaño fijo.
            iconoLabel = new JLabel();
            iconoLabel.setPreferredSize(new Dimension(120, 120));
            iconoLabel.setMinimumSize(new Dimension(120, 120));
            iconoLabel.setMaximumSize(new Dimension(120, 120));
        }

        // Centrar la imagen en el panel.
        iconoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Agregar los componentes al panel central con espaciado.
        panelCentro.add(Box.createVerticalGlue()); // Espacio flexible arriba
        panelCentro.add(iconoLabel);
        panelCentro.add(Box.createRigidArea(new Dimension(0, 20))); // Espacio entre la imagen y el título
        panelCentro.add(labelLogo);
        labelLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentro.add(Box.createRigidArea(new Dimension(0, 10))); // Espacio entre el título y el subtítulo
        panelCentro.add(subTitulo);
        subTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentro.add(Box.createVerticalGlue()); // Espacio flexible abajo

        // Agregar el panel con los elementos al centro del PanelIzquierdoInicio.
        add(panelCentro, BorderLayout.CENTER);
    }

    /**
     * Sobrescribe el método paintComponent para personalizar el fondo del panel con
     * un gradiente de color.
     *
     * @param g Objeto Graphics que permite dibujar en el panel.
     */
    @Override
    protected void paintComponent(Graphics g) {
        // Llama al método original de la superclase para mantener la funcionalidad
        // base.
        super.paintComponent(g);

        // Convierte el objeto Graphics a Graphics2D para habilitar mejoras en la
        // calidad del dibujo.
        Graphics2D g2d = (Graphics2D) g;

        // Define un degradado de color azul claro a azul oscuro que va de arriba hacia
        // abajo.
        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(41, 128, 185), // Color inicial (parte superior)
                0, getHeight(), new Color(26, 83, 119) // Color final (parte inferior)
        );

        // Aplica el degradado como fondo del panel.
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}
