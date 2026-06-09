package modelo;

// Integrantes: [Nombre 1] - [Nombre 2]
// Universidad de Cartagena - POO 2026-1

/**
 * Usuario normal del sistema.
 * Se autentica con el código de su tarjeta TransCaribe.
 * Puede: consultar saldo, recargar tarjeta, ver movimientos.
 *
 * Hereda de Usuario (jerarquía de roles por herencia).
 */
public class UsuarioNormal extends Usuario {

    private TarjetaUsuario tarjeta;

    public UsuarioNormal(String nombre, String codigoAcceso,
                         TarjetaUsuario tarjeta) {
        super(nombre, codigoAcceso, "USUARIO");
        this.tarjeta = tarjeta;
    }

    @Override
    public String getDescripcionRol() {
        return "Usuario registrado — puede consultar saldo y recargar tarjeta.";
    }

    @Override
    public String toCSV() {
        // Formato: ROL;Nombre;CodigoAcceso;NumeroTarjeta
        return "USUARIO;" + getNombre() + ";" + getCodigoAcceso()
                + ";" + tarjeta.getNumeroTarjeta();
    }

    // ── Getters & Setters ───────────────────────────────────────────────────

    public TarjetaUsuario getTarjeta()                 { return tarjeta; }
    public void           setTarjeta(TarjetaUsuario t) { this.tarjeta = t; }
}