package com.motocoredb.views.Main.navbar;

import javax.swing.*;
import com.motocoredb.utils.Colors;
import java.awt.*;
import java.util.function.Consumer;

public class Navbar extends JPanel {
    private NavButton activeButton;

    public Navbar(String[] options, Consumer<String> navActionListener) {
        setLayout(new BorderLayout());
        setBackground(Colors.PRIMARY_BLUE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 4, 0, Colors.SECONDARY_BLUE),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        // Panel de logo y título
        JPanel logoPanel = createLogoPanel();
        add(logoPanel, BorderLayout.WEST);

        // Panel de botones de navegación
        JPanel buttonsPanel = createButtonsPanel(navActionListener, options);
        add(buttonsPanel, BorderLayout.CENTER);
    }

    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        logoPanel.setBackground(Colors.PRIMARY_BLUE);

        JLabel logoIcon = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Colors.SECONDARY_BLUE);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                String text = "DB";
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(text, x, y);
                g2.dispose();
            }
        };
        logoIcon.setPreferredSize(new Dimension(32, 32));

        JLabel logoLabel = new JLabel("MotoCoreDB");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoLabel.setForeground(Color.WHITE);

        logoPanel.add(logoIcon);
        logoPanel.add(logoLabel);

        return logoPanel;
    }

    private JPanel createButtonsPanel(Consumer<String> listener, String[] buttonLabels) {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonsPanel.setBackground(Colors.PRIMARY_BLUE);

        for (String label : buttonLabels) {
            NavButton button = new NavButton(label);
            
            // Configurar la acción para el botón
            button.addActionListener(e -> {
                setActiveButton(button); // Cambiar el botón activo
                listener.accept(label); // Informar al listener sobre el cambio de panel
            });

            buttonsPanel.add(button);

            // Definir botón activo inicial
            if (label.equalsIgnoreCase("Inventario")) {
                setActiveButton(button);
            }
        }

        return buttonsPanel;
    }

    public void setActiveButton(NavButton button) {
        if (activeButton != null) {
            activeButton.setActive(false); // Desactivamos el botón anterior
        }
        activeButton = button;
        if (activeButton != null) {
            activeButton.setActive(true); // Activamos el nuevo botón
        }
    }
}