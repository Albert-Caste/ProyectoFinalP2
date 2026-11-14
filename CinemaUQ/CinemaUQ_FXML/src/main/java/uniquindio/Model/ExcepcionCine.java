package uniquindio.Model;

/**
 * Error de una regla de negocio.
 */
public class ExcepcionCine extends RuntimeException {
    /**
     * Construye el error.
     * @param mensaje detalle
     */
    public ExcepcionCine(String mensaje) { super(mensaje); }
}
