package uniquindio.Pattern;

import java.util.*;

/**
 * Sujeto observable para notificaciones internas.
 */
public class CentroAvisos {
    private final List<Observador> observadores=new ArrayList<>();
    /** Crea el centro de notificaciones. */
    public CentroAvisos() { }
    /**
     * Suscribe un observador.
     * @param observador destinatario
     */
    public void suscribir(Observador observador) { observadores.add(observador); }
    /**
     * Difunde un evento.
     * @param mensaje evento
     */
    public void publicar(String mensaje) { observadores.forEach(o->o.notificar(mensaje)); }
}
