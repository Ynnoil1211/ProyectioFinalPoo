package controlador;

// Integrantes: Lionny Lin - 0222510050 & Samuel Campo - 0222510057
// Universidad de Cartagena - POO 2026-1

import modelo.*;
import excepciones.*;

import java.io.*;
import java.util.*;

//Gestiona los administradores del sistema StandTransCaribe
/**
 * JUSTIFICACION DE NO EXTENDER SERIALIZABLE:
 * Dado que los datos se manejan desde un archivo de texto plano (.txt), decidimos manejar la lectura y salida de
 *  datos mediante clases estándar de entrada/salida de texto de Java (PrintWriter, FileWriter, BufferedReader, FileReader)
 *  para escribir esas líneas resultantes en los archivos .txt, o para leerlas y reconstruir los objetos separando las cadenas con .split(";").
 * Esto, con el fin de manejar la Legibilidad y Depuración: Si hay un error en los datos de los usuarios o las tarjetas,
 *  pueden abrir usuarios.txt y ver o editar los datos directamente con cualquier editor de texto.
 */

public class GestorAdmins {

    private ArrayList<Administrador> admins;
    private Administrador sesionActiva;   // null si nadie está logueado

    private static final String ARCHIVO = "data/usuarios.txt";

    public GestorAdmins() {
        this.admins = new ArrayList<>();
        this.sesionActiva = null;
        cargarDesdeArchivo();
    }

    // ── Autenticación :

    /**
     * @param identificador id del admin
     * @param clave contraseña
     * @return el Admin autenticado
     * @throws CredencialesInvalidasException si no coincide ningún admin
     */
    public Administrador iniciarSesion(String identificador, String clave) throws CredencialesInvalidasException {
        for (Administrador u : admins) {
            boolean matchId= u.getId().equalsIgnoreCase(identificador);
            if (matchId && u.autenticar(clave)) {
                sesionActiva = u;
                return u;
            }
        }
        throw new CredencialesInvalidasException();
    }

    public void cerrarSesion() {
        sesionActiva = null;
    }

    // ── Persistencia :
    public void cargarDesdeArchivo() {
        admins.clear();
        cargarEjemplos();   // siempre hay al menos un admin por defecto

        File f = new File(ARCHIVO);

        try {
            if (!f.exists()) {
                throw new IOException("El archivo de usuarios no existe."); // Lanzamos IOException en caso de fallo en carga de archivo.
            }

            try (BufferedReader br = new BufferedReader(new FileReader(f))) {  // Aqui usamos un Try-With-Resources para cerrar automaticamente el archivo.
                String linea;
                while ((linea = br.readLine()) != null) {
                    linea = linea.trim(); // Eliminamos espacio en blanco
                    if (linea.isEmpty() || linea.startsWith("#")) continue;  // Ignoramos lineas vacias y '#' que indica un comentario
                    String[] p = linea.split(";");
                    if (p.length < 3) continue;   // El parsing se hace usando ';' como separador, en caso de ser menor que 3, se asume que hubo error y se descarta.
                    String ID = p[0].trim();     // Primer dato (ID del Admim)
                    String nombre = p[1].trim();      // Segundo dato (Nombre del Admin)
                    String clave = p[2].trim();  // Tercer dato (Password del Admin)
                    admins.add(new Administrador(ID,nombre, clave)); // Procesar admin y agregarlo a la lista
                }
            }
        } catch (IOException e) {
            System.err.println("Error cargando admins: " + e.getMessage());
            // Como ya cargamos los ejemplos al inicio de la función,
            // si el archivo no existe o falla su lectura, guardamos el archivo con esos ejemplos base.
            guardarEnArchivo();
        }
    }

    public void guardarEnArchivo() {
        new File("data").mkdirs();  //mkdirs() crea la carpeta "data" en caso de que no exista
        // Try with resource para cierre automatico, y FileWriter(ruta, false) para sobreescritura forzosa
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO, false))) {
            pw.println("# usuarios.txt | ID;Nombre;Clave"); // La primera linea
            for (Administrador u : admins)
                pw.println(u.toCSV());
            // Ejemplo de salida esperada: admin2;nombre2;admin123;
        } catch (IOException e) {
            System.err.println("Error guardando admins: " + e.getMessage());
        }
    }
    private void cargarEjemplos() {
        admins.add(new Administrador("admin", "ElAdmin", "admin123"));
    }

    // ── Getters :
    public Administrador haySesion() {
        return sesionActiva;
    }
    public ArrayList<Administrador> getAdmins() {
        return admins;
    }
}