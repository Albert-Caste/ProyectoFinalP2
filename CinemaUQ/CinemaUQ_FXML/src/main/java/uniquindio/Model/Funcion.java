package uniquindio.Model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Proyección de una película en una sala.
 */
public class Funcion implements Serializable {
    private final int id, peliculaId, salaId;
    private final LocalDateTime fecha;
    private final long precio;
    private boolean cancelada;
    /**
     * Crea la función.
     * @param id id
     * @param peliculaId película
     * @param salaId sala
     * @param fecha fecha/hora
     * @param precio precio por asiento
     */
    public Funcion(int id,int peliculaId,int salaId,LocalDateTime fecha,long precio) { this.id=id;this.peliculaId=peliculaId;this.salaId=salaId;this.fecha=fecha;this.precio=precio; }
    /**
     * @return id
     */
    public int getId() { return id; }
    /**
     * @return película
     */
    public int getPeliculaId() { return peliculaId; }
    /**
     * @return sala
     */
    public int getSalaId() { return salaId; }
    /**
     * @return fecha/hora
     */
    public LocalDateTime getFecha() { return fecha; }
    /**
     * @return precio
     */
    public long getPrecio() { return precio; }
    /**
     * @return cancelada
     */
    public boolean isCancelada() { return cancelada; }
    /**
     * Marca la función como cancelada.
     */
    public void cancelar() { cancelada=true; }
    /**
     * @return texto legible
     */
    @Override public String toString() { return "#"+id+" · "+fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))+" · $"+precio+(cancelada?" [CANCELADA]":""); }
}
