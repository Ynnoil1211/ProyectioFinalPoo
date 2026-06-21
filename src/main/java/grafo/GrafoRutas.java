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
    public void agregarArista(String origenId, String destinoId, String nombreRuta, int pesoMinutos, int horaInicio, int horaFin) {
        NodoEstacion nodo = nodos.get(origenId.toUpperCase());
        if (nodo == null) return;
        nodo.agregarConexion(new AristaRuta(destinoId.toUpperCase(), nombreRuta, pesoMinutos, horaInicio, horaFin));
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
            List<Integer> pesos  = ruta.getPesosTramos();
            for (String p : paradas) {
                // Aseguremos que todas las paradas existan como nodos:
                agregarNodo(p, p);  // nombre = ID en este caso
                // Dado que nodos es un mapa, evitamos duplicado automaticamente
            }
            if (pesos == null || pesos.size() != paradas.size() - 1) {
                throw new IllegalStateException(
                        "Ruta " + ruta.getNombreRuta() + ": se esperaban "
                                + (paradas.size() - 1) + " pesos pero se encontraron "
                                + (pesos == null ? 0 : pesos.size()));
            }
            for (int i = 0; i < paradas.size() - 1; i++) {
                String desde = paradas.get(i).toUpperCase(); // estacion actual
                String hasta = paradas.get(i + 1).toUpperCase();  // estacion proxima
                int peso = pesos.get(i);  // minutos estimados por tramo
                // agregamos un nuevo tramo señalando las conexiones
                agregarArista(desde, hasta, ruta.getNombreRuta(), peso, ruta.getHoraInicio(), ruta.getHoraFin());
                // Arista inversa (mismo tiempo, misma ruta, pero en orden inverso)
                agregarArista(hasta, desde, ruta.getNombreRuta(), peso, ruta.getHoraInicio(), ruta.getHoraFin());
            }
        }
    }

    // ── Consultas:

    public NodoEstacion getNodo(String id) {
        return nodos.get(id.toUpperCase());
    }
    public Collection<NodoEstacion> getNodos() {
        return nodos.values();
    }
    public boolean existeNodo(String id) {
        return nodos.containsKey(id.toUpperCase());
    }
    public List<String> obtenerIdsEstacionesOrdenados() {
        List<String> ids = new ArrayList<>(nodos.keySet());
        Collections.sort(ids);
        return ids;
    }
    public int getTotalNodos() {
        return nodos.size();
    }
    // Calculo de todas las conexiones
    public int getTotalAristas() {
        int total = 0;
        for (NodoEstacion n : nodos.values()) {
            // Sumamos el tamaño de la lista de conexiones de cada nodo
            total += n.getConexiones().size();
        }
        return total;
    }

    @Override
    public String toString() {
        return "GrafoRutas [" + getTotalNodos() + " nodos, "
                + getTotalAristas() + " aristas]";
    }
}