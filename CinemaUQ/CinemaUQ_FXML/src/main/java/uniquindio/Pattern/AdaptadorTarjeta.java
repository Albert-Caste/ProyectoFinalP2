package uniquindio.Pattern;

import uniquindio.Model.Cliente;

/**
 * Adapter: adapta la tarjeta virtual a la interfaz de pagos.
 */
public class AdaptadorTarjeta implements Pago {
    /** Crea el adaptador de tarjeta virtual. */
    public AdaptadorTarjeta() { }
    /**
     * Ejecuta el cargo en el monedero.
     * @param cliente cliente
     * @param importe importe
     * @param referencia detalle
     */
    public void cobrar(Cliente cliente,long importe,String referencia) { cliente.getTarjeta().debitar(importe,referencia); }
}
