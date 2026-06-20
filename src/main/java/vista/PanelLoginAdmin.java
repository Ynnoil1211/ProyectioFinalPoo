package vista;

// Integrantes: Lionny Lin - 0222510050 & Samuel Campo - 0222510057
// Universidad de Cartagena - POO 2026-1

import excepciones.CredencialesInvalidasException;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Pantalla de autenticacion exclusiva para administradores.
 * Usa GestorAdmins.iniciarSesion(id, clave), que recorre la
 * lista de Administrador cargada desde usuarios.txt.
 */
public class PanelLoginAdmin extends JPanel {

    private final StandApp app;

    private JTextField     txtId;
    private JPasswordField txtClave;
    private JLabel         lblError;

    public PanelLoginAdmin(StandApp app) {
        this.app = app;
        setBackground(StandApp.COL_FONDO);
        setLayout(new GridBagLayout());
        add(crearCard());
    }

    private JPanel crearCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 230), 1),
                new EmptyBorder(28, 36, 28, 36)));
        card.setPreferredSize(new Dimension(380, 280));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        JLabel titulo = new JLabel("Acceso administrador");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(StandApp.COL_PRIMARIO);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0;
        card.add(titulo, gbc);

        JLabel sub = new JLabel("Solo personal de TransCaribe");
        sub.setFont(StandApp.F_NORMAL);
        sub.setForeground(Color.GRAY);
        sub.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        card.add(sub, gbc);

        gbc.gridwidth = 1; gbc.weightx = 0.35;
        JLabel lbl1 = new JLabel("ID admin:");
        lbl1.setFont(StandApp.F_NORMAL);
        gbc.gridx = 0; gbc.gridy = 2;
        card.add(lbl1, gbc);

        txtId = new JTextField(14);
        txtId.setFont(StandApp.F_NORMAL);
        gbc.gridx = 1; gbc.weightx = 0.65;
        card.add(txtId, gbc);

        JLabel lbl2 = new JLabel("Contrasena:");
        lbl2.setFont(StandApp.F_NORMAL);
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.35;
        card.add(lbl2, gbc);

        txtClave = new JPasswordField(14);
        txtClave.setFont(StandApp.F_NORMAL);
        gbc.gridx = 1; gbc.weightx = 0.65;
        card.add(txtClave, gbc);

        lblError = new JLabel(" ");
        lblError.setFont(StandApp.F_NORMAL);
        lblError.setForeground(new Color(180, 30, 30));
        lblError.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        card.add(lblError, gbc);

        JButton btnLogin = new JButton("Entrar");
        btnLogin.setFont(StandApp.F_TITULO);
        btnLogin.setBackground(StandApp.COL_PRIMARIO);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setPreferredSize(new Dimension(180, 36));
        btnLogin.addActionListener(e -> intentarLogin());
        txtClave.addActionListener(e -> intentarLogin());

        gbc.gridy = 5; gbc.insets = new Insets(14, 4, 4, 4);
        card.add(btnLogin, gbc);

        return card;
    }

    private void intentarLogin() {
        String id    = txtId.getText().trim();
        String clave = new String(txtClave.getPassword()).trim();

        if (id.isEmpty() || clave.isEmpty()) {
            lblError.setText("Completa ambos campos.");
            return;
        }

        try {
            app.getGestorAdmins().iniciarSesion(id, clave);
            lblError.setText(" ");
            txtId.setText("");
            txtClave.setText("");
            app.entrarComoAdmin();
        } catch (CredencialesInvalidasException e) {
            lblError.setText(e.getMessage());
            txtClave.setText("");
        }
    }
}