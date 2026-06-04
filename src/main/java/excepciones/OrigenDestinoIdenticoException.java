package excepciones;

// Integrantes: [Nombre 1] - [Nombre 2]
// Universidad de Cartagena - POO 2026-1

/**
 * Se lanza cuando el usuario elige el mismo lugar como origen y destino.
 */
public class OrigenDestinoIdenticoException extends Exception {

    public OrigenDestinoIdenticoException(String lugar) {
        super("Ya te encuentras en tu lugar de destino: \"" + lugar
                + "\". No es necesario tomar ninguna ruta.");
    }

    public OrigenDestinoIdenticoException() {
        super("El origen y el destino no pueden ser el mismo lugar.");
    }
}