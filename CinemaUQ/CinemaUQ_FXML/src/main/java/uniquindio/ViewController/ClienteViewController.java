package uniquindio.ViewController;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.util.StringConverter;
import uniquindio.App;
import uniquindio.Controller.CineController;
import uniquindio.Model.*;

import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * ViewController FXML del cliente: cartelera, asientos, compras, tarjeta y puntos.
 */
public class ClienteViewController {
    @FXML private Label saludo,saldo,disponibles,puntos,resumen,detalleFuncion,leyenda;
    @FXML private Label saldoTarjeta,puntosDetalle;
    @FXML private ComboBox<Funcion> funcionCombo;
    @FXML private ComboBox<Articulo> articuloCombo;
    @FXML private Spinner<Integer> cantidad;
    @FXML private GridPane asientosGrid;
    @FXML private CheckBox redimirEntradas,redimirProductos;
    @FXML private ListView<String> carrito,listaPuntos;
    @FXML private ListView<Movimiento> listaMovimientos;
    @FXML private ListView<Compra> listaCompras;
    @FXML private TextField valorRecarga;
    private final Set<String> seleccion=new TreeSet<>();
    private final Map<Integer,Integer> cantidades=new LinkedHashMap<>();
    private App app;
    private Cliente cliente;
    /**
     * Conecta modelo y aplicación.
     * @param app aplicación
     * @param cliente usuario autenticado
     */
    public void setApp(App app,Cliente cliente) {
        this.app=app;this.cliente=cliente;
        cantidad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,20,1));
        funcionCombo.setConverter(new StringConverter<>() {
            /**
     * @param f función
     * @return título legible
     */
            public String toString(Funcion f) { return f==null?"Solo confitería":app.getCine().describirFuncion(f)+" · "+f.getFecha().format(DateTimeFormatter.ofPattern("dd/MM HH:mm")); }
            /**
     * @param s texto
     * @return no se permite editar
     */
            public Funcion fromString(String s) { return null; }
        });
        funcionCombo.setOnAction(e->{ seleccion.clear();dibujarAsientos();actualizarResumen(); });
        redimirEntradas.setOnAction(e->actualizarResumen());redimirProductos.setOnAction(e->actualizarResumen());
        refrescar();
    }
    /**
     * Actualiza todos los controles con los datos del modelo.
     */
    private void refrescar() {
        CineController c=app.getCine();
        saludo.setText("Hola, "+cliente.getNombre());
        saldo.setText("$"+cliente.getTarjeta().getSaldo());puntos.setText(cliente.getPuntos().getSaldo()+" pts");
        saldoTarjeta.setText("Saldo disponible: $"+cliente.getTarjeta().getSaldo()+" · "+(cliente.getTarjeta().isActiva()?"ACTIVA":"INACTIVA"));
        puntosDetalle.setText("Puntos disponibles: "+cliente.getPuntos().getSaldo()+" · 1 punto por $1.000 pagados · vencen al año");
        listaPuntos.setItems(FXCollections.observableArrayList(cliente.getPuntos().getLotes().stream().map(Object::toString).toList()));
        listaMovimientos.setItems(FXCollections.observableArrayList(cliente.getTarjeta().getMovimientos()));
        listaCompras.setItems(FXCollections.observableArrayList(c.compras(cliente)));
        Funcion anterior=funcionCombo.getValue();
        funcionCombo.setItems(FXCollections.observableArrayList(c.funcionesDisponibles()));
        if(anterior!=null && funcionCombo.getItems().contains(anterior)) funcionCombo.setValue(anterior);
        else if(!funcionCombo.getItems().isEmpty()) funcionCombo.setValue(funcionCombo.getItems().get(0));
        articuloCombo.setItems(FXCollections.observableArrayList(c.articulos()));
        if(!articuloCombo.getItems().isEmpty() && articuloCombo.getValue()==null) articuloCombo.setValue(articuloCombo.getItems().get(0));
        dibujarAsientos();actualizarCarrito();
    }
    /**
     * Dibuja la cuadrícula de asientos en JavaFX.
     */
    private void dibujarAsientos() {
        asientosGrid.getChildren().clear();
        Funcion f=funcionCombo.getValue();
        if(f==null) { detalleFuncion.setText("Elige una función para comprar entradas o compra solo confitería.");return; }
        CineController c=app.getCine();Sala sala=c.salaDe(f);Set<String> ocupados=c.asientosOcupados(f);
        detalleFuncion.setText(c.describirFuncion(f)+" · "+f.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))+" · Entrada $"+c.precioEntrada(f));
        for(int fila=0;fila<sala.getFilas();fila++) {
            Label etiqueta=new Label(String.valueOf((char)('A'+fila)));etiqueta.getStyleClass().add("seat-row");
            asientosGrid.add(etiqueta,0,fila);
            for(int col=1;col<=sala.getColumnas();col++) {
                String codigo=""+(char)('A'+fila)+col;
                Button b=new Button(""+col);b.setMinSize(37,31);b.setMaxSize(37,31);
                boolean bloqueado=sala.estaBloqueado(codigo),ocupado=ocupados.contains(codigo);
                b.getStyleClass().add("seat");
                if(bloqueado || ocupado) { b.getStyleClass().add("seat-occupied");b.setDisable(true); }
                else if(seleccion.contains(codigo)) b.getStyleClass().add("seat-selected");
                else if(sala.esAccesible(codigo)) b.getStyleClass().add("seat-accessible");
                b.setTooltip(new Tooltip(codigo+(ocupado?" · Ocupado":bloqueado?" · Bloqueado":" · Disponible")));
                b.setOnAction(e->{ if(!seleccion.add(codigo)) seleccion.remove(codigo);dibujarAsientos();actualizarResumen(); });
                asientosGrid.add(b,col,fila);
            }
        }
        disponibles.setText(seleccion.isEmpty()?"Ningún asiento seleccionado":"Asientos: "+String.join(", ",seleccion));
    }
    /**
     * Añade confitería al carrito local.
     */
    @FXML private void agregarArticulo() {
        Articulo a=articuloCombo.getValue();if(a==null) return;
        cantidades.merge(a.getId(),cantidad.getValue(),Integer::sum);
        actualizarCarrito();
    }
    /**
     * Quita una línea de confitería seleccionada.
     */
    @FXML private void quitarArticulo() {
        int i=carrito.getSelectionModel().getSelectedIndex();
        if(i<0) return;
        Integer id=new ArrayList<>(cantidades.keySet()).get(i);
        cantidades.remove(id);actualizarCarrito();
    }
    /**
     * Refresca la lista de artículos añadidos.
     */
    private void actualizarCarrito() {
        List<String> filas=new ArrayList<>();
        for(var e:cantidades.entrySet()) {
            Articulo a=app.getCine().articulos().stream().filter(x->x.getId()==e.getKey()).findFirst().orElse(null);
            if(a!=null) filas.add(e.getValue()+" x "+a.getNombre()+" · $"+app.getCine().precioFinal(a)*e.getValue());
        }
        carrito.setItems(FXCollections.observableArrayList(filas));actualizarResumen();
    }
    /**
     * Calcula el total y los puntos necesarios.
     */
    private void actualizarResumen() {
        if(app==null) return;
        resumen.setText(app.getCine().resumen(funcionCombo.getValue(),seleccion,cantidades,redimirEntradas.isSelected(),redimirProductos.isSelected()));
    }
    /**
     * Confirma la compra con la tarjeta virtual simulada.
     */
    @FXML private void comprar() {
        try {
            Compra compra=app.getCine().comprar(cliente,funcionCombo.getValue(),Set.copyOf(seleccion),new LinkedHashMap<>(cantidades),redimirEntradas.isSelected(),redimirProductos.isSelected());
            Ui.info("¡Compra #"+compra.getId()+" confirmada!\nPagado: $"+compra.getPagado()+"\nPuntos usados: "+compra.getPuntosUsados()+"\nPuntos ganados: "+compra.getPuntosGanados());
            seleccion.clear();cantidades.clear();redimirEntradas.setSelected(false);redimirProductos.setSelected(false);refrescar();
        } catch(Exception e) { Ui.error(e); }
    }
    /**
     * Solicita la cancelación de una compra seleccionada.
     */
    @FXML private void cancelarCompra() {
        Compra compra=listaCompras.getSelectionModel().getSelectedItem();
        if(compra==null) { Ui.info("Selecciona una compra.");return; }
        if(!Ui.confirmar("¿Cancelar compra #"+compra.getId()+"?\n>24h: 100%; 2-24h: 80%; <2h: no permitido.")) return;
        try { long valor=app.getCine().cancelarCompra(compra,false);Ui.info("Compra cancelada. Reembolso a la tarjeta: $"+valor);refrescar(); }
        catch(Exception e) { Ui.error(e); }
    }
    /**
     * Envía una solicitud de recarga al administrador.
     */
    @FXML private void solicitarRecarga() {
        try { app.getCine().solicitarRecarga(cliente,Ui.dinero(valorRecarga.getText()));Ui.info("Solicitud registrada. Un administrador debe aprobar la recarga.");valorRecarga.clear(); }
        catch(Exception e) { Ui.error(e); }
    }
    /**
     * Actualiza manualmente los datos.
     */
    @FXML private void actualizar() { refrescar(); }
    /**
     * Cierra la sesión.
     */
    @FXML private void salir() { app.mostrarAcceso(); }
}
