package vista;

// Integrantes: [Nombre 1] - [Nombre 2]
// Universidad de Cartagena - POO 2026-1

import controlador.GestorRutas;
import excepciones.*;
import modelo.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Interfaz grafica principal del Kiosco TransCaribe.
 *
 * JTabbedPane con tres pestanas:
 *   1. Consulta ciudadana  — el kiosco publico
 *   2. Mi tarjeta          — saldo y recargas
 *   3. Administracion      — CRUD de rutas (rutas.txt)
 */
public class KioscoUI extends JFrame {

    // ── Controlador ─────────────────────────────────────────────────────────
    private final GestorRutas gestor;

    // ── Pestana 1: Consulta ─────────────────────────────────────────────────
    private JComboBox<String>  cboOrigen;
    private JComboBox<String>  cboDestino;
    private JSpinner           spinnerHora;
    private JTextArea          txtResultado;

    // ── Pestana 2: Tarjeta ──────────────────────────────────────────────────
    private JLabel    lblSaldo;
    private JTextArea txtMovimientos;

    // ── Pestana 3: Administracion ───────────────────────────────────────────
    private JTable            tablaRutas;
    private DefaultTableModel modeloTabla;
    private JTextField        txtNombre, txtHoraInicio, txtHoraFin,
            txtParadas, txtBarrio;
    private JComboBox<String> cboTipoNueva;

    // ── Paleta de colores ───────────────────────────────────────────────────
    private static final Color COL_PRIMARIO = new Color(0, 83, 159);
    private static final Color COL_ACENTO   = new Color(255, 107, 43);
    private static final Color COL_FONDO    = new Color(245, 247, 250);
    private static final Color COL_TEXTO    = new Color(30, 40, 60);

    private static final Font F_TITULO  = new Font("Segoe UI", Font.BOLD,  15);
    private static final Font F_NORMAL  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_MONO    = new Font("Consolas",  Font.PLAIN, 12);

    // ── Constructor ─────────────────────────────────────────────────────────

    public KioscoUI() {
        gestor = new GestorRutas();
        gestor.cargarDesdeArchivo();
        inicializarUI();
    }

    // ══════════════════════════════════════════════════════════════════════
    //  INICIALIZACION
    // ══════════════════════════════════════════════════════════════════════

    public void inicializarUI() {
        setTitle("Kiosco Informativo — TransCaribe | Estacion Chambacú");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 580);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(F_NORMAL);
        tabs.addTab("  Consulta de ruta  ", crearPestanaConsulta());
        tabs.addTab("  Mi tarjeta  ",        crearPestanaTarjeta());
        tabs.addTab("  Administracion  ",    crearPestanaAdmin());

        setLayout(new BorderLayout());
        add(crearBanner(), BorderLayout.NORTH);
        add(tabs,          BorderLayout.CENTER);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  BANNER SUPERIOR
    // ══════════════════════════════════════════════════════════════════════

    private JPanel crearBanner() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COL_PRIMARIO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

        JLabel titulo   = new JLabel("KIOSCO INFORMATIVO - TRANSCARIBE");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(Color.WHITE);

        JLabel estacion = new JLabel("Estacion: Chambacú");
        estacion.setFont(F_NORMAL);
        estacion.setForeground(new Color(180, 210, 255));

        panel.add(titulo,   BorderLayout.WEST);
        panel.add(estacion, BorderLayout.EAST);
        return panel;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  PESTANA 1: CONSULTA CIUDADANA
    // ══════════════════════════════════════════════════════════════════════

