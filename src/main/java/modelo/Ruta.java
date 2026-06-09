package modelo;

// Integrantes: [Nombre 1] - [Nombre 2]
// Universidad de Cartagena - POO 2026-1 | Kiosco TransCaribe v2

import java.util.ArrayList;

/**
 * Clase abstracta base de toda ruta del sistema TransCaribe.
 *
 * SOLID-SRP : solo conoce datos y comportamiento de una ruta.
 * SOLID-OCP : se extiende con nuevos tipos sin modificar esta clase.
 * SOLID-LSP : RutaTroncal y RutaAlimentadora sustituyen a Ruta sin romper nada.
 */
public abstract class Ruta {

    private String            nombreRuta;
    private int               horaInicio;
    private int               horaFin;
    private ArrayList<String> listadoParadas;

    public Ruta(String nombreRuta, int horaInicio, int horaFin,
                ArrayList<String> listadoParadas) {
        this.nombreRuta     = nombreRuta;
        this.horaInicio     = horaInicio;
        this.horaFin        = horaFin;
        this.listadoParadas = listadoParadas;
    }

    // ── Métodos abstractos (polimorfismo) ───────────────────────────────────

    /** @return true si la ruta opera a la hora indicada */
    public abstract boolean calcularDisponibilidad(int horaActual);

    /** @return instrucciones formateadas para mostrar en el kiosco */
    public abstract String obtenerInstrucciones(String origen, String destino);

    /** @return tipo legible: "Troncal" o "Alimentadora" */
    public abstract String getTipo();

    // ── Persistencia ────────────────────────────────────────────────────────

    /**
     * Serializa la ruta a CSV para rutas.txt
     * Formato: Tipo;Nombre;HoraInicio;HoraFin;Parada1,Parada2,...
     */
    public String toCSV() {
        return getTipo() + ";" + nombreRuta + ";" + horaInicio + ";"
                + horaFin + ";" + String.join(",", listadoParadas);
    }

    // ── Getters & Setters ───────────────────────────────────────────────────

    public String            getNombreRuta()     { return nombreRuta; }
    public void              setNombreRuta(String v) { this.nombreRuta = v; }
    public int               getHoraInicio()     { return horaInicio; }
    public void              setHoraInicio(int v){ this.horaInicio = v; }
    public int               getHoraFin()        { return horaFin; }
    public void              setHoraFin(int v)   { this.horaFin = v; }
    public ArrayList<String> getListadoParadas() { return listadoParadas; }
    public void              setListadoParadas(ArrayList<String> v) { this.listadoParadas = v; }

    @Override
    public String toString() {
        return "[" + getTipo() + "] " + nombreRuta
                + " (" + horaInicio + ":00-" + horaFin + ":00) | "
                + String.join(" -> ", listadoParadas);
    }
}