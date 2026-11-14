package uniquindio.Model;

import java.io.*;
import java.time.*;
import java.util.*;

/**
 * Almacén único de datos (Singleton), persistido en un archivo local.
 */
public class Cinema implements Serializable {
    private static transient Cinema instancia;
    private static final String ARCHIVO="cinemauq-datos.bin";
    private int secuencia=1;
    private final Map<Integer,Usuario> usuarios=new LinkedHashMap<>();
    private final Map<Integer,Pelicula> peliculas=new LinkedHashMap<>();
    private final Map<Integer,Sala> salas=new LinkedHashMap<>();
    private final Map<Integer,Funcion> funciones=new LinkedHashMap<>();
    private final Map<Integer,Articulo> articulos=new LinkedHashMap<>();
    private final Map<Integer,Promocion> promociones=new LinkedHashMap<>();
    private final List<Compra> compras=new ArrayList<>();
    private final List<String> solicitudes=new ArrayList<>();
    /**
     * Impide instanciar desde fuera.
     */
    private Cinema() { }
    /**
     * @return instancia única, recuperada del disco si existe
     */
    public static synchronized Cinema getInstancia() {
        if(instancia==null) {
            try(ObjectInputStream in=new ObjectInputStream(new FileInputStream(ARCHIVO))) {
                instancia=(Cinema)in.readObject();
            } catch(Exception ignored) {
                instancia=new Cinema(); instancia.inicializar(); instancia.guardar();
            }
        }
        return instancia;
    }
    /**
     * @return identificador único
     */
    public int nuevoId() { return secuencia++; }
    /**
     * @return usuarios
     */
    public Collection<Usuario> getUsuarios() { return usuarios.values(); }
    /**
     * @return películas
     */
    public Collection<Pelicula> getPeliculas() { return peliculas.values(); }
    /**
     * @return salas
     */
    public Collection<Sala> getSalas() { return salas.values(); }
    /**
     * @return funciones
     */
    public Collection<Funcion> getFunciones() { return funciones.values(); }
    /**
     * @return artículos
     */
    public Collection<Articulo> getArticulos() { return articulos.values(); }
    /**
     * @return promociones
     */
    public Collection<Promocion> getPromociones() { return promociones.values(); }
    /**
     * @return compras
     */
    public List<Compra> getCompras() { return compras; }
    /**
     * @return solicitudes de recarga
     */
    public List<String> getSolicitudes() { return solicitudes; }
    /**
     * @param id id
     * @return película
     */
    public Pelicula pelicula(int id) { return peliculas.get(id); }
    /**
     * @param id id
     * @return sala
     */
    public Sala sala(int id) { return salas.get(id); }
    /**
     * @param id id
     * @return función
     */
    public Funcion funcion(int id) { return funciones.get(id); }
    /**
     * @param id id
     * @return artículo
     */
    public Articulo articulo(int id) { return articulos.get(id); }
    /**
     * @param id id
     * @return usuario
     */
    public Usuario usuario(int id) { return usuarios.get(id); }
    /**
     * Agrega usuario.
     * @param usuario usuario
     */
    public void agregar(Usuario usuario) { usuarios.put(usuario.getId(),usuario); }
    /**
     * Agrega película.
     * @param pelicula película
     */
    public void agregar(Pelicula pelicula) { peliculas.put(pelicula.getId(),pelicula); }
    /**
     * Agrega sala.
     * @param sala sala
     */
    public void agregar(Sala sala) { salas.put(sala.getId(),sala); }
    /**
     * Agrega función.
     * @param funcion función
     */
    public void agregar(Funcion funcion) { funciones.put(funcion.getId(),funcion); }
    /**
     * Agrega artículo.
     * @param articulo artículo
     */
    public void agregar(Articulo articulo) { articulos.put(articulo.getId(),articulo); }
    /**
     * Agrega promoción.
     * @param promocion promoción
     */
    public void agregar(Promocion promocion) { promociones.put(promocion.getId(),promocion); }
    /**
     * Persiste todos los datos.
     */
    public void guardar() {
        try(ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(ARCHIVO))) { out.writeObject(this); }
        catch(IOException e) { throw new ExcepcionCine("No se pudieron guardar los datos: "+e.getMessage()); }
    }
    /**
     * Crea datos suficientes para probar el sistema.
     */
    private void inicializar() {
        agregar(new Administrador(nuevoId(),"Administrador","admin@cinemauq.com","Admin123"));
        Cliente demo=new Cliente(nuevoId(),"Cliente Demo","cliente@cinemauq.com","Cliente123");
        demo.getTarjeta().recargar(150000);demo.getPuntos().acumular(450,0);agregar(demo);
        Pelicula p1=new Pelicula(nuevoId(),"Beyond Horizons","Ciencia ficción",118);
        Pelicula p2=new Pelicula(nuevoId(),"El Último Viaje","Aventura",105);
        agregar(p1);agregar(p2);
        Sala s1=new Sala(nuevoId(),"Sala 1",8,12),s2=new Sala(nuevoId(),"Sala 2",6,10);
        agregar(s1);agregar(s2);
        agregar(new Funcion(nuevoId(),p1.getId(),s1.getId(),LocalDate.now().plusDays(2).atTime(19,15),16000));
        agregar(new Funcion(nuevoId(),p2.getId(),s2.getId(),LocalDate.now().plusDays(3).atTime(17,30),14000));
        agregar(new Funcion(nuevoId(),p1.getId(),s2.getId(),LocalDate.now().plusDays(4).atTime(20,0),16000));
        Producto maiz=new Producto(nuevoId(),"Crispetas medianas",12000,100);
        Producto gaseosa=new Producto(nuevoId(),"Gaseosa",7000,120);
        Producto nachos=new Producto(nuevoId(),"Nachos",11000,80);
        agregar(maiz);agregar(gaseosa);agregar(nachos);
        agregar(new Combo(nuevoId(),"Crispetas + gaseosa",17000,List.of(maiz,gaseosa)));
    }
}