    private JPanel crearPestanaConsulta() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(16, 18, 16, 18));
        panel.setBackground(COL_FONDO);

        // ── Formulario ──────────────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 230)),
                "Planifica tu viaje", TitledBorder.LEFT, TitledBorder.TOP, F_TITULO));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        String[] origenes  = {"Chambacú","Portal del Cabrero","Las Delicias",
                "Ejecutivos","Bodeguita"};
        String[] destinos  = {"-- Selecciona destino --","Manga","Crespo","Olaya",
                "Bocagrande","Las Delicias","Portal del Cabrero",
                "Ejecutivos","Bodeguita"};

        cboOrigen  = new JComboBox<>(origenes); cboOrigen.setFont(F_NORMAL);
        cboDestino = new JComboBox<>(destinos); cboDestino.setFont(F_NORMAL);

        agregarCampo(form, gbc, "Ubicacion actual:",      cboOrigen,  0);
        agregarCampo(form, gbc, "Adonde vas?",            cboDestino, 1);

        spinnerHora = new JSpinner(new SpinnerNumberModel(17, 0, 23, 1));
        spinnerHora.setFont(F_NORMAL);
        ((JSpinner.DefaultEditor) spinnerHora.getEditor()).getTextField().setColumns(3);
        agregarCampo(form, gbc, "Hora de consulta (0-23):", spinnerHora, 2);

        JButton btnConsultar = new JButton("Consultar ruta");
        btnConsultar.setFont(F_TITULO);
        btnConsultar.setBackground(COL_PRIMARIO);
        btnConsultar.setForeground(Color.WHITE);
        btnConsultar.setFocusPainted(false);
        btnConsultar.setBorderPainted(false);
        btnConsultar.addActionListener(e -> consultarRuta());

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 8, 8, 8);
        form.add(btnConsultar, gbc);

        // ── Resultado ───────────────────────────────────────────────────
        txtResultado = new JTextArea(7, 40);
        txtResultado.setFont(F_MONO);
        txtResultado.setEditable(false);
        txtResultado.setBackground(new Color(240, 245, 255));
        txtResultado.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        txtResultado.setText("Selecciona origen, destino y hora para calcular tu ruta.");

        JScrollPane scroll = new JScrollPane(txtResultado);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 230)),
                "Resultado", TitledBorder.LEFT, TitledBorder.TOP, F_TITULO));

        panel.add(form,   BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc,
                              String etiqueta, JComponent campo, int fila) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(F_NORMAL);
        lbl.setForeground(COL_TEXTO);
        gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 1; gbc.weightx = 0.3;
        panel.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(campo, gbc);
    }

    // ── Accion de consulta ───────────────────────────────────────────────────

    public void consultarRuta() {
        String origen  = (String) cboOrigen.getSelectedItem();
        String destino = (String) cboDestino.getSelectedItem();
        int    hora    = (int)    spinnerHora.getValue();

        if (destino == null || destino.startsWith("--")) {
            txtResultado.setText("Por favor selecciona un destino.");
            return;
        }

        try {
            Ruta ruta = gestor.buscarRutaOptima(origen, destino, hora);

            if (ruta != null) {
                txtResultado.setText(
                        "RUTA ENCONTRADA\n"
                                + "─────────────────────────────────────────────\n"
                                + ruta.obtenerInstrucciones(origen, destino));
                gestor.registrarConsulta(
                        new RegistroConsulta(origen, destino, ruta.getNombreRuta(), hora));

            } else {
                // Ruta existe pero ya cerro -> contingencia
                txtResultado.setText(
                        "RUTA DIRECTA NO DISPONIBLE\n"
                                + "─────────────────────────────────────────────\n"
                                + gestor.generarContingencia(destino) + "\n\n"
                                + "[calcularDisponibilidad(" + hora + ") retorno false]\n"
                                + "Polimorfismo en accion: la RutaAlimentadora evaluo "
                                + "su horario de cierre.");
                gestor.registrarConsulta(
                        new RegistroConsulta(origen, destino, "CONTINGENCIA", hora));
            }

        } catch (OrigenDestinoIdenticoException e) {
            txtResultado.setText("AVISO: " + e.getMessage());
        } catch (FueraDeServicioSistemaException e) {
            txtResultado.setText("FUERA DE SERVICIO: " + e.getMessage());
        } catch (RutaNoEncontradaException e) {
            txtResultado.setText("DESTINO NO CUBIERTO: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  PESTANA 2: TARJETA / SALDO
    // ══════════════════════════════════════════════════════════════════════

    private JPanel crearPestanaTarjeta() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(16, 18, 16, 18));
        panel.setBackground(COL_FONDO);

        TarjetaUsuario tarjeta = gestor.getTarjeta();

        // ── Card de saldo ───────────────────────────────────────────────
        JPanel cardSaldo = new JPanel(new GridLayout(3, 1, 4, 4));
        cardSaldo.setBackground(COL_PRIMARIO);
        cardSaldo.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel lblNombre = new JLabel(tarjeta.getNombreTitular());
        lblNombre.setFont(F_TITULO); lblNombre.setForeground(Color.WHITE);

        lblSaldo = new JLabel("Saldo: $" + String.format("%.0f", tarjeta.getSaldo()));
        lblSaldo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblSaldo.setForeground(new Color(100, 230, 180));

        String ultimos4 = tarjeta.getNumeroTarjeta()
                .substring(tarjeta.getNumeroTarjeta().length() - 4);
        JLabel lblNum = new JLabel("**** **** " + ultimos4);
        lblNum.setFont(F_MONO); lblNum.setForeground(new Color(180, 210, 255));

        cardSaldo.add(lblNombre);
        cardSaldo.add(lblSaldo);
        cardSaldo.add(lblNum);

        // ── Botones de recarga ──────────────────────────────────────────
        JPanel panelRecargas = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        panelRecargas.setBackground(COL_FONDO);

        for (int monto : new int[]{5000, 10000, 20000, 50000}) {
            JButton btn = new JButton("+ $" + String.format("%,d", monto)
                    .replace(",", "."));
            btn.setFont(F_NORMAL);
            btn.setBackground(COL_ACENTO);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            final int m = monto;
            btn.addActionListener(e -> {
                gestor.getTarjeta().recargar(m);
                lblSaldo.setText("Saldo: $"
                        + String.format("%.0f", gestor.getTarjeta().getSaldo()));
                txtMovimientos.setText(gestor.getTarjeta().getResumenMovimientos());
                JOptionPane.showMessageDialog(this,
                        "Recarga exitosa.\nNuevo saldo: $"
                                + String.format("%.0f", gestor.getTarjeta().getSaldo()));
            });
            panelRecargas.add(btn);
        }

        // ── Historial ───────────────────────────────────────────────────
        txtMovimientos = new JTextArea(6, 40);
        txtMovimientos.setFont(F_MONO);
        txtMovimientos.setEditable(false);
        txtMovimientos.setText(tarjeta.getResumenMovimientos());

        JScrollPane scrollMov = new JScrollPane(txtMovimientos);
        scrollMov.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 230)),
                "Ultimos movimientos", TitledBorder.LEFT, TitledBorder.TOP, F_TITULO));

        JPanel norte = new JPanel(new BorderLayout(0, 8));
        norte.setBackground(COL_FONDO);
        norte.add(cardSaldo,     BorderLayout.NORTH);
        norte.add(panelRecargas, BorderLayout.SOUTH);

        panel.add(norte,    BorderLayout.NORTH);
        panel.add(scrollMov, BorderLayout.CENTER);
        return panel;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  PESTANA 3: ADMINISTRACION (CRUD)
    // ══════════════════════════════════════════════════════════════════════

    private JPanel crearPestanaAdmin() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(16, 18, 16, 18));
        panel.setBackground(COL_FONDO);

        // ── Tabla de rutas ──────────────────────────────────────────────
        String[] cols = {"Tipo", "Nombre", "H. inicio", "H. fin", "Paradas"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaRutas = new JTable(modeloTabla);
        tablaRutas.setFont(F_NORMAL);
        tablaRutas.setRowHeight(24);
        tablaRutas.getTableHeader().setFont(F_TITULO);
        recargarTabla();

        JScrollPane scrollTabla = new JScrollPane(tablaRutas);
        scrollTabla.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 230)),
                "Rutas registradas (rutas.txt)",
                TitledBorder.LEFT, TitledBorder.TOP, F_TITULO));

        // ── Formulario nueva ruta ───────────────────────────────────────
        JPanel formNueva = new JPanel(new GridBagLayout());
        formNueva.setBackground(Color.WHITE);
        formNueva.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 230)),
                "Agregar nueva ruta",
                TitledBorder.LEFT, TitledBorder.TOP, F_TITULO));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        cboTipoNueva  = new JComboBox<>(new String[]{"Troncal","Alimentadora"});
        txtNombre     = new JTextField(8);
        txtHoraInicio = new JTextField(4);
        txtHoraFin    = new JTextField(4);
        txtParadas    = new JTextField(20);
        txtBarrio     = new JTextField(12);

        for (JComponent c : new JComponent[]{cboTipoNueva, txtNombre,
                txtHoraInicio, txtHoraFin, txtParadas, txtBarrio}) {
            c.setFont(F_NORMAL);
        }

        Object[][] campos = {
                {"Tipo:",       cboTipoNueva,  "Nombre:",  txtNombre},
                {"H. inicio:",  txtHoraInicio, "H. fin:",  txtHoraFin},
                {"Paradas:",    txtParadas,    "Barrio:",   txtBarrio}
        };

        for (int fila = 0; fila < campos.length; fila++) {
            for (int col = 0; col < 4; col++) {
                gbc.gridx = col; gbc.gridy = fila;
                gbc.weightx = (col % 2 == 0) ? 0.1 : 0.4;
                if (col % 2 == 0) {
                    JLabel lbl = new JLabel((String) campos[fila][col]);
                    lbl.setFont(F_NORMAL);
                    formNueva.add(lbl, gbc);
                } else {
                    formNueva.add((JComponent) campos[fila][col], gbc);
                }
            }
        }

        JButton btnAgregar  = new JButton("Guardar en rutas.txt");
        JButton btnEliminar = new JButton("Eliminar seleccionada");

        btnAgregar.setFont(F_NORMAL); btnAgregar.setBackground(COL_PRIMARIO);
        btnAgregar.setForeground(Color.WHITE); btnAgregar.setFocusPainted(false);
        btnAgregar.setBorderPainted(false);

        btnEliminar.setFont(F_NORMAL); btnEliminar.setBackground(new Color(200, 60, 60));
        btnEliminar.setForeground(Color.WHITE); btnEliminar.setFocusPainted(false);
        btnEliminar.setBorderPainted(false);

        btnAgregar.addActionListener(e  -> agregarRuta());
        btnEliminar.addActionListener(e -> eliminarRutaSeleccionada());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botones.setBackground(Color.WHITE);
        botones.add(btnAgregar);
        botones.add(btnEliminar);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 6, 6, 6);
        formNueva.add(botones, gbc);

        panel.add(scrollTabla, BorderLayout.CENTER);
        panel.add(formNueva,   BorderLayout.SOUTH);
        return panel;
    }

    // ── Acciones de administracion ───────────────────────────────────────────

    public void recargarTabla() {
        modeloTabla.setRowCount(0);
        for (Ruta r : gestor.getRutas()) {
            String tipo    = (r instanceof RutaTroncal) ? "Troncal" : "Alimentadora";
            String paradas = String.join(", ", r.getListadoParadas());
            modeloTabla.addRow(new Object[]{
                    tipo, r.getNombreRuta(),
                    r.getHoraInicio() + ":00",
                    r.getHoraFin()    + ":00",
                    paradas
            });
        }
    }

    public void agregarRuta() {
        try {
            String tipo       = (String) cboTipoNueva.getSelectedItem();
            String nombre     = txtNombre.getText().trim();
            int    horaInicio = Integer.parseInt(txtHoraInicio.getText().trim());
            int    horaFin    = Integer.parseInt(txtHoraFin.getText().trim());
            String barrio     = txtBarrio.getText().trim();

            ArrayList<String> paradas = new ArrayList<>();
            for (String p : txtParadas.getText().split(",")) paradas.add(p.trim());

            if (nombre.isEmpty() || paradas.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nombre y paradas son obligatorios.");
                return;
            }

            Ruta nueva = "Troncal".equals(tipo)
                    ? new RutaTroncal(nombre, horaInicio, horaFin, paradas)
                    : new RutaAlimentadora(nombre, horaInicio, horaFin, paradas,
                    barrio.isEmpty() ? "Sin barrio" : barrio);

            gestor.agregarRuta(nueva);
            recargarTabla();
            JOptionPane.showMessageDialog(this,
                    "Ruta " + nombre + " guardada en rutas.txt");

            txtNombre.setText(""); txtHoraInicio.setText("");
            txtHoraFin.setText(""); txtParadas.setText(""); txtBarrio.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Las horas deben ser numeros enteros (ej: 5, 20).");
        }
    }

    public void eliminarRutaSeleccionada() {
        int fila = tablaRutas.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona una ruta de la tabla para eliminar.");
            return;
        }
        String nombre = (String) modeloTabla.getValueAt(fila, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Eliminar la ruta " + nombre + " del sistema?",
                "Confirmar eliminacion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            gestor.eliminarRuta(nombre);
            recargarTabla();
            JOptionPane.showMessageDialog(this,
                    "Ruta eliminada y rutas.txt actualizado.");
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  MAIN
    // ══════════════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new KioscoUI().setVisible(true);
        });
    }
}