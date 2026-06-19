package controlador;

// Integrantes: Lionny Lin - 0222510050 & Samuel Campo - 0222510057
// Universidad de Cartagena - POO 2026-1

import modelo.*;
import excepciones.*;

import java.io.*;
import java.util.*;

/**
 * Gestiona la autenticación y el ciclo de vida de los usuarios del sistema.
 * Opera polimórficamente sobre ArrayList<Usuario> (DIP).
 *
 * SOLID-SRP : solo maneja identidad, autenticación y persistencia de usuarios.
 */
public class GestorUsuarios {

    private ArrayList<Usuario> usuarios;
    private Usuario            sesionActiva;   // null si nadie está logueado

    private static final String ARCHIVO = "data/usuarios.txt";

    public GestorUsuarios() {
        this.usuarios     = new ArrayList<>();
        this.sesionActiva = null;
    }

    // ── Autenticación ───────────────────────────────────────────────────────

    /**
     * Intenta iniciar sesión con las credenciales proporcionadas.
     * Recorre polimórficamente la lista de usuarios.
     *
     * @param identificador nombre de usuario o número de tarjeta
     * @param clave         contraseña o PIN
     * @return el Usuario autenticado
     * @throws CredencialesInvalidasException si no coincide ningún usuario
     */
    public Usuario iniciarSesion(String identificador, String clave)
            throws CredencialesInvalidasException {

        for (Usuario u : usuarios) {
            boolean matchId;
            if (u instanceof UsuarioNormal) {
                // Para usuario normal, el identificador es el número de tarjeta
                matchId = ((UsuarioNormal) u).getTarjeta()
                        .getNumeroTarjeta().equalsIgnoreCase(identificador);
            } else {
                // Para administrador, el identificador es el nombre
                matchId = u.getNombre().equalsIgnoreCase(identificador);
            }
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

    /** @return true si hay una sesión activa y el usuario es ADMIN */
    public boolean esAdmin() {
        return sesionActiva instanceof Administrador;
    }

    // ── CRUD de usuarios (solo ADMIN) ───────────────────────────────────────

    public void agregarUsuario(Usuario u) {
        usuarios.add(u);
        guardarEnArchivo();
    }

    public boolean eliminarUsuario(String nombre) {
        boolean ok = usuarios.removeIf(
                u -> u.getNombre().equalsIgnoreCase(nombre));
        if (ok) guardarEnArchivo();
        return ok;
    }

    // ── Persistencia ────────────────────────────────────────────────────────

    public void cargarDesdeArchivo() {
        usuarios.clear();
        cargarEjemplos();   // siempre hay al menos un admin
        File f = new File(ARCHIVO);
        if (!f.exists()) { guardarEnArchivo(); return; }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) continue;
                String[] p = linea.split(";");
                if (p.length < 4) continue;
                if ("ADMIN".equalsIgnoreCase(p[0])) {
                    // solo cargar admins adicionales; el ejemplo ya está
                } else if ("USUARIO".equalsIgnoreCase(p[0]) && p.length >= 4) {
                    TarjetaUsuario t = new TarjetaUsuario(p[3], p[1], 0);
                    usuarios.add(new UsuarioNormal(p[1], p[2], t));
                }
            }
        } catch (IOException e) {
            System.err.println("Error cargando usuarios: " + e.getMessage());
        }
    }

    public void guardarEnArchivo() {
        new File("data").mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO, false))) {
            pw.println("# usuarios.txt | ROL;Nombre;Clave;Extra");
            for (Usuario u : usuarios) pw.println(u.toCSV());
        } catch (IOException e) {
            System.err.println("Error guardando usuarios: " + e.getMessage());
        }
    }

    private void cargarEjemplos() {
        usuarios.add(new Administrador("admin", "admin123", "Operaciones"));
        usuarios.add(new UsuarioNormal("Usuario","Usuario123", new TarjetaUsuario("TC-0003", "Usuario",  22000)));
    }

    // ── Getters ─────────────────────────────────────────────────────────────

    public ArrayList<Usuario> getUsuarios()   { return usuarios; }
    public Usuario            getSesion()     { return sesionActiva; }
    public boolean            haySesion()     { return sesionActiva != null; }
}