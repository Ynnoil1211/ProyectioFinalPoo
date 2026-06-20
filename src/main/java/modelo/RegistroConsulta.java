package modelo;

// Integrantes: Lionny Lin - 0222510050 & Samuel Campo - 0222510057
// Universidad de Cartagena - POO 2026-1

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Log de cada consulta ciudadana. Se escribe en append. */
public class RegistroConsulta {

    private LocalDateTime timestamp;
    private String origen;
    private String destino;
    private String rutaRecomendada;
    private int horaSimulada;

    private static final String ARCHIVO = "data/historial_consultas.txt";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public RegistroConsulta(String origen, String destino, String ruta, int hora) {
        this.timestamp = LocalDateTime.now();
        this.origen = origen;
        this.destino = destino;
        this.rutaRecomendada = ruta;
        this.horaSimulada = hora;
    }
    public void registrar() {
        // Try with resource para cierre automatico, y FileWriter(ruta, true) para escribir al final del archivo (modo append) sin borrar lo anterior
        try (FileWriter fw = new FileWriter(ARCHIVO, true)) {
            // Extraemos la concatenación a una variable para mantener el código limpio y legible
            String lineaRegistro = timestamp.format(FMT) + ";" + origen + ";" + destino + ";" + rutaRecomendada + ";" + horaSimulada;
            fw.write(lineaRegistro + "\n"); // Escribimos el registro en el historial con su respectivo salto de línea
            // Ejemplo de salida esperada: 2026-06-19 18:51;Bodeguita;Portal;PORTAL;17
        } catch (IOException e) {
            System.err.println("Error historial: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return timestamp.format(FMT) + " | " + origen + " -> " + destino + " | " + rutaRecomendada;
    }
}