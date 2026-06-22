package controlador;

// Integrantes: Lionny Lin - 0222510050 & Samuel Campo - 0222510057
// Universidad de Cartagena - POO 2026-1

import grafo.*;
import modelo.*;
import excepciones.*;

import java.io.*;
import java.util.*;

/**
 * Controlador central de rutas.
 * Gestiona el ArrayList<Ruta>, construye el grafo y delega la búsqueda de camino óptimo a la clase AlgoritmoDijkstra.
 */

/**
 * JUSTIFICACION DE NO EXTENDER SERIALIZABLE:
 * Dado que los datos se manejan desde un archivo de texto plano (.txt), decidimos manejar la lectura y salida de
 *  datos mediante clases estándar de entrada/salida de texto de Java (PrintWriter, FileWriter, BufferedReader, FileReader)
 *  para escribir esas líneas resultantes en los archivos .txt, o para leerlas y reconstruir los objetos separando las cadenas con .split(";").
 * Esto, con el fin de manejar la Legibilidad y Depuración: Si hay un error en los datos de los usuarios o las tarjetas,
 *  pueden abrir usuarios.txt y ver o editar los datos directamente con cualquier editor de texto.
 */
public class GestorRutas {

    private ArrayList<Ruta> rutas;
    private GrafoRutas grafo;
    private AlgoritmoRuta algoritmo; // aqui definimos el algoritmo que usaremos como cualquiera que implemente la interfaz AlgoritmoRuta
    //todos los datos se guardaran en la subcarpeta data/
    private static final String ARCHIVO_RUTAS = "data/rutas.txt";
        //En caso de ocurrir una RutaNoEncontradaException, se activara el plan de contingencia.
        //Para esto, guardamos cada parada y su estacion mas cercana en un HashMap.
    private static final Map<String, String> CONTINGENCIA = new HashMap<>();
    static {
        //Zona norte
        CONTINGENCIA.put("bocagrande", "Bodeguita (tome T103 o X105)");
        CONTINGENCIA.put("crespo", "Centro o Bodeguita (tome T102)");

        // Zona Centro
        CONTINGENCIA.put("manga", "Las Delicias o Lo Amador (tome X105)");
        CONTINGENCIA.put("pie de la popa", "Las Delicias (camine o tome T101)");
        CONTINGENCIA.put("torices", "Lo Amador (camine o tome ruta alterna)");

        // Zona Suroccidente
        CONTINGENCIA.put("el bosque", "María Auxiliadora (tome X102)");
        CONTINGENCIA.put("zaragocilla", "Cuatro Vientos (camine o ruta alterna)");

        // Zona Sur
        CONTINGENCIA.put("san fernando", "Madre Bernarda (tome A104)");
        CONTINGENCIA.put("blas de lezo", "Castellana (tome A107 o A108)");

        CONTINGENCIA.put("av. pedro romero", "Cuatro Vientos");
        CONTINGENCIA.put("13 de junio", "Cuatro Vientos");

        // Zona Suroriente
        CONTINGENCIA.put("olaya", "Las Delicias o Ejecutivos (tome X104)");
    }

    public GestorRutas() {
        this.rutas = new ArrayList<>();
        this.grafo = new GrafoRutas();
        this.algoritmo = new AlgoritmoDijkstra();
        cargarDesdeArchivo();
    }

    // A continuacion, la implementacion para buscar la ruta mas optima usando grafo + AlgortimoDijkstra.
    /**
     * @return lista de IDs de estaciones (camino mínimo), nunca null
     * @throws OrigenDestinoIdenticoException si origen == destino
     * @throws FueraDeServicioException si hora fuera del rango operativo
     * @throws RutaNoEncontradaException si no hay camino posible
     */
    public List<String> buscarRutaOptima(String origen, String destino, int hora)
            throws  OrigenDestinoIdenticoException, FueraDeServicioException, RutaNoEncontradaException {
        if (origen.equalsIgnoreCase(destino))
            throw new OrigenDestinoIdenticoException(origen);

        if (hora < 4 || hora >= 23)
            throw new FueraDeServicioException(hora);

        List<String> camino = this.algoritmo.calcularRuta(this.grafo, origen, destino, hora);

        if (camino.isEmpty())
            throw new RutaNoEncontradaException(destino);

        return camino;
    }

