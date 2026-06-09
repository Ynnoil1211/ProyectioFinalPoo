package controlador;

// Integrantes: [Nombre 1] - [Nombre 2]
// Universidad de Cartagena - POO 2026-1

import grafo.*;
import modelo.*;
import excepciones.*;

import java.io.*;
import java.util.*;

/**
 * Controlador central de rutas.
 * Gestiona el ArrayList<Ruta>, construye el grafo y delega
 * la búsqueda de camino óptimo a un IAlgoritmoRuta.
 *
 * SOLID-SRP : gestiona rutas, grafo y persistencia de rutas.
 * SOLID-OCP : el algoritmo de búsqueda es intercambiable (IAlgoritmoRuta).
 * SOLID-DIP : depende de Ruta (abstracción) y de IAlgoritmoRuta (interfaz).
 */
public class GestorRutas {

    private ArrayList<Ruta>  rutas;
    private GrafoRutas       grafo;
    private AlgoritmoDijkstra   algoritmo;

    private static final String ARCHIVO_RUTAS = "data/rutas.txt";

    private static final Map<String, String> CONTINGENCIA = new HashMap<>();
    static {
        CONTINGENCIA.put("manga",      "Las Delicias (tome T101)");
        CONTINGENCIA.put("crespo",     "Ejecutivos (tome T103)");
        CONTINGENCIA.put("olaya",      "Las Delicias (tome T102)");
        CONTINGENCIA.put("bocagrande", "Bodeguita (tome T102)");
    }

    public GestorRutas() {
        this.rutas     = new ArrayList<>();
        this.grafo     = new GrafoRutas();
        this.algoritmo = new AlgoritmoDijkstra();   // DIP: inyectable
    }

    /** Permite cambiar el algoritmo en tiempo de ejecución. */
    public void setAlgoritmo(AlgoritmoDijkstra algoritmo) {
        this.algoritmo = algoritmo;
    }

    // ── Búsqueda de ruta óptima (grafo + Dijkstra) ──────────────────────────

    /**
     * Busca la ruta óptima entre dos estaciones usando el grafo.
     *
     * @return lista de IDs de estaciones (camino mínimo), nunca null
     * @throws OrigenDestinoIdenticoException si origen == destino
     * @throws FueraDeServicioException       si hora fuera del rango operativo
     * @throws RutaNoEncontradaException      si no hay camino posible
     */
    public List<String> buscarRutaOptima(String origen, String destino, int hora)
            throws OrigenDestinoIdenticoException,
            FueraDeServicioException,
            RutaNoEncontradaException {

        if (origen.equalsIgnoreCase(destino))
            throw new OrigenDestinoIdenticoException(origen);

        if (hora < 4 || hora >= 23)
            throw new FueraDeServicioException(hora);

        List<String> camino = algoritmo.calcularRuta(grafo, origen, destino, hora);

        if (camino.isEmpty())
            throw new RutaNoEncontradaException(destino);

        return camino;
    }

    /**
     * Dado un camino de IDs, devuelve instrucciones legibles para cada tramo.
     * Recorre el camino y busca qué ruta cubre cada par consecutivo.
     */
    public String generarInstrucciones(List<String> camino, int hora) {
        if (camino.size() < 2) return "Ya se encuentra en el destino.";
        StringBuilder sb = new StringBuilder();
        sb.append("RUTA OPTIMA — ").append(camino.size() - 1)
                .append(" tramo(s)\n");
        sb.append("Recorrido: ").append(String.join(" -> ", camino)).append("\n");
        sb.append("─────────────────────────────\n");

        for (int i = 0; i < camino.size() - 1; i++) {
            String desde = camino.get(i);
            String hasta = camino.get(i + 1);
            NodoEstacion nodo = grafo.getNodo(desde);
            if (nodo == null) continue;
            for (AristaRuta arista : nodo.getConexiones()) {
                if (arista.getDestino().equalsIgnoreCase(hasta)
                        && arista.estaDisponible(hora)) {
                    sb.append("Tramo ").append(i + 1).append(": ")
                            .append(desde).append(" -> ").append(hasta)
                            .append(" | Ruta ").append(arista.getNombreRuta())
                            .append(" (~").append(arista.getPesoMinutos())
                            .append(" min)\n");
                    break;
                }
            }
        }
        sb.append("Tarifa total: $")
                .append(String.format("%.0f", (camino.size() - 1) * 2700.0));
        return sb.toString();
    }

