package uniquindio.Model;

import java.io.Serializable;

/**
 * Película de la cartelera.
 */
public class Pelicula implements Serializable {
    private final int id;
    private String titulo, genero;
    private int duracion;
    private boolean activa = true;
    /**
     * Crea una película.
     * @param id id
     * @param titulo título
     * @param genero género
     * @param duracion minutos
     */
    public Pelicula(int id, String titulo, String genero, int duracion) { this.id=id; this.titulo=titulo; this.genero=genero; this.duracion=duracion; }
    /**
     * @return id
     */
    public int getId() { return id; }
    /**
     * @return título
     */
    public String getTitulo() { return titulo; }
    /**
     * @return género
     */
    public String getGenero() { return genero; }
    /**
     * @return duración en minutos
     */
    public int getDuracion() { return duracion; }
    /**
     * @return si está activa
     */
    public boolean isActiva() { return activa; }
    /**
     * Actualiza los datos.
     * @param titulo título
     * @param genero género
     * @param duracion minutos
     */
    public void actualizar(String titulo,String genero,int duracion) { this.titulo=titulo; this.genero=genero; this.duracion=duracion; }
    /**
     * Cambia la disponibilidad de la película.
     */
    public void alternar() { activa=!activa; }
    /**
     * @return texto para controles JavaFX
     */
    @Override public String toString() { return titulo+" · "+genero+" · "+duracion+" min"+(activa?"":" [Inactiva]"); }
}
