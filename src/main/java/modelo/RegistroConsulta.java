package modelo;

// Integrantes: [Nombre 1] - [Nombre 2]
// Universidad de Cartagena - POO 2026-1

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Log de cada consulta ciudadana. Se escribe en append. */
public class RegistroConsulta {

    private LocalDateTime timestamp;
    private String        origen;
    private String        destino;
    private String        rutaRecomendada;
    private int           horaSimulada;

    private static final String ARCHIVO = "data/historial_consultas.txt";
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public RegistroConsulta(String origen, String destino,
                            String ruta, int hora) {
        this.timestamp       = LocalDateTime.now();
        this.origen          = origen;
        this.destino         = destino;
        this.rutaRecomendada = ruta;
        this.horaSimulada    = hora;
    }

    public void registrar() {
        try (FileWriter fw = new FileWriter(ARCHIVO, true)) {
            fw.write(timestamp.format(FMT) + ";" + origen + ";"
                    + destino + ";" + rutaRecomendada
                    + ";" + horaSimulada + "\n");
        } catch (IOException e) {
            System.err.println("Error historial: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return timestamp.format(FMT) + " | " + origen + " -> "
                + destino + " | " + rutaRecomendada;
    }
}