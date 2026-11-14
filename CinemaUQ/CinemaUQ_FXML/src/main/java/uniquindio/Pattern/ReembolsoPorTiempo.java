package uniquindio.Pattern;

import uniquindio.Model.*;
import java.time.*;

/**
 * Strategy: más de 24h 100%; 2-24h 80%; menos de 2h no se permite.
 */
public class ReembolsoPorTiempo implements PoliticaReembolso {
    /**
     * Calcula el valor según la antelación.
     * @param compra compra
     * @param funcion función
     * @return valor o -1
     */
    public long calcular(Compra compra,Funcion funcion) {
        if(funcion==null) return -1;
        long minutos=Duration.between(LocalDateTime.now(),funcion.getFecha()).toMinutes();
        if(minutos<120) return -1;
        return minutos>1440?compra.getPagado():compra.getPagado()*80/100;
    }
}
