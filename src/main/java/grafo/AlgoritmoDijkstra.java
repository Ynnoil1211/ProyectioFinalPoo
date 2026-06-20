package grafo;
// Integrantes: Lionny Lin - 0222510050 & Samuel Campo - 0222510057
// Universidad de Cartagena - POO 2026-1

import java.util.*;

/**
 * Algoritmo de Dijkstra para camino mínimo en el grafo de rutas.
 * Encuentra el trayecto de menor tiempo entre dos estaciones,considerando únicamente las aristas disponibles a la hora indicada.
 * Complejidad: O((V + E) log V) con cola de prioridad.
 */
public class AlgoritmoDijkstra {
    public List<String> calcularRuta(GrafoRutas grafo, String origen, String destino, int hora) {
        String origenKey = origen.toUpperCase();
        String destinoKey = destino.toUpperCase();

        // Validar que los nodos existan
        if (!grafo.existeNodo(origenKey) || !grafo.existeNodo(destinoKey)) {
            return Collections.emptyList();
        }

        /**Creamos una clase interna auxiliar para la cola de prioridad,
         * esto con el fin de agrupar cada nodo con el costo de llegar hasta él
         */
        class Par {
            int costo;
            String nodo;

            Par(int costo, String nodo) {
                this.costo = costo;
                this.nodo = nodo;
            }
        }

        Map<String, Integer> dist = new HashMap<>();  //Guarda el tiempo mínimo para llegar a cada estación. Al principio, todas se configuran con una distancia "infinita"
        Map<String, String> previo = new HashMap<>(); //Guarda cuál fue la estación anterior para saber cómo se llegó allí

        // Cola que ordena automáticamente los nodos sacando siempre el de menor costo
        PriorityQueue<Par> cola = new PriorityQueue<>(Comparator.comparingInt(p -> p.costo));

        // Inicializar todos los nodos con distancia "infinita"
        for (NodoEstacion n : grafo.getNodos()) {
            dist.put(n.getIdEstacion(), Integer.MAX_VALUE);
        }

        dist.put(origenKey, 0); // Aqui sobreescribimos el costo de la estacion origen como 0
        cola.offer(new Par(0, origenKey)); // Metemos la estacion origen en la cola, con un costo acumulado incial de 0 minutos

        // Bucle principal de Dijkstra
        while (!cola.isEmpty()) {
            Par actual = cola.poll(); //Saca de la cola el elemento que tenga el menor tiempo acumulado.

            // Si ya llegamos al destino, no hace falta seguir buscando
            if (actual.nodo.equals(destinoKey)) break;

            /**
             * Filtramos caminos viejos u obsoletos. Debido a cómo funciona Dijkstra,
             * una estación puede meterse a la cola varias veces con tiempos diferentes.
             * Ejemplo:
             * Si sacamos una estación con un costo de 20 minutos,
             * pero en el mapa dist ya registramos antes un camino mejor de 12 minutos,
             * esta línea ejecuta un continue para ignorar el camino lento y saltar al siguiente elemento de la cola.
             * */
            if (actual.costo > dist.get(actual.nodo)) continue;

            NodoEstacion nodoEstacion = grafo.getNodo(actual.nodo); //pasamos de string a NodoEstacion

            for (AristaRuta arista : nodoEstacion.getConexiones()) {  //para cada una de las aristas

                // Si el bus ya no pasa a esta hora, ignoramos este camino
                if (!arista.estaDisponible(hora)) continue;

                String vecino = arista.getDestino();
                int nuevoCosto = actual.costo + arista.getPesoMinutos();

                /**
                 * RELAJACION:
                 * ¿El nuevo tiempo que calculamos es menor que el "infinito" o el minimo anterior guardado para esa estación vecina?
                 * */
                if (nuevoCosto < dist.get(vecino)) {
                    dist.put(vecino, nuevoCosto);  // Actualiza el costo de esa estación vecina con el nuevo valor más bajo
                    previo.put(vecino, actual.nodo); // Actualiza la relacion de rapidez para llegar al vecino
                    // Finalmente, metemos al vecino en la cola con su nuevo tiempo minimo para que el algoritmo explore sus conexiones en los siguientes turnos del bucle
                    cola.offer(new Par(nuevoCosto, vecino)); //
                }
            }
        }

        // Reconstruir el camino desde el destino hacia el origen
        if (!previo.containsKey(destinoKey) && !origenKey.equals(destinoKey)) {
            return Collections.emptyList(); // No se encontró conexión
        }
        LinkedList<String> camino = new LinkedList<>();  // Por la rapidez en insercion
        String paso = destinoKey;
        while (paso != null) {
            /**
             *  Añade cada estación al inicio de la lista. Como estamos leyendo la ruta al revés (de destino a origen),
             *  al empujar cada elemento al frente, la lista se voltea automáticamente
             * */
            camino.addFirst(paso);
            paso = previo.get(paso);
        }

        return camino;
    }
}