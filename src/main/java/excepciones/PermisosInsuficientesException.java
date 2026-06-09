package excepciones;
// Integrantes: [Nombre 1] - [Nombre 2]
/** Un usuario sin rol ADMIN intenta una operación restringida. */
public class PermisosInsuficientesException extends Exception {
    public PermisosInsuficientesException(String operacion) {
        super("Acceso denegado: se requiere rol ADMIN para \"" + operacion + "\".");
    }
}