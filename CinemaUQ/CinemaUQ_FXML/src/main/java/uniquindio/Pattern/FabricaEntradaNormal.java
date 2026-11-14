package uniquindio.Pattern;

import uniquindio.Model.*;

/**
 * Factory Method concreto para entradas estándar.
 */
public class FabricaEntradaNormal extends FabricaEntrada {
    /**
     * Crea una entrada estándar.
     * @param funcion función
     * @param asiento asiento
     * @param precio importe
     * @return entrada
     */
    public Entrada crear(Funcion funcion,String asiento,long precio) { return new Entrada(funcion.getId(),asiento,precio); }
}
