package uniquindio.Pattern;

import uniquindio.Model.*;

/**
 * Strategy: reembolso completo por cancelación del cine.
 */
public class ReembolsoTotal implements PoliticaReembolso {
    /**
     * @param compra compra
     * @param funcion función
     * @return pago completo
     */
    public long calcular(Compra compra,Funcion funcion) { return compra.getPagado(); }
}
