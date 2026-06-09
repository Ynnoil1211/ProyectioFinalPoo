package modelo;

// Integrantes: [Nombre 1] - [Nombre 2]
// Universidad de Cartagena - POO 2026-1

import java.util.ArrayList;

/** Rutas troncales (T101, T102, T103). Horario extendido ~21:30. */
public class RutaTroncal extends Ruta {

    public RutaTroncal(String nombre, int inicio, int fin,
                       ArrayList<String> paradas) {
        super(nombre, inicio, fin, paradas);
    }

    @Override public String getTipo() { return "Troncal"; }

    @Override
    public boolean calcularDisponibilidad(int hora) {
        return hora >= getHoraInicio() && hora <= getHoraFin();
    }

    @Override
    public String obtenerInstrucciones(String origen, String destino) {
        int mins = (getListadoParadas().size() - 1) * 6;
        return "RUTA: " + getNombreRuta() + " [TRONCAL]\n"
                + "Recorrido : " + String.join(" -> ", getListadoParadas()) + "\n"
                + "Aborde en : Estacion " + origen + "\n"
                + "Bajese en : " + destino + "\n"
                + "Tiempo est: ~" + mins + " min  |  Tarifa: $2.700\n"
                + "Cierre    : " + getHoraFin() + ":30 h";
    }
}