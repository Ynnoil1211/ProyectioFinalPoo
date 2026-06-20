package vista;

// Integrantes: Lionny Lin - 0222510050 & Samuel Campo - 0222510057
// Universidad de Cartagena - POO 2026-1

import controlador.*;

import javax.swing.*;
import java.awt.*;

/**
 * Punto de entrada y contenedor principal del Stand Informativo TransCaribe.
 *
 * Usa CardLayout para alternar entre:
 *   "PUBLICO"     — consulta de ruta + consulta/recarga de tarjeta por numero (sin login)
 *   "LOGIN_ADMIN" — autenticacion exclusiva de administradores (GestorAdmins)
 *   "ADMIN"       — panel de administracion (CRUD rutas y tarjetas)
 *
 * Los tres controladores reales del proyecto (GestorRutas, GestorAdmins,
 * GestorTarjetas) ya cargan sus datos en el constructor, asi que aqui
 * solo se instancian una vez y se inyectan en cada panel.
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