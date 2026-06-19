package excepciones;
// Integrantes: Lionny Lin - 0222510050 & Samuel Campo - 0222510057
// Universidad de Cartagena - POO 2026-1
/**
 * Se lanza cuando el destino ingresado no tiene ninguna ruta
 * registrada en el sistema.
 */
public class RutaNoEncontradaException extends Exception {

    private final String destinoBuscado;

    public RutaNoEncontradaException(String destino) {
        super("El destino \"" + destino + "\" aun no esta cubierto "
                + "por la red TransCaribe. Comuniquese con atencion al usuario.");
        this.destinoBuscado = destino;
    }

    public String getDestinoBuscado() { return destinoBuscado; }
}