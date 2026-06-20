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

    public RutaAlimentadora(String nombre, int inicio, int fin, ArrayList<String> paradas, String barrio) {
        super(nombre, inicio, fin, paradas);
        this.barrioAsociado = barrio;
    }

    @Override public String getTipo() { return "Alimentadora"; }

    @Override
    public boolean calcularDisponibilidad(int hora) {
        return hora >= getHoraInicio() && hora <= getHoraFin();
    }

    @Override
    public String obtenerInstrucciones(String origen, String destino) {
        int mins = (getListadoParadas().size() - 1) * 7;
        return "RUTA: " + getNombreRuta() + " [ALIMENTADORA -> " + barrioAsociado + "]\n"
                + "Recorrido : " + String.join(" -> ", getListadoParadas()) + "\n"
                + "Aborde en : zona exterior de estacion " + origen + "\n"
                + "Bajese en : " + destino + "\n"
                + "Tiempo est: ~" + mins + " min  |  Tarifa: $2.700\n"
                + "AVISO     : cierra a las " + getHoraFin() + ":00 h";
    }

    public String getBarrioAsociado()              { return barrioAsociado; }
    public void   setBarrioAsociado(String barrio) { this.barrioAsociado = barrio; }

    @Override
    public String toCSV() {
        return super.toCSV() + ";" + barrioAsociado;
    }
}