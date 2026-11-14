package uniquindio.ViewController;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

/**
 * Utilidades sencillas para mensajes y validaciones de las vistas.
 */
public final class Ui {
    /**
     * Evita instancias.
     */
    private Ui() { }
    /**
     * Muestra información.
     * @param texto mensaje
     */
    public static void info(String texto) { mensaje(Alert.AlertType.INFORMATION,"CinemaUQ",texto); }
    /**
     * Muestra un error.
     * @param error excepción
     */
    public static void error(Exception error) { mensaje(Alert.AlertType.ERROR,"Revisa los datos",error.getMessage()==null?"Error inesperado.":error.getMessage()); }
    /**
     * Pregunta antes de ejecutar una operación.
     * @param texto pregunta
     * @return aceptación
     */
    public static boolean confirmar(String texto) {
        Optional<ButtonType> respuesta=new Alert(Alert.AlertType.CONFIRMATION,texto,ButtonType.YES,ButtonType.NO).showAndWait();
        return respuesta.isPresent() && respuesta.get()==ButtonType.YES;
    }
    /**
     * Convierte texto a entero.
     * @param texto texto
     * @return entero
     */
    public static int entero(String texto) { return Integer.parseInt(texto.trim()); }
    /**
     * Convierte texto a dinero entero.
     * @param texto texto
     * @return monto
     */
    public static long dinero(String texto) { return Long.parseLong(texto.trim()); }
    /**
     * Crea un cuadro de diálogo.
     * @param tipo tipo
     * @param titulo título
     * @param texto mensaje
     */
    private static void mensaje(Alert.AlertType tipo,String titulo,String texto) { Alert a=new Alert(tipo,texto,ButtonType.OK);a.setTitle(titulo);a.setHeaderText(null);a.showAndWait(); }
}
