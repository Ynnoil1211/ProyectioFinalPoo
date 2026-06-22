package controlador;

// Integrantes: Lionny Lin - 0222510050 & Samuel Campo - 0222510057
// Universidad de Cartagena - POO 2026-1

import modelo.Ruta;
import modelo.TarjetaUsuario;
import excepciones.TarjetaNoEncontradaException;

import java.io.*;
import java.util.*;

/**
 * Gestiona el CRUD de tarjetas de transporte.
 * Solo el rol ADMIN puede crear y eliminar tarjetas.
 * Cualquier usuario puede recargar y consultar una tarjeta mediante un codigo de tarjeta.
 */

/**
 * JUSTIFICACION DE NO EXTENDER SERIALIZABLE:
 * Dado que los datos se manejan desde un archivo de texto plano (.txt), decidimos manejar la lectura y salida de
 *  datos mediante clases estándar de entrada/salida de texto de Java (PrintWriter, FileWriter, BufferedReader, FileReader)
 *  para escribir esas líneas resultantes en los archivos .txt, o para leerlas y reconstruir los objetos separando las cadenas con .split(";").
 * Esto, con el fin de manejar la Legibilidad y Depuración: Si hay un error en los datos de los usuarios o las tarjetas,
 *  pueden abrir usuarios.txt y ver o editar los datos directamente con cualquier editor de texto.
 */

public class GestorTarjetas {

    private ArrayList<TarjetaUsuario> tarjetas;
    private static final String ARCHIVO = "data/tarjetas.txt";
    private static final String MOVIMIENTOS_ARCHIVO = "data/tarjeta.txt";

    public GestorTarjetas() {
        this.tarjetas = new ArrayList<>();
        cargarDesdeArchivo();
    }

    // ── Búsqueda :

    /**
     * Busca una tarjeta por número.
     * @throws TarjetaNoEncontradaException si no existe
     */
    public TarjetaUsuario buscarPorNumero(String numero) throws TarjetaNoEncontradaException  {
        for (TarjetaUsuario t : tarjetas) {
            if (t.getNumeroTarjeta().equalsIgnoreCase(numero)) return t;
        }
        throw new TarjetaNoEncontradaException(numero);
    }

    // ── CRUD :

    // CREATE — solo ADMIN debe llamar este metodo
    public void crearTarjeta(TarjetaUsuario tarjeta) {
        tarjetas.add(tarjeta);
        guardarEnArchivo();
    }

    // READ — retorna copia de la lista
    public ArrayList<TarjetaUsuario> listarTarjetas() {
        return new ArrayList<>(tarjetas);
    }

    // UPDATE — recarga saldo de una tarjeta existente
    public void recargarTarjeta(String idTarjeta, double monto) throws TarjetaNoEncontradaException {
        TarjetaUsuario tarjeta = buscarPorNumero(idTarjeta);
        // llamar al metodo para recargar
        tarjeta.recargar(monto);
        guardarEnArchivo();
        guardarMovimientosEnArchivo();
    }


    // DELETE — solo ADMIN debe llamar este metodo
    public boolean eliminarTarjeta(String numero) {
        boolean found = false;
        for (TarjetaUsuario t : tarjetas) {
            if (t.getNumeroTarjeta().equalsIgnoreCase(numero)) {
                found = true;
                tarjetas.remove(t);
                break;             // Detenemos el ciclo de inmediato
            }
        }
        if (found) {
            guardarEnArchivo();
            guardarMovimientosEnArchivo();
        }
        return found;
    }
    public void guardarMovimientosEnArchivo() {
        new File("data").mkdirs();  //mkdirs() crea la carpeta "data" en caso de que no exista
        // Try with resource para cierre automatico, y FileWriter(ruta, false) para sobreescritura forzosa
        try (PrintWriter pw = new PrintWriter(new FileWriter(MOVIMIENTOS_ARCHIVO, false))) {

            for (TarjetaUsuario t : tarjetas) {
                pw.println("ID:" + t.getNumeroTarjeta());
                pw.println("Saldo:" + t.getSaldo());
                pw.println("---Movimientos---");

                // Imprimimos cada movimiento de la tarjeta
                for (String mov : t.getMovimientos()) {
                    pw.println(mov);
                }
                pw.println(); // Salto de linea vacio para separar una tarjeta de otra
            }

        } catch (IOException e) {
            System.err.println("Error guardando movimientos: " + e.getMessage());
        }
    }

    public ArrayList<String> obtenerMovimientosPorId(String idTarjeta) {
        ArrayList<String> movimientosEncontrados = new ArrayList<>();
        File f = new File(MOVIMIENTOS_ARCHIVO);
        try {
            if (!f.exists()) {
                throw new IOException("El archivo de movimientos no existe."); // Lanzamos IOException en caso de fallo en carga de archivo.
            }
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {  // Aqui usamos un Try-With-Resources para cerrar automaticamente el archivo.
                String linea;
                boolean leyendoMovimientos = false;
                boolean esTarjetaCorrecta = false;

                while ((linea = br.readLine()) != null) {
                    linea = linea.trim();

                    if (linea.startsWith("ID:")) {
                        leyendoMovimientos = false;
                        String num = linea.substring(3).trim(); // Cortamos "Numero:"
                        if (num.equals(idTarjeta)) {
                            esTarjetaCorrecta = true;
                        } else {
                            esTarjetaCorrecta = false;
                        }
                    } else if (linea.startsWith("---Movimientos---") && esTarjetaCorrecta) {
                        leyendoMovimientos = true;
                    } else if (leyendoMovimientos) {
                        if (linea.isEmpty()) {
                            break; // Si encuentra una linea vacia, se asume que termino el bloque de la tarjeta
                        }
                        movimientosEncontrados.add(linea);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error cargando movimientos: " + e.getMessage());
        }
        return movimientosEncontrados;
    }
    // ── Persistencia ────────────────────────────────────────────────────────

    public void cargarDesdeArchivo() {
        tarjetas.clear();
        File f = new File(ARCHIVO);
        try {
            if (!f.exists()) {
                throw new IOException("El archivo de tarjetas no existe.");
            }
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    linea = linea.trim();

                    if (linea.isEmpty() || linea.startsWith("#")) continue;
                    String[] p = linea.split(";");
                    if (p.length < 2) continue; // Cambiado a 2 parámetros (id y saldo)

                    String id = p[0].trim();
                    double saldo = Double.parseDouble(p[1].trim());

                    TarjetaUsuario nuevaTarjeta = new TarjetaUsuario(id, saldo);

                    // SOLUCIÓN AL STACKOVERFLOW: El gestor le asigna los movimientos tras crearla
                    nuevaTarjeta.getMovimientos().addAll(obtenerMovimientosPorId(id));

                    tarjetas.add(nuevaTarjeta);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error cargando tarjetas: " + e.getMessage());
        }
    }

    public void guardarEnArchivo() {
        new File("data").mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO, false))) {
            pw.println("# tarjetas.txt | NumeroTarjeta;Saldo"); // Cabecera sin Titular
            for (TarjetaUsuario t : tarjetas)
                pw.println(t.toCSV());
        } catch (IOException e) {
            System.err.println("Error guardando tarjetas: " + e.getMessage());
        }
    }

    public ArrayList<TarjetaUsuario> getTarjetas() { return tarjetas; }
}