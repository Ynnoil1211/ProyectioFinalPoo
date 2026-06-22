package grafo; // O el paquete donde lo vayas a guardar

import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.util.ArrayList;

public class PanelMapaRutas extends JPanel {

    // Clase interna para guardar la información de cada segmento de la ruta
    static class Tramo {
        JButton origen, destino;
        Color color;

        Tramo(JButton origen, JButton destino, Color color) {
            this.origen = origen;
            this.destino = destino;
            this.color = color;
        }
    }

    // Lista que almacenará todas las líneas a dibujar
    private final ArrayList<Tramo> tramos = new ArrayList<>();

    // Método para inyectarle líneas al panel
    public void agregarTramo(JButton origen, JButton destino, Color color) {
        tramos.add(new Tramo(origen, destino, color));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setStroke(new BasicStroke(4)); // Grosor de las líneas

        // Dibujamos todos los tramos guardados
        for (Tramo t : tramos) {
            if (t.origen != null && t.destino != null) {
                g2d.setColor(t.color);

                // Conectamos desde el centro de cada botón
                int x1 = t.origen.getX() + (t.origen.getWidth() / 2);
                int y1 = t.origen.getY() + (t.origen.getHeight() / 2);
                int x2 = t.destino.getX() + (t.destino.getWidth() / 2);
                int y2 = t.destino.getY() + (t.destino.getHeight() / 2);

                g2d.drawLine(x1, y1, x2, y2);
            }
        }
    }
}