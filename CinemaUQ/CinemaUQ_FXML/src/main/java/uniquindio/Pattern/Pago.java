package uniquindio.Pattern;

import uniquindio.Model.Cliente;

/**
 * Puerto de pagos que puede adaptarse a distintos medios.
 */
public interface Pago {
    /**
     * Cobra el importe.
     * @param cliente titular
     * @param importe monto
     * @param referencia compra
     */
    void cobrar(Cliente cliente,long importe,String referencia);
}
