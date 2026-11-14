package uniquindio.Model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

/**
 * Puntos con vencimiento anual y consumo por vencimiento próximo.
 */
public class CuentaPuntos implements Serializable {
    /** Crea una cuenta de puntos vacía. */
    public CuentaPuntos() { }
    /**
     * Lote de puntos con origen y vencimiento.
     */
    public static class Lote implements Serializable {
        private int cantidad;
        private final LocalDate vence;
        private final int compraId;
        /**
     * Crea un lote.
     * @param cantidad puntos
     * @param vence vencimiento
     * @param compraId compra origen
     */
        public Lote(int cantidad,LocalDate vence,int compraId) { this.cantidad=cantidad;this.vence=vence;this.compraId=compraId; }
        /**
     * @return puntos disponibles
     */
        public int getCantidad() { return cantidad; }
        /**
     * @return vencimiento
     */
        public LocalDate getVence() { return vence; }
        /**
     * @return compra que originó el lote
     */
        public int getCompraId() { return compraId; }
        /**
     * @return descripción
     */
        @Override public String toString() { return cantidad+" pts · vence "+vence; }
    }
    private final List<Lote> lotes=new ArrayList<>();
    private int deuda, acumulados, redimidos;
    /**
     * @return puntos vigentes disponibles
     */
    public int getSaldo() { return Math.max(0,lotes.stream().filter(l->!l.vence.isBefore(LocalDate.now())).mapToInt(l->l.cantidad).sum()-deuda); }
    /**
     * @return puntos acumulados históricos
     */
    public int getAcumulados() { return acumulados; }
    /**
     * @return puntos redimidos históricos
     */
    public int getRedimidos() { return redimidos; }
    /**
     * @return lotes de puntos vigentes
     */
    public List<Lote> getLotes() { return lotes.stream().filter(l->l.cantidad>0&&!l.vence.isBefore(LocalDate.now())).sorted(Comparator.comparing(Lote::getVence)).toList(); }
    /**
     * Suma puntos de una compra y compensa deudas por anulaciones previas.
     * @param puntos cantidad
     * @param compraId origen
     */
    public void acumular(int puntos,int compraId) {
        if(puntos<=0) return;
        acumulados+=puntos;
        int pagoDeuda=Math.min(puntos,deuda);deuda-=pagoDeuda;
        if(puntos>pagoDeuda) lotes.add(new Lote(puntos-pagoDeuda,LocalDate.now().plusYears(1),compraId));
    }
    /**
     * Usa primero los lotes más próximos a vencer.
     * @param puntos cantidad
     * @return fragmentos usados para posibles devoluciones
     */
    public List<Lote> redimir(int puntos) {
        if(puntos<0 || getSaldo()<puntos) throw new ExcepcionCine("Puntos insuficientes o vencidos.");
        List<Lote> usados=new ArrayList<>(); int faltan=puntos;
        for(Lote lote:getLotes()) {
            int uso=Math.min(faltan,lote.cantidad);
            if(uso>0) { lote.cantidad-=uso; usados.add(new Lote(uso,lote.vence,lote.compraId));faltan-=uso; }
            if(faltan==0) break;
        }
        redimidos+=puntos;
        return usados;
    }
    /**
     * Devuelve puntos redimidos, sin ampliar su vencimiento original.
     * @param usados lotes consumidos
     */
    public void devolver(List<Lote> usados) {
        for(Lote l:usados) if(!l.vence.isBefore(LocalDate.now())) lotes.add(new Lote(l.cantidad,l.vence,l.compraId));
    }
    /**
     * Revoca puntos generados por una compra cancelada.
     * @param compraId compra
     * @param puntos puntos originalmente ganados
     */
    public void revocar(int compraId,int puntos) {
        int retirados=0;
        for(Lote l:lotes) if(l.compraId==compraId) { retirados+=l.cantidad; l.cantidad=0; }
        deuda+=Math.max(0,puntos-retirados);
    }
}
