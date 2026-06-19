package vista;

// Integrantes: Lionny Lin - 0222510050 & Samuel Campo - 0222510057
// Universidad de Cartagena - POO 2026-1
import controlador.*;

import javax.swing.*;
import java.awt.*;

/**
 * Punto de entrada y contenedor principal del Kiosco TransCaribe.
 *
 * Usa CardLayout para alternar entre las tres páginas:
 *   "PUBLICO"  — consulta pública de rutas (sin login)
 *   "LOGIN"    — pantalla de autenticación
 *   "USUARIO"  — panel de usuario normal (saldo, recarga)
 *   "ADMIN"    — panel de administrador (CRUD rutas y tarjetas)
 *
 * Los controladores se crean aquí y se inyectan en cada panel (DIP).
 */
public class KioscoApp extends JFrame {

    // ── Controladores ────────────────────────────────────────────────────────
    private final GestorRutas    gestorRutas;
    private final GestorUsuarios gestorUsuarios;
    private final GestorTarjetas gestorTarjetas;

    // ── Navegación ───────────────────────────────────────────────────────────
    private final CardLayout  cardLayout;
    private final JPanel      cardContainer;

    // ── Páginas ──────────────────────────────────────────────────────────────
    private final PanelPublico  panelPublico;
    private final PanelLogin    panelLogin;
    private PanelUsuario        panelUsuario;   // se recrea al login
    private PanelAdmin          panelAdmin;     // se recrea al login

    public static final String CARD_PUBLICO  = "PUBLICO";
    public static final String CARD_LOGIN    = "LOGIN";
    public static final String CARD_USUARIO  = "USUARIO";
    public static final String CARD_ADMIN    = "ADMIN";

    // ── Paleta ───────────────────────────────────────────────────────────────
    public static final Color COL_PRIMARIO  = new Color(0, 83, 159);
    public static final Color COL_ACENTO    = new Color(255, 107, 43);
    public static final Color COL_FONDO     = new Color(245, 247, 250);
    public static final Color COL_TEXTO     = new Color(30, 40, 60);
    public static final Font  F_TITULO      = new Font("Segoe UI", Font.BOLD,  15);
    public static final Font  F_NORMAL      = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font  F_MONO        = new Font("Consolas",  Font.PLAIN, 12);

    public KioscoApp() {
        // ── Inicializar controladores ────────────────────────────────────────
        gestorRutas    = new GestorRutas();
        gestorUsuarios = new GestorUsuarios();
        gestorTarjetas = new GestorTarjetas();

        gestorRutas.cargarDesdeArchivo();
        gestorUsuarios.cargarDesdeArchivo();
        gestorTarjetas.cargarDesdeArchivo();

        // ── Frame ────────────────────────────────────────────────────────────
        setTitle("Kiosco Informativo TransCaribe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 620);
        setMinimumSize(new Dimension(720, 560));
        setLocationRelativeTo(null);

        // ── Banner superior ──────────────────────────────────────────────────
        add(crearBanner(), BorderLayout.NORTH);

        // ── CardLayout ───────────────────────────────────────────────────────
        cardLayout    = new CardLayout();
        cardContainer = new JPanel(cardLayout);

        panelPublico  = new PanelPublico(this);
        panelLogin    = new PanelLogin(this);

        cardContainer.add(panelPublico, CARD_PUBLICO);
        cardContainer.add(panelLogin,   CARD_LOGIN);

        add(cardContainer, BorderLayout.CENTER);

        // ── Nav inferior ─────────────────────────────────────────────────────
        add(crearNavBar(), BorderLayout.SOUTH);

        mostrar(CARD_PUBLICO);
    }

    // ── Navegación pública ───────────────────────────────────────────────────

    public void mostrar(String card) {
        cardLayout.show(cardContainer, card);
    }

    /**
     * Llamado por PanelLogin tras autenticación exitosa.
     * Crea (o recrea) el panel correcto y navega a él.
     */
    public void entrarSesion() {
        if (gestorUsuarios.esAdmin()) {
            panelAdmin = new PanelAdmin(this);
            cardContainer.add(panelAdmin, CARD_ADMIN);
            mostrar(CARD_ADMIN);
        } else {
            panelUsuario = new PanelUsuario(this);
            cardContainer.add(panelUsuario, CARD_USUARIO);
            mostrar(CARD_USUARIO);
        }
    }

    public void cerrarSesion() {
        gestorUsuarios.cerrarSesion();
        mostrar(CARD_PUBLICO);
    }

    // ── Banner ───────────────────────────────────────────────────────────────

    private JPanel crearBanner() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(COL_PRIMARIO);
        p.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

        JLabel titulo = new JLabel("KIOSCO INFORMATIVO — TRANSCARIBE");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(Color.WHITE);

        JLabel estacion = new JLabel("Estacion: Chambacú");
        estacion.setFont(F_NORMAL);
        estacion.setForeground(new Color(180, 210, 255));

        p.add(titulo,   BorderLayout.WEST);
        p.add(estacion, BorderLayout.EAST);
        return p;
    }

    private JPanel crearNavBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 6));
        p.setBackground(new Color(230, 235, 245));

        JButton btnPublico = new JButton("Consulta publica");
        JButton btnLogin   = new JButton("Iniciar sesion");
        JButton btnSalir   = new JButton("Cerrar sesion");

        for (JButton b : new JButton[]{btnPublico, btnLogin, btnSalir}) {
            b.setFont(F_NORMAL);
            b.setFocusPainted(false);
        }
        btnPublico.addActionListener(e -> mostrar(CARD_PUBLICO));
        btnLogin.addActionListener(e   -> mostrar(CARD_LOGIN));
        btnSalir.addActionListener(e   -> cerrarSesion());

        p.add(btnPublico);
        p.add(btnLogin);
        p.add(btnSalir);
        return p;
    }

    // ── Getters para los paneles ─────────────────────────────────────────────

    public GestorRutas    getGestorRutas()    { return gestorRutas; }
    public GestorUsuarios getGestorUsuarios() { return gestorUsuarios; }
    public GestorTarjetas getGestorTarjetas() { return gestorTarjetas; }

    // ── Main ─────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new KioscoApp().setVisible(true);
        });
    }
}