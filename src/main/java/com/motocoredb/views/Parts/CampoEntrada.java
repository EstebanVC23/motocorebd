package com.motocoredb.views.Parts;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

/**
 * Clase CampoEntrada que extiende JPanel para representar un campo de entrada
 * personalizado.
 * Permite la creación de campos de texto, contraseña o solo números enteros.
 */
public class CampoEntrada extends JPanel {
    private JTextField campoTexto;
    private JPasswordField campoPassword;
    private JLabel label;
    private JLabel icono;
    private String tipo;

    /**
     * Constructor de la clase CampoEntrada.
     *
     * @param tipo      Tipo de campo ("texto", "password", "entero").
     * @param labelText Texto de la etiqueta asociada al campo.
     * @param iconoText Texto o ícono que acompaña al campo.
     * @param ancho     Ancho del campo de entrada.
     * @param alto      Alto del campo de entrada.
     */
    public CampoEntrada(String tipo, String labelText, String iconoText, int ancho, int alto) {
        this.tipo = tipo;
        setOpaque(false);
        setLayout(new BorderLayout(10, 0));

        // Crear el ícono
        icono = new JLabel(iconoText);
        icono.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        icono.setForeground(new Color(41, 128, 185));

        // Crear la etiqueta
        label = new JLabel(labelText);
        label.setForeground(new Color(44, 62, 80));
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JPanel panelLabel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelLabel.setOpaque(false);
        panelLabel.add(label);

        Dimension campoDimension = new Dimension(ancho, alto);

        // Crear el campo según el tipo
        crearCampo(tipo, campoDimension);
    }

    /**
     * Crea el campo de entrada según su tipo.
     * 
     * @param tipo           Tipo de campo (texto, password, entero).
     * @param campoDimension Dimensión del campo.
     */
    private void crearCampo(String tipo, Dimension campoDimension) {
        if (tipo.equals("password")) {
            campoPassword = new JPasswordField(20);
            estilizarCampo(campoPassword, campoDimension);
            addComponentes(campoPassword);
        } else {
            campoTexto = new JTextField(20);
            estilizarCampo(campoTexto, campoDimension);

            if (tipo.equals("entero")) { // Aplicar filtro solo si es un campo numérico
                ((AbstractDocument) campoTexto.getDocument()).setDocumentFilter(new DocumentFilter() {
                    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                            throws BadLocationException {
                        if (string.matches("\\d*")) { // Solo permite números
                            super.insertString(fb, offset, string, attr);
                        }
                    }

                    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                            throws BadLocationException {
                        if (text.matches("\\d*")) { // Solo permite números
                            super.replace(fb, offset, length, text, attrs);
                        }
                    }
                });
            }
            addComponentes(campoTexto);
        }
    }

    /**
     * Aplica estilos visuales comunes a los campos de entrada.
     * 
     * @param campo     Campo de entrada.
     * @param dimension Dimensión del campo.
     */
    private void estilizarCampo(JComponent campo, Dimension dimension) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(41, 128, 185)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        campo.setBackground(new Color(236, 240, 241));
        campo.setPreferredSize(dimension);
    }

    /**
     * Añade los componentes al panel principal.
     * 
     * @param campo Componente de entrada a agregar.
     */
    private void addComponentes(JComponent campo) {
        add(label, BorderLayout.NORTH);
        add(icono, BorderLayout.WEST);
        add(campo, BorderLayout.CENTER);
    }

    /**
     * Obtiene el texto ingresado en el campo.
     * 
     * @return Texto del campo o cadena vacía si no es válido.
     */
    public String getText() {
        if (tipo.equals("texto") || tipo.equals("entero")) {
            return campoTexto.getText();
        } else if (tipo.equals("password")) {
            return new String(campoPassword.getPassword());
        }
        return "";
    }
}
