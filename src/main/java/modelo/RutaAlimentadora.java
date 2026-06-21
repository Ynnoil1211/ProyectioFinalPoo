package modelo;

// Integrantes: Lionny Lin - 0222510050 & Samuel Campo - 0222510057
// Universidad de Cartagena - POO 2026-1
import java.util.ArrayList;

/**
 * Rutas alimentadoras (A103, A205, A310).
 * Barrios residenciales, cierre ~20:00.
 * Atributo propio: barrioAsociado.
 */
public class RutaAlimentadora extends Ruta {

    private String barrioAsociado;

    public RutaAlimentadora(String nombre, int inicio, int fin, ArrayList<String> paradas, String barrio, ArrayList<Integer> pesosTramos) {
        super(nombre, inicio, fin, paradas, pesosTramos);
        this.barrioAsociado = barrio;
    }

    @Override
    public String getTipo() { return "Alimentadora"; }

    @Override
    public boolean calcularDisponibilidad(int hora) {
        return hora >= getHoraInicio() && hora <= getHoraFin();
    }

    @Override
    public String obtenerInstrucciones(String origen, String destino) {
        int mins = 0;
        for (int peso : getPesosTramos()) mins += peso; // suma de tiempos reales por tramo
        return "RUTA: " + getNombreRuta() + " [ALIMENTADORA -> " + barrioAsociado + "]\n"
                + "Recorrido : " + String.join(" -> ", getListadoParadas()) + "\n"
                + "Aborde en : zona exterior de estacion " + origen + "\n"
                + "Bajese en : " + destino + "\n"
                + "Tiempo est: ~" + mins + " min  |  Tarifa: $3.900\n"
                + "AVISO     : cierra a las " + getHoraFin() + ":00 h";
    }

    public String getBarrioAsociado() { return barrioAsociado; }
    public void setBarrioAsociado(String barrio) { this.barrioAsociado = barrio; }

    @Override
    public String toCSV() {
        // super.toCSV() ya devuelve "lineaRuta\nlineaPesos";
        // insertamos el barrio al final de la PRIMERA linea
        String[] lineas = super.toCSV().split("\n", 2);
        return lineas[0] + ";" + barrioAsociado + "\n" + lineas[1];
    }
}