package com.motocoredb.views.forms.appointments;

import javax.swing.*;

import com.motocoredb.views.utils.FormStyleManager;

import java.awt.*;

public abstract class AppointmentFormBase extends JFrame {
    protected JPanel mainPanel;
    protected JPanel formPanel;
    protected JPanel buttonPanel;
    protected JPanel headerPanel;

    protected AppointmentFormBase(String title, int width, int height) {
        setTitle(title);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(FormStyleManager.BACKGROUND_COLOR);
        add(mainPanel);

        initializeBaseComponents();
    }

    private void initializeBaseComponents() {
        headerPanel = createHeaderPanel();
        formPanel = createFormPanel();
        buttonPanel = createButtonPanel();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    protected abstract void initializeForm();

    protected JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setOpaque(false);

        JLabel headerTitle = FormStyleManager.createHeaderLabel(getTitle());
        headerPanel.add(headerTitle);

        return headerPanel;
    }

    protected JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(FormStyleManager.PANEL_COLOR);
        return formPanel;
    }

    protected JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton saveButton = FormStyleManager.createPrimaryButton("Guardar");
        JButton cancelButton = FormStyleManager.createSecondaryButton("Cancelar");

        saveButton.addActionListener(e -> onSave());
        cancelButton.addActionListener(e -> onCancel());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    protected abstract void onSave();

    protected abstract void onCancel();
}