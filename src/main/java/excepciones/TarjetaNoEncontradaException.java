package excepciones;
// Integrantes: [Nombre 1] - [Nombre 2]
/** No existe tarjeta con ese número en el sistema. */
public class TarjetaNoEncontradaException extends Exception {
    public TarjetaNoEncontradaException(String numero) {
        super("No existe tarjeta con numero \"" + numero + "\" en el sistema.");
    }
}