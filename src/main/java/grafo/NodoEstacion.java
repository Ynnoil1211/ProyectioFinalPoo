package grafo;

// Integrantes: Lionny Lin - 0222510050 & Samuel Campo - 0222510057
// Universidad de Cartagena - POO 2026-1

import java.util.ArrayList;
import java.util.List;

/**
 * Nodo del grafo de rutas, que representa una estación o paradero de la red TransCaribe.
 */
public class NodoEstacion {

    private String idEstacion;
    private String nombreEstacion;
    private List<AristaRuta> conexiones;

    public NodoEstacion(String idEstacion, String nombreEstacion) {
        this.idEstacion = idEstacion;
        this.nombreEstacion = nombreEstacion;
        this.conexiones = new ArrayList<>();
    }

    public void agregarConexion(AristaRuta arista) {
        conexiones.add(arista);
    }

    public String getIdEstacion()     { return idEstacion; }
    public String getNombreEstacion() { return nombreEstacion; }
    public List<AristaRuta> getConexiones()     { return conexiones; }

    @Override
    public String toString() {
        return idEstacion + " (" + nombreEstacion + ")";
    }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NodoEstacion)) return false;
        return idEstacion.equalsIgnoreCase(((NodoEstacion) o).idEstacion);
    }
    @Override
    public int hashCode() {
        return idEstacion.toUpperCase().hashCode();
    }
}