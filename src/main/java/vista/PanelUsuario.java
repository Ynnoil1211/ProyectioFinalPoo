package vista;

// Integrantes: [Nombre 1] - [Nombre 2]
// Universidad de Cartagena - POO 2026-1

import excepciones.TarjetaNoEncontradaException;
import modelo.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Página 3a — Panel de usuario normal.
 * Muestra saldo, historial de movimientos y permite recargar.
 * Solo accesible tras login con rol USUARIO.
 */
public class PanelUsuario extends JPanel {

    private final KioscoApp     app;
    private final UsuarioNormal usuario;
    private final TarjetaUsuario tarjeta;

    private JLabel    lblSaldo;
    private JTextArea txtMovimientos;

    public PanelUsuario(KioscoApp app) {
        this.app     = app;
        this.usuario = (UsuarioNormal) app.getGestorUsuarios().getSesion();
        this.tarjeta = usuario.getTarjeta();

        setBackground(KioscoApp.COL_FONDO);
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(16, 18, 16, 18));

        add(crearCardSaldo(),    BorderLayout.NORTH);
        add(crearPanelRecargas(), BorderLayout.CENTER);
        add(crearHistorial(),    BorderLayout.SOUTH);
    }

    private JPanel crearCardSaldo() {
        JPanel p = new JPanel(new GridLayout(4, 1, 4, 4));
        p.setBackground(KioscoApp.COL_PRIMARIO);
        p.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lblBienvenida = new JLabel("Bienvenido, " + usuario.getNombre());
        lblBienvenida.setFont(KioscoApp.F_TITULO);
        lblBienvenida.setForeground(Color.WHITE);

        JLabel lblNumero = new JLabel("Tarjeta: **** "
                + tarjeta.getNumeroTarjeta()
                .substring(tarjeta.getNumeroTarjeta().length() - 4));
        lblNumero.setFont(KioscoApp.F_MONO);
        lblNumero.setForeground(new Color(180, 210, 255));

        lblSaldo = new JLabel("Saldo: $" + String.format("%.0f", tarjeta.getSaldo()));
        lblSaldo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblSaldo.setForeground(new Color(100, 230, 180));

        JLabel lblRol = new JLabel(usuario.getDescripcionRol());
        lblRol.setFont(KioscoApp.F_NORMAL);
        lblRol.setForeground(new Color(180, 210, 255));

        p.add(lblBienvenida);
        p.add(lblNumero);
        p.add(lblSaldo);
        p.add(lblRol);
        return p;
    }

    private JPanel crearPanelRecargas() {
        JPanel p = new JPanel();
        p.setBackground(KioscoApp.COL_FONDO);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 230)),
                "Recargar tarjeta",
                TitledBorder.LEFT, TitledBorder.TOP, KioscoApp.F_TITULO));

        for (int monto : new int[]{5000, 10000, 20000, 50000}) {
            JButton btn = new JButton("+ $" + String.format("%,d", monto)
                    .replace(",", "."));
            btn.setFont(KioscoApp.F_NORMAL);
            btn.setBackground(KioscoApp.COL_ACENTO);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setPreferredSize(new Dimension(140, 36));
            final int m = monto;
            btn.addActionListener(e -> {
                try {
                    app.getGestorTarjetas()
                            .recargarTarjeta(tarjeta.getNumeroTarjeta(), m);
                    lblSaldo.setText("Saldo: $"
                            + String.format("%.0f", tarjeta.getSaldo()));
                    txtMovimientos.setText(tarjeta.getResumenMovimientos());
                    JOptionPane.showMessageDialog(app,
                            "Recarga exitosa!\nNuevo saldo: $"
                                    + String.format("%.0f", tarjeta.getSaldo()));
                } catch (TarjetaNoEncontradaException ex) {
                    JOptionPane.showMessageDialog(app, ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            p.add(btn);
        }

        // Recarga manual
        JTextField txtMonto = new JTextField(8);
        txtMonto.setFont(KioscoApp.F_NORMAL);
        txtMonto.setToolTipText("Ingrese monto personalizado");
        JButton btnManual = new JButton("Recargar monto");
        btnManual.setFont(KioscoApp.F_NORMAL);
        btnManual.setFocusPainted(false);
        btnManual.addActionListener(e -> {
            try {
                double m = Double.parseDouble(txtMonto.getText().trim());
                app.getGestorTarjetas()
                        .recargarTarjeta(tarjeta.getNumeroTarjeta(), m);
                lblSaldo.setText("Saldo: $"
                        + String.format("%.0f", tarjeta.getSaldo()));
                txtMovimientos.setText(tarjeta.getResumenMovimientos());
                txtMonto.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(app, "Ingrese un monto valido.");
            } catch (TarjetaNoEncontradaException ex) {
                JOptionPane.showMessageDialog(app, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        p.add(new JLabel("Otro monto: $"));
        p.add(txtMonto);
        p.add(btnManual);
        return p;
    }

    private JScrollPane crearHistorial() {
        txtMovimientos = new JTextArea(5, 50);
        txtMovimientos.setFont(KioscoApp.F_MONO);
        txtMovimientos.setEditable(false);
        txtMovimientos.setText(tarjeta.getResumenMovimientos());
        JScrollPane s = new JScrollPane(txtMovimientos);
        s.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 230)),
                "Ultimos movimientos",
                TitledBorder.LEFT, TitledBorder.TOP, KioscoApp.F_TITULO));
        return s;
    }
}