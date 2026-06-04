package modelo;

// Integrantes: [Nombre 1] - [Nombre 2]
// Universidad de Cartagena - POO 2026-1

import java.util.ArrayList;

/**
 * Rutas alimentadoras del sistema TransCaribe (A103, A205, A310...).
 * Penetran en barrios residenciales con horario más corto (~20:00 h).
 *
 * Atributo propio: barrioAsociado — identifica el barrio que sirve.
 * Este método es el corazón del polimorfismo: cuando devuelve false,
 * GestorRutas activa automáticamente el plan de contingencia.
 */
public class RutaAlimentadora extends Ruta {

    private String barrioAsociado;

    public RutaAlimentadora(String nombreRuta, int horaInicio, int horaFin,
                            ArrayList<String> listadoParadas, String barrioAsociado) {
        super(nombreRuta, horaInicio, horaFin, listadoParadas);
        this.barrioAsociado = barrioAsociado;
    }

    /**
     * Cierre más temprano que las troncales.
     * GestorRutas llama este método polimórficamente sin saber el tipo concreto.
     */
    @Override
    public boolean calcularDisponibilidad(int horaActual) {
        return horaActual >= getHoraInicio() && horaActual <= getHoraFin();
    }

    /**
     * Instrucciones específicas mencionando el barrio destino.
     */
    @Override
    public String obtenerInstrucciones(String origen, String destino) {
        String recorrido = String.join(" -> ", getListadoParadas());
        int mins = (getListadoParadas().size() - 1) * 7;
        return "RUTA: " + getNombreRuta() + " [ALIMENTADORA]\n"
                + "Barrio    : " + barrioAsociado + "\n"
                + "Recorrido : " + recorrido + "\n"
                + "Aborde en : zona exterior de estacion " + origen + "\n"
                + "Bajese en : " + destino + "\n"
                + "Tiempo est: ~" + mins + " min\n"
                + "Tarifa    : $2.700\n"
                + "AVISO     : Esta ruta cierra a las " + getHoraFin() + ":00 h";
    }

    // ── Getter / Setter propio ──────────────────────────────────────────────

    public String getBarrioAsociado()                     { return barrioAsociado; }
    public void   setBarrioAsociado(String barrioAsociado){ this.barrioAsociado = barrioAsociado; }

    @Override
    public String toString() {
        return "[ALIMENTADORA -> " + barrioAsociado + "] " + super.toString();
    }
}