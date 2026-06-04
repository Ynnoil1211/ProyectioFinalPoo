package modelo;

// Integrantes: [Nombre 1] - [Nombre 2]
// Universidad de Cartagena - POO 2026-1

import java.util.ArrayList;

/**
 * Rutas troncales del sistema TransCaribe (T101, T102, T103).
 * Circulan por los corredores principales con horario extendido (~21:30 h).
 *
 * SOLID - LSP: puede usarse donde se espere una Ruta sin romper nada.
 */
public class RutaTroncal extends Ruta {

    public RutaTroncal(String nombreRuta, int horaInicio, int horaFin,
                       ArrayList<String> listadoParadas) {
        super(nombreRuta, horaInicio, horaFin, listadoParadas);
    }

    /**
     * Disponible si la hora está dentro de la ventana de operación troncal.
     */
    @Override
    public boolean calcularDisponibilidad(int horaActual) {
        return horaActual >= getHoraInicio() && horaActual <= getHoraFin();
    }

    /**
     * Instrucciones para el corredor troncal.
     */
    @Override
    public String obtenerInstrucciones(String origen, String destino) {
        String recorrido = String.join(" -> ", getListadoParadas());
        int mins = (getListadoParadas().size() - 1) * 6;
        return "RUTA: " + getNombreRuta() + " [TRONCAL]\n"
                + "Recorrido : " + recorrido + "\n"
                + "Aborde en : " + origen + "\n"
                + "Bajese en : " + destino + "\n"
                + "Tiempo est: ~" + mins + " min\n"
                + "Tarifa    : $2.700\n"
                + "Cierre    : " + getHoraFin() + ":30 h";
    }

    @Override
    public String toString() {
        return "[TRONCAL] " + super.toString();
    }
}