    /**
     * Dado un camino de IDs, devuelve instrucciones legibles para cada tramo.
     *
     * REGLA DE LOS 90 MINUTOS (tarifa integrada):
     *   - El reloj de 90 minutos arranca en la primera validacion del viaje.
     *   - Mientras no pasen mas de 90 min acumulados desde esa validacion,
     *     se permiten hasta 4 transbordos sin costo adicional.
     *   - Un transbordo NO es gratis si:
     *       a) ya pasaron mas de 90 min desde la primera validacion, o
     *       b) ya se usaron los 4 transbordos disponibles, o
     *       c) la ruta se vuelve a tomar en sentido contrario (regreso).
     *   - Continuar en el MISMO bus/ruta (sin cambiar de nombreRuta) nunca
     *     cuenta como transbordo: no consume cupo ni reinicia nada.
     */
    public String generarInstrucciones(List<String> camino, int hora) {
        if (camino.size() < 2) return "Ya se encuentra en el destino.";

        final int VENTANA_MINUTOS  = 90;
        final int MAX_TRANSBORDOS  = 4;

        String rutaAnterior = null;
        int tarifasACobrar = 0;
        int transbordosEnVentana = 0;
        int minutosDesdeValidacion = 0; // reloj de 90 min
        int tiempoTotalViaje = 0;


        Map<String, String> sentidoPorRutaEnVentana = new HashMap<>();
        // Nueva lista solo para guardar la secuencia de buses/ruta
        List<String> rutasUsadas = new ArrayList<>();

        for (int i = 0; i < camino.size() - 1; i++) {
            String desde = camino.get(i);
            String hasta = camino.get(i + 1);
            NodoEstacion nodo = this.grafo.getNodo(desde);

            if (nodo == null) continue;

            for (AristaRuta arista : nodo.getConexiones()) {
                if (arista.getDestino().equalsIgnoreCase(hasta) && arista.estaDisponible(hora)) {
                    String nombreRutaActual = arista.getNombreRuta();
                    boolean continuaMismoBus = nombreRutaActual.equalsIgnoreCase(rutaAnterior);
                    int tiempoTramo = tiempoTotalViaje += arista.getPesoMinutos(); // sumamos el tiempo gastado en este viaje

                    if (!continuaMismoBus) {
                        rutasUsadas.add(nombreRutaActual); //nueva ruta
                    }

                    if (tarifasACobrar == 0) {
                        tarifasACobrar = 1;
                        minutosDesdeValidacion = tiempoTramo;
                        transbordosEnVentana = 0;
                        sentidoPorRutaEnVentana.clear();
                        sentidoPorRutaEnVentana.put(nombreRutaActual, desde + "->" + hasta);

                    } else if (continuaMismoBus) {
                        minutosDesdeValidacion += tiempoTramo;

                    } else {
                        boolean dentroDeVentana = minutosDesdeValidacion <= VENTANA_MINUTOS;
                        boolean cupoDisponible  = transbordosEnVentana < MAX_TRANSBORDOS;
                        String sentidoPrevio = sentidoPorRutaEnVentana.get(nombreRutaActual);
                        boolean mismoSentido = sentidoPrevio == null || !sentidoPrevio.equals(hasta + "->" + desde);

                        if (dentroDeVentana && cupoDisponible && mismoSentido) {
                            transbordosEnVentana++;
                            minutosDesdeValidacion += tiempoTramo;
                            sentidoPorRutaEnVentana.putIfAbsent(nombreRutaActual, desde + "->" + hasta);
                        } else {
                            tarifasACobrar++;
                            minutosDesdeValidacion = tiempoTramo;
                            transbordosEnVentana = 0;
                            sentidoPorRutaEnVentana.clear();
                            sentidoPorRutaEnVentana.put(nombreRutaActual, desde + "->" + hasta);
                        }
                    }

                    rutaAnterior = nombreRutaActual;
                    break;
                }
            }
        }

        double totalAPagar = tarifasACobrar * modelo.TarjetaUsuario.TARIFA;

        StringBuilder sb = new StringBuilder();
        sb.append("Recorrido: ").append(String.join(" -> ", camino)).append("\n");
        sb.append("Rutas a tomar: ").append(String.join(" -> ", rutasUsadas)).append("\n");
        sb.append("Tiempo total: ").append(tiempoTotalViaje).append(" min\n\n");

        sb.append("Pasajes cobrados: ").append(tarifasACobrar).append("\n");
        sb.append("Total a pagar: $").append(String.format("%.0f", totalAPagar));

        if (tarifasACobrar > 1) {
            sb.append("\nNota: Se cobró pasaje adicional (exceso de tiempo, límite de transbordos o retorno).");
        } else {
            sb.append("\nNota: Viaje cubierto con un solo pasaje.");
        }

        return sb.toString();
    }

    //Para simepre obtener una respuesta, llamaremos al mapa previamente construido con el metodo getOrDefault, asi, al no obtener una respuesta,
    // se lanza la frase default "la estacion troncal mas cercana".
    //Ejemplo:
    //Contingencia: dirijase a *Cuatro Vientos* y continue en transporte alterno.
    //O: Contingencia: dirijase a *la estacion tronal* mas cercana y continue en transporte alterno.
    public String generarContingencia(String destino) {
        String plan = CONTINGENCIA.getOrDefault(destino.toLowerCase(), "la estacion troncal mas cercana");
        return "Contingencia: dirijase a " + plan + " y continue en transporte alterno.";
    }

