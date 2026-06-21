package modelo;

// Integrantes: Lionny Lin - 0222510050 & Samuel Campo - 0222510057
// Universidad de Cartagena - POO 2026-1
import java.util.ArrayList;
import java.util.stream.Collectors;

//Clase abstracta base de toda ruta del sistema TransCaribe.
/**
 * JUSTIFICACION DE NO EXTENDER SERIALIZABLE:
 * Dado que los datos se manejan desde un archivo de texto plano (.txt), decidimos manejar la lectura y salida de
 *  datos mediante clases estándar de entrada/salida de texto de Java (PrintWriter, FileWriter, BufferedReader, FileReader)
 *  para escribir esas líneas resultantes en los archivos .txt, o para leerlas y reconstruir los objetos separando las cadenas con .split(";").
 * Esto, con el fin de manejar la Legibilidad y Depuración: Si hay un error en los datos de los usuarios o las tarjetas,
 *  pueden abrir usuarios.txt y ver o editar los datos directamente con cualquier editor de texto.
 */
public abstract class Ruta {

    private String nombreRuta;
    private int horaInicio;
    private int horaFin;
    private ArrayList<String> listadoParadas;
    private ArrayList<Integer> pesosTramos;

    public Ruta(String nombreRuta, int horaInicio, int horaFin, ArrayList<String> listadoParadas, ArrayList<Integer> pesosTramos) {
        this.nombreRuta = nombreRuta;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.listadoParadas = listadoParadas;
        if (pesosTramos == null || pesosTramos.size() != listadoParadas.size() - 1) {
            throw new IllegalArgumentException(
                    "Ruta " + nombreRuta + ": se esperaban " + (listadoParadas.size() - 1)
                            + " pesos pero se recibieron " + (pesosTramos == null ? 0 : pesosTramos.size()));
        }
        this.pesosTramos = pesosTramos;
    }

    // ── Métodos abstractos (polimorfismo) :
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
     *          Pesos;t1,t2,t3...
     */
    public String toCSV() {
        String lineaRuta = getTipo() + ";" + nombreRuta + ";" + horaInicio + ";"
                + horaFin + ";" + String.join(",", listadoParadas);
        String lineaPesos = "Pesos;" + pesosTramos.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        return lineaRuta + "\n" + lineaPesos;
    }

    // ── Getters & Setters ───────────────────────────────────────────────────
// ── Getters & Setters :
    public String getNombreRuta() { return nombreRuta; }
    public void setNombreRuta(String v) { this.nombreRuta = v; }
    public int getHoraInicio() { return horaInicio; }
    public void setHoraInicio(int v){ this.horaInicio = v; }
    public int getHoraFin() { return horaFin; }
    public void setHoraFin(int v) { this.horaFin = v; }
    public ArrayList<String> getListadoParadas() { return listadoParadas; }
    public void setListadoParadas(ArrayList<String> v) { this.listadoParadas = v; }
    public ArrayList<Integer> getPesosTramos() {return pesosTramos;}
    public void setPesosTramos(ArrayList<Integer> pesosTramos) {this.pesosTramos = pesosTramos;}

    @Override
    public String toString() {
        return "[" + getTipo() + "] " + nombreRuta + " (" + horaInicio + ":00-" + horaFin + ":00) | " + String.join(" -> ", listadoParadas);
    }
}