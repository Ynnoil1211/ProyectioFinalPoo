package modelo;

// Integrantes: [Nombre 1] - [Nombre 2]
// Universidad de Cartagena - POO 2026-1

/**
 * Clase abstracta base de la jerarquía de usuarios del sistema.
 *
 * Herencia:
 *   Usuario
 *   ├── UsuarioNormal  (recarga tarjeta, consulta saldo)
 *   └── Administrador  (CRUD rutas, CRUD tarjetas)
 *
 * SOLID-SRP : solo conoce identidad y credenciales.
 * SOLID-LSP : las subclases pueden usarse donde se espere un Usuario.
 * SOLID-OCP : se agrega un nuevo rol (ej: Supervisor) sin modificar esta clase.
 */
public abstract class Usuario {

    private String nombre;
    private String codigoAcceso;   // contraseña hasheada o PIN
    private String rol;            // "USUARIO" | "ADMIN"

    public Usuario(String nombre, String codigoAcceso, String rol) {
        this.nombre       = nombre;
        this.codigoAcceso = codigoAcceso;
        this.rol          = rol;
    }

    // ── Métodos abstractos ──────────────────────────────────────────────────

    /** Descripción legible del rol para mostrar en la UI */
    public abstract String getDescripcionRol();

    /** Serializa el usuario a CSV para usuarios.txt */
    public abstract String toCSV();

    // ── Autenticación ───────────────────────────────────────────────────────

    /** @return true si el código proporcionado coincide con el almacenado */
    public boolean autenticar(String codigoIngresado) {
        return this.codigoAcceso.equals(codigoIngresado);
    }

    // ── Getters & Setters ───────────────────────────────────────────────────

    public String getNombre()                   { return nombre; }
    public void   setNombre(String nombre)      { this.nombre = nombre; }
    public String getCodigoAcceso()             { return codigoAcceso; }
    public void   setCodigoAcceso(String v)     { this.codigoAcceso = v; }
    public String getRol()                      { return rol; }
    protected void setRol(String rol)           { this.rol = rol; }

    @Override
    public String toString() {
        return "[" + rol + "] " + nombre;
    }
}