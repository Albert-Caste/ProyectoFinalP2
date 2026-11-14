package uniquindio.Pattern;

import uniquindio.Model.Cliente;

/**
 * Command: recarga una tarjeta virtual.
 */
public class ComandoRecarga implements Comando {
    private final Cliente cliente;
    private final long valor;
    /**
     * Construye la orden.
     * @param cliente destinatario
     * @param valor importe
     */
    public ComandoRecarga(Cliente cliente,long valor) { this.cliente=cliente;this.valor=valor; }
    /**
     * Ejecuta la recarga.
     */
    public void ejecutar() { cliente.getTarjeta().recargar(valor); }
}
