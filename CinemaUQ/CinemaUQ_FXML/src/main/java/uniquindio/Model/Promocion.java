package uniquindio.Model;

import java.io.Serializable;

/**
 * Descuento porcentual aplicable a artículos y entradas pagadas.
 */
public class Promocion implements Serializable {
    private final int id;
    private final String nombre;
    private final int porcentaje;
    private boolean activa = true;
    /**
     * Crea la promoción.
     * @param id id
     * @param nombre nombre
     * @param porcentaje descuento de 1 a 80
     */
    public Promocion(int id,String nombre,int porcentaje) { this.id=id;this.nombre=nombre;this.porcentaje=porcentaje; }
    /**
     * @return identificador
     */
    public int getId() { return id; }
    /**
     * @return porcentaje
     */
    public int getPorcentaje() { return porcentaje; }
    /**
     * @return activa
     */
    public boolean isActiva() { return activa; }
    /**
     * Cambia el estado.
     */
    public void alternar() { activa=!activa; }
    /**
     * @return descripción
     */
    @Override public String toString() { return "#"+id+" "+nombre+" ("+porcentaje+"%)"+(activa?" [Activa]":" [Inactiva]"); }
}
