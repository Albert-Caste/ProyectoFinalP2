package uniquindio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uniquindio.Controller.CineController;
import uniquindio.Model.Cliente;
import uniquindio.ViewController.*;

/**
 * Aplicación JavaFX: carga vistas FXML y comparte el controlador de dominio.
 */
public class App extends Application {
    private Stage escenario;
    private final CineController cine=new CineController();
    /**
     * Inicializa la ventana.
     * @param stage ventana principal
     */
    @Override public void start(Stage stage) {
        escenario=stage;escenario.setTitle("CinemaUQ · Gestión de cine");mostrarAcceso();
    }
    /**
     * @return controlador de negocio
     */
    public CineController getCine() { return cine; }
    /**
     * Abre el acceso.
     */
    public void mostrarAcceso() { abrir("Acceso.fxml",null); }
    /**
     * Abre el panel de cliente.
     * @param cliente usuario actual
     */
    public void mostrarCliente(Cliente cliente) { abrir("Cliente.fxml",cliente); }
    /**
     * Abre el panel administrador.
     */
    public void mostrarAdministrador() { abrir("Administrador.fxml",null); }
    /**
     * Carga un FXML y conecta su ViewController.
     * @param archivo FXML
     * @param cliente cliente cuando corresponda
     */
    private void abrir(String archivo,Cliente cliente) {
        try {
            FXMLLoader loader=new FXMLLoader(App.class.getResource("/uniquindio/"+archivo));
            Parent root=loader.load();
            Object vc=loader.getController();
            if(vc instanceof AccesoViewController a) a.setApp(this);
            if(vc instanceof ClienteViewController c) c.setApp(this,cliente);
            if(vc instanceof AdministradorViewController a) a.setApp(this);
            Scene escena=new Scene(root,1100,760);
            escena.getStylesheets().add(App.class.getResource("/uniquindio/cinema.css").toExternalForm());
            escenario.setScene(escena);escenario.setMinWidth(930);escenario.setMinHeight(660);escenario.show();
        } catch(Exception e) { e.printStackTrace();throw new IllegalStateException("No se pudo cargar "+archivo,e); }
    }
    /**
     * Punto de entrada.
     * @param args argumentos
     */
    public static void main(String[] args) { launch(args); }
}