    // CRUD sobre rutas.txt:
    public void cargarDesdeArchivo() {
        rutas.clear();
        File f = new File(ARCHIVO_RUTAS);
        try {
            if (!f.exists()) {
                throw new IOException("El archivo de rutas no existe."); //Lanzamos IOException en caso de fallo en carga de archivo.
            }
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {  // Aqui usamos un Try-With-Resources para cerrar automaticamente el archivo.
                String linea;
                while ((linea = br.readLine()) != null) {
                    linea = linea.trim(); // Eliminamos espacio en blanco
                    if (linea.isEmpty() || linea.startsWith("#")) continue;  // Ignoramos lineas vacias y '#' que indica un comentario
                    String[] p = linea.split(";");
                    if (p.length < 5) continue;   // El parsing se hace usando ';' como separador, en caso de ser menor que 5, se asume que hubo error y se descarta.
                    String tipo = p[0].trim();  // Troncal || Alimentadora
                    String nom  = p[1].trim();  // Codigo (A101...)
                    int hi = Integer.parseInt(p[2].trim());  //Hora Inicio
                    int hf = Integer.parseInt(p[3].trim());  // Hora Final
                    ArrayList<String> paradas = new ArrayList<>();
                    for (String parada : p[4].split(","))
                        paradas.add(parada.trim());  // Paradas divididas por ','.

                    String lineaPesos = br.readLine();
                    if (lineaPesos == null || !lineaPesos.trim().toUpperCase().startsWith("PESOS;")) {
                        continue; //ignoramos esta secuencia de ruta y peso
                    }
                    lineaPesos = lineaPesos.trim();
                    String[] pesosStr = lineaPesos.substring(lineaPesos.indexOf(';') + 1).split(",");
                    ArrayList<Integer> pesos = new ArrayList<>();
                    for (String pesoStr : pesosStr)
                        pesos.add(Integer.parseInt(pesoStr.trim()));

                    if (pesos.size() != paradas.size() - 1) {
                        continue; //ignoramos esta secuencia de ruta y peso
                    }

                    if ("Troncal".equalsIgnoreCase(tipo)) {
                        rutas.add(new RutaTroncal(nom, hi, hf, paradas, pesos));   // Procesar ruta Troncal.
                    } else if ("Alimentadora".equalsIgnoreCase(tipo) && p.length >= 6) {   // el sexto elemento es el barrio
                        rutas.add(new RutaAlimentadora(nom, hi, hf, paradas, p[5].trim(),pesos));  // Procesar Ruta Alimentadora, p[5] es el barrio por el que circula la ruta
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error cargando rutas: " + e.getMessage());
        } finally{
            reconstruirGrafo();  //apesar de que hayan errores, construimos siempre el grafo.
        }
    }

    //proceso inverso para guardar datos.
    public void guardarEnArchivo() {
        new File("data").mkdirs();  //mkdirs() crea la carpeta "data" en caso de que no exista
        // Try with resource para cierre automatico, y FileWriter(ruta, false) para sobreescritura forzosa
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_RUTAS, false))) {
            pw.println("# rutas.txt | Formato: Tipo;Nombre;HInicio;HFin;Paradas[;Barrio]"); // La primera linea
            for (Ruta r : rutas)
                pw.println(r.toCSV());  // Polimorfismo
                // Ejemplo de salida esperada: Troncal;T01;5;23;Portal,Pradera,Centro
        } catch (IOException e) {
            System.err.println("Error guardando rutas: " + e.getMessage());
        }
    }

    public void agregarRuta(Ruta r)  {
        rutas.add(r);
        reconstruirGrafo();  // Reconstruimos el grafo para actualizar datos
        guardarEnArchivo();
    }

    public boolean eliminarRuta(String nombre) {
        boolean found = false;
        for (Ruta r : rutas) {
            if (r.getNombreRuta().equalsIgnoreCase(nombre)) {
                found = true;
                rutas.remove(r);
                break;             // Detenemos el ciclo de inmediato
            }
        }
        if (found) { // Hacemos el update de inmediato
            reconstruirGrafo();
            guardarEnArchivo();
        }
        return found;
    }

    public void actualizarRuta(String nombre, Ruta nueva) {
        eliminarRuta(nombre);
        agregarRuta(nueva);
    }

    private void reconstruirGrafo() {
        grafo.construirDesdeRutas(rutas);
    }

    public ArrayList<Ruta> getRutas()   { return rutas; }
    public GrafoRutas getGrafo()   { return grafo; }
}