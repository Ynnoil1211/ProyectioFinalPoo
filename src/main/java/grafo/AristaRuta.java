package grafo;

// Integrantes: Lionny Lin - 0222510050 & Samuel Campo - 0222510057
// Universidad de Cartagena - POO 2026-1

/**
 * Arista dirigida del grafo de rutas.
 * Conecta dos estaciones indicando la ruta que hace ese trayecto,
 * el tiempo estimado (peso) y el horario en que está disponible.
 */
public class AristaRuta {

    private String destino;       // ID de la estación destino
    private String nombreRuta;    // ej: "T101", "A103"
    private int    pesoMinutos;   // tiempo estimado del tramo
    private int    horaInicio;
    private int    horaFin;

    public AristaRuta(String destino, String nombreRuta,
                      int pesoMinutos, int horaInicio, int horaFin) {
        this.destino     = destino;
        this.nombreRuta  = nombreRuta;
        this.pesoMinutos = pesoMinutos;
        this.horaInicio  = horaInicio;
        this.horaFin     = horaFin;
    }

    /** @return true si esta arista opera a la hora indicada */
    public boolean estaDisponible(int hora) {
        return hora >= horaInicio && hora <= horaFin;
    }

    public String getDestino()     { return destino; }
    public String getNombreRuta()  { return nombreRuta; }
    public int    getPesoMinutos() { return pesoMinutos; }
    public int    getHoraInicio()  { return horaInicio; }
    public int    getHoraFin()     { return horaFin; }

    @Override
    public String toString() {
        return "-> " + destino + " via " + nombreRuta
                + " (" + pesoMinutos + " min)";
    }
}