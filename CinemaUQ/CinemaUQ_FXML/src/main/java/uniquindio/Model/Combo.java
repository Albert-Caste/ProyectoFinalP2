package uniquindio.Model;

import java.util.*;

/**
 * Combo compuesto por productos (patrón Composite).
 */
public class Combo implements Articulo {
    private final int id;
    private final String nombre;
    private final long precio;
    private final List<Producto> productos;
    /**
     * Crea un combo.
     * @param id id
     * @param nombre nombre
     * @param precio precio
     * @param productos componentes
     */
    public Combo(int id,String nombre,long precio,List<Producto> productos) { this.id=id;this.nombre=nombre;this.precio=precio;this.productos=new ArrayList<>(productos); }
    /**
     * @return id
     */
    public int getId() { return id; }
    /**
     * @return nombre
     */
    public String getNombre() { return nombre; }
    /**
     * @return precio
     */
    public long getPrecio() { return precio; }
    /**
     * @return productos que contiene
     */
    public List<Producto> getProductos() { return Collections.unmodifiableList(productos); }
    /**
     * @return combos disponibles teniendo en cuenta componentes repetidos
     */
    public int getStock() {
        Map<Producto,Integer> demanda = new HashMap<>();
        for (Producto p:productos) demanda.merge(p,1,Integer::sum);
        return demanda.entrySet().stream().mapToInt(e->e.getKey().getStock()/e.getValue()).min().orElse(0);
    }
    /**
     * @return descripción
     */
    @Override public String toString() { return "COMBO "+nombre+" · $"+precio+" · stock "+getStock(); }
}
