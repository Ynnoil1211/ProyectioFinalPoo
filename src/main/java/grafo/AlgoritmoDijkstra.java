package grafo;

// Integrantes: [Nombre 1] - [Nombre 2]
// Universidad de Cartagena - POO 2026-1

import java.util.*;

/**
 * Algoritmo de Dijkstra para camino mínimo en el grafo de rutas.
 *
 * Encuentra el trayecto de menor tiempo entre dos estaciones,
 * considerando únicamente las aristas disponibles a la hora indicada.
 *
 * Complejidad: O((V + E) log V) con cola de prioridad.
 *
 * SOLID-OCP : implementa IAlgoritmoRuta sin modificar ninguna otra clase.
 * SOLID-SRP : solo conoce el algoritmo de búsqueda.
 */
public class AlgoritmoDijkstra implements IAlgoritmoRuta {

    @Override
    public List<String> calcularRuta(GrafoRutas grafo, String origen,
                                     String destino, int hora) {

        String origenKey  = origen.toUpperCase();
        String destinoKey = destino.toUpperCase();

        // Verificar que los nodos existen
        if (!grafo.existeNodo(origenKey) || !grafo.existeNodo(destinoKey)) {
            return Collections.emptyList();
        }

        // distancias[nodo] = menor tiempo acumulado desde origen
        Map<String, Integer>  dist    = new HashMap<>();
        // predecesor[nodo] = nodo desde el que llegamos con menor costo
        Map<String, String>   previo  = new HashMap<>();

        // Cola de prioridad: (costo, idNodo)
        PriorityQueue<int[]> cola = new PriorityQueue<>(
                Comparator.comparingInt(a -> a[0]));

        // Inicializar todas las distancias a infinito
        for (NodoEstacion n : grafo.getNodos()) {
            dist.put(n.getIdEstacion(), Integer.MAX_VALUE);
        }
        dist.put(origenKey, 0);
        cola.offer(new int[]{0, origenKey.hashCode()});

        // Mapa auxiliar hashCode -> idEstacion (para recuperar el ID desde la cola)
        Map<Integer, String> hashToId = new HashMap<>();
        for (NodoEstacion n : grafo.getNodos()) {
            hashToId.put(n.getIdEstacion().hashCode(), n.getIdEstacion());
        }

        // ── Dijkstra ────────────────────────────────────────────────────────
        while (!cola.isEmpty()) {
            int[]  par        = cola.poll();
            int    costoActual = par[0];
            String idActual   = hashToId.get(par[1]);

            if (idActual == null) continue;
            if (idActual.equals(destinoKey)) break;
            if (costoActual > dist.getOrDefault(idActual, Integer.MAX_VALUE))
                continue;  // entrada obsoleta

            NodoEstacion nodoActual = grafo.getNodo(idActual);
            if (nodoActual == null) continue;

            for (AristaRuta arista : nodoActual.getConexiones()) {
                // Solo considerar aristas disponibles a la hora actual
                if (!arista.estaDisponible(hora)) continue;

                String  vecino    = arista.getDestino();
                int     nuevoDist = costoActual + arista.getPesoMinutos();
                int     distVec   = dist.getOrDefault(vecino, Integer.MAX_VALUE);

                if (nuevoDist < distVec) {
                    dist.put(vecino, nuevoDist);
                    previo.put(vecino, idActual);
                    cola.offer(new int[]{nuevoDist, vecino.hashCode()});
                    hashToId.put(vecino.hashCode(), vecino);
                }
            }
        }

        // ── Reconstruir camino ──────────────────────────────────────────────
        if (!previo.containsKey(destinoKey) && !origenKey.equals(destinoKey)) {
            return Collections.emptyList();  // no hay camino
        }

        LinkedList<String> camino = new LinkedList<>();
        String actual = destinoKey;
        while (actual != null) {
            camino.addFirst(actual);
            actual = previo.get(actual);
        }

        // Verificar que el camino realmente arranca en el origen
        if (!camino.getFirst().equals(origenKey)) {
            return Collections.emptyList();
        }

        return camino;
    }
}