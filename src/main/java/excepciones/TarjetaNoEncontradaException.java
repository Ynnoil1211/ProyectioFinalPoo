package excepciones;
// Integrantes: Lionny Lin - 0222510050 & Samuel Campo - 0222510057
// Universidad de Cartagena - POO 2026-1
/** No existe tarjeta con ese número en el sistema. */
public class TarjetaNoEncontradaException extends Exception {
    public TarjetaNoEncontradaException(String numero) {
        super("No existe tarjeta con numero \"" + numero + "\" en el sistema.");
    }
}