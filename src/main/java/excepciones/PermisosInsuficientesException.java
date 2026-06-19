package excepciones;
// Integrantes: Lionny Lin - 0222510050 & Samuel Campo - 0222510057
// Universidad de Cartagena - POO 2026-1
/** Un usuario sin rol ADMIN intenta una operación restringida. */
public class PermisosInsuficientesException extends Exception {
    public PermisosInsuficientesException(String operacion) {
        super("Acceso denegado: se requiere rol ADMIN para \"" + operacion + "\".");
    }
}