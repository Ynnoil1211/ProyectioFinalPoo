package modelo;

// Integrantes: Lionny Lin - 0222510050 & Samuel Campo - 0222510057
// Universidad de Cartagena - POO 2026-1

/**
 * Administrador:
 * Accede con usuario/contraseña independiente de tarjeta.
 * Puede: CRUD de rutas, CRUD de tarjetas
 */
/**
 * JUSTIFICACION DE NO EXTENDER SERIALIZABLE:
 * Dado que los datos se manejan desde un archivo de texto plano (.txt), decidimos manejar la lectura y salida de
 *  datos mediante clases estándar de entrada/salida de texto de Java (PrintWriter, FileWriter, BufferedReader, FileReader)
 *  para escribir esas líneas resultantes en los archivos .txt, o para leerlas y reconstruir los objetos separando las cadenas con .split(";").
 * Esto, con el fin de manejar la Legibilidad y Depuración: Si hay un error en los datos de los usuarios o las tarjetas,
 *  pueden abrir usuarios.txt y ver o editar los datos directamente con cualquier editor de texto.
 */

public class Administrador {
    private String id;
    private String nombre;
    private String password;

    public Administrador(String id, String nombre, String password) {
        this.id = id;
        this.nombre = nombre;
        this.password = password;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    //LOGGING
    public boolean autenticar(String codigoIngresado) {
        return this.password.equals(codigoIngresado);
    }

    /**
     * Serializa el administrador a CSV para usuarios.txt
     */
    public String toCSV() {
        // Formato: ID;Nombre;Password
        return id + ";" + nombre + ";" + password;
    }

    @Override
    public String toString() {
        return "Administrador{" + "id='" + id + '\'' + ", nombre='" + nombre + '\'' + '}';
    }
}