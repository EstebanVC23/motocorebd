package com.motocoredb.views.Parts;

import javax.swing.*;
import java.awt.*;

/**
 * Clase Boton que extiende JButton y proporciona un botón estilizado con un
 * diseño personalizado.
 */
public class Boton extends JButton {
    private final Color COLOR_BUTTON2 = new Color(41, 128, 185);

    /**
     * Constructor de la clase Boton.
     *
     * @param contenido Texto que se mostrará en el botón.
     * @param ancho     Ancho del botón en píxeles.
     * @param altura    Altura del botón en píxeles.
     * @param sizeFont  Tamaño de la letra del boton.
     * @param tipoBoton Estilo del boton elegido.
     */
    public Boton(String contenido, int ancho, int altura, int sizeFont, int tipoBoton) {
        super(contenido);

        if (tipoBoton == 1) {
            boton1(ancho, altura, sizeFont);
        } else if (tipoBoton == 2) {
            boton2(sizeFont);
        }
    }

    /**
     * Estilo del boton 1
     * 
     * @param ancho    ancho del boton
     * @param altura   altura del boton
     * @param sizeFont Tamaño de la letra del boton
     */
    private void boton1(int ancho, int altura, int sizeFont) {
        // Configuración del estilo del botón
        setFont(new Font("Segoe UI", Font.BOLD, sizeFont)); // Fuente y tamaño del texto
        setForeground(Color.white); // Color del texto
        setBackground(new Color(52, 152, 219)); // Color de fondo
        setOpaque(true); // Hace que el fondo sea visible
        setContentAreaFilled(true); // Asegura que el área de contenido se llene con el color de fondo
        setFocusPainted(false); // Elimina el borde de enfoque al seleccionar el botón
        setPreferredSize(new Dimension(ancho, altura)); // Establece el tamaño del botón
    }

    /**
     * Estilo del boton 2
     * 
     * @param sizeFont Tamaño de la letra del boton
     */
    private void boton2(int sizeFont) {
        setFont(new Font("Segoe UI", Font.ITALIC, sizeFont));
        setForeground(COLOR_BUTTON2);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBackground(null);
    }
}
