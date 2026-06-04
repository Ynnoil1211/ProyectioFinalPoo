package modelo;

// Integrantes: [Nombre 1] - [Nombre 2]
// Universidad de Cartagena - POO 2026-1

import excepciones.SaldoInsuficienteException;
import java.io.*;
import java.util.ArrayList;

/**
 * Modela la tarjeta de transporte del ciudadano.
 * Gestiona saldo y movimientos, con persistencia en tarjeta.txt.
 *
 * SOLID - SRP: solo conoce logica de saldo y movimientos.
 */
public class TarjetaUsuario {

    private String            nombreTitular;
    private String            numeroTarjeta;
    private double            saldo;
    private ArrayList<String> historialMovimientos;

    private static final double TARIFA_VIAJE = 2700.0;
    private static final String ARCHIVO      = "data/tarjeta.txt";

    public TarjetaUsuario(String nombreTitular, String numeroTarjeta,
                          double saldoInicial) {
        this.nombreTitular        = nombreTitular;
        this.numeroTarjeta        = numeroTarjeta;
        this.saldo                = saldoInicial;
        this.historialMovimientos = new ArrayList<>();
    }

    /**
     * Recarga el saldo y registra el movimiento.
     * @param monto cantidad en pesos colombianos (debe ser > 0)
     */
    public void recargar(double monto) {
        if (monto <= 0) {
            System.err.println("Monto de recarga debe ser mayor a cero.");
            return;
        }
        saldo += monto;
        historialMovimientos.add("+ $" + String.format("%.0f", monto) + " (Recarga)");
        guardarEnArchivo();
    }

    /**
     * Descuenta el valor de un viaje ($2.700).
     * @throws SaldoInsuficienteException si el saldo no alcanza
     */
    public void descontarViaje() throws SaldoInsuficienteException {
        if (saldo < TARIFA_VIAJE) {
            throw new SaldoInsuficienteException(saldo);
        }
        saldo -= TARIFA_VIAJE;
        historialMovimientos.add("- $" + String.format("%.0f", TARIFA_VIAJE) + " (Viaje)");
        guardarEnArchivo();
    }

    /** Retorna los últimos 5 movimientos formateados. */
    public String getResumenMovimientos() {
        if (historialMovimientos.isEmpty()) return "Sin movimientos registrados.";
        int inicio = Math.max(0, historialMovimientos.size() - 5);
        StringBuilder sb = new StringBuilder();
        for (int i = inicio; i < historialMovimientos.size(); i++) {
            sb.append(historialMovimientos.get(i)).append("\n");
        }
        return sb.toString().trim();
    }

    /** Sobreescribe tarjeta.txt con el estado actual. */
    public void guardarEnArchivo() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO, false))) {
            pw.println("Titular:" + nombreTitular);
            pw.println("Numero:"  + numeroTarjeta);
            pw.println("Saldo:"   + saldo);
            pw.println("---Movimientos---");
            for (String mov : historialMovimientos) pw.println(mov);
        } catch (IOException e) {
            System.err.println("Error al guardar tarjeta: " + e.getMessage());
        }
    }

    /** Lee tarjeta.txt y restaura el estado. */
    public void cargarDesdeArchivo() {
        File f = new File(ARCHIVO);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            boolean leyendoMov = false;
            while ((linea = br.readLine()) != null) {
                if      (linea.startsWith("Titular:"))       nombreTitular = linea.substring(8);
                else if (linea.startsWith("Numero:"))        numeroTarjeta = linea.substring(7);
                else if (linea.startsWith("Saldo:"))         saldo = Double.parseDouble(linea.substring(6));
                else if (linea.equals("---Movimientos---"))  leyendoMov = true;
                else if (leyendoMov && !linea.isEmpty())     historialMovimientos.add(linea);
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error al cargar tarjeta: " + e.getMessage());
        }
    }

    // ── Getters & Setters ───────────────────────────────────────────────────

    public String            getNombreTitular()   { return nombreTitular; }
    public String            getNumeroTarjeta()   { return numeroTarjeta; }
    public double            getSaldo()            { return saldo; }
    public ArrayList<String> getHistorialMovimientos() { return historialMovimientos; }

    @Override
    public String toString() {
        return nombreTitular + " | **** "
                + numeroTarjeta.substring(numeroTarjeta.length() - 4)
                + " | Saldo: $" + String.format("%.0f", saldo);
    }
}