package vista;

// Integrantes: [Nombre 1] - [Nombre 2]
// Universidad de Cartagena - POO 2026-1

import excepciones.CredencialesInvalidasException;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Página 2 — Autenticación.
 *
 * Usuario normal : ingresa número de tarjeta + PIN
 * Administrador  : ingresa nombre de usuario + contraseña
 *
 * Tras login exitoso, KioscoApp redirige al panel correcto
 * según el rol (polimorfismo en acción).
 */
public class PanelLogin extends JPanel {

    private final KioscoApp app;

    private JTextField     txtIdentificador;
    private JPasswordField txtClave;
    private JLabel         lblError;

    public PanelLogin(KioscoApp app) {
        this.app = app;
        setBackground(KioscoApp.COL_FONDO);
        setLayout(new GridBagLayout());
        add(crearCard());
    }

    private JPanel crearCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 230), 1),
                new EmptyBorder(28, 36, 28, 36)));
        card.setPreferredSize(new Dimension(400, 320));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        // ── Título ─────────────────────────────────────────────────────────
        JLabel titulo = new JLabel("Iniciar sesion");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(KioscoApp.COL_PRIMARIO);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0;
        card.add(titulo, gbc);

        JLabel subtitulo = new JLabel(
                "<html><center>N° de tarjeta (usuario) o<br>nombre de usuario (admin)</center></html>");
        subtitulo.setFont(KioscoApp.F_NORMAL);
        subtitulo.setForeground(Color.GRAY);
        subtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        card.add(subtitulo, gbc);

        // ── Campos ─────────────────────────────────────────────────────────
        gbc.gridwidth = 1; gbc.weightx = 0.35;
        JLabel lbl1 = new JLabel("Usuario / Tarjeta:");
        lbl1.setFont(KioscoApp.F_NORMAL);
        gbc.gridx = 0; gbc.gridy = 2;
        card.add(lbl1, gbc);

        txtIdentificador = new JTextField(16);
        txtIdentificador.setFont(KioscoApp.F_NORMAL);
        gbc.gridx = 1; gbc.weightx = 0.65;
        card.add(txtIdentificador, gbc);

        JLabel lbl2 = new JLabel("Contrasena / PIN:");
        lbl2.setFont(KioscoApp.F_NORMAL);
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.35;
        card.add(lbl2, gbc);

        txtClave = new JPasswordField(16);
        txtClave.setFont(KioscoApp.F_NORMAL);
        gbc.gridx = 1; gbc.weightx = 0.65;
        card.add(txtClave, gbc);

        // ── Error ──────────────────────────────────────────────────────────
        lblError = new JLabel(" ");
        lblError.setFont(KioscoApp.F_NORMAL);
        lblError.setForeground(new Color(180, 30, 30));
        lblError.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        card.add(lblError, gbc);

        // ── Botón ──────────────────────────────────────────────────────────
        JButton btnLogin = new JButton("Entrar");
        btnLogin.setFont(KioscoApp.F_TITULO);
        btnLogin.setBackground(KioscoApp.COL_PRIMARIO);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setPreferredSize(new Dimension(200, 38));
        btnLogin.addActionListener(e -> intentarLogin());

        // Enter también hace login
        txtClave.addActionListener(e -> intentarLogin());

        gbc.gridy = 5; gbc.insets = new Insets(14, 4, 4, 4);
        card.add(btnLogin, gbc);

        return card;
    }

    private void intentarLogin() {
        String id    = txtIdentificador.getText().trim();
        String clave = new String(txtClave.getPassword()).trim();

        if (id.isEmpty() || clave.isEmpty()) {
            lblError.setText("Complete todos los campos.");
            return;
        }

        try {
            app.getGestorUsuarios().iniciarSesion(id, clave);
            lblError.setText(" ");
            txtIdentificador.setText("");
            txtClave.setText("");
            app.entrarSesion();   // KioscoApp decide a qué panel ir según el rol
        } catch (CredencialesInvalidasException e) {
            lblError.setText(e.getMessage());
            txtClave.setText("");
        }
    }
}