    public String generarContingencia(String destino) {
        String plan = CONTINGENCIA.getOrDefault(
                destino.toLowerCase(), "la estacion troncal mas cercana");
        return "Ruta directa no disponible a " + destino + ".\n"
                + "Contingencia: dirijase a " + plan
                + " y continue en transporte alterno.";
    }

    // ── CRUD sobre rutas.txt ────────────────────────────────────────────────

    public void cargarDesdeArchivo() {
        rutas.clear();
        File f = new File(ARCHIVO_RUTAS);
        if (!f.exists()) { cargarEjemplos(); guardarEnArchivo(); return; }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) continue;
                String[] p = linea.split(";");
                if (p.length < 5) continue;
                String tipo = p[0].trim();
                String nom  = p[1].trim();
                int    hi   = Integer.parseInt(p[2].trim());
                int    hf   = Integer.parseInt(p[3].trim());
                ArrayList<String> paradas = new ArrayList<>();
                for (String s : p[4].split(",")) paradas.add(s.trim());
                if ("Troncal".equalsIgnoreCase(tipo)) {
                    rutas.add(new RutaTroncal(nom, hi, hf, paradas));
                } else if ("Alimentadora".equalsIgnoreCase(tipo) && p.length >= 6) {
                    rutas.add(new RutaAlimentadora(nom, hi, hf, paradas, p[5].trim()));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error cargando rutas: " + e.getMessage());
            cargarEjemplos();
        }
        reconstruirGrafo();
    }

    public void guardarEnArchivo() {
        new File("data").mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_RUTAS, false))) {
            pw.println("# rutas.txt | Formato: Tipo;Nombre;HInicio;HFin;Paradas[;Barrio]");
            for (Ruta r : rutas) pw.println(r.toCSV());
        } catch (IOException e) {
            System.err.println("Error guardando rutas: " + e.getMessage());
        }
    }

    public void agregarRuta(Ruta r)  { rutas.add(r); reconstruirGrafo(); guardarEnArchivo(); }

    public boolean eliminarRuta(String nombre) {
        boolean ok = rutas.removeIf(r -> r.getNombreRuta().equalsIgnoreCase(nombre));
        if (ok) { reconstruirGrafo(); guardarEnArchivo(); }
        return ok;
    }

    public void actualizarRuta(String nombre, Ruta nueva) {
        eliminarRuta(nombre);
        agregarRuta(nueva);
    }

    private void reconstruirGrafo() {
        grafo.construirDesdeRutas(rutas);
    }

    // ── Datos de ejemplo ────────────────────────────────────────────────────

    private void cargarEjemplos() {
        ArrayList<String> p1 = new ArrayList<>(List.of(
                "Portal","Ejecutivos","Las Delicias","Chambacú","Bodeguita"));
        rutas.add(new RutaTroncal("T101", 5, 21, p1));

        ArrayList<String> p2 = new ArrayList<>(List.of(
                "Portal","Bocagrande","Las Delicias","Bodeguita"));
        rutas.add(new RutaTroncal("T102", 5, 21, p2));

        ArrayList<String> p3 = new ArrayList<>(List.of(
                "Portal","Ejecutivos","Chambacú","Crespo"));
        rutas.add(new RutaTroncal("T103", 5, 21, p3));

        ArrayList<String> a1 = new ArrayList<>(List.of(
                "Chambacú","Manga","Pie de la Popa"));
        rutas.add(new RutaAlimentadora("A103", 5, 20, a1, "Manga"));

        ArrayList<String> a2 = new ArrayList<>(List.of(
                "Chambacú","Crespo","Marbella"));
        rutas.add(new RutaAlimentadora("A205", 5, 20, a2, "Crespo"));

        ArrayList<String> a3 = new ArrayList<>(List.of(
                "Las Delicias","Olaya","Nelson Mandela"));
        rutas.add(new RutaAlimentadora("A310", 5, 19, a3, "Olaya"));

        reconstruirGrafo();
    }

    // ── Getters ─────────────────────────────────────────────────────────────

    public ArrayList<Ruta> getRutas()   { return rutas; }
    public GrafoRutas      getGrafo()   { return grafo; }
}