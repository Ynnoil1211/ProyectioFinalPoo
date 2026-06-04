package excepciones;

// Integrantes: [Nombre 1] - [Nombre 2]
// Universidad de Cartagena - POO 2026-1

/**
 * Se lanza cuando la hora consultada está fuera del rango operativo
 * total del sistema TransCaribe (antes de las 04:30 o después de las 23:00).
 */
public class FueraDeServicioSistemaException extends Exception {

    private final int horaConsultada;

    public FueraDeServicioSistemaException(int hora) {
        super("El sistema TransCaribe no opera a las " + hora + ":00 h. "
                + "El servicio funciona de 04:30 a 23:00. "
                + "Intente consultar en el horario de operacion.");
        this.horaConsultada = hora;
    }

    public int getHoraConsultada() { return horaConsultada; }
}