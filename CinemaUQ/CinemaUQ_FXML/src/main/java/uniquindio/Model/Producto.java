package uniquindio.Model;

/**
 * Producto individual de confitería.
 */
public class Producto implements Articulo {
    private final int id;
    private String nombre;
    private long precio;
    private int stock;
    /**
     * Crea un producto.
     * @param id id
     * @param nombre nombre
     * @param precio precio
     * @param stock existencias
     */
    public Producto(int id,String nombre,long precio,int stock) { this.id=id;this.nombre=nombre;this.precio=precio;this.stock=stock; }
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
     * @return existencias
     */
    public int getStock() { return stock; }
    /**
     * Actualiza el producto.
     * @param nombre nombre
     * @param precio precio
     * @param stock existencias
     */
    public void actualizar(String nombre,long precio,int stock) { this.nombre=nombre;this.precio=precio;this.stock=stock; }
    /**
     * Reduce inventario.
     * @param cantidad unidades
     */
    public void descontar(int cantidad) { if(cantidad>stock) throw new ExcepcionCine("Stock insuficiente de "+nombre);stock-=cantidad; }
    /**
     * Devuelve unidades al inventario.
     * @param cantidad unidades
     */
    public void reponer(int cantidad) { stock+=cantidad; }
    /**
     * @return descripción
     */
    @Override public String toString() { return nombre+" · $"+precio+" · stock "+stock; }
}
