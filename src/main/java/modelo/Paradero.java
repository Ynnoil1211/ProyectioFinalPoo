package modelo;

// Integrantes: Lionny Lin - 0222510050 & Samuel Campo - 0222510057
// Universidad de Cartagena - POO 2026-1

// Estacion física del sistema, nodo del grafo de rutas.
public class Paradero {

    private String id;      // clave única, ej: "CHAMBACU"
    private String nombre;
    private String barrio;

    public Paradero(String id, String nombre, String barrio) {
        this.id = id;
        this.nombre = nombre;
        this.barrio = barrio;
    }

    public String getId()                  { return id; }
    public String getNombre()              { return nombre; }
    public void setNombre(String v)      { this.nombre = v; }
    public String getBarrio()              { return barrio; }
    public void setBarrio(String v)      { this.barrio = v; }

    public String toCSV() {
        return id + ";" + nombre + ";" + barrio;
    }

    @Override
    public String toString() {
        return nombre + " (" + barrio + ")";
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Paradero)) return false;
        return id.equalsIgnoreCase(((Paradero) o).id);
    }
    @Override
    public int hashCode() {
        return id.toUpperCase().hashCode();
    }
}