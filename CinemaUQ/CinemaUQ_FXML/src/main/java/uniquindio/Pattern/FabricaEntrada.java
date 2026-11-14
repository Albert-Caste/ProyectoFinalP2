package uniquindio.Pattern;

import uniquindio.Model.*;

/**
 * Factory Method: contrato de fabricación de entradas.
 */
public abstract class FabricaEntrada {
    /**
     * Crea una entrada.
     * @param funcion función
     * @param asiento asiento
     * @param precio precio
     * @return entrada
     */
    public abstract Entrada crear(Funcion funcion,String asiento,long precio);
}
