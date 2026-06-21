package vista;

// Integrantes: Lionny Lin - 0222510050 & Samuel Campo - 0222510057
// Universidad de Cartagena - POO 2026-1

import controlador.*;
import modelo.*;
import excepciones.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ════════════════════════════════════════════════════════════════════════
 *  VISTA COMPLETA — Stand Informativo TransCaribe (archivo unico)
 * ════════════════════════════════════════════════════════════════════════
 *
 * Contiene las 4 clases de la capa vista, fusionadas en un solo archivo:
 *
 *   1. StandApp        (public) — Punto de entrada, JFrame, CardLayout.
 *   2. PanelPublico               — Consulta de ruta + consulta/recarga de tarjeta.
 *   3. PanelLoginAdmin            — Autenticacion de administradores.
 *   4. PanelAdmin                 — CRUD de rutas y tarjetas (solo admin).
 *
 * Solo una clase de nivel superior puede ser "public" por archivo y debe
 * coincidir con el nombre del archivo (StandApp.java); por eso las otras
 * tres quedan sin modificador (visibilidad de paquete), lo cual es
 * perfectamente valido porque las cuatro viven en el paquete "vista".
 */
public class StandApp extends JFrame {

    // ── Controladores (ya cargan datos en su propio constructor) ───────────
    private final GestorRutas    gestorRutas;
    private final GestorAdmins   gestorAdmins;
    private final GestorTarjetas gestorTarjetas;

    // ── Navegacion ───────────────────────────────────────────────────────────
    private final CardLayout cardLayout;
    private final JPanel     cardContainer;

    private final PanelPublico    panelPublico;
    private final PanelLoginAdmin panelLoginAdmin;
    private PanelAdmin            panelAdmin;   // se crea tras login exitoso

    public static final String CARD_PUBLICO     = "PUBLICO";
    public static final String CARD_LOGIN_ADMIN = "LOGIN_ADMIN";
    public static final String CARD_ADMIN       = "ADMIN";

    // ── Paleta visual ────────────────────────────────────────────────────────
    public static final Color COL_PRIMARIO = new Color(0, 83, 159);
    public static final Color COL_ACENTO   = new Color(255, 107, 43);
    public static final Color COL_FONDO    = new Color(245, 247, 250);
    public static final Color COL_TEXTO    = new Color(30, 40, 60);
    public static final Font  F_TITULO     = new Font("Segoe UI", Font.BOLD,  15);
    public static final Font  F_NORMAL     = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font  F_MONO       = new Font("Consolas",  Font.PLAIN, 12);

    public StandApp() {
        // Cada gestor carga sus propios datos en el constructor (ver tus clases)
        gestorRutas    = new GestorRutas();
        gestorAdmins   = new GestorAdmins();
        gestorTarjetas = new GestorTarjetas();

        setTitle("Stand Informativo TransCaribe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 620);
        setMinimumSize(new Dimension(720, 560));
        setLocationRelativeTo(null);

        add(crearBanner(), BorderLayout.NORTH);

        cardLayout    = new CardLayout();
        cardContainer = new JPanel(cardLayout);

        panelPublico    = new PanelPublico(this);
        panelLoginAdmin = new PanelLoginAdmin(this);

        cardContainer.add(panelPublico,    CARD_PUBLICO);
        cardContainer.add(panelLoginAdmin, CARD_LOGIN_ADMIN);

        add(cardContainer, BorderLayout.CENTER);
        add(crearNavBar(), BorderLayout.SOUTH);

        mostrar(CARD_PUBLICO);
    }

    // ── Navegacion publica ───────────────────────────────────────────────────

    public void mostrar(String card) {
        cardLayout.show(cardContainer, card);
    }

    /** Llamado por PanelLoginAdmin tras autenticacion exitosa. */
    public void entrarComoAdmin() {
        panelAdmin = new PanelAdmin(this);
        cardContainer.add(panelAdmin, CARD_ADMIN);
        mostrar(CARD_ADMIN);
    }

    public void cerrarSesionAdmin() {
        gestorAdmins.cerrarSesion();
        mostrar(CARD_PUBLICO);
    }

    // ── Banner / Nav ─────────────────────────────────────────────────────────

    private JPanel crearBanner() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(COL_PRIMARIO);
        p.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

        JLabel titulo = new JLabel("STAND INFORMATIVO — TRANSCARIBE");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Consulta de rutas y tarjetas");
        sub.setFont(F_NORMAL);
        sub.setForeground(new Color(180, 210, 255));

        p.add(titulo, BorderLayout.WEST);
        p.add(sub,    BorderLayout.EAST);
        return p;
    }

    private JPanel crearNavBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 6));
        p.setBackground(new Color(230, 235, 245));

        JButton btnPublico = new JButton("Panel publico");
        JButton btnAdmin   = new JButton("Acceso administrador");
        JButton btnSalir   = new JButton("Cerrar sesion admin");

        for (JButton b : new JButton[]{btnPublico, btnAdmin, btnSalir}) {
            b.setFont(F_NORMAL);
            b.setFocusPainted(false);
        }
        btnPublico.addActionListener(e -> mostrar(CARD_PUBLICO));
        btnAdmin.addActionListener(e   -> mostrar(CARD_LOGIN_ADMIN));
        btnSalir.addActionListener(e   -> cerrarSesionAdmin());

        p.add(btnPublico);
        p.add(btnAdmin);
        p.add(btnSalir);
        return p;
    }

    // ── Getters para los paneles ─────────────────────────────────────────────

    public GestorRutas    getGestorRutas()    { return gestorRutas; }
    public GestorAdmins   getGestorAdmins()   { return gestorAdmins; }
    public GestorTarjetas getGestorTarjetas() { return gestorTarjetas; }

    // ── Main ─────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new StandApp().setVisible(true);
        });
    }
}


