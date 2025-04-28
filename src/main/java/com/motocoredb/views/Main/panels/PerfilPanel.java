package com.motocoredb.views.Main.panels;

import com.motocoredb.models.User;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class PerfilPanel extends JPanel {
    
    private final User currentUser;
    
    public PerfilPanel(User user) {
        this.currentUser = user;
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel de información del perfil
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(5, 2, 10, 20));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel titleLabel = new JLabel("Información del Usuario");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        
        // Datos del usuario
        infoPanel.add(new JLabel("Nombre completo:"));
        infoPanel.add(new JLabel(currentUser.getFullName()));
        
        infoPanel.add(new JLabel("Nombre de usuario:"));
        infoPanel.add(new JLabel(currentUser.getUsername()));
        
        infoPanel.add(new JLabel("Rol:"));
        infoPanel.add(new JLabel(currentUser.getRole()));
        
        infoPanel.add(new JLabel("Estado:"));
        infoPanel.add(new JLabel(currentUser.getStatus()));
        
        // Verificar si lastLogin es nulo antes de llamar a toString()
        infoPanel.add(new JLabel("Último inicio de sesión:"));
        String lastLoginText;
        if (currentUser.getLastLogin() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            lastLoginText = dateFormat.format(currentUser.getLastLogin());
        } else {
            lastLoginText = "Primera sesión";
        }
        infoPanel.add(new JLabel(lastLoginText));
        
        // Agregar componentes al panel principal
        add(titleLabel, BorderLayout.NORTH);
        add(infoPanel, BorderLayout.CENTER);
    }
}