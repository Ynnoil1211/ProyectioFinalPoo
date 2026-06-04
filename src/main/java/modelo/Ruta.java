package modelo;

// Integrantes: [Nombre 1] - [Nombre 2]
// Universidad de Cartagena - POO 2026-1
// Proyecto: Kiosco Informativo TransCaribe

import java.util.ArrayList;

/**
 * Clase abstracta que representa una ruta del sistema TransCaribe.
 * Toda ruta es obligatoriamente RutaTroncal o RutaAlimentadora.
 *
 * SOLID - SRP : solo conoce datos y comportamiento de una ruta.
 * SOLID - OCP : se extiende con nuevos tipos sin modificar esta clase.
 * SOLID - LSP : las subclases sustituyen a Ruta sin romper el sistema.
 */
public abstract class Ruta {

    private String nombreRuta;
    private int    horaInicio;
    private int    horaFin;
    private ArrayList<String> listadoParadas;

    public Ruta(String nombreRuta, int horaInicio, int horaFin,
                ArrayList<String> listadoParadas) {
        this.nombreRuta     = nombreRuta;
        this.horaInicio     = horaInicio;
        this.horaFin        = horaFin;
        this.listadoParadas = listadoParadas;
    }

    // ── Métodos abstractos ──────────────────────────────────────────────────

    /**
     * Determina si la ruta opera a la hora indicada.
     * Cada subclase implementa su propia lógica de horario.
     *
     * @param horaActual hora en formato entero (0-23)
     * @return true si disponible, false si ya cerró
     */
    public abstract boolean calcularDisponibilidad(int horaActual);

    /**
     * Genera instrucciones de viaje entre dos puntos.
     *
     * @param origen  parada o estación de salida
     * @param destino parada o barrio destino
     * @return String con instrucciones formateadas para el kiosco
     */
    public abstract String obtenerInstrucciones(String origen, String destino);

    // ── Persistencia ────────────────────────────────────────────────────────

    /**
     * Serializa la ruta a formato CSV para rutas.txt
     * Formato: Tipo;Nombre;HoraInicio;HoraFin;Parada1,Parada2,...
     */
    public String toCSV() {
        String tipo    = (this instanceof RutaTroncal) ? "Troncal" : "Alimentadora";
        String paradas = String.join(",", listadoParadas);
        return tipo + ";" + nombreRuta + ";" + horaInicio + ";" + horaFin + ";" + paradas;
    }

    // ── Getters & Setters ───────────────────────────────────────────────────

    public String getNombreRuta()                       { return nombreRuta; }
    public void   setNombreRuta(String nombreRuta)      { this.nombreRuta = nombreRuta; }

    public int  getHoraInicio()                         { return horaInicio; }
    public void setHoraInicio(int horaInicio)           { this.horaInicio = horaInicio; }

    public int  getHoraFin()                            { return horaFin; }
    public void setHoraFin(int horaFin)                 { this.horaFin = horaFin; }

    public ArrayList<String> getListadoParadas()        { return listadoParadas; }
    public void setListadoParadas(ArrayList<String> l)  { this.listadoParadas = l; }

    @Override
    public String toString() {
        return nombreRuta + " (" + horaInicio + ":00-" + horaFin + ":00) | "
                + String.join(" -> ", listadoParadas);
    }
}