package vista;

// Integrantes: [Nombre 1] - [Nombre 2]
// Universidad de Cartagena - POO 2026-1

import excepciones.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

/**
 * Página 1 — Consulta pública de rutas.
 * Accesible sin login. Usa el grafo + Dijkstra internamente.
 */
public class PanelPublico extends JPanel {

    private final KioscoApp app;

    private JComboBox<String> cboOrigen;
    private JComboBox<String> cboDestino;
    private JSpinner          spinnerHora;
    private JTextArea         txtResultado;

    private static final String[] ESTACIONES = {
            "Chambacú", "Portal", "Las Delicias", "Ejecutivos",
            "Bodeguita", "Bocagrande", "Manga", "Crespo",
            "Olaya", "Marbella", "Pie de la Popa"
    };

    public PanelPublico(KioscoApp app) {
        this.app = app;
        setBackground(KioscoApp.COL_FONDO);
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(16, 18, 16, 18));
        add(crearFormulario(), BorderLayout.NORTH);
        add(crearAreaResultado(), BorderLayout.CENTER);
    }

    private JPanel crearFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 230)),
                "Planifica tu viaje",
                TitledBorder.LEFT, TitledBorder.TOP, KioscoApp.F_TITULO));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        cboOrigen  = new JComboBox<>(ESTACIONES);
        cboDestino = new JComboBox<>(ESTACIONES);
        cboDestino.setSelectedIndex(1);
        cboOrigen.setFont(KioscoApp.F_NORMAL);
        cboDestino.setFont(KioscoApp.F_NORMAL);

        spinnerHora = new JSpinner(new SpinnerNumberModel(17, 0, 23, 1));
        spinnerHora.setFont(KioscoApp.F_NORMAL);
        ((JSpinner.DefaultEditor) spinnerHora.getEditor())
                .getTextField().setColumns(3);

        campo(p, gbc, "Estacion actual:",         cboOrigen,  0);
        campo(p, gbc, "A donde vas?",             cboDestino, 1);
        campo(p, gbc, "Hora de consulta (0-23):", spinnerHora, 2);

        JButton btn = new JButton("Buscar ruta optima");
        btn.setFont(KioscoApp.F_TITULO);
        btn.setBackground(KioscoApp.COL_PRIMARIO);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.addActionListener(e -> buscarRuta());

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 8, 8, 8);
        p.add(btn, gbc);
        return p;
    }

    private JScrollPane crearAreaResultado() {
        txtResultado = new JTextArea(9, 50);
        txtResultado.setFont(KioscoApp.F_MONO);
        txtResultado.setEditable(false);
        txtResultado.setBackground(new Color(240, 245, 255));
        txtResultado.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        txtResultado.setText("Selecciona origen, destino y hora para calcular tu ruta optima (Dijkstra).");

        JScrollPane s = new JScrollPane(txtResultado);
        s.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 230)),
                "Resultado", TitledBorder.LEFT, TitledBorder.TOP, KioscoApp.F_TITULO));
        return s;
    }

    private void buscarRuta() {
        String origen  = (String) cboOrigen.getSelectedItem();
        String destino = (String) cboDestino.getSelectedItem();
        int    hora    = (int)    spinnerHora.getValue();

        try {
            List<String> camino = app.getGestorRutas()
                    .buscarRutaOptima(origen, destino, hora);
            String instrucciones = app.getGestorRutas()
                    .generarInstrucciones(camino, hora);
            txtResultado.setText("CAMINO MINIMO (Dijkstra)\n"
                    + "=".repeat(45) + "\n" + instrucciones);

            // Registrar consulta en historial
            modelo.RegistroConsulta log = new modelo.RegistroConsulta(
                    origen, destino, camino.get(camino.size() - 1), hora);
            log.registrar();

        } catch (OrigenDestinoIdenticoException e) {
            txtResultado.setText("AVISO: " + e.getMessage());
        } catch (FueraDeServicioException e) {
            txtResultado.setText("FUERA DE SERVICIO: " + e.getMessage());
        } catch (RutaNoEncontradaException e) {
            txtResultado.setText("SIN CONEXION: " + e.getMessage()
                    + "\n\n" + app.getGestorRutas().generarContingencia(destino));
        }
    }

    private void campo(JPanel p, GridBagConstraints gbc,
                       String lbl, JComponent comp, int fila) {
        JLabel l = new JLabel(lbl);
        l.setFont(KioscoApp.F_NORMAL);
        l.setForeground(KioscoApp.COL_TEXTO);
        gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 1; gbc.weightx = 0.3;
        p.add(l, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        p.add(comp, gbc);
    }
}