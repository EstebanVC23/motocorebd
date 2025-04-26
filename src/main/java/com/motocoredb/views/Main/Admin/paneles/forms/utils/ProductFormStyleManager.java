package com.motocoredb.views.Main.Admin.paneles.forms.utils;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ProductFormStyleManager {
    // Esquema de colores
    public static final Color PRIMARY_COLOR = new Color(25, 118, 210);
    public static final Color SECONDARY_COLOR = new Color(66, 165, 245);
    public static final Color ACCENT_COLOR = new Color(255, 110, 64); 
    public static final Color BACKGROUND_COLOR = new Color(252, 252, 252);
    public static final Color PANEL_COLOR = new Color(255, 255, 255);
    public static final Color TEXT_COLOR = new Color(33, 33, 33);
    public static final Color LABEL_COLOR = new Color(97, 97, 97);
    public static final Color SUCCESS_COLOR = new Color(46, 125, 50);
    public static final Color ERROR_COLOR = new Color(198, 40, 40);

    // Fuentes
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FIELD_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 13);

    /**
     * Crea un botón principal con estilo personalizado
     */
    public static JButton createPrimaryButton(String text) {
        return createStyledButton(text, PRIMARY_COLOR, Color.WHITE);
    }

    /**
     * Crea un botón secundario con estilo personalizado
     */
    public static JButton createSecondaryButton(String text) {
        return createStyledButton(text, Color.WHITE, PRIMARY_COLOR);
    }

    /**
     * Crea una etiqueta de encabezado con estilo personalizado
     */
    public static JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(TITLE_FONT);
        label.setForeground(PRIMARY_COLOR);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        return label;
    }

    /**
     * Crea una etiqueta estándar con estilo personalizado
     */
    public static JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(LABEL_COLOR);
        return label;
    }

    /**
     * Crea un campo de texto con estilo personalizado y bordes redondeados
     */
    public static JTextField createStyledTextField() {
        JTextField field = new JTextField() {
            @Override 
            protected void paintComponent(Graphics g) {
                if (!isOpaque() && getBorder() instanceof RoundedCornerBorder) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setPaint(getBackground());
                    g2.fill(((RoundedCornerBorder)getBorder()).getBorderShape(
                            0, 0, getWidth()-1, getHeight()-1));
                    g2.dispose();
                }
                super.paintComponent(g);
            }
            @Override 
            public void updateUI() {
                super.updateUI();
                setOpaque(false);
                setBorder(new RoundedCornerBorder());
            }
        };
        field.setFont(FIELD_FONT);
        field.setBackground(new Color(245, 245, 245));
        return field;
    }

    /**
     * Método privado para crear botones con estilo
     */
    private static JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                
                FontMetrics fm = g2.getFontMetrics();
                Rectangle textRect = new Rectangle(0, 0, getWidth(), getHeight());
                String buttonText = text;
                
                int textX = (textRect.width - fm.stringWidth(buttonText)) / 2;
                int textY = (textRect.height - fm.getHeight()) / 2 + fm.getAscent();
                
                g2.setColor(fgColor);
                g2.setFont(getFont());
                g2.drawString(buttonText, textX, textY);
                g2.dispose();
            }
            
            @Override
            public Dimension getPreferredSize() {
                Dimension size = super.getPreferredSize();
                size.width += 20;
                size.height = 36;
                return size;
            }
        };
        
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(8, 16, 8, 16));
        
        return button;
    }

    /**
     * Crea un borde con título personalizado
     */
    public static Border createTitledBorder(String title) {
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 0)),
                title);
        titledBorder.setTitleFont(HEADING_FONT);
        titledBorder.setTitleColor(PRIMARY_COLOR);
        titledBorder.setTitlePosition(TitledBorder.ABOVE_TOP);
        titledBorder.setTitleJustification(TitledBorder.LEFT);
        
        return BorderFactory.createCompoundBorder(
                titledBorder,
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );
    }

    /**
     * Muestra un diálogo de éxito personalizado
     */
    public static void showSuccessDialog(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent, 
            message, 
            "Operación Exitosa", 
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Muestra un diálogo de error personalizado
     */
    public static void showErrorDialog(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent, 
            message, 
            "Error", 
            JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Clase interna para crear bordes redondeados
     */
    public static class RoundedCornerBorder extends AbstractBorder {
        private static final int RADIUS = 12;
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(SECONDARY_COLOR.brighter());
            g2.draw(getBorderShape(x, y, width-1, height-1));
            g2.dispose();
        }
        
        public Shape getBorderShape(int x, int y, int width, int height) {
            return new RoundRectangle2D.Double(x, y, width, height, RADIUS, RADIUS);
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(6, 10, 6, 10);
        }
    }
    
    /**
     * Panel con esquinas redondeadas
     */
    public static class RoundedPanel extends JPanel {
        private final int radius;
        
        public RoundedPanel(LayoutManager layout, int radius) {
            super(layout);
            this.radius = radius;
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth()-1, getHeight()-1, radius, radius));
            g2.dispose();
        }
    }
}
