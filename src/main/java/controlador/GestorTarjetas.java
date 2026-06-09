package controlador;

// Integrantes: [Nombre 1] - [Nombre 2]
// Universidad de Cartagena - POO 2026-1

import modelo.TarjetaUsuario;
import excepciones.TarjetaNoEncontradaException;

import java.io.*;
import java.util.*;

/**
 * Gestiona el CRUD de tarjetas de transporte.
 * Solo el rol ADMIN puede crear y eliminar tarjetas.
 * Cualquier usuario puede recargar y consultar su propia tarjeta.
 *
 * SOLID-SRP: solo conoce lógica de tarjetas y persistencia en tarjetas.txt.
 */
public class GestorTarjetas {

    private ArrayList<TarjetaUsuario> tarjetas;
    private static final String ARCHIVO = "data/tarjetas.txt";

    public GestorTarjetas() {
        this.tarjetas = new ArrayList<>();
    }

    // ── Búsqueda ────────────────────────────────────────────────────────────

    /**
     * Busca una tarjeta por número.
     * @throws TarjetaNoEncontradaException si no existe
     */
    public TarjetaUsuario buscarPorNumero(String numero)
            throws TarjetaNoEncontradaException {
        for (TarjetaUsuario t : tarjetas) {
            if (t.getNumeroTarjeta().equalsIgnoreCase(numero)) return t;
        }
        throw new TarjetaNoEncontradaException(numero);
    }

    // ── CRUD ────────────────────────────────────────────────────────────────

    /** CREATE — solo ADMIN debe llamar este método */
    public void crearTarjeta(TarjetaUsuario tarjeta) {
        tarjetas.add(tarjeta);
        guardarEnArchivo();
    }

    /** READ — retorna copia de la lista */
    public ArrayList<TarjetaUsuario> listarTarjetas() {
        return new ArrayList<>(tarjetas);
    }

    /** UPDATE — recarga saldo de una tarjeta existente */
    public void recargarTarjeta(String numero, double monto)
            throws TarjetaNoEncontradaException {
        buscarPorNumero(numero).recargar(monto);
        guardarEnArchivo();
    }

    /** DELETE — solo ADMIN debe llamar este método */
    public boolean eliminarTarjeta(String numero) {
        boolean ok = tarjetas.removeIf(
                t -> t.getNumeroTarjeta().equalsIgnoreCase(numero));
        if (ok) guardarEnArchivo();
        return ok;
    }

    // ── Persistencia ────────────────────────────────────────────────────────

    public void cargarDesdeArchivo() {
        tarjetas.clear();
        File f = new File(ARCHIVO);
        if (!f.exists()) { cargarEjemplos(); guardarEnArchivo(); return; }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) continue;
                String[] p = linea.split(";");
                if (p.length < 3) continue;
                tarjetas.add(new TarjetaUsuario(
                        p[0].trim(), p[1].trim(),
                        Double.parseDouble(p[2].trim())));
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error cargando tarjetas: " + e.getMessage());
            cargarEjemplos();
        }
    }

    public void guardarEnArchivo() {
        new File("data").mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO, false))) {
            pw.println("# tarjetas.txt | NumeroTarjeta;Titular;Saldo");
            for (TarjetaUsuario t : tarjetas) pw.println(t.toCSV());
        } catch (IOException e) {
            System.err.println("Error guardando tarjetas: " + e.getMessage());
        }
    }

    private void cargarEjemplos() {
        tarjetas.add(new TarjetaUsuario("TC-0001", "Carlos Perez",  15000));
        tarjetas.add(new TarjetaUsuario("TC-0002", "Maria Lopez",   8500));
    }

    public ArrayList<TarjetaUsuario> getTarjetas() { return tarjetas; }
}