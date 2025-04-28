package com.motocoredb.views.Main.panels;

import com.motocoredb.models.User;
import com.motocoredb.utils.Colors;

import javax.swing.*;
import java.awt.*;

public class PerfilPanel extends JPanel {
    public PerfilPanel(User usuario) {
        initUI(usuario);
    }

    private void initUI(User usuario) {
        setLayout(new BorderLayout());
        setBackground(Colors.LIGHT_BLUE);

        // Encabezado
        JLabel headerLabel = new JLabel("Perfil de Usuario");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(Colors.PRIMARY_BLUE);
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        add(headerLabel, BorderLayout.NORTH);

        // Panel de contenido
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colors.DARK_BLUE, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;

        addField("Nombre Completo:", usuario.getFullName(), gbc, contentPanel);
        addField("Usuario:", usuario.getUsername(), gbc, contentPanel);
        addField("Rol:", usuario.getRole(), gbc, contentPanel);
        addField("Registrado:", usuario.getCreatedAt().toString(), gbc, contentPanel);
        addField("Último Acceso:", usuario.getLastLogin().toString(), gbc, contentPanel);

        add(contentPanel, BorderLayout.CENTER);

        // Barra inferior
        JLabel footerLabel = new JLabel("Actualizado recientemente");
        footerLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        footerLabel.setForeground(Colors.INACTIVE_TEXT);
        footerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        footerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(footerLabel, BorderLayout.SOUTH);
    }

    private void addField(String label, String value, GridBagConstraints gbc, JPanel panel) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(Colors.DARK_BLUE);
        panel.add(lbl, gbc);

        gbc.gridx++;
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        val.setForeground(Colors.INACTIVE_TEXT);
        panel.add(val, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
    }
}