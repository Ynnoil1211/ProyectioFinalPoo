package grafo;

// Integrantes: Lionny Lin - 0222510050 & Samuel Campo - 0222510057
// Universidad de Cartagena - POO 2026-1

import java.util.List;

/**
 * Interface para cualquier algoritmo de búsqueda de camino mínimo,
 * permitiendo cambiar de algoritmo sin dañar el codigo.
 * SOLID-DIP: GestorRutas depende de esta abstracción, no de Dijkstra.
 * SOLID-OCP: agregar un nuevo algoritmo = nueva clase, nada se modifica.
 */
public interface AlgoritmoRuta {
    /**
     * Calcula la ruta óptima entre dos estaciones.
     * @param grafo el grafo con todas las estaciones y conexiones
     * @param origen ID de la estación de partida
     * @param destino ID de la estación destino
     * @param hora hora actual (0-23) para filtrar rutas disponibles
     * @return List<String> = lista de las paradas a tomar (ruta optima)
     */
    List<String> calcularRuta(GrafoRutas grafo, String origen, String destino, int hora);
}