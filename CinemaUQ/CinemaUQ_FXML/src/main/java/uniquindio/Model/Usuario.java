package uniquindio.Model;

import java.io.Serializable;

/**
 * Persona que accede a CinemaUQ.
 */
public abstract class Usuario implements Serializable {
    private final int id;
    private String nombre;
    private final String correo;
    private final String clave;

    /**
     * Construye un usuario.
     * @param id identificador
     * @param nombre nombre
     * @param correo correo
     * @param clave contraseña de demostración
     */
    protected Usuario(int id, String nombre, String correo, String clave) {
        this.id = id; this.nombre = nombre; this.correo = correo.toLowerCase().trim(); this.clave = clave;
    }
    /**
     * @return identificador
     */
    public int getId() { return id; }
    /**
     * @return nombre
     */
    public String getNombre() { return nombre; }
    /**
     * @param nombre nuevo nombre
     */
    public void setNombre(String nombre) { this.nombre = nombre; }
    /**
     * @return correo
     */
    public String getCorreo() { return correo; }
    /**
     * @param clave contraseña que se verifica
     * @return true si coincide
     */
    public boolean validarClave(String clave) { return this.clave.equals(clave); }
    /**
     * @return nombre legible
     */
    @Override public String toString() { return nombre + " (" + correo + ")"; }
}
