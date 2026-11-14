package uniquindio.Model;

import java.io.Serializable;
import java.util.*;

/**
 * Sala con asientos habilitados, bloqueados y accesibles.
 */
public class Sala implements Serializable {
    private final int id, filas, columnas;
    private final String nombre;
    private final Set<String> bloqueados = new HashSet<>();
    /**
     * Construye una sala.
     * @param id id
     * @param nombre nombre
     * @param filas filas (1-8)
     * @param columnas columnas (1-12)
     */
    public Sala(int id,String nombre,int filas,int columnas) { this.id=id; this.nombre=nombre; this.filas=filas; this.columnas=columnas; }
    /**
     * @return id
     */
    public int getId() { return id; }
    /**
     * @return nombre
     */
    public String getNombre() { return nombre; }
    /**
     * @return filas
     */
    public int getFilas() { return filas; }
    /**
     * @return columnas
     */
    public int getColumnas() { return columnas; }
    /**
     * @return número total de asientos
     */
    public int getCapacidad() { return filas*columnas; }
    /**
     * @param asiento código, por ejemplo A1
     * @return si existe
     */
    public boolean existe(String asiento) {
        if (asiento==null || !asiento.matches("[A-H]([1-9]|1[0-2])")) return false;
        int fila=asiento.charAt(0)-'A';
        return fila<filas && Integer.parseInt(asiento.substring(1))<=columnas;
    }
    /**
     * @param asiento código
     * @return si está bloqueado
     */
    public boolean estaBloqueado(String asiento) { return bloqueados.contains(asiento); }
    /**
     * Activa o desactiva un asiento.
     * @param asiento código
     */
    public void alternarBloqueo(String asiento) {
        if(!existe(asiento)) throw new ExcepcionCine("Asiento inexistente.");
        if(!bloqueados.add(asiento)) bloqueados.remove(asiento);
    }
    /**
     * @param asiento código
     * @return si está identificado como accesible
     */
    public boolean esAccesible(String asiento) { return filas>=2 && asiento.equals((char)('A'+filas-2)+"1"); }
    /**
     * @return nombre legible
     */
    @Override public String toString() { return nombre+" ("+filas+"x"+columnas+")"; }
}
