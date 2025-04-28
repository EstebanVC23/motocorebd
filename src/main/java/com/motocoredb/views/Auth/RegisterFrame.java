package com.motocoredb.views.Auth;

import com.motocoredb.controllers.AuthController;
import com.motocoredb.models.Staff;
import com.motocoredb.models.User;

import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * Clase que representa la interfaz gráfica de registro para MotoCoreDB
 * Solo permite registrar usuarios si existe un miembro de staff con el documento de identidad indicado
 */
public class RegisterFrame extends JFrame {

    // Definición de colores y fuentes
    private final Color COLOR_FONDO = new Color(236, 240, 241);
    private final Color COLOR_TEXTO = new Color(44, 62, 80);
    private final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 24);

    // Componentes de la interfaz
    private JTextField campoIdentityDocument;
    private JTextField campoUsername;
    private JPasswordField campoPassword;
    private JPasswordField campoConfirmPassword;
    private JButton botonRegistrar;
    private JButton botonVolver;
    private final AuthController authController;

    /**
     * Constructor de la clase RegisterFrame
     * 
     * @param authController Controlador de autenticación
     */
    public RegisterFrame(AuthController authController) {
        this.authController = authController;
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setTitle("MotoCoreDB - Registro de Usuario");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new GridLayout(1, 2));
    }

    private void inicializarComponentes() {
        crearPanelIzquierdo();
        crearPanelDerecho();
        configurarEventos();
    }

    private void crearPanelIzquierdo() {
        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setBackground(new Color(52, 152, 219));
        panelIzquierdo.setLayout(new BorderLayout());
        
        JLabel titulo = new JLabel("REGÍSTRATE", SwingConstants.CENTER);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        
        JLabel subtitulo = new JLabel("Crea tu cuenta de usuario", SwingConstants.CENTER);
        subtitulo.setForeground(Color.WHITE);
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        
        panelIzquierdo.add(titulo, BorderLayout.CENTER);
        panelIzquierdo.add(subtitulo, BorderLayout.SOUTH);
        panelIzquierdo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        add(panelIzquierdo);
    }

    private void crearPanelDerecho() {
        JPanel panelDerecho = new JPanel();
        panelDerecho.setBackground(COLOR_FONDO);
        panelDerecho.setLayout(new BorderLayout(20, 20));
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Panel del título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setOpaque(false);
        JLabel labelTitulo = new JLabel("Registrar Usuario");
        labelTitulo.setForeground(COLOR_TEXTO);
        labelTitulo.setFont(FUENTE_TITULO);
        panelTitulo.add(labelTitulo);

        // Panel del formulario
        JPanel panelFormulario = new JPanel();
        panelFormulario.setOpaque(false);
        panelFormulario.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campo de documento de identidad
        JPanel panelIdentityDocument = new JPanel(new BorderLayout(5, 5));
        panelIdentityDocument.setOpaque(false);
        panelIdentityDocument.add(new JLabel("Documento de Identidad:"), BorderLayout.NORTH);
        campoIdentityDocument = new JTextField();
        campoIdentityDocument.setPreferredSize(new Dimension(300, 30));
        panelIdentityDocument.add(campoIdentityDocument, BorderLayout.CENTER);

        // Campo de nombre de usuario
        JPanel panelUsername = new JPanel(new BorderLayout(5, 5));
        panelUsername.setOpaque(false);
        panelUsername.add(new JLabel("Nombre de Usuario:"), BorderLayout.NORTH);
        campoUsername = new JTextField();
        campoUsername.setPreferredSize(new Dimension(300, 30));
        panelUsername.add(campoUsername, BorderLayout.CENTER);

        // Campo de contraseña
        JPanel panelPassword = new JPanel(new BorderLayout(5, 5));
        panelPassword.setOpaque(false);
        panelPassword.add(new JLabel("Contraseña:"), BorderLayout.NORTH);
        campoPassword = new JPasswordField();
        campoPassword.setPreferredSize(new Dimension(300, 30));
        panelPassword.add(campoPassword, BorderLayout.CENTER);

        // Campo de confirmar contraseña
        JPanel panelConfirmPassword = new JPanel(new BorderLayout(5, 5));
        panelConfirmPassword.setOpaque(false);
        panelConfirmPassword.add(new JLabel("Confirmar Contraseña:"), BorderLayout.NORTH);
        campoConfirmPassword = new JPasswordField();
        campoConfirmPassword.setPreferredSize(new Dimension(300, 30));
        panelConfirmPassword.add(campoConfirmPassword, BorderLayout.CENTER);

        // Botones
        botonRegistrar = new JButton("Registrar");
        botonRegistrar.setPreferredSize(new Dimension(150, 40));
        botonRegistrar.setBackground(new Color(46, 204, 113));
        botonRegistrar.setForeground(Color.WHITE);
        botonRegistrar.setFocusPainted(false);

        botonVolver = new JButton("Volver al Login");
        botonVolver.setBorderPainted(false);
        botonVolver.setContentAreaFilled(false);
        botonVolver.setForeground(new Color(41, 128, 185));
        botonVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Agregar componentes al formulario
        panelFormulario.add(panelIdentityDocument, gbc);
        panelFormulario.add(panelUsername, gbc);
        panelFormulario.add(panelPassword, gbc);
        panelFormulario.add(panelConfirmPassword, gbc);
        panelFormulario.add(botonRegistrar, gbc);
        panelFormulario.add(botonVolver, gbc);

        // Agregar paneles al panel derecho
        panelDerecho.add(panelTitulo, BorderLayout.NORTH);
        panelDerecho.add(panelFormulario, BorderLayout.CENTER);

        add(panelDerecho);
    }

    private void configurarEventos() {
        botonRegistrar.addActionListener(e -> {
            String identityDocument = campoIdentityDocument.getText().trim();
            String username = campoUsername.getText().trim();
            String password = new String(campoPassword.getPassword()).trim();
            String confirmPassword = new String(campoConfirmPassword.getPassword()).trim();

            // Validaciones básicas
            if (identityDocument.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Por favor complete todos los campos", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verificar que las contraseñas coincidan
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, 
                    "Las contraseñas no coinciden", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verificar que exista un miembro del staff con ese documento de identidad
            Staff existingStaff = authController.findStaffByIdentityDocument(identityDocument);
            if (existingStaff == null) {
                JOptionPane.showMessageDialog(this, 
                    "No existe un miembro del personal con ese documento de identidad", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verificar que este staff no tenga ya un usuario asociado
            if (authController.isStaffAssociatedWithUser(existingStaff.getStaffId())) {
                JOptionPane.showMessageDialog(this, 
                    "Este miembro del personal ya tiene un usuario registrado", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verificar que el nombre de usuario no esté ya en uso
            if (authController.isUsernameInUse(username)) {
                JOptionPane.showMessageDialog(this, 
                    "El nombre de usuario ya está en uso", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Crear un nuevo usuario con los datos del staff
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setFullName(existingStaff.getFullName());
            newUser.setRole(existingStaff.getPosition());
            newUser.setStatus("Active");
            newUser.setCreatedAt(Timestamp.from(Instant.now()));
            newUser.setLastLogin(null);
            
            // Intentar registrar el usuario
            boolean registroExitoso = authController.registerUser(newUser, existingStaff.getStaffId());
            
            if (registroExitoso) {
                JOptionPane.showMessageDialog(this, 
                    "Usuario registrado exitosamente", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Volver a la pantalla de login
                dispose();
                new LoginFrame(authController).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error al registrar el usuario", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        // Acción para volver al login
        botonVolver.addActionListener(e -> {
            dispose();
            new LoginFrame(authController).setVisible(true);
        });
    }
}