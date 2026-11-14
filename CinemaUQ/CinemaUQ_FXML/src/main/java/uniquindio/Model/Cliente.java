package uniquindio.Model;

/**
 * Cliente con tarjeta virtual y puntos de fidelidad.
 */
public class Cliente extends Usuario {
    private final TarjetaVirtual tarjeta = new TarjetaVirtual();
    private final CuentaPuntos puntos = new CuentaPuntos();
    /**
     * Construye un cliente.
     * @param id identificador
     * @param nombre nombre
     * @param correo correo
     * @param clave contraseña
     */
    public Cliente(int id, String nombre, String correo, String clave) { super(id, nombre, correo, clave); }
    /**
     * @return tarjeta
     */
    public TarjetaVirtual getTarjeta() { return tarjeta; }
    /**
     * @return cuenta de puntos
     */
    public CuentaPuntos getPuntos() { return puntos; }
}