/**
 * Panel publico — accesible sin autenticacion.
 *
 * Pestana 1: Consulta de ruta optima (GestorRutas + Dijkstra)
 * Pestana 2: Consulta de saldo y recarga de tarjeta, ingresando
 *            directamente el numero de tarjeta (sin login, segun
 *            lo definido: cualquiera con el numero puede operarla).
 */
class PanelPublico extends JPanel {

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


/**
 * Pantalla de autenticacion exclusiva para administradores.
 * Usa GestorAdmins.iniciarSesion(id, clave), que recorre la
 * lista de Administrador cargada desde usuarios.txt.
 */
class PanelLoginAdmin extends JPanel {

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


/**
 * Panel de administracion. Solo accesible tras login exitoso en
 * PanelLoginAdmin (GestorAdmins.iniciarSesion).
 *
 * Pestana 1: CRUD de Rutas    -> GestorRutas (agregarRuta, eliminarRuta)
 * Pestana 2: CRUD de Tarjetas -> GestorTarjetas (crearTarjeta, eliminarTarjeta)
 */
class PanelAdmin extends JPanel {

    private final StandApp app;

    // ── Tab Rutas ────────────────────────────────────────────────────────────
    private JTable            tablaRutas;
    private DefaultTableModel modeloRutas;
    private JTextField        txtNombre, txtHi, txtHf, txtParadas, txtBarrio, txtPesos;
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
        txtPesos   = new JTextField(22);

        for (JComponent c : new JComponent[]{cboTipo, txtNombre,
                txtHi, txtHf, txtParadas, txtBarrio, txtPesos}) c.setFont(StandApp.F_NORMAL);

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

        // Fila de pesos por tramo: ahora Ruta exige un peso (minutos reales,
        // ver rutas.txt) por cada tramo entre paradas consecutivas. Sin este
        // campo, RutaTroncal/RutaAlimentadora no se pueden construir.
        JLabel lblPesos = new JLabel("Pesos por tramo, min (coma):");
        lblPesos.setFont(StandApp.F_NORMAL);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.weightx = 0.15;
        form.add(lblPesos, gbc);

        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 0.85;
        form.add(txtPesos, gbc);

        JButton btnGuardar  = boton("Agregar ruta",  StandApp.COL_PRIMARIO,   e -> guardarRuta());
        JButton btnEliminar = boton("Eliminar ruta", new Color(190, 50, 50),  e -> eliminarRuta());

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btns.setBackground(Color.WHITE);
        btns.add(btnGuardar); btns.add(btnEliminar);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
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

    /**
     * Crea RutaTroncal o RutaAlimentadora y la pasa a GestorRutas.agregarRuta().
     * Ahora tambien parsea los pesos por tramo (txtPesos) y valida que su
     * cantidad coincida con (paradas - 1), tal como lo exige el constructor
     * de Ruta.
     */
    private void guardarRuta() {
        try {
            String tipo   = (String) cboTipo.getSelectedItem();
            String nombre = txtNombre.getText().trim();
            int    hi     = Integer.parseInt(txtHi.getText().trim());
            int    hf     = Integer.parseInt(txtHf.getText().trim());
            String barrio = txtBarrio.getText().trim();

            if (nombre.isEmpty() || txtParadas.getText().trim().isEmpty()
                    || txtPesos.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Nombre, paradas y pesos son obligatorios.");
                return;
            }

            ArrayList<String> paradas = new ArrayList<>();
            for (String s : txtParadas.getText().split(",")) paradas.add(s.trim());

            ArrayList<Integer> pesos = new ArrayList<>();
            for (String s : txtPesos.getText().split(",")) pesos.add(Integer.parseInt(s.trim()));

            if (pesos.size() != paradas.size() - 1) {
                JOptionPane.showMessageDialog(this,
                        "Debes ingresar " + (paradas.size() - 1)
                                + " peso(s) -uno por cada tramo entre paradas consecutivas-, "
                                + "se recibieron " + pesos.size() + ".");
                return;
            }

            Ruta nueva = "Troncal".equals(tipo)
                    ? new RutaTroncal(nombre, hi, hf, paradas, pesos)
                    : new RutaAlimentadora(nombre, hi, hf, paradas,
                    barrio.isEmpty() ? "Sin barrio" : barrio, pesos);

            app.getGestorRutas().agregarRuta(nueva);   // reconstruye grafo y guarda
            recargarTablaRutas();
            JOptionPane.showMessageDialog(this, "Ruta guardada en rutas.txt");

            txtNombre.setText(""); txtHi.setText(""); txtHf.setText("");
            txtParadas.setText(""); txtBarrio.setText(""); txtPesos.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Las horas y los pesos deben ser numeros enteros "
                            + "(ej. horas: 5, 21 — pesos: 3,4,5,8).");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
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