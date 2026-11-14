package uniquindio.Model;

import java.io.Serializable;
import java.util.*;

/**
 * Monedero virtual interno; no conecta con bancos reales.
 */
public class TarjetaVirtual implements Serializable {
    private long saldo;
    private boolean activa=true;
    private final List<Movimiento> movimientos=new ArrayList<>();
    /**
     * @return saldo actual
     */
    public long getSaldo() { return saldo; }
    /**
     * @return si la tarjeta está activa
     */
    public boolean isActiva() { return activa; }
    /**
     * Activa o desactiva la tarjeta.
     */
    public void alternar() { activa=!activa; }
    /**
     * @return movimientos
     */
    public List<Movimiento> getMovimientos() { return Collections.unmodifiableList(movimientos); }
    /**
     * Cobra una compra.
     * @param valor importe
     * @param referencia detalle
     */
    public void debitar(long valor,String referencia) {
        if(!activa || valor>saldo || valor<0) throw new ExcepcionCine("Tarjeta inactiva o saldo insuficiente.");
        saldo-=valor; movimientos.add(new Movimiento("COMPRA",-valor,saldo,referencia));
    }
    /**
     * Recarga autorizada por el administrador.
     * @param valor importe
     */
    public void recargar(long valor) {
        if(valor<=0) throw new ExcepcionCine("La recarga debe ser positiva.");
        saldo+=valor; movimientos.add(new Movimiento("RECARGA",valor,saldo,"Recarga administrativa"));
    }
    /**
     * Devuelve dinero por una compra cancelada.
     * @param valor importe
     * @param referencia detalle
     */
    public void reembolsar(long valor,String referencia) {
        if(valor<0) throw new ExcepcionCine("Reembolso inválido.");
        saldo+=valor; movimientos.add(new Movimiento("REEMBOLSO",valor,saldo,referencia));
    }
}
