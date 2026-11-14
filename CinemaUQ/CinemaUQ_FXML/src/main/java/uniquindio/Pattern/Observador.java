package uniquindio.Pattern;

/**
 * Observer: recibe eventos de compras y operaciones.
 */
public interface Observador {
    /**
     * Recibe un aviso.
     * @param mensaje evento
     */
    void notificar(String mensaje);
}
