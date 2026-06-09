package grafo;

// Integrantes: [Nombre 1] - [Nombre 2]
// Universidad de Cartagena - POO 2026-1

import java.util.List;

/**
 * Contrato para cualquier algoritmo de búsqueda de camino mínimo.
 * Permite intercambiar Dijkstra por BFS o A* sin tocar GestorRutas.
 *
 * SOLID-ISP : interfaz mínima, solo lo que necesita el consumidor.
 * SOLID-DIP : GestorRutas depende de esta abstracción, no de Dijkstra.
 * SOLID-OCP : agregar un nuevo algoritmo = nueva clase, nada se modifica.
 */
public interface IAlgoritmoRuta {

    /**
     * Calcula la ruta óptima entre dos estaciones.
     *
     * @param grafo   el grafo con todas las estaciones y conexiones
     * @param origen  ID de la estación de partida
     * @param destino ID de la estación destino
     * @param hora    hora actual (0-23) para filtrar rutas disponibles
     * @return lista ordenada de IDs de estaciones desde origen hasta destino,
     *         o lista vacía si no hay camino posible
     */
    List<String> calcularRuta(GrafoRutas grafo, String origen,
                              String destino, int hora);
}