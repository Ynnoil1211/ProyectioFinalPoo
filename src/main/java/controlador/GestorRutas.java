package controlador;

// Integrantes: [Nombre 1] - [Nombre 2]
// Universidad de Cartagena - POO 2026-1

import modelo.*;
import excepciones.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Controlador principal del sistema Kiosco TransCaribe.
 * Gestiona la coleccion de rutas y toda la logica de busqueda.
 *
 * SOLID - SRP : centraliza busqueda y persistencia de rutas.
 * SOLID - OCP : agregar un nuevo tipo de ruta no requiere modificar esta clase.
 * SOLID - DIP : opera sobre ArrayList<Ruta> (abstraccion), nunca sobre
 *               RutaTroncal o RutaAlimentadora directamente.
 */
public class GestorRutas {

    private ArrayList<Ruta>  rutas;
    private TarjetaUsuario   tarjeta;

    private static final String ARCHIVO_RUTAS = "data/rutas.txt";

    /**
     * Mapa de contingencia: barrio destino -> estacion troncal mas cercana.
     * Si la ruta alimentadora ya cerro, GestorRutas consulta este mapa.
     */
    private static final HashMap<String, String> CONTINGENCIA = new HashMap<>();
    static {
        CONTINGENCIA.put("manga",       "Las Delicias (tome la T101)");
        CONTINGENCIA.put("crespo",      "Ejecutivos (tome la T103)");
        CONTINGENCIA.put("olaya",       "Las Delicias (tome la T102)");
        CONTINGENCIA.put("bocagrande",  "Bodeguita (tome la T102)");
    }

    public GestorRutas() {
        this.rutas   = new ArrayList<>();
        this.tarjeta = new TarjetaUsuario("Usuario", "0000000000007743", 12500);
    }

    // ── Logica de negocio ───────────────────────────────────────────────────

    /**
     * Busca la ruta optima para el trayecto solicitado a la hora indicada.
     *
     * Este metodo recorre POLIMORFICAMENTE el ArrayList<Ruta>:
     * cada elemento puede ser RutaTroncal o RutaAlimentadora,
     * pero el codigo solo invoca el contrato de Ruta (DIP).
     *
     * @return la Ruta disponible, o null si existe ruta pero ya cerro (contingencia)
     * @throws OrigenDestinoIdenticoException  si origen == destino
     * @throws FueraDeServicioSistemaException si hora fuera del rango operativo
     * @throws RutaNoEncontradaException       si ningun ruta cubre el destino
     */
    public Ruta buscarRutaOptima(String origen, String destino, int hora)
            throws OrigenDestinoIdenticoException,
            FueraDeServicioSistemaException,
            RutaNoEncontradaException {

        // Validacion 1: origen == destino
        if (origen.equalsIgnoreCase(destino)) {
            throw new OrigenDestinoIdenticoException(origen);
        }

        // Validacion 2: fuera del horario del sistema
        if (hora < 4 || hora >= 23) {
            throw new FueraDeServicioSistemaException(hora);
        }

        // Busqueda polimorfica ─────────────────────────────────────────────
        // Java resuelve en tiempo de ejecucion si llamar a
        // RutaTroncal.calcularDisponibilidad() o RutaAlimentadora.calcularDisponibilidad()
        for (Ruta ruta : rutas) {
            boolean cubreDestino = ruta.getListadoParadas().stream()
                    .anyMatch(p -> p.equalsIgnoreCase(destino));

            if (cubreDestino && ruta.calcularDisponibilidad(hora)) {
                return ruta;   // ruta disponible encontrada
            }
        }

        // Verificar si el destino existe pero sus rutas ya cerraron
        boolean existeRuta = rutas.stream().anyMatch(r ->
                r.getListadoParadas().stream()
                        .anyMatch(p -> p.equalsIgnoreCase(destino)));

        if (!existeRuta) {
            throw new RutaNoEncontradaException(destino);
        }

        // Rutas existen pero ya cerraron -> retorna null -> activa contingencia
        return null;
    }

    /**
     * Plan de contingencia cuando la ruta directa ya cerro.
     * @param destino barrio al que queria llegar el usuario
     */
    public String generarContingencia(String destino) {
        String clave = destino.toLowerCase();
        String plan  = CONTINGENCIA.getOrDefault(clave,
                "la estacion troncal mas cercana a su destino");
        return "La ruta directa a " + destino + " ya cerro su servicio.\n"
                + "Plan de contingencia: dirijase a " + plan
                + "\ny continue en transporte alterno desde alli.";
    }

    /** Registra la consulta en historial_consultas.txt. */
    public void registrarConsulta(RegistroConsulta registro) {
        registro.registrar();
    }

    // ── CRUD sobre rutas.txt ────────────────────────────────────────────────

