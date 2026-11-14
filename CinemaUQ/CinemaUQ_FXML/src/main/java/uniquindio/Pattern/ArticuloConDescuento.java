package uniquindio.Pattern;

import uniquindio.Model.Articulo;

/**
 * Decorator: modifica el precio sin alterar el artículo original.
 */
public class ArticuloConDescuento implements Articulo {
    private final Articulo base;
    private final int porcentaje;
    /**
     * Construye el decorador.
     * @param base artículo
     * @param porcentaje descuento
     */
    public ArticuloConDescuento(Articulo base,int porcentaje) { this.base=base;this.porcentaje=porcentaje; }
    /**
     * @return id
     */
    public int getId() { return base.getId(); }
    /**
     * @return nombre
     */
    public String getNombre() { return base.getNombre(); }
    /**
     * @return precio rebajado
     */
    public long getPrecio() { return base.getPrecio()*(100-porcentaje)/100; }
    /**
     * @return inventario del artículo original
     */
    public int getStock() { return base.getStock(); }
}
