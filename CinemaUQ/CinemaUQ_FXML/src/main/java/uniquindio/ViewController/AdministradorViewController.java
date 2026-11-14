package uniquindio.ViewController;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import uniquindio.App;
import uniquindio.Controller.CineController;
import uniquindio.Model.*;

import java.io.File;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * ViewController FXML del administrador: gestión y reportes.
 */
public class AdministradorViewController {
    @FXML private Label ventas,entradas,ocupacion,reembolsos,datosCliente;
    @FXML private ListView<Pelicula> listaPeliculas;
    @FXML private TextField titulo,genero,duracion;
    @FXML private ListView<Sala> listaSalas;
    @FXML private TextField nombreSala,filas,columnas,asiento;
    @FXML private ComboBox<Sala> salaAsiento,salaFuncion;
    @FXML private ComboBox<Pelicula> peliculaFuncion;
    @FXML private DatePicker fechaFuncion;
    @FXML private TextField horaFuncion,precioFuncion;
    @FXML private ListView<Funcion> listaFunciones;
    @FXML private ListView<Articulo> listaArticulos;
    @FXML private TextField nombreProducto,precioProducto,stockProducto,nombreCombo,precioCombo;
    @FXML private ComboBox<Producto> comboPrimero,comboSegundo;
    @FXML private ListView<Promocion> listaPromociones;
    @FXML private TextField nombrePromocion,porcentajePromocion;
    @FXML private ListView<Cliente> listaClientes;
    @FXML private ListView<Compra> listaCompras;
    @FXML private ListView<String> listaSolicitudes;
    @FXML private TextField valorRecarga;
    @FXML private DatePicker fechaDesde,fechaHasta;
    @FXML private TextArea textoReporte;
    @FXML private BarChart<String,Number> barras;
    @FXML private PieChart pastel;
    @FXML private LineChart<String,Number> linea;
    private App app;
    /**
     * Conecta la aplicación y configura los controles.
     * @param app aplicación
     */
    public void setApp(App app) {
        this.app=app;
        fechaDesde.setValue(LocalDate.now().minusMonths(1));fechaHasta.setValue(LocalDate.now());
        fechaFuncion.setValue(LocalDate.now().plusDays(1));horaFuncion.setText("19:00");
        listaPeliculas.getSelectionModel().selectedItemProperty().addListener((o,a,p)->{
            if(p!=null) { titulo.setText(p.getTitulo());genero.setText(p.getGenero());duracion.setText(""+p.getDuracion()); }
        });
        listaArticulos.getSelectionModel().selectedItemProperty().addListener((o,a,p)->{
            if(p instanceof Producto x) { nombreProducto.setText(x.getNombre());precioProducto.setText(""+x.getPrecio());stockProducto.setText(""+x.getStock()); }
        });
        listaClientes.getSelectionModel().selectedItemProperty().addListener((o,a,c)->mostrarCliente(c));
        refrescar();
    }
    /**
     * Refresca las listas, estadísticas y reportes.
     */
    @FXML private void refrescar() {
        CineController c=app.getCine();
        listaPeliculas.setItems(FXCollections.observableArrayList(c.peliculas()));
        listaSalas.setItems(FXCollections.observableArrayList(c.salas()));
        salaAsiento.setItems(FXCollections.observableArrayList(c.salas()));
        salaFuncion.setItems(FXCollections.observableArrayList(c.salas()));
        peliculaFuncion.setItems(FXCollections.observableArrayList(c.peliculas().stream().filter(Pelicula::isActiva).toList()));
        listaFunciones.setItems(FXCollections.observableArrayList(c.funciones()));
        listaArticulos.setItems(FXCollections.observableArrayList(c.articulos()));
        comboPrimero.setItems(FXCollections.observableArrayList(c.productos()));
        comboSegundo.setItems(FXCollections.observableArrayList(c.productos()));
        listaPromociones.setItems(FXCollections.observableArrayList(c.promociones()));
        Cliente anterior=listaClientes.getSelectionModel().getSelectedItem();
        listaClientes.setItems(FXCollections.observableArrayList(c.clientes()));
        if(anterior!=null) listaClientes.getSelectionModel().select(anterior);
        listaSolicitudes.setItems(FXCollections.observableArrayList(c.solicitudes()));
        LocalDate desde=fechaDesde.getValue(),hasta=fechaHasta.getValue();
        if(desde!=null && hasta!=null && !hasta.isBefore(desde)) {
            ventas.setText("Ventas netas: $"+c.ventasNetas(desde,hasta));
            entradas.setText("Entradas vendidas: "+c.ventasPorPelicula(desde,hasta).values().stream().mapToLong(Long::longValue).sum());
            reembolsos.setText("Reembolsos: $"+c.comprasPeriodo(desde,hasta).stream().mapToLong(Compra::getReembolso).sum());
        }
        ocupacion.setText("Ocupación actual: "+c.asientosVendidos()+" / "+c.capacidadDisponible());
    }
    /**
     * Muestra tarjeta y compras de un cliente.
     * @param cliente cliente
     */
    private void mostrarCliente(Cliente cliente) {
        if(cliente==null) { listaCompras.getItems().clear();datosCliente.setText("Selecciona un cliente.");return; }
        datosCliente.setText("Saldo $"+cliente.getTarjeta().getSaldo()+" · "+(cliente.getTarjeta().isActiva()?"ACTIVA":"INACTIVA")+" · "+cliente.getPuntos().getSaldo()+" puntos");
        listaCompras.setItems(FXCollections.observableArrayList(app.getCine().compras(cliente)));
    }
    /**
     * Crea una película.
     */
    @FXML private void crearPelicula() { intentar(()->app.getCine().crearPelicula(titulo.getText(),genero.getText(),Ui.entero(duracion.getText()))); }
    /**
     * Actualiza la película seleccionada.
     */
    @FXML private void editarPelicula() { intentar(()->app.getCine().editarPelicula(listaPeliculas.getSelectionModel().getSelectedItem(),titulo.getText(),genero.getText(),Ui.entero(duracion.getText()))); }
    /**
     * Activa o desactiva una película.
     */
    @FXML private void alternarPelicula() { intentar(()->app.getCine().alternarPelicula(listaPeliculas.getSelectionModel().getSelectedItem())); }
    /**
     * Crea una sala.
     */
    @FXML private void crearSala() { intentar(()->app.getCine().crearSala(nombreSala.getText(),Ui.entero(filas.getText()),Ui.entero(columnas.getText()))); }
    /**
     * Bloquea o desbloquea un asiento de una sala.
     */
    @FXML private void alternarAsiento() { intentar(()->app.getCine().alternarAsiento(salaAsiento.getValue(),asiento.getText())); }
    /**
     * Crea una función.
     */
    @FXML private void crearFuncion() {
        intentar(()->app.getCine().crearFuncion(peliculaFuncion.getValue(),salaFuncion.getValue(),
                LocalDateTime.of(fechaFuncion.getValue(),LocalTime.parse(horaFuncion.getText().trim())),Ui.dinero(precioFuncion.getText())));
    }
    /**
     * Cancela la función seleccionada y devuelve el dinero.
     */
    @FXML private void cancelarFuncion() {
        Funcion f=listaFunciones.getSelectionModel().getSelectedItem();
        if(f!=null && Ui.confirmar("¿Cancelar la función y devolver el 100% a sus compradores?")) intentar(()->app.getCine().cancelarFuncion(f));
    }
    /**
     * Crea un producto individual.
     */
    @FXML private void crearProducto() { intentar(()->app.getCine().crearProducto(nombreProducto.getText(),Ui.dinero(precioProducto.getText()),Ui.entero(stockProducto.getText()))); }
    /**
     * Actualiza el producto seleccionado.
     */
    @FXML private void editarProducto() {
        Articulo a=listaArticulos.getSelectionModel().getSelectedItem();
        intentar(()->app.getCine().editarProducto(a instanceof Producto p?p:null,nombreProducto.getText(),Ui.dinero(precioProducto.getText()),Ui.entero(stockProducto.getText())));
    }
    /**
     * Crea un combo de dos productos.
     */
    @FXML private void crearCombo() { intentar(()->app.getCine().crearCombo(nombreCombo.getText(),Ui.dinero(precioCombo.getText()),comboPrimero.getValue(),comboSegundo.getValue())); }
    /**
     * Crea una promoción porcentual.
     */
    @FXML private void crearPromocion() { intentar(()->app.getCine().crearPromocion(nombrePromocion.getText(),Ui.entero(porcentajePromocion.getText()))); }
    /**
     * Activa o desactiva una promoción.
     */
    @FXML private void alternarPromocion() { intentar(()->app.getCine().alternarPromocion(listaPromociones.getSelectionModel().getSelectedItem())); }
    /**
     * Recarga la tarjeta del cliente seleccionado.
     */
    @FXML private void recargar() { intentar(()->app.getCine().recargar(listaClientes.getSelectionModel().getSelectedItem(),Ui.dinero(valorRecarga.getText()))); }
    /**
     * Activa o bloquea la tarjeta del cliente seleccionado.
     */
    @FXML private void alternarTarjeta() { intentar(()->app.getCine().alternarTarjeta(listaClientes.getSelectionModel().getSelectedItem())); }
    /**
     * Genera texto y gráficos para el periodo elegido.
     */
    @FXML private void generarReporte() {
        try {
            LocalDate desde=fechaDesde.getValue(),hasta=fechaHasta.getValue();
            textoReporte.setText(app.getCine().reporte(desde,hasta));
            graficar(desde,hasta);refrescar();
        } catch(Exception e) { Ui.error(e); }
    }
    /**
     * Llena gráficos JavaFX de barras, pastel y líneas.
     * @param desde inicio
     * @param hasta fin
     */
    private void graficar(LocalDate desde,LocalDate hasta) {
        CineController c=app.getCine();
        barras.getData().clear();XYChart.Series<String,Number> porPelicula=new XYChart.Series<>();porPelicula.setName("Entradas");
        c.ventasPorPelicula(desde,hasta).forEach((nombre,numero)->porPelicula.getData().add(new XYChart.Data<>(nombre,numero)));
        barras.getData().add(porPelicula);
        int vendidos=c.asientosVendidos(),total=c.capacidadDisponible();
        pastel.setData(FXCollections.observableArrayList(new PieChart.Data("Ocupados",vendidos),new PieChart.Data("Libres",Math.max(0,total-vendidos))));
        linea.getData().clear();XYChart.Series<String,Number> porDia=new XYChart.Series<>();porDia.setName("Ventas netas");
        c.ventasPorDia(desde,hasta).forEach((fecha,valor)->porDia.getData().add(new XYChart.Data<>(fecha.toString(),valor)));
        linea.getData().add(porDia);
    }
    /**
     * Exporta el reporte visible a un archivo TXT.
     */
    @FXML private void exportarReporte() {
        try {
            FileChooser chooser=new FileChooser();chooser.setTitle("Guardar reporte CinemaUQ");chooser.setInitialFileName("reporte-cinemauq.txt");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Texto (*.txt)","*.txt"));
            File destino=chooser.showSaveDialog(textoReporte.getScene().getWindow());
            if(destino!=null) { app.getCine().exportarTXT(destino,fechaDesde.getValue(),fechaHasta.getValue());Ui.info("Reporte guardado en:\n"+destino.getAbsolutePath()); }
        } catch(Exception e) { Ui.error(e); }
    }
    /**
     * Ejecuta una acción con manejo uniforme de errores y actualización.
     * @param accion acción
     */
    private void intentar(Runnable accion) {
        try { accion.run();refrescar();Ui.info("Operación realizada correctamente."); }
        catch(Exception e) { Ui.error(e); }
    }
    /**
     * Cierra la sesión.
     */
    @FXML private void salir() { app.mostrarAcceso(); }
}
