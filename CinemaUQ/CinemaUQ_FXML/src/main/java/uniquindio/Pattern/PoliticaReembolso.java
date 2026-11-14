package uniquindio.Pattern;

import uniquindio.Model.Compra;
import uniquindio.Model.Funcion;

/**
 * Strategy: define cuánto dinero se devuelve.
 */
public interface PoliticaReembolso {
    /**
     * @param compra compra
     * @param funcion función
     * @return valor reembolsable, o -1 si no se permite
     */
    long calcular(Compra compra,Funcion funcion);
}
