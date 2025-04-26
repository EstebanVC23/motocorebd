package com.motocoredb.views.Main.navbar;

import javax.swing.*;

import com.motocoredb.utils.Colors;

import java.awt.*;

public class ContentCard extends JPanel {
    public ContentCard(String title, String content) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(207, 216, 220), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Colors.DARK_BLUE);
        
        JLabel contentLabel = new JLabel(content);
        contentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        contentLabel.setForeground(Colors.INACTIVE_TEXT);
        contentLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        add(titleLabel, BorderLayout.NORTH);
        add(contentLabel, BorderLayout.CENTER);
    }
}
