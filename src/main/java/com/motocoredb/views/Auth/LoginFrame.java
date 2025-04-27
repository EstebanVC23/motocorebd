package com.motocoredb.views.Auth;

import com.motocoredb.controllers.AuthController;
import com.motocoredb.models.User;
import com.motocoredb.utils.SessionManager;
import com.motocoredb.views.Main.Admin.AdminMainFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Clase que representa la interfaz gráfica de inicio de sesión para MotoCoreDB
 */
public class LoginFrame extends JFrame {

    // Definición de colores y fuentes
    private final Color COLOR_FONDO = new Color(236, 240, 241);
    private final Color COLOR_TEXTO = new Color(44, 62, 80);
    private final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 24);

    // Componentes de la interfaz
    private JTextField campoUsuario;
    private JPasswordField campoPassword;
    private JButton botonLogin;
    private JButton botonRegistro;
    private final AuthController authController;

    /**
     * Constructor de la clase LoginFrame
     * 
     * @param authController Controlador de autenticación
     */
    public LoginFrame(AuthController authController) {
        this.authController = authController;
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setTitle("MotoCoreDB - Login");
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
        
        JLabel titulo = new JLabel("MotoCoreDB", SwingConstants.CENTER);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        
        JLabel subtitulo = new JLabel("Sistema de Gestión de Motocicletas", SwingConstants.CENTER);
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
        JLabel labelTitulo = new JLabel("Iniciar Sesión");
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

        // Campo de usuario
        JPanel panelUsuario = new JPanel(new BorderLayout(5, 5));
        panelUsuario.setOpaque(false);
        panelUsuario.add(new JLabel("Usuario:"), BorderLayout.NORTH);
        campoUsuario = new JTextField();
        campoUsuario.setPreferredSize(new Dimension(300, 30));
        panelUsuario.add(campoUsuario, BorderLayout.CENTER);

        // Campo de contraseña
        JPanel panelPassword = new JPanel(new BorderLayout(5, 5));
        panelPassword.setOpaque(false);
        panelPassword.add(new JLabel("Contraseña:"), BorderLayout.NORTH);
        campoPassword = new JPasswordField();
        campoPassword.setPreferredSize(new Dimension(300, 30));
        panelPassword.add(campoPassword, BorderLayout.CENTER);

        // Botones
        botonLogin = new JButton("Ingresar");
        botonLogin.setPreferredSize(new Dimension(150, 40));
        botonLogin.setBackground(new Color(46, 204, 113));
        botonLogin.setForeground(Color.WHITE);
        botonLogin.setFocusPainted(false);

        botonRegistro = new JButton("Registrar nuevo usuario");
        botonRegistro.setBorderPainted(false);
        botonRegistro.setContentAreaFilled(false);
        botonRegistro.setForeground(new Color(41, 128, 185));
        botonRegistro.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Agregar componentes al formulario
        panelFormulario.add(panelUsuario, gbc);
        panelFormulario.add(panelPassword, gbc);
        panelFormulario.add(botonLogin, gbc);
        panelFormulario.add(botonRegistro, gbc);

        // Agregar paneles al panel derecho
        panelDerecho.add(panelTitulo, BorderLayout.NORTH);
        panelDerecho.add(panelFormulario, BorderLayout.CENTER);

        add(panelDerecho);
    }

    private void configurarEventos() {
        botonLogin.addActionListener(e -> {
        String username = campoUsuario.getText().trim();
        String password = new String(campoPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor complete todos los campos", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        User authenticatedUser = authController.login(username, password);
        if (authenticatedUser != null) {
            SessionManager.setSession(authenticatedUser); // Guarda el usuario logueado
            dispose();
            new AdminMainFrame(authenticatedUser, authController).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Usuario o contraseña incorrectos", 
                "Error de autenticación", 
                JOptionPane.ERROR_MESSAGE);
        }
    });

        // Evento para el botón de registro
        botonRegistro.addActionListener(e -> {
            // Aquí deberías abrir la ventana de registro
            // new RegisterFrame(authController).setVisible(true);
            dispose();
        });

        // Evento para presionar Enter en los campos
        campoPassword.addActionListener(e -> botonLogin.doClick());
    }
}