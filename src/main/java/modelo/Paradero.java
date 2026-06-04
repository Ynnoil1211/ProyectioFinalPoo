package modelo;

// Integrantes: [Nombre 1] - [Nombre 2]
// Universidad de Cartagena - POO 2026-1

import java.io.FileWriter;
import java.io.IOException;

/**
 * Modela una parada física de la red TransCaribe.
 *
 * SOLID - SRP: solo conoce datos de una parada y como persistirse.
 */
public class Paradero {

    private int    idParadero;
    private String nombre;
    private String barrio;

    public Paradero(int idParadero, String nombre, String barrio) {
        this.idParadero = idParadero;
        this.nombre     = nombre;
        this.barrio     = barrio;
    }

    /** Información formateada para mostrar en el kiosco. */
    public String getInfo() {
        return "Paradero #" + idParadero + " | " + nombre + " | Barrio: " + barrio;
    }

    /** Persiste este paradero en paraderos.txt (modo append). */
    public void guardarEnArchivo() {
        try (FileWriter fw = new FileWriter("data/paraderos.txt", true)) {
            fw.write(idParadero + ";" + nombre + ";" + barrio + "\n");
        } catch (IOException e) {
            System.err.println("Error al guardar paradero: " + e.getMessage());
        }
    }

    // ── Getters & Setters ───────────────────────────────────────────────────

    public int    getIdParadero()               { return idParadero; }
    public void   setIdParadero(int idParadero) { this.idParadero = idParadero; }

    public String getNombre()                   { return nombre; }
    public void   setNombre(String nombre)      { this.nombre = nombre; }

    public String getBarrio()                   { return barrio; }
    public void   setBarrio(String barrio)      { this.barrio = barrio; }

    @Override
    public String toString() { return getInfo(); }
}