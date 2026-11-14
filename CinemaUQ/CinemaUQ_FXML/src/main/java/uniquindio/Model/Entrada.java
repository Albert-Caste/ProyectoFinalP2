package uniquindio.Model;

import java.io.Serializable;

/**
 * Entrada para un asiento y una función.
 */
public class Entrada implements Serializable {
    private final int funcionId;
    private final String asiento;
    private final long precio;
    /**
     * Construye una entrada.
     * @param funcionId función
     * @param asiento asiento
     * @param precio importe
     */
    public Entrada(int funcionId,String asiento,long precio) { this.funcionId=funcionId;this.asiento=asiento;this.precio=precio; }
    /**
     * @return función
     */
    public int getFuncionId() { return funcionId; }
    /**
     * @return asiento
     */
    public String getAsiento() { return asiento; }
    /**
     * @return precio
     */
    public long getPrecio() { return precio; }
}
