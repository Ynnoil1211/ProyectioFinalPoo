package vista;

// Integrantes: Lionny Lin - 0222510050 & Samuel Campo - 0222510057
// Universidad de Cartagena - POO 2026-1

import modelo.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Panel de administracion. Solo accesible tras login exitoso en
 * PanelLoginAdmin (GestorAdmins.iniciarSesion).
 *
 * Pestana 1: CRUD de Rutas    -> GestorRutas (agregarRuta, eliminarRuta)
 * Pestana 2: CRUD de Tarjetas -> GestorTarjetas (crearTarjeta, eliminarTarjeta)
 */
public class PanelAdmin extends JPanel {

    private final StandApp app;

    // ── Tab Rutas ────────────────────────────────────────────────────────────
    private JTable            tablaRutas;
    private DefaultTableModel modeloRutas;
    private JTextField        txtNombre, txtHi, txtHf, txtParadas, txtBarrio;
    private JComboBox<String> cboTipo;

    // ── Tab Tarjetas ─────────────────────────────────────────────────────────
    private JTable            tablaTarjetas;
    private DefaultTableModel modeloTarjetas;
    private JTextField        txtNumTarjeta, txtTitular, txtSaldoInicial;

    public PanelAdmin(StandApp app) {
        this.app = app;
        setBackground(StandApp.COL_FONDO);
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 14, 10, 14));

        Administrador admin = app.getGestorAdmins().haySesion();
        JLabel lblAdm = new JLabel("Panel de administracion — "
                + (admin != null ? admin.getNombre() : ""));
        lblAdm.setFont(StandApp.F_TITULO);
        lblAdm.setForeground(StandApp.COL_PRIMARIO);
        lblAdm.setBorder(new EmptyBorder(4, 0, 8, 0));
        add(lblAdm, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(StandApp.F_NORMAL);
        tabs.addTab("  Gestion de Rutas  ",    crearTabRutas());
        tabs.addTab("  Gestion de Tarjetas  ", crearTabTarjetas());
        add(tabs, BorderLayout.CENTER);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  TAB 1: RUTAS
    // ══════════════════════════════════════════════════════════════════════

    private JPanel crearTabRutas() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBackground(StandApp.COL_FONDO);
        p.setBorder(new EmptyBorder(10, 8, 10, 8));

        modeloRutas = new DefaultTableModel(
                new String[]{"Tipo","Nombre","H.Ini","H.Fin","Paradas"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaRutas = new JTable(modeloRutas);
        tablaRutas.setFont(StandApp.F_NORMAL);
        tablaRutas.setRowHeight(24);
        tablaRutas.getTableHeader().setFont(StandApp.F_TITULO);
        recargarTablaRutas();

        JScrollPane scroll = new JScrollPane(tablaRutas);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 230)),
                "Rutas registradas (rutas.txt)",
                TitledBorder.LEFT, TitledBorder.TOP, StandApp.F_TITULO));
        p.add(scroll, BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 230)),
                "Agregar ruta", TitledBorder.LEFT, TitledBorder.TOP, StandApp.F_TITULO));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        cboTipo    = new JComboBox<>(new String[]{"Troncal","Alimentadora"});
        txtNombre  = new JTextField(7);
        txtHi      = new JTextField(4);
        txtHf      = new JTextField(4);
        txtParadas = new JTextField(22);
        txtBarrio  = new JTextField(12);

        for (JComponent c : new JComponent[]{cboTipo, txtNombre,
                txtHi, txtHf, txtParadas, txtBarrio}) c.setFont(StandApp.F_NORMAL);

        Object[][] cols = {
                {"Tipo:", cboTipo, "Nombre:", txtNombre},
                {"H.inicio:", txtHi, "H.fin:", txtHf},
                {"Paradas (coma):", txtParadas, "Barrio (si Alimentadora):", txtBarrio}
        };
        for (int f = 0; f < cols.length; f++) {
            for (int c = 0; c < 4; c++) {
                gbc.gridx = c; gbc.gridy = f;
                gbc.weightx = (c % 2 == 0) ? 0.15 : 0.35;
                if (c % 2 == 0) {
                    JLabel l = new JLabel((String) cols[f][c]);
                    l.setFont(StandApp.F_NORMAL);
                    form.add(l, gbc);
                } else {
                    form.add((JComponent) cols[f][c], gbc);
                }
            }
        }

        JButton btnGuardar  = boton("Agregar ruta",  StandApp.COL_PRIMARIO,   e -> guardarRuta());
        JButton btnEliminar = boton("Eliminar ruta", new Color(190, 50, 50),  e -> eliminarRuta());

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btns.setBackground(Color.WHITE);
        btns.add(btnGuardar); btns.add(btnEliminar);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 6, 6, 6);
        form.add(btns, gbc);
        p.add(form, BorderLayout.SOUTH);
        return p;
    }

    private void recargarTablaRutas() {
        modeloRutas.setRowCount(0);
        for (Ruta r : app.getGestorRutas().getRutas()) {
            modeloRutas.addRow(new Object[]{
                    r.getTipo(), r.getNombreRuta(),
                    r.getHoraInicio() + ":00",
                    r.getHoraFin()    + ":00",
                    String.join(", ", r.getListadoParadas())
            });
        }
    }

    /** Crea RutaTroncal o RutaAlimentadora y la pasa a GestorRutas.agregarRuta(). */
    private void guardarRuta() {
        try {
            String tipo   = (String) cboTipo.getSelectedItem();
            String nombre = txtNombre.getText().trim();
            int    hi     = Integer.parseInt(txtHi.getText().trim());
            int    hf     = Integer.parseInt(txtHf.getText().trim());
            String barrio = txtBarrio.getText().trim();

            if (nombre.isEmpty() || txtParadas.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nombre y paradas son obligatorios.");
                return;
            }
            ArrayList<String> paradas = new ArrayList<>();
            for (String s : txtParadas.getText().split(",")) paradas.add(s.trim());

            Ruta nueva = "Troncal".equals(tipo)
                    ? new RutaTroncal(nombre, hi, hf, paradas)
                    : new RutaAlimentadora(nombre, hi, hf, paradas,
                    barrio.isEmpty() ? "Sin barrio" : barrio);

            app.getGestorRutas().agregarRuta(nueva);   // reconstruye grafo y guarda
            recargarTablaRutas();
            JOptionPane.showMessageDialog(this, "Ruta guardada en rutas.txt");

            txtNombre.setText(""); txtHi.setText(""); txtHf.setText("");
            txtParadas.setText(""); txtBarrio.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Las horas deben ser enteros (ej: 5, 21).");
        }
    }

    private void eliminarRuta() {
        int fila = tablaRutas.getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(this, "Selecciona una ruta."); return; }
        String nombre = (String) modeloRutas.getValueAt(fila, 1);
        int ok = JOptionPane.showConfirmDialog(this,
                "Eliminar la ruta " + nombre + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            app.getGestorRutas().eliminarRuta(nombre);
            recargarTablaRutas();
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  TAB 2: TARJETAS
    // ══════════════════════════════════════════════════════════════════════

    private JPanel crearTabTarjetas() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBackground(StandApp.COL_FONDO);
        p.setBorder(new EmptyBorder(10, 8, 10, 8));

        modeloTarjetas = new DefaultTableModel(
                new String[]{"Numero","Titular","Saldo"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaTarjetas = new JTable(modeloTarjetas);
        tablaTarjetas.setFont(StandApp.F_NORMAL);
        tablaTarjetas.setRowHeight(24);
        tablaTarjetas.getTableHeader().setFont(StandApp.F_TITULO);
        recargarTablaTarjetas();

        JScrollPane scroll = new JScrollPane(tablaTarjetas);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 230)),
                "Tarjetas registradas (tarjetas.txt)",
                TitledBorder.LEFT, TitledBorder.TOP, StandApp.F_TITULO));
        p.add(scroll, BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 230)),
                "Crear / Eliminar tarjeta",
                TitledBorder.LEFT, TitledBorder.TOP, StandApp.F_TITULO));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        txtNumTarjeta   = new JTextField(10);
        txtTitular      = new JTextField(14);
        txtSaldoInicial = new JTextField(8);
        for (JComponent c : new JComponent[]{txtNumTarjeta, txtTitular, txtSaldoInicial})
            c.setFont(StandApp.F_NORMAL);

        String[] labels = {"Numero:","Titular:","Saldo ini:"};
        JComponent[] fields = {txtNumTarjeta, txtTitular, txtSaldoInicial};
        for (int i = 0; i < labels.length; i++) {
            JLabel l = new JLabel(labels[i]);
            l.setFont(StandApp.F_NORMAL);
            gbc.gridx = i * 2; gbc.gridy = 0; gbc.weightx = 0.1;
            form.add(l, gbc);
            gbc.gridx = i * 2 + 1; gbc.weightx = 0.3;
            form.add(fields[i], gbc);
        }

        JButton btnCrear   = boton("Crear tarjeta",   StandApp.COL_PRIMARIO,  e -> crearTarjeta());
        JButton btnEliminar= boton("Eliminar tarjeta", new Color(190, 50, 50), e -> eliminarTarjeta());

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btns.setBackground(Color.WHITE);
        btns.add(btnCrear); btns.add(btnEliminar);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 6;
        gbc.insets = new Insets(10, 6, 6, 6);
        form.add(btns, gbc);
        p.add(form, BorderLayout.SOUTH);
        return p;
    }

    private void recargarTablaTarjetas() {
        modeloTarjetas.setRowCount(0);
        for (TarjetaUsuario t : app.getGestorTarjetas().getTarjetas()) {
            modeloTarjetas.addRow(new Object[]{
                    t.getNumeroTarjeta(), t.getTitular(),
                    "$" + String.format("%.0f", t.getSaldo())
            });
        }
    }

    private void crearTarjeta() {
        String num      = txtNumTarjeta.getText().trim();
        String titular   = txtTitular.getText().trim();
        String saldoStr  = txtSaldoInicial.getText().trim();
        if (num.isEmpty() || titular.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Numero y titular son obligatorios.");
            return;
        }
        try {
            double saldo = saldoStr.isEmpty() ? 0 : Double.parseDouble(saldoStr);
            app.getGestorTarjetas().crearTarjeta(new TarjetaUsuario(num, titular, saldo));
            recargarTablaTarjetas();
            JOptionPane.showMessageDialog(this, "Tarjeta creada y guardada.");
            txtNumTarjeta.setText(""); txtTitular.setText(""); txtSaldoInicial.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Saldo debe ser un numero.");
        }
    }

    private void eliminarTarjeta() {
        int fila = tablaTarjetas.getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(this, "Selecciona una tarjeta."); return; }
        String num = (String) modeloTarjetas.getValueAt(fila, 0);
        int ok = JOptionPane.showConfirmDialog(this,
                "Eliminar tarjeta " + num + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            app.getGestorTarjetas().eliminarTarjeta(num);
            recargarTablaTarjetas();
        }
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private JButton boton(String txt, Color bg, java.awt.event.ActionListener al) {
        JButton b = new JButton(txt);
        b.setFont(StandApp.F_NORMAL);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.addActionListener(al);
        return b;
    }
}