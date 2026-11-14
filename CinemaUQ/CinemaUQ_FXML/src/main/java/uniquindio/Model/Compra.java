package uniquindio.Model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Compra creada con Builder y administrada mediante el patrón State.
 */
public class Compra implements Serializable {
    /**
     * Estado de una compra (State).
     */
    public interface Estado extends Serializable {
        /**
     * @return descripción del estado
     */
        String nombre();
        /**
     * @return si admite cancelación
     */
        boolean permiteCancelar();
    }
    /**
     * Compra pagada.
     */
    public static class Pagada implements Estado {
        /** Crea el estado pagada. */
        public Pagada() { }
        /**
     * @return nombre
     */
        public String nombre() { return "PAGADA"; }
        /**
     * @return true
     */
        public boolean permiteCancelar() { return true; }
    }
    /**
     * Compra cancelada; impide reembolsos repetidos.
     */
    public static class Cancelada implements Estado {
        /** Crea el estado cancelada. */
        public Cancelada() { }
        /**
     * @return nombre
     */
        public String nombre() { return "CANCELADA"; }
        /**
     * @return false
     */
        public boolean permiteCancelar() { return false; }
    }
    /**
     * Línea de confitería.
     */
    public static class Linea implements Serializable {
        private final int articuloId,cantidad;
        private final String nombre;
        private final boolean conPuntos;
        private final long importe;
        /**
     * Crea una línea.
     * @param articuloId id
     * @param nombre nombre
     * @param cantidad unidades
     * @param conPuntos redención
     * @param importe dinero pagado
     */
        public Linea(int articuloId,String nombre,int cantidad,boolean conPuntos,long importe) { this.articuloId=articuloId;this.nombre=nombre;this.cantidad=cantidad;this.conPuntos=conPuntos;this.importe=importe; }
        /**
     * @return id
     */
        public int getArticuloId() { return articuloId; }
        /**
     * @return unidades
     */
        public int getCantidad() { return cantidad; }
        /**
     * @return nombre
     */
        public String getNombre() { return nombre; }
        /**
     * @return importe
     */
        public long getImporte() { return importe; }
        /**
     * @return si se pagó con puntos
     */
        public boolean isConPuntos() { return conPuntos; }
    }
    /**
     * Constructor fluido de compra (Builder).
     */
    public static class Builder {
        private final Compra compra=new Compra();
        /** Inicia la construcción de una compra. */
        public Builder() { }
        /**
     * @param id identificador
     * @return builder
     */
        public Builder id(int id) { compra.id=id;return this; }
        /**
     * @param clienteId cliente
     * @return builder
     */
        public Builder cliente(int clienteId) { compra.clienteId=clienteId;return this; }
        /**
     * @param funcionId función, o 0 para confitería
     * @return builder
     */
        public Builder funcion(int funcionId) { compra.funcionId=funcionId;return this; }
        /**
     * @param entradas entradas compradas
     * @return builder
     */
        public Builder entradas(List<Entrada> entradas) { compra.entradas.addAll(entradas);return this; }
        /**
     * @param lineas productos comprados
     * @return builder
     */
        public Builder productos(List<Linea> lineas) { compra.lineas.addAll(lineas);return this; }
        /**
     * @param importe valor pagado
     * @return builder
     */
        public Builder pagado(long importe) { compra.pagado=importe;return this; }
        /**
     * @param cantidad puntos gastados
     * @return builder
     */
        public Builder puntosUsados(int cantidad) { compra.puntosUsados=cantidad;return this; }
        /**
     * @param cantidad puntos obtenidos
     * @return builder
     */
        public Builder puntosGanados(int cantidad) { compra.puntosGanados=cantidad;return this; }
        /**
     * @param lotes lotes redimidos
     * @return builder
     */
        public Builder lotes(List<CuentaPuntos.Lote> lotes) { compra.lotesUsados.addAll(lotes);return this; }
        /**
     * @return compra construida
     */
        public Compra build() { return compra; }
    }
    private int id,clienteId,funcionId,puntosUsados,puntosGanados;
    private long pagado,reembolso;
    private final LocalDateTime fecha=LocalDateTime.now();
    private final List<Entrada> entradas=new ArrayList<>();
    private final List<Linea> lineas=new ArrayList<>();
    private final List<CuentaPuntos.Lote> lotesUsados=new ArrayList<>();
    private Estado estado=new Pagada();
    /**
     * Oculta el constructor para exigir Builder.
     */
    private Compra() { }
    /**
     * @return id
     */
    public int getId() { return id; }
    /**
     * @return cliente
     */
    public int getClienteId() { return clienteId; }
    /**
     * @return función
     */
    public int getFuncionId() { return funcionId; }
    /**
     * @return fecha
     */
    public LocalDateTime getFecha() { return fecha; }
    /**
     * @return entradas
     */
    public List<Entrada> getEntradas() { return Collections.unmodifiableList(entradas); }
    /**
     * @return líneas
     */
    public List<Linea> getLineas() { return Collections.unmodifiableList(lineas); }
    /**
     * @return importe cobrado
     */
    public long getPagado() { return pagado; }
    /**
     * @return puntos usados
     */
    public int getPuntosUsados() { return puntosUsados; }
    /**
     * @return puntos ganados
     */
    public int getPuntosGanados() { return puntosGanados; }
    /**
     * @return lotes consumidos
     */
    public List<CuentaPuntos.Lote> getLotesUsados() { return Collections.unmodifiableList(lotesUsados); }
    /**
     * @return dinero devuelto
     */
    public long getReembolso() { return reembolso; }
    /**
     * @return estado
     */
    public String getEstado() { return estado.nombre(); }
    /**
     * @return si se puede iniciar una cancelación
     */
    public boolean permiteCancelar() { return estado.permiteCancelar(); }
    /**
     * Transiciona de pagada a cancelada una sola vez.
     * @param valor importe reembolsado
     */
    public void cancelar(long valor) {
        if(!estado.permiteCancelar()) throw new ExcepcionCine("La compra ya fue cancelada.");
        reembolso=valor; estado=new Cancelada();
    }
    /**
     * @return texto para la lista
     */
    @Override public String toString() { return "#"+id+" · "+fecha.toLocalDate()+" · "+entradas.size()+" entradas · $"+pagado+" · "+getEstado(); }
}
