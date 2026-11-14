package uniquindio.Model;

/**
 * Usuario autorizado para operaciones administrativas.
 */
public class Administrador extends Usuario {
    /**
     * Construye un administrador.
     * @param id identificador
     * @param nombre nombre
     * @param correo correo
     * @param clave contraseña
     */
    public Administrador(int id, String nombre, String correo, String clave) { super(id, nombre, correo, clave); }
}
