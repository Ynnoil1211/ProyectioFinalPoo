package excepciones;
// Integrantes: [Nombre 1] - [Nombre 2]
/** Credenciales incorrectas al iniciar sesión. */
public class CredencialesInvalidasException extends Exception {
    public CredencialesInvalidasException() {
        super("Codigo o contrasena incorrectos. Verifique e intente de nuevo.");
    }
    public CredencialesInvalidasException(String msg) { super(msg); }
}