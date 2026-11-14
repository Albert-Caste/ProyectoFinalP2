package uniquindio.ViewController;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import uniquindio.App;
import uniquindio.Model.*;

/**
 * ViewController de acceso y registro.
 */
public class AccesoViewController {
    @FXML private TextField correoLogin,nombreRegistro,correoRegistro;
    @FXML private PasswordField claveLogin,claveRegistro;
    private App app;
    /**
     * Conecta la aplicación.
     * @param app aplicación
     */
    public void setApp(App app) { this.app=app; }
    /**
     * Inicia sesión y dirige al panel correspondiente.
     */
    @FXML private void ingresar() {
        try {
            Usuario u=app.getCine().iniciarSesion(correoLogin.getText(),claveLogin.getText());
            if(u instanceof Cliente c) app.mostrarCliente(c);else app.mostrarAdministrador();
        } catch(Exception e) { Ui.error(e); }
    }
    /**
     * Registra e inicia sesión con un cliente nuevo.
     */
    @FXML private void registrar() {
        try { Cliente c=app.getCine().registrar(nombreRegistro.getText(),correoRegistro.getText(),claveRegistro.getText());app.mostrarCliente(c); }
        catch(Exception e) { Ui.error(e); }
    }
}
