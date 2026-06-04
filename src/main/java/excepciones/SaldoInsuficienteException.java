package excepciones;

// Integrantes: [Nombre 1] - [Nombre 2]
// Universidad de Cartagena - POO 2026-1

/**
 * Se lanza cuando el saldo de la tarjeta no cubre el valor del viaje ($2.700).
 */
public class SaldoInsuficienteException extends Exception {

    private final double saldoActual;

    public SaldoInsuficienteException(double saldo) {
        super("Saldo insuficiente. Saldo actual: $" + String.format("%.0f", saldo)
                + ". Se requieren $2.700 para el viaje. Por favor recargue su tarjeta.");
        this.saldoActual = saldo;
    }

    public double getSaldoActual() { return saldoActual; }
}