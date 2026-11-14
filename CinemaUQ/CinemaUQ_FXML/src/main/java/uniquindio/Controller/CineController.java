package uniquindio.Controller;

import uniquindio.Model.*;
import uniquindio.Pattern.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador del dominio y Facade: coordina las operaciones de CinemaUQ.
 */
public class CineController {
    /**
     * Puntos por entrada redimida.
     */
    public static final int PUNTOS_ENTRADA=120;
    /**
     * Puntos por producto individual.
     */
    public static final int PUNTOS_PRODUCTO=40;
    /**
     * Puntos por combo.
     */
    public static final int PUNTOS_COMBO=70;
    private final Cinema cine=Cinema.getInstancia();
    private final Pago pago=new ProxyPago(new AdaptadorTarjeta());
    private final FabricaEntrada fabrica=new FabricaEntradaNormal();
    private final CentroAvisos avisos=new CentroAvisos();
    private final List<String> notificaciones=new ArrayList<>();

    /**
     * Construye la fachada y conecta el observador de eventos.
     */
    public CineController() { avisos.suscribir(notificaciones::add); }
    /**
     * @return últimos eventos emitidos
     */
    public List<String> getNotificaciones() { return List.copyOf(notificaciones); }
    /**
     * Verifica el acceso.
     * @param correo correo
     * @param clave contraseña
     * @return usuario autenticado
     */
    public Usuario iniciarSesion(String correo,String clave) {
        return cine.getUsuarios().stream().filter(u->u.getCorreo().equalsIgnoreCase(correo.trim()) && u.validarClave(clave)).findFirst()
                .orElseThrow(()->new ExcepcionCine("Correo o contraseña incorrectos."));
    }
    /**
     * Registra un cliente.
     * @param nombre nombre
     * @param correo correo
     * @param clave contraseña
     * @return nuevo cliente
     */
    public synchronized Cliente registrar(String nombre,String correo,String clave) {
        validarTexto(nombre,"Nombre");validarTexto(correo,"Correo");validarTexto(clave,"Contraseña");
        if(!correo.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) throw new ExcepcionCine("Correo no válido.");
        if(clave.length()<4) throw new ExcepcionCine("La contraseña debe tener al menos 4 caracteres.");
        if(cine.getUsuarios().stream().anyMatch(u->u.getCorreo().equalsIgnoreCase(correo.trim()))) throw new ExcepcionCine("El correo ya está registrado.");
        Cliente cliente=new Cliente(cine.nuevoId(),nombre.trim(),correo.trim(),clave);cine.agregar(cliente);cine.guardar();return cliente;
    }
    /**
     * @return películas registradas
     */
    public List<Pelicula> peliculas() { return new ArrayList<>(cine.getPeliculas()); }
    /**
     * @return salas registradas
     */
    public List<Sala> salas() { return new ArrayList<>(cine.getSalas()); }
    /**
     * @return funciones registradas
     */
    public List<Funcion> funciones() { return cine.getFunciones().stream().sorted(Comparator.comparing(Funcion::getFecha)).toList(); }
    /**
     * @return funciones futuras activas
     */
    public List<Funcion> funcionesDisponibles() {
        return funciones().stream().filter(f->!f.isCancelada() && f.getFecha().isAfter(LocalDateTime.now()) && cine.pelicula(f.getPeliculaId()).isActiva()).toList();
    }
    /**
     * @return artículos
     */
    public List<Articulo> articulos() { return new ArrayList<>(cine.getArticulos()); }
    /**
     * @return productos individuales
     */
    public List<Producto> productos() { return cine.getArticulos().stream().filter(a->a instanceof Producto).map(a->(Producto)a).toList(); }
    /**
     * @return promociones
     */
    public List<Promocion> promociones() { return new ArrayList<>(cine.getPromociones()); }
    /**
     * @return clientes
     */
    public List<Cliente> clientes() { return cine.getUsuarios().stream().filter(u->u instanceof Cliente).map(u->(Cliente)u).toList(); }
    /**
     * @return compras
     */
    public List<Compra> compras() { return new ArrayList<>(cine.getCompras()); }
    /**
     * @param cliente cliente
     * @return sus compras
     */
    public List<Compra> compras(Cliente cliente) { return compras().stream().filter(c->c.getClienteId()==cliente.getId()).toList(); }
    /**
     * @return solicitudes de recarga
     */
    public List<String> solicitudes() { return List.copyOf(cine.getSolicitudes()); }
    /**
     * @param funcion función
     * @return título de película y sala
     */
    public String describirFuncion(Funcion funcion) {
        return funcion==null?"Confitería":cine.pelicula(funcion.getPeliculaId()).getTitulo()+" · "+cine.sala(funcion.getSalaId()).getNombre();
    }
    /**
     * @param id función
     * @return función o null
     */
    public Funcion buscarFuncion(int id) { return cine.funcion(id); }
    /**
     * @param funcion función
     * @return sala asociada
     */
    public Sala salaDe(Funcion funcion) { return cine.sala(funcion.getSalaId()); }
    /**
     * @param funcion función
     * @return asientos vendidos y vigentes
     */
    public Set<String> asientosOcupados(Funcion funcion) {
        return cine.getCompras().stream().filter(c->c.permiteCancelar() && c.getFuncionId()==funcion.getId())
                .flatMap(c->c.getEntradas().stream()).map(Entrada::getAsiento).collect(Collectors.toSet());
    }
    /**
     * @return porcentaje de descuento de la primera promoción activa
     */
    public int descuentoActual() { return cine.getPromociones().stream().filter(Promocion::isActiva).mapToInt(Promocion::getPorcentaje).findFirst().orElse(0); }
    /**
     * @param articulo artículo
     * @return precio tras descuento
     */
    public long precioFinal(Articulo articulo) { return new ArticuloConDescuento(articulo,descuentoActual()).getPrecio(); }
    /**
     * @param funcion función
     * @return precio de entrada con descuento
     */
    public long precioEntrada(Funcion funcion) { return funcion.getPrecio()*(100-descuentoActual())/100; }
    /**
     * @param articulo artículo
     * @return puntos necesarios para una unidad
     */
    public int puntosArticulo(Articulo articulo) { return articulo instanceof Combo?PUNTOS_COMBO:PUNTOS_PRODUCTO; }
    /**
     * @param funcion función
     * @param asientos asientos
     * @param cantidades productos
     * @param puntosEntradas redimir entradas
     * @param puntosProductos redimir productos
     * @return texto del resumen
     */
    public String resumen(Funcion funcion,Set<String> asientos,Map<Integer,Integer> cantidades,boolean puntosEntradas,boolean puntosProductos) {
        long total=asientos.size()*(puntosEntradas?0:funcion==null?0:precioEntrada(funcion));
        int puntos=asientos.size()*(puntosEntradas?PUNTOS_ENTRADA:0);
        for(var e:cantidades.entrySet()) {
            Articulo a=cine.articulo(e.getKey());if(a==null) continue;
            total+=puntosProductos?0:precioFinal(a)*e.getValue();
            puntos+=puntosProductos?puntosArticulo(a)*e.getValue():0;
        }
        return "Pagar con tarjeta: $"+total+"     |     Redimir: "+puntos+" puntos     |     Promoción: "+descuentoActual()+"%";
    }
    /** Realiza una compra atómica y registra sus entradas, inventario, pago y puntos.
     * @param cliente comprador @param funcion función (puede ser null para confitería) @param asientos asientos elegidos
     * @param cantidades unidades por artículo @param puntosEntradas pagar entradas con puntos @param puntosProductos pagar confitería con puntos
     * @return compra confirmada */
    public synchronized Compra comprar(Cliente cliente,Funcion funcion,Set<String> asientos,Map<Integer,Integer> cantidades,boolean puntosEntradas,boolean puntosProductos) {
        if(asientos.isEmpty() && cantidades.isEmpty()) throw new ExcepcionCine("Selecciona asientos o productos.");
        if(!asientos.isEmpty()) {
            if(funcion==null || funcion.isCancelada() || !funcion.getFecha().isAfter(LocalDateTime.now()) || !cine.pelicula(funcion.getPeliculaId()).isActiva())
                throw new ExcepcionCine("La función no está disponible.");
            Sala sala=salaDe(funcion);Set<String> ocupados=asientosOcupados(funcion);
            for(String asiento:asientos) if(!sala.existe(asiento) || sala.estaBloqueado(asiento) || ocupados.contains(asiento))
                throw new ExcepcionCine("El asiento "+asiento+" no está disponible.");
        }
        Map<Producto,Integer> demanda=new HashMap<>();
        List<Compra.Linea> lineas=new ArrayList<>();
        long total=asientos.size()*(puntosEntradas?0:precioEntrada(funcion));
        int puntos=asientos.size()*(puntosEntradas?PUNTOS_ENTRADA:0);
        for(var e:cantidades.entrySet()) {
            Articulo a=cine.articulo(e.getKey());int cantidad=e.getValue();
            if(a==null || cantidad<=0 || cantidad>50) throw new ExcepcionCine("Cantidad o producto inválido.");
            if(a instanceof Producto p) demanda.merge(p,cantidad,Integer::sum);
            else if(a instanceof Combo combo) for(Producto p:combo.getProductos()) demanda.merge(p,cantidad,Integer::sum);
            long importe=puntosProductos?0:precioFinal(a)*cantidad;
            total+=importe; puntos+=puntosProductos?puntosArticulo(a)*cantidad:0;
            lineas.add(new Compra.Linea(a.getId(),a.getNombre(),cantidad,puntosProductos,importe));
        }
        for(var e:demanda.entrySet()) if(e.getValue()>e.getKey().getStock()) throw new ExcepcionCine("Stock insuficiente de "+e.getKey().getNombre());
        if(cliente.getPuntos().getSaldo()<puntos) throw new ExcepcionCine("Puntos insuficientes. Necesitas "+puntos+".");
        if(!cliente.getTarjeta().isActiva() || cliente.getTarjeta().getSaldo()<total) throw new ExcepcionCine("Tarjeta inactiva o saldo insuficiente. Solicita recarga.");
        int id=cine.nuevoId();
        List<Entrada> entradas=new ArrayList<>();
        if(funcion!=null) for(String asiento:new TreeSet<>(asientos)) entradas.add(fabrica.crear(funcion,asiento,puntosEntradas?0:precioEntrada(funcion)));
        pago.cobrar(cliente,total,"Compra #"+id);
        List<CuentaPuntos.Lote> lotes=cliente.getPuntos().redimir(puntos);
        for(var e:demanda.entrySet()) e.getKey().descontar(e.getValue());
        int ganados=(int)(total/1000); // 1 punto por cada $1.000 COP realmente pagados.
        Compra compra=new Compra.Builder().id(id).cliente(cliente.getId()).funcion(entradas.isEmpty()?0:funcion.getId())
                .entradas(entradas).productos(lineas).pagado(total).puntosUsados(puntos).puntosGanados(ganados).lotes(lotes).build();
        cine.getCompras().add(compra);cliente.getPuntos().acumular(ganados,id);
        cine.guardar();avisos.publicar("Compra #"+id+" realizada por "+cliente.getNombre());
        return compra;
    }
    /**
     * Cancela una compra bajo las reglas de antelación.
     * @param compra compra
     * @param administrador true si el cine cancela la función
     * @return importe devuelto
     */
    public synchronized long cancelarCompra(Compra compra,boolean administrador) {
        if(!compra.permiteCancelar()) throw new ExcepcionCine("Esta compra ya fue cancelada.");
        Funcion funcion=cine.funcion(compra.getFuncionId());
        PoliticaReembolso politica=administrador?new ReembolsoTotal():new ReembolsoPorTiempo();
        long valor=politica.calcular(compra,funcion);
        if(valor<0) throw new ExcepcionCine("No se permite cancelar con menos de 2 horas o compras solo de confitería.");
        Cliente cliente=(Cliente)cine.usuario(compra.getClienteId());
        compra.cancelar(valor); // State bloquea reembolsos duplicados.
        cliente.getTarjeta().reembolsar(valor,"Compra #"+compra.getId());
        cliente.getPuntos().revocar(compra.getId(),compra.getPuntosGanados());
        cliente.getPuntos().devolver(compra.getLotesUsados());
        // Se devuelve el inventario de confitería no entregada al cancelar.
        for(Compra.Linea linea:compra.getLineas()) {
            Articulo a=cine.articulo(linea.getArticuloId());
            if(a instanceof Producto p) p.reponer(linea.getCantidad());
            else if(a instanceof Combo combo) for(Producto p:combo.getProductos()) p.reponer(linea.getCantidad());
        }
        cine.guardar();avisos.publicar("Compra #"+compra.getId()+" cancelada; reembolso $"+valor);
        return valor;
    }
    /**
     * Solicita una recarga sin modificar saldo.
     * @param cliente cliente
     * @param valor monto solicitado
     */
    public void solicitarRecarga(Cliente cliente,long valor) {
        if(valor<=0) throw new ExcepcionCine("Indica un valor positivo.");
        cine.getSolicitudes().add(cliente.getCorreo()+" solicita $"+valor);cine.guardar();avisos.publicar("Solicitud de recarga: "+cliente.getCorreo());
    }
    /**
     * Registra una película.
     * @param titulo título
     * @param genero género
     * @param duracion minutos
     */
    public void crearPelicula(String titulo,String genero,int duracion) {
        validarTexto(titulo,"Título");validarTexto(genero,"Género");if(duracion<1) throw new ExcepcionCine("Duración inválida.");
        cine.agregar(new Pelicula(cine.nuevoId(),titulo,genero,duracion));cine.guardar();
    }
    /**
     * Actualiza una película.
     * @param pelicula película
     * @param titulo título
     * @param genero género
     * @param duracion minutos
     */
    public void editarPelicula(Pelicula pelicula,String titulo,String genero,int duracion) {
        if(pelicula==null) throw new ExcepcionCine("Selecciona una película.");validarTexto(titulo,"Título");validarTexto(genero,"Género");
        if(duracion<1) throw new ExcepcionCine("Duración inválida.");pelicula.actualizar(titulo,genero,duracion);cine.guardar();
    }
    /**
     * Cambia el estado de una película.
     * @param pelicula película
     */
    public void alternarPelicula(Pelicula pelicula) { if(pelicula==null) throw new ExcepcionCine("Selecciona una película.");pelicula.alternar();cine.guardar(); }
    /**
     * Crea una sala.
     * @param nombre nombre
     * @param filas filas
     * @param columnas columnas
     */
    public void crearSala(String nombre,int filas,int columnas) {
        validarTexto(nombre,"Sala");if(filas<1 || filas>8 || columnas<1 || columnas>12) throw new ExcepcionCine("Filas 1-8; columnas 1-12.");
        cine.agregar(new Sala(cine.nuevoId(),nombre,filas,columnas));cine.guardar();
    }
    /**
     * Bloquea o desbloquea un asiento si no está vendido.
     * @param sala sala
     * @param asiento código
     */
    public void alternarAsiento(Sala sala,String asiento) {
        if(sala==null) throw new ExcepcionCine("Selecciona una sala.");asiento=asiento.toUpperCase().trim();
        for(Funcion f:cine.getFunciones()) if(f.getSalaId()==sala.getId() && !f.isCancelada() && f.getFecha().isAfter(LocalDateTime.now()) && asientosOcupados(f).contains(asiento))
            throw new ExcepcionCine("Hay una entrada vigente para ese asiento.");
        sala.alternarBloqueo(asiento);cine.guardar();
    }
    /**
     * Crea una función y evita traslapes en la misma sala.
     * @param pelicula película
     * @param sala sala
     * @param fecha fecha/hora
     * @param precio precio
     */
    public void crearFuncion(Pelicula pelicula,Sala sala,LocalDateTime fecha,long precio) {
        if(pelicula==null || sala==null || !pelicula.isActiva()) throw new ExcepcionCine("Selecciona película activa y sala.");
        if(fecha==null || !fecha.isAfter(LocalDateTime.now())) throw new ExcepcionCine("La función debe ser futura.");
        if(precio<=0) throw new ExcepcionCine("Precio inválido.");
        for(Funcion f:cine.getFunciones()) if(!f.isCancelada() && f.getSalaId()==sala.getId()) {
            LocalDateTime inicio=f.getFecha(),fin=inicio.plusMinutes(cine.pelicula(f.getPeliculaId()).getDuracion()+15);
            LocalDateTime nuevoFin=fecha.plusMinutes(pelicula.getDuracion()+15);
            if(fecha.isBefore(fin) && nuevoFin.isAfter(inicio)) throw new ExcepcionCine("La sala ya tiene función en ese horario.");
        }
        cine.agregar(new Funcion(cine.nuevoId(),pelicula.getId(),sala.getId(),fecha,precio));cine.guardar();
    }
    /**
     * Cancela una función y reembolsa el 100% a todos los compradores.
     * @param funcion función
     */
    public synchronized void cancelarFuncion(Funcion funcion) {
        if(funcion==null || funcion.isCancelada()) throw new ExcepcionCine("Selecciona una función vigente.");
        funcion.cancelar();
        for(Compra c:new ArrayList<>(cine.getCompras())) if(c.getFuncionId()==funcion.getId() && c.permiteCancelar()) cancelarCompra(c,true);
        cine.guardar();avisos.publicar("Función #"+funcion.getId()+" cancelada por CinemaUQ.");
    }
    /**
     * Crea un producto.
     * @param nombre nombre
     * @param precio precio
     * @param stock unidades
     */
    public void crearProducto(String nombre,long precio,int stock) {
        validarTexto(nombre,"Producto");if(precio<=0 || stock<0) throw new ExcepcionCine("Precio o inventario inválido.");
        cine.agregar(new Producto(cine.nuevoId(),nombre,precio,stock));cine.guardar();
    }
    /**
     * Actualiza producto.
     * @param producto producto
     * @param nombre nombre
     * @param precio precio
     * @param stock unidades
     */
    public void editarProducto(Producto producto,String nombre,long precio,int stock) {
        if(producto==null) throw new ExcepcionCine("Selecciona un producto.");validarTexto(nombre,"Nombre");
        if(precio<=0 || stock<0) throw new ExcepcionCine("Precio o inventario inválido.");
        producto.actualizar(nombre,precio,stock);cine.guardar();
    }
    /**
     * Crea combo de dos productos.
     * @param nombre nombre
     * @param precio precio
     * @param primero producto
     * @param segundo producto
     */
    public void crearCombo(String nombre,long precio,Producto primero,Producto segundo) {
        validarTexto(nombre,"Combo");if(precio<=0 || primero==null || segundo==null) throw new ExcepcionCine("Datos del combo inválidos.");
        cine.agregar(new Combo(cine.nuevoId(),nombre,precio,List.of(primero,segundo)));cine.guardar();
    }
    /**
     * Crea promoción.
     * @param nombre nombre
     * @param porcentaje porcentaje
     */
    public void crearPromocion(String nombre,int porcentaje) {
        validarTexto(nombre,"Promoción");if(porcentaje<1 || porcentaje>80) throw new ExcepcionCine("Descuento entre 1% y 80%.");
        if(cine.getPromociones().stream().anyMatch(Promocion::isActiva)) throw new ExcepcionCine("Desactiva la promoción anterior.");
        cine.agregar(new Promocion(cine.nuevoId(),nombre,porcentaje));cine.guardar();
    }
    /**
     * Activa o desactiva una promoción.
     * @param promocion promoción
     */
    public void alternarPromocion(Promocion promocion) {
        if(promocion==null) throw new ExcepcionCine("Selecciona una promoción.");
        if(!promocion.isActiva() && cine.getPromociones().stream().anyMatch(Promocion::isActiva)) throw new ExcepcionCine("Solo una promoción puede estar activa.");
        promocion.alternar();cine.guardar();
    }
    /**
     * Recarga una tarjeta usando Command.
     * @param cliente cliente
     * @param valor importe
     */
    public void recargar(Cliente cliente,long valor) {
        if(cliente==null) throw new ExcepcionCine("Selecciona un cliente.");
        new ComandoRecarga(cliente,valor).ejecutar();
        cine.getSolicitudes().removeIf(s->s.startsWith(cliente.getCorreo()+" solicita"));
        cine.guardar();avisos.publicar("Tarjeta recargada: "+cliente.getCorreo());
    }
    /**
     * Activa o bloquea una tarjeta.
     * @param cliente cliente
     */
    public void alternarTarjeta(Cliente cliente) {
        if(cliente==null) throw new ExcepcionCine("Selecciona un cliente.");cliente.getTarjeta().alternar();cine.guardar();
    }
    /**
     * @param desde inicio
     * @param hasta fin
     * @return compras pagadas en el periodo, por fecha de compra
     */
    public List<Compra> comprasPeriodo(LocalDate desde,LocalDate hasta) {
        return compras().stream().filter(c->!c.getFecha().toLocalDate().isBefore(desde) && !c.getFecha().toLocalDate().isAfter(hasta)).toList();
    }
    /**
     * @param desde inicio
     * @param hasta fin
     * @return ventas cobradas menos reembolsos del periodo de compra
     */
    public long ventasNetas(LocalDate desde,LocalDate hasta) {
        return comprasPeriodo(desde,hasta).stream().mapToLong(c->c.getPagado()-c.getReembolso()).sum();
    }
    /**
     * @param desde inicio
     * @param hasta fin
     * @return ventas agrupadas por película
     */
    public Map<String,Long> ventasPorPelicula(LocalDate desde,LocalDate hasta) {
        Map<String,Long> datos=new LinkedHashMap<>();
        for(Compra c:comprasPeriodo(desde,hasta)) if(c.getFuncionId()!=0 && c.permiteCancelar()) {
            String nombre=cine.pelicula(cine.funcion(c.getFuncionId()).getPeliculaId()).getTitulo();
            datos.merge(nombre,(long)c.getEntradas().size(),Long::sum);
        }
        return datos;
    }
    /**
     * @param desde inicio
     * @param hasta fin
     * @return ventas netas por fecha
     */
    public Map<LocalDate,Long> ventasPorDia(LocalDate desde,LocalDate hasta) {
        Map<LocalDate,Long> datos=new TreeMap<>();
        for(Compra c:comprasPeriodo(desde,hasta)) datos.merge(c.getFecha().toLocalDate(),c.getPagado()-c.getReembolso(),Long::sum);
        return datos;
    }
    /**
     * @return asientos vendidos en todas las funciones futuras activas
     */
    public int asientosVendidos() { return cine.getFunciones().stream().filter(f->!f.isCancelada() && f.getFecha().isAfter(LocalDateTime.now())).mapToInt(f->asientosOcupados(f).size()).sum(); }
    /**
     * @return capacidad total de funciones futuras activas
     */
    public int capacidadDisponible() { return cine.getFunciones().stream().filter(f->!f.isCancelada() && f.getFecha().isAfter(LocalDateTime.now())).mapToInt(f->salaDe(f).getCapacidad()).sum(); }
    /**
     * Genera reporte operativo legible y con totales.
     * @param desde inicio
     * @param hasta fin
     * @return reporte
     */
    public String reporte(LocalDate desde,LocalDate hasta) {
        if(desde==null || hasta==null || hasta.isBefore(desde)) throw new ExcepcionCine("Selecciona un rango válido.");
        List<Compra> lista=comprasPeriodo(desde,hasta);
        StringBuilder sb=new StringBuilder("CINEMAUQ - REPORTE OPERATIVO\nGenerado: "+LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                +"\nPeriodo: "+desde+" a "+hasta+"\nRegistros: "+lista.size()+"\n\n");
        long bruto=lista.stream().mapToLong(Compra::getPagado).sum();
        long devoluciones=lista.stream().mapToLong(Compra::getReembolso).sum();
        sb.append("VENTAS BRUTAS: $").append(bruto).append("\nREEMBOLSOS: $").append(devoluciones)
                .append("\nVENTAS NETAS: $").append(bruto-devoluciones)
                .append("\nCOMPRAS CANCELADAS: ").append(lista.stream().filter(c->!c.permiteCancelar()).count()).append("\n");
        sb.append("\nENTRADAS POR PELÍCULA\n");
        ventasPorPelicula(desde,hasta).forEach((k,v)->sb.append(" - ").append(k).append(": ").append(v).append("\n"));
        sb.append("\nOCUPACIÓN POR FUNCIÓN (ACTUAL)\n");
        for(Funcion f:funciones()) sb.append(" - ").append(describirFuncion(f)).append(" · ").append(f.getFecha())
                .append(": ").append(asientosOcupados(f).size()).append("/").append(salaDe(f).getCapacidad()).append("\n");
        sb.append("\nPRODUCTOS Y COMBOS VENDIDOS (NETO)\n");
        Map<String,Integer> cantidades=new TreeMap<>();
        for(Compra c:lista) if(c.permiteCancelar()) for(Compra.Linea l:c.getLineas()) cantidades.merge(l.getNombre(),l.getCantidad(),Integer::sum);
        cantidades.forEach((k,v)->sb.append(" - ").append(k).append(": ").append(v).append("\n"));
        sb.append("\nRECARGAS DEL PERIODO\n");
        long recargas=0;int numRecargas=0;
        for(Cliente cliente:clientes()) for(Movimiento m:cliente.getTarjeta().getMovimientos()) if(m.getTipo().equals("RECARGA") &&
                !m.getFecha().toLocalDate().isBefore(desde) && !m.getFecha().toLocalDate().isAfter(hasta)) { recargas+=m.getValor();numRecargas++; }
        sb.append("Operaciones: ").append(numRecargas).append(" · Total: $").append(recargas).append("\n");
        sb.append("\nPUNTOS POR CLIENTE (HISTÓRICO)\n");
        for(Cliente c:clientes()) sb.append(" - ").append(c.getNombre()).append(": acumulados ").append(c.getPuntos().getAcumulados())
                .append(" · redimidos ").append(c.getPuntos().getRedimidos()).append(" · vigentes ").append(c.getPuntos().getSaldo()).append("\n");
        sb.append("\nDETALLE DE COMPRAS\n");
        for(Compra c:lista) sb.append(c).append(" · reembolso $").append(c.getReembolso()).append("\n");
        return sb.toString();
    }
    /**
     * Exporta el reporte en TXT.
     * @param archivo destino
     * @param desde inicio
     * @param hasta fin
     */
    public void exportarTXT(File archivo,LocalDate desde,LocalDate hasta) {
        try { Files.writeString(archivo.toPath(),reporte(desde,hasta),StandardCharsets.UTF_8); }
        catch(IOException e) { throw new ExcepcionCine("No se pudo exportar: "+e.getMessage()); }
    }
    /**
     * Comprueba que un campo no esté vacío.
     * @param texto valor
     * @param campo nombre
     */
    private void validarTexto(String texto,String campo) {
        if(texto==null || texto.isBlank()) throw new ExcepcionCine(campo+" es obligatorio.");
    }
}
