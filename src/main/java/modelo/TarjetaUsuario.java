package modelo;

// Integrantes: Lionny Lin - 0222510050 & Samuel Campo - 0222510057
// Universidad de Cartagena - POO 2026-1

import controlador.GestorTarjetas;
import excepciones.SaldoInsuficienteException;
import java.io.*;
import java.util.ArrayList;

/**
 * Tarjetas para pagar pasajes. Saldo, movimientos y persistencia en tarjetas.txt.
 */
public class TarjetaUsuario {

    private String numeroTarjeta;
    private double saldo;
    private ArrayList<String> movimientos;
    public static final double TARIFA = 3900.0;

    public TarjetaUsuario(String numeroTarjeta, double saldo) {
        this.numeroTarjeta = numeroTarjeta;
        this.saldo = saldo;
        this.movimientos = new ArrayList<>();
    }

    public void recargar(double monto) {
        if (monto <= 0) throw new IllegalArgumentException("Monto invalido.");
        saldo += monto;
        movimientos.add(String.format("+ $%.0f  (Recarga)", monto));
    }

    public void descontarViaje() throws SaldoInsuficienteException {
        if (saldo < TARIFA) throw new SaldoInsuficienteException(saldo);
        saldo -= TARIFA;
        movimientos.add(String.format("- $%.0f  (Viaje)", TARIFA));
    }

    public String getResumenMovimientos() {
        if (movimientos.isEmpty()) return "Sin movimientos.";
        int desde = Math.max(0, movimientos.size() - 5);  //mostramos los ultimos 5 movimientos.
        StringBuilder sb = new StringBuilder();
        for (int i = desde; i < movimientos.size(); i++)
            sb.append(movimientos.get(i)).append("\n");
        return sb.toString().trim();
    }
    /** Serializa a CSV: NumeroTarjeta;Titular;Saldo */
    public String toCSV() {
        return numeroTarjeta + ";" + String.format("%.0f", saldo);
    }

    // ── Getters & Setters ───────────────────────────────────────────────────

    public String getNumeroTarjeta() { return numeroTarjeta; }
    public void setNumeroTarjeta(String v) { this.numeroTarjeta = v; }
    public double getSaldo() { return saldo; }
    public void setSaldo(double v) { this.saldo = v; }
    public ArrayList<String> getMovimientos() { return movimientos; }

    @Override
    public String toString() {
        return numeroTarjeta + " | $" + String.format("%.0f", saldo);
    }
}