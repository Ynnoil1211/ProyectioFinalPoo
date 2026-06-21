package modelo;

// Integrantes: Lionny Lin - 0222510050 & Samuel Campo - 0222510057
// Universidad de Cartagena - POO 2026-1
import java.util.ArrayList;

/** Rutas troncales (T101, T102, T103). Horario extendido ~21:30. */
public class RutaTroncal extends Ruta {

    public RutaTroncal(String nombre, int inicio, int fin, ArrayList<String> paradas, ArrayList<Integer> pesosTramos) {
        super(nombre, inicio, fin, paradas, pesosTramos);
    }

    @Override
    public String getTipo() { return "Troncal"; }

    @Override
    public boolean calcularDisponibilidad(int hora) {
        return hora >= getHoraInicio() && hora <= getHoraFin();
    }

    @Override
    public String obtenerInstrucciones(String origen, String destino) {
        int mins = 0;
        for (int peso : getPesosTramos()) mins += peso; // suma de tiempos reales por tramo
        return "RUTA: " + getNombreRuta() + " [TRONCAL]\n"
                + "Recorrido : " + String.join(" -> ", getListadoParadas()) + "\n"
                + "Aborde en : Estacion " + origen + "\n"
                + "Bajese en : " + destino + "\n"
                + "Tiempo est: ~" + mins + " min  |  Tarifa: $3.900\n"
                + "Cierre    : " + getHoraFin() + ":30 h";
    }
}