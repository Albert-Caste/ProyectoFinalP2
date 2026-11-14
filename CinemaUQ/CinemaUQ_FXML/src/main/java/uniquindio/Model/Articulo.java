package uniquindio.Model;

import java.io.Serializable;

/**
 * Componente vendible: producto individual o combo (Composite).
 */
public interface Articulo extends Serializable {
    /**
     * @return identificador
     */
    int getId();
    /**
     * @return nombre
     */
    String getNombre();
    /**
     * @return precio
     */
    long getPrecio();
    /**
     * @return unidades disponibles
     */
    int getStock();
}
