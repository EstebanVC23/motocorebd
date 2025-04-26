package com.motocoredb.views.Main.navbar;

import javax.swing.*;
import com.motocoredb.utils.Colors;
import java.awt.*;
import java.awt.event.*;

public class NavButton extends JButton {
    private boolean active; // Nuevo atributo para controlar el estado activo

    public NavButton(String text) {
        super(text);
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setForeground(Color.WHITE); // Texto blanco inicialmente
        setBackground(null); // Fondo transparente
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setContentAreaFilled(false);

        addMouseListener(new NavButtonHoverEffect(this));
    }

    public void setActive(boolean active) {
        this.active = active; // Controlamos el estado del botón
        if (active) {
            setForeground(Color.WHITE); // Texto blanco al estar activo
            setBackground(Colors.DARK_BLUE); // Fondo azul oscuro al estar activo
            setContentAreaFilled(true); // Fondo visible
        } else {
            setForeground(Color.WHITE); // Texto blanco al no estar activo
            setBackground(null); // Fondo transparente
            setContentAreaFilled(false); // Sin fondo visible
        }
        repaint(); // Forzamos que el botón se actualice visualmente
    }

    public boolean isActive() {
        return active;
    }
}

class NavButtonHoverEffect extends MouseAdapter {
    private final NavButton button;

    public NavButtonHoverEffect(NavButton button) {
        this.button = button;
    }

    @Override
    public void mouseEntered(MouseEvent evt) {
        if (!button.isActive()) {
            button.setForeground(Colors.DARK_BLUE); // Cambia a azul oscuro al pasar el mouse
        }
    }

    @Override
    public void mouseExited(MouseEvent evt) {
        if (!button.isActive()) {
            button.setForeground(Color.WHITE); // Vuelve a blanco si no está activo
        }
    }
}