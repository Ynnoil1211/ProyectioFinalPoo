package modelo;

// Integrantes: Lionny Lin - 0222510050 & Samuel Campo - 0222510057
// Universidad de Cartagena - POO 2026-1

/**
 * Administrador del sistema Kiosco TransCaribe.
 * Accede con usuario/contraseña independiente de tarjeta.
 * Puede: CRUD de rutas, CRUD de tarjetas, gestión de usuarios.
 *
 * Hereda de Usuario (jerarquía de roles por herencia).
 */
public class Administrador extends Usuario {

    private String dependencia;   // ej: "Operaciones", "TI"

    public Administrador(String nombre, String codigoAcceso,
                         String dependencia) {
        super(nombre, codigoAcceso, "ADMIN");
        this.dependencia = dependencia;
    }

    @Override
    public String getDescripcionRol() {
        return "Administrador (" + dependencia + ") — acceso total al sistema.";
    }

    @Override
    public String toCSV() {
        // Formato: ROL;Nombre;CodigoAcceso;Dependencia
        return "ADMIN;" + getNombre() + ";" + getCodigoAcceso()
                + ";" + dependencia;
    }

    public String getDependencia()               { return dependencia; }
    public void   setDependencia(String v)       { this.dependencia = v; }
}