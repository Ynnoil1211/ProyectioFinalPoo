package grafo;

// Integrantes: Lionny Lin - 0222510050 & Samuel Campo - 0222510057
// Universidad de Cartagena - POO 2026-1

import modelo.Ruta;
import java.util.*;

/**
 * Grafo dirigido y ponderado de la red TransCaribe.
 * Cada NodoEstacion es una parada física.
 * Cada AristaRuta es un tramo entre dos paradas consecutivas de una ruta.
 * GestorRutas construye este grafo a partir de la lista de rutas cargadas
 * desde archivo, y luego delega la búsqueda a la clase AlgoritmoDijkstra.
 */
public class GrafoRutas {

    // mapa: idEstacion -> NodoEstacion (key -> value)
    private Map<String, NodoEstacion> nodos;

    public GrafoRutas() {
        this.nodos = new HashMap<>();
    }

    // Construccion del Grafo:

    // Agrega un nodo al grafo (estación), si ya existe, lo ignora
    public void agregarNodo(String id, String nombre) {
        nodos.putIfAbsent(id.toUpperCase(), new NodoEstacion(id.toUpperCase(), nombre));
    }

    /**
     * Conecta dos estaciones con una arista dirigida (tramo de ruta).
     * Se llama para cada par consecutivo de paradas de cada ruta.
     */
    public void agregarArista(String origenId, String destinoId,
                              String nombreRuta, int pesoMinutos,
                              int horaInicio, int horaFin) {
        NodoEstacion nodo = nodos.get(origenId.toUpperCase());
        if (nodo == null) return;
        nodo.agregarConexion(new AristaRuta(
                destinoId.toUpperCase(), nombreRuta,
                pesoMinutos, horaInicio, horaFin));
    }

    /**
     * Construye el grafo a partir de una lista de rutas del modelo.
     * Para cada ruta, conecta cada parada con la siguiente (paradas[i] -> paradas[i+1]).
     * También agrega la arista inversa para permitir recorridos en ambas direcciones.
     */
    public void construirDesdeRutas(List<Ruta> rutas) {
        // Ruta contiene nombre, horario y lista de paradas.
        nodos.clear();
        for (Ruta ruta : rutas) {
            List<String> paradas = ruta.getListadoParadas();
            for (String p : paradas) {
                // Aseguremos que todas las paradas existan como nodos:
                agregarNodo(p, p);  // nombre = ID en este caso
                // Dado que nodos es un mapa, evitamos duplicado automaticamente
            }
            for (int i = 0; i < paradas.size() - 1; i++) {
                String desde = paradas.get(i).toUpperCase();
                String hasta = paradas.get(i + 1).toUpperCase();
                int peso = 6;  // minutos estimados por tramo
                agregarArista(desde, hasta,
                        ruta.getNombreRuta(), peso,
                        ruta.getHoraInicio(), ruta.getHoraFin());
                // Arista inversa (mismo tiempo, misma ruta)
                agregarArista(hasta, desde,
                        ruta.getNombreRuta(), peso,
                        ruta.getHoraInicio(), ruta.getHoraFin());
            }
        }
    }

    // ── Consultas ───────────────────────────────────────────────────────────

    public NodoEstacion getNodo(String id) {
        return nodos.get(id.toUpperCase());
    }

    public Collection<NodoEstacion> getNodos() {
        return nodos.values();
    }

    public boolean existeNodo(String id) {
        return nodos.containsKey(id.toUpperCase());
    }

    public int getTotalNodos()   { return nodos.size(); }
    public int getTotalAristas() {
        return nodos.values().stream()
                .mapToInt(n -> n.getConexiones().size())
                .sum();
    }

    @Override
    public String toString() {
        return "GrafoRutas [" + getTotalNodos() + " nodos, "
                + getTotalAristas() + " aristas]";
    }
}