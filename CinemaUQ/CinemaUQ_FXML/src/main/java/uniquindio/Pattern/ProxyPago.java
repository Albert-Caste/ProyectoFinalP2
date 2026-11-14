package uniquindio.Pattern;

import uniquindio.Model.*;

/**
 * Proxy: valida saldo y estado antes de delegar el cobro.
 */
public class ProxyPago implements Pago {
    private final Pago real;
    /**
     * Construye el proxy.
     * @param real procesador de pago
     */
    public ProxyPago(Pago real) { this.real=real; }
    /**
     * Verifica y cobra.
     * @param cliente titular
     * @param importe importe
     * @param referencia detalle
     */
    public void cobrar(Cliente cliente,long importe,String referencia) {
        if(!cliente.getTarjeta().isActiva()) throw new ExcepcionCine("La tarjeta está inactiva.");
        if(cliente.getTarjeta().getSaldo()<importe) throw new ExcepcionCine("Saldo insuficiente. Solicita una recarga al administrador.");
        real.cobrar(cliente,importe,referencia);
    }
}
