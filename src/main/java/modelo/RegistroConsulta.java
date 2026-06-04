package modelo;

// Integrantes: [Nombre 1] - [Nombre 2]
// Universidad de Cartagena - POO 2026-1

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

/**
 * Representa una consulta realizada en el kiosco.
 * Cada uso del kiosco crea un RegistroConsulta y lo escribe en
 * historial_consultas.txt en modo APPEND — nunca sobreescribe.
 *
 * SOLID - SRP: solo sabe como registrarse en el historial.
 */
public class RegistroConsulta {

    private LocalDate fecha;
    private String    hora;
    private String    origen;
    private String    destino;
    private String    rutaRecomendada;

    private static final String ARCHIVO = "data/historial_consultas.txt";

    public RegistroConsulta(String origen, String destino,
                            String rutaRecomendada, int horaSimulada) {
        this.fecha            = LocalDate.now();
        this.hora             = String.format("%02d:00", horaSimulada);
        this.origen           = origen;
        this.destino          = destino;
        this.rutaRecomendada  = rutaRecomendada;
    }

    /**
     * Escribe este registro en historial_consultas.txt (append).
     * Formato: Fecha;Hora;Origen;Destino;RutaRecomendada
     */
    public void registrar() {
        try (FileWriter fw = new FileWriter(ARCHIVO, true)) {
            fw.write(fecha + ";" + hora + ";" + origen + ";"
                    + destino + ";" + rutaRecomendada + "\n");
        } catch (IOException e) {
            System.err.println("Error al escribir historial: " + e.getMessage());
        }
    }

    // ── Getters ─────────────────────────────────────────────────────────────

    public LocalDate getFecha()            { return fecha; }
    public String    getHora()             { return hora; }
    public String    getOrigen()           { return origen; }
    public String    getDestino()          { return destino; }
    public String    getRutaRecomendada()  { return rutaRecomendada; }

    @Override
    public String toString() {
        return fecha + " | " + hora + " | " + origen
                + " -> " + destino + " | Ruta: " + rutaRecomendada;
    }
}