package uniquindio.Model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Movimiento de una tarjeta virtual.
 */
public class Movimiento implements Serializable {
    private final LocalDateTime fecha=LocalDateTime.now();
    private final String tipo, detalle;
    private final long valor, saldo;
    /**
     * Registra un movimiento.
     * @param tipo operación
     * @param valor importe
     * @param saldo saldo resultante
     * @param detalle detalle
     */
    public Movimiento(String tipo,long valor,long saldo,String detalle) { this.tipo=tipo;this.valor=valor;this.saldo=saldo;this.detalle=detalle; }
    /**
     * @return fecha
     */
    public LocalDateTime getFecha() { return fecha; }
    /**
     * @return tipo
     */
    public String getTipo() { return tipo; }
    /**
     * @return valor
     */
    public long getValor() { return valor; }
    /**
     * @return texto legible
     */
    @Override public String toString() { return fecha.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))+" · "+tipo+" · $"+valor+" · saldo $"+saldo+" · "+detalle; }
}
