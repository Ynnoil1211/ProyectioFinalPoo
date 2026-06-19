package excepciones;
// Integrantes: Lionny Lin - 0222510050 & Samuel Campo - 0222510057
// Universidad de Cartagena - POO 2026-1
/** Credenciales incorrectas al iniciar sesión. */
public class CredencialesInvalidasException extends Exception {
    public CredencialesInvalidasException() {
        super("Codigo o contrasena incorrectos. Verifique e intente de nuevo.");
    }
    public CredencialesInvalidasException(String msg) { super(msg); }
}