    /**
     * Lee rutas.txt y reconstruye los objetos Ruta.
     * Actua como fabrica: instancia RutaTroncal o RutaAlimentadora
     * segun el campo Tipo de cada linea.
     *
     * Formato: Tipo;Nombre;HoraInicio;HoraFin;Parada1,Parada2,...[;Barrio]
     */
    public void cargarDesdeArchivo() {
        rutas.clear();
        File archivo = new File(ARCHIVO_RUTAS);
        if (!archivo.exists()) {
            cargarDatosEjemplo();
            guardarEnArchivo();
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) continue;

                String[] p = linea.split(";");
                if (p.length < 5) continue;

                String tipo       = p[0].trim();
                String nombre     = p[1].trim();
                int    horaInicio = Integer.parseInt(p[2].trim());
                int    horaFin    = Integer.parseInt(p[3].trim());

                ArrayList<String> paradas = new ArrayList<>();
                for (String s : p[4].split(",")) paradas.add(s.trim());

                if (tipo.equalsIgnoreCase("Troncal")) {
                    rutas.add(new RutaTroncal(nombre, horaInicio, horaFin, paradas));
                } else if (tipo.equalsIgnoreCase("Alimentadora") && p.length >= 6) {
                    rutas.add(new RutaAlimentadora(nombre, horaInicio, horaFin,
                            paradas, p[5].trim()));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error al cargar rutas: " + e.getMessage());
            cargarDatosEjemplo();
        }
    }

    /**
     * Sobreescribe rutas.txt con el estado actual de la coleccion.
     * Se llama despues de cualquier operacion de alta o baja.
     */
    public void guardarEnArchivo() {
        new File("data").mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_RUTAS, false))) {
            pw.println("# rutas.txt - Sistema Kiosco TransCaribe");
            pw.println("# Formato: Tipo;Nombre;HoraInicio;HoraFin;Paradas[;Barrio]");
            for (Ruta r : rutas) {
                String linea = r.toCSV();
                if (r instanceof RutaAlimentadora) {
                    linea += ";" + ((RutaAlimentadora) r).getBarrioAsociado();
                }
                pw.println(linea);
            }
        } catch (IOException e) {
            System.err.println("Error al guardar rutas: " + e.getMessage());
        }
    }

    /** Agrega una ruta nueva y persiste el cambio (CREATE). */
    public void agregarRuta(Ruta ruta) {
        rutas.add(ruta);
        guardarEnArchivo();
    }

    /**
     * Elimina una ruta por nombre y persiste el cambio (DELETE).
     * @return true si fue eliminada, false si no se encontro
     */
    public boolean eliminarRuta(String nombreRuta) {
        boolean eliminada = rutas.removeIf(
                r -> r.getNombreRuta().equalsIgnoreCase(nombreRuta));
        if (eliminada) guardarEnArchivo();
        return eliminada;
    }

    // ── Datos de ejemplo si no existe rutas.txt ─────────────────────────────

    private void cargarDatosEjemplo() {
        ArrayList<String> p1 = new ArrayList<>();
        p1.add("Portal"); p1.add("Ejecutivos");
        p1.add("Las Delicias"); p1.add("Chambacú"); p1.add("Bodeguita");
        rutas.add(new RutaTroncal("T101", 5, 21, p1));

        ArrayList<String> p2 = new ArrayList<>();
        p2.add("Portal"); p2.add("Bocagrande");
        p2.add("Las Delicias"); p2.add("Bodeguita");
        rutas.add(new RutaTroncal("T102", 5, 21, p2));

        ArrayList<String> p3 = new ArrayList<>();
        p3.add("Portal"); p3.add("Ejecutivos");
        p3.add("Chambacú"); p3.add("Crespo");
        rutas.add(new RutaTroncal("T103", 5, 21, p3));

        ArrayList<String> a1 = new ArrayList<>();
        a1.add("Chambacú"); a1.add("Manga"); a1.add("Pie de la Popa");
        rutas.add(new RutaAlimentadora("A103", 5, 20, a1, "Manga"));

        ArrayList<String> a2 = new ArrayList<>();
        a2.add("Chambacú"); a2.add("Crespo"); a2.add("Marbella");
        rutas.add(new RutaAlimentadora("A205", 5, 20, a2, "Crespo"));

        ArrayList<String> a3 = new ArrayList<>();
        a3.add("Las Delicias"); a3.add("Olaya"); a3.add("Nelson Mandela");
        rutas.add(new RutaAlimentadora("A310", 5, 19, a3, "Olaya"));
    }

    // ── Getters ─────────────────────────────────────────────────────────────

    public ArrayList<Ruta> getRutas()           { return rutas; }
    public TarjetaUsuario  getTarjeta()          { return tarjeta; }
    public void            setTarjeta(TarjetaUsuario t) { this.tarjeta = t; }
}