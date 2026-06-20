package vista;

// Integrantes: Lionny Lin - 0222510050 & Samuel Campo - 0222510057
// Universidad de Cartagena - POO 2026-1

import excepciones.*;
import modelo.TarjetaUsuario;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

/**
 * Panel publico — accesible sin autenticacion.
 *
 * Pestana 1: Consulta de ruta optima (GestorRutas + Dijkstra)
 * Pestana 2: Consulta de saldo y recarga de tarjeta, ingresando
 *            directamente el numero de tarjeta (sin login, segun
 *            lo definido: cualquiera con el numero puede operarla).
 */
public class PanelPublico extends JPanel {

    private final StandApp app;

    // ── Tab Ruta ─────────────────────────────────────────────────────────────
    private JComboBox<String> comboOrigen, comboDestino;
    private JSpinner    spinnerHora;
    private JTextArea   txtResultadoRuta;

    // ── Tab Tarjeta ──────────────────────────────────────────────────────────
    private JTextField txtNumTarjeta;
    private JLabel      lblTitular, lblSaldo;
    private JTextArea   txtMovimientos;
    private TarjetaUsuario tarjetaActual; // ultima tarjeta consultada

    public PanelPublico(StandApp app) {
        this.app = app;
        setBackground(StandApp.COL_FONDO);
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(StandApp.F_NORMAL);
        tabs.addTab("  Consultar ruta  ",   crearTabRuta());
        tabs.addTab("  Mi tarjeta  ",        crearTabTarjeta());
        add(tabs, BorderLayout.CENTER);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  TAB 1: CONSULTA DE RUTA
    // ══════════════════════════════════════════════════════════════════════

    private JPanel crearTabRuta() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(StandApp.COL_FONDO);
        panel.setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 230)),
                "Planifica tu viaje",
                TitledBorder.LEFT, TitledBorder.TOP, StandApp.F_TITULO));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // Estaciones disponibles, tomadas directamente del grafo ya construido
        List<String> estaciones = app.getGestorRutas().getGrafo()
                .obtenerIdsEstacionesOrdenados();
        String[] arrEstaciones = estaciones.toArray(new String[0]);

        comboOrigen  = new JComboBox<>(arrEstaciones);
        comboDestino = new JComboBox<>(arrEstaciones);
        comboOrigen.setFont(StandApp.F_NORMAL);
        comboDestino.setFont(StandApp.F_NORMAL);

        // Si hay al menos dos estaciones, deja el destino preseleccionado
        // distinto al origen para evitar el caso origen == destino por defecto
        if (arrEstaciones.length > 1) {
            comboDestino.setSelectedIndex(1);
        }

        spinnerHora = new JSpinner(new SpinnerNumberModel(17, 0, 23, 1));
        spinnerHora.setFont(StandApp.F_NORMAL);
        ((JSpinner.DefaultEditor) spinnerHora.getEditor())
                .getTextField().setColumns(3);

        campo(form, gbc, "Estacion de origen:",         comboOrigen, 0);
        campo(form, gbc, "Estacion de destino:",        comboDestino, 1);
        campo(form, gbc, "Hora de consulta (0-23):",    spinnerHora, 2);

        JButton btnBuscar = new JButton("Buscar ruta optima");
        btnBuscar.setFont(StandApp.F_TITULO);
        btnBuscar.setBackground(StandApp.COL_PRIMARIO);
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setBorderPainted(false);
        btnBuscar.addActionListener(e -> buscarRuta());

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 8, 8, 8);
        form.add(btnBuscar, gbc);

        txtResultadoRuta = new JTextArea(9, 50);
        txtResultadoRuta.setFont(StandApp.F_MONO);
        txtResultadoRuta.setEditable(false);
        txtResultadoRuta.setBackground(new Color(240, 245, 255));
        txtResultadoRuta.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        txtResultadoRuta.setText("Selecciona origen, destino y hora de consulta.\n"
                + "Las estaciones disponibles se cargan automaticamente\n"
                + "desde las rutas registradas en rutas.txt.");

        JScrollPane scroll = new JScrollPane(txtResultadoRuta);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 230)),
                "Resultado", TitledBorder.LEFT, TitledBorder.TOP, StandApp.F_TITULO));

        panel.add(form,   BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Llama a GestorRutas.buscarRutaOptima() (Dijkstra) y muestra el
     * resultado usando generarInstrucciones(). Captura las tres
     * excepciones que ese metodo puede lanzar.
     */
    private void buscarRuta() {
        String origen  = (String) comboOrigen.getSelectedItem();
        String destino = (String) comboDestino.getSelectedItem();
        int    hora    = (int) spinnerHora.getValue();

        if (origen == null || destino == null) {
            txtResultadoRuta.setText("No hay estaciones disponibles para seleccionar.");
            return;
        }

        try {
            List<String> camino = app.getGestorRutas()
                    .buscarRutaOptima(origen, destino, hora);
            String instrucciones = app.getGestorRutas()
                    .generarInstrucciones(camino, hora);
            txtResultadoRuta.setText(instrucciones);

            // Registrar la consulta en el historial (modo append)
            new modelo.RegistroConsulta(origen, destino,
                    camino.get(camino.size() - 1), hora).registrar();

        } catch (OrigenDestinoIdenticoException e) {
            txtResultadoRuta.setText("AVISO: " + e.getMessage());
        } catch (FueraDeServicioException e) {
            txtResultadoRuta.setText("FUERA DE SERVICIO: " + e.getMessage());
        } catch (RutaNoEncontradaException e) {
            txtResultadoRuta.setText("SIN CONEXION DIRECTA: " + e.getMessage()
                    + "\n\n" + app.getGestorRutas().generarContingencia(destino));
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  TAB 2: TARJETA (consulta + recarga por numero, sin login)
    // ══════════════════════════════════════════════════════════════════════

    private JPanel crearTabTarjeta() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(StandApp.COL_FONDO);
        panel.setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel buscador = new JPanel(new GridBagLayout());
        buscador.setBackground(Color.WHITE);
        buscador.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 230)),
                "Consultar tarjeta", TitledBorder.LEFT, TitledBorder.TOP, StandApp.F_TITULO));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        txtNumTarjeta = new JTextField(16);
        txtNumTarjeta.setFont(StandApp.F_NORMAL);
        campo(buscador, gbc, "Numero de tarjeta:", txtNumTarjeta, 0);

        JButton btnConsultar = new JButton("Consultar");
        btnConsultar.setFont(StandApp.F_NORMAL);
        btnConsultar.setBackground(StandApp.COL_PRIMARIO);
        btnConsultar.setForeground(Color.WHITE);
        btnConsultar.setFocusPainted(false);
        btnConsultar.setBorderPainted(false);
        btnConsultar.addActionListener(e -> consultarTarjeta());
        txtNumTarjeta.addActionListener(e -> consultarTarjeta());

        gbc.gridx = 1; gbc.gridy = 1;
        buscador.add(btnConsultar, gbc);

        JPanel cardInfo = new JPanel(new GridLayout(3, 1, 4, 4));
        cardInfo.setBackground(StandApp.COL_PRIMARIO);
        cardInfo.setBorder(new EmptyBorder(14, 18, 14, 18));

        lblTitular = new JLabel("Sin tarjeta consultada");
        lblTitular.setFont(StandApp.F_TITULO);
        lblTitular.setForeground(Color.WHITE);

        lblSaldo = new JLabel("Saldo: —");
        lblSaldo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblSaldo.setForeground(new Color(100, 230, 180));

        JLabel lblTarifa = new JLabel("Tarifa por viaje: $"
                + String.format("%.0f", TarjetaUsuario.TARIFA));
        lblTarifa.setFont(StandApp.F_NORMAL);
        lblTarifa.setForeground(new Color(180, 210, 255));

        cardInfo.add(lblTitular);
        cardInfo.add(lblSaldo);
        cardInfo.add(lblTarifa);

        JPanel panelRecargas = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        panelRecargas.setBackground(StandApp.COL_FONDO);
        for (int monto : new int[]{5000, 10000, 20000, 50000}) {
            JButton btn = new JButton("+ $" + String.format("%,d", monto).replace(",", "."));
            btn.setFont(StandApp.F_NORMAL);
            btn.setBackground(StandApp.COL_ACENTO);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            final int m = monto;
            btn.addActionListener(e -> recargar(m));
            panelRecargas.add(btn);
        }

        txtMovimientos = new JTextArea(6, 50);
        txtMovimientos.setFont(StandApp.F_MONO);
        txtMovimientos.setEditable(false);
        txtMovimientos.setText("Consulta una tarjeta para ver sus movimientos.");

        JScrollPane scrollMov = new JScrollPane(txtMovimientos);
        scrollMov.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 230)),
                "Ultimos movimientos", TitledBorder.LEFT, TitledBorder.TOP, StandApp.F_TITULO));

        JPanel norte = new JPanel(new BorderLayout(0, 10));
        norte.setBackground(StandApp.COL_FONDO);
        norte.add(buscador, BorderLayout.NORTH);
        norte.add(cardInfo, BorderLayout.CENTER);
        norte.add(panelRecargas, BorderLayout.SOUTH);

        panel.add(norte,    BorderLayout.NORTH);
        panel.add(scrollMov, BorderLayout.CENTER);
        return panel;
    }

    private void campo(JPanel p, GridBagConstraints gbc,
                       String lbl, JComponent comp, int fila) {
        JLabel l = new JLabel(lbl);
        l.setFont(StandApp.F_NORMAL);
        l.setForeground(StandApp.COL_TEXTO);
        gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 1; gbc.weightx = 0.3;
        p.add(l, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        p.add(comp, gbc);
    }

    /** Busca la tarjeta por numero usando GestorTarjetas.buscarPorNumero(). */
    private void consultarTarjeta() {
        String num = txtNumTarjeta.getText().trim();
        if (num.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa el numero de tarjeta.");
            return;
        }
        try {
            tarjetaActual = app.getGestorTarjetas().buscarPorNumero(num);
            actualizarVistaTarjeta();
        } catch (TarjetaNoEncontradaException e) {
            tarjetaActual = null;
            lblTitular.setText("No encontrada");
            lblSaldo.setText("Saldo: —");
            txtMovimientos.setText(e.getMessage());
        }
    }

    /** Recarga la tarjeta actualmente consultada via GestorTarjetas.recargarTarjeta(). */
    private void recargar(int monto) {
        if (tarjetaActual == null) {
            JOptionPane.showMessageDialog(this,
                    "Primero consulta una tarjeta por su numero.");
            return;
        }
        try {
            app.getGestorTarjetas()
                    .recargarTarjeta(tarjetaActual.getNumeroTarjeta(), monto);
            actualizarVistaTarjeta();
            JOptionPane.showMessageDialog(this,
                    "Recarga exitosa.\nNuevo saldo: $"
                            + String.format("%.0f", tarjetaActual.getSaldo()));
        } catch (TarjetaNoEncontradaException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarVistaTarjeta() {
        lblTitular.setText(tarjetaActual.getTitular()
                + "  (" + tarjetaActual.getNumeroTarjeta() + ")");
        lblSaldo.setText("Saldo: $" + String.format("%.0f", tarjetaActual.getSaldo()));
        txtMovimientos.setText(tarjetaActual.getResumenMovimientos());
    